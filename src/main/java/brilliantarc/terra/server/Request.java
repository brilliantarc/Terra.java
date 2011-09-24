package brilliantarc.terra.server;

import brilliantarc.terra.SingularityException;
import brilliantarc.terra.User;
import brilliantarc.terra.node.*;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static brilliantarc.terra.server.Accepts.JSON;
import static brilliantarc.terra.server.Accepts.XML;

/**
 * Used internally to communicate with the Terra server.  Functions as a builder
 * around the Jersey client libraries to create HTTP/JSON (or XML) calls to Terra.
 *
 * Requests are not thread-safe.  Use should use a request on a single thread,
 * as the responses from Terra could collide.
 */
public class Request {

    private com.sun.jersey.api.client.Client client;
    private User user;

    public static ObjectMapper mapper = createMapper();

    private String baseUri;
    private String url;
    private String credentials;
    private RequestType requestType = RequestType.GET;
    private Accepts accepts = JSON;
    private String response;

    private static Log log = LogFactory.getLog("brilliantarc.terra");

    public static Map<String, Function<JsonNode, Meme>> JSON_FUNCTIONS = new ImmutableMap.Builder<String, Function<JsonNode, Meme>>()
            .put("Category", new Function<JsonNode, Meme>() {
                public Meme apply(JsonNode jsonNode) {
                    return Category.fromJson(jsonNode);
                }
            }).put("Heading", new Function<JsonNode, Meme>() {
                public Meme apply(JsonNode jsonNode) {
                    return Heading.fromJson(jsonNode);
                }
            }).put("Option", new Function<JsonNode, Meme>() {
                public Meme apply(JsonNode jsonNode) {
                    return Option.fromJson(jsonNode);
                }
            }).put("Property", new Function<JsonNode, Meme>() {
                public Meme apply(JsonNode jsonNode) {
                    return Property.fromJson(jsonNode);
                }
            }).put("Superheading", new Function<JsonNode, Meme>() {
                public Meme apply(JsonNode jsonNode) {
                    return Superheading.fromJson(jsonNode);
                }
            }).put("Synonym", new Function<JsonNode, Meme>() {
                public Meme apply(JsonNode jsonNode) {
                    return Synonym.fromJson(jsonNode);
                }
            }).put("Taxonomy", new Function<JsonNode, Meme>() {
                public Meme apply(JsonNode jsonNode) {
                    return Taxonomy.fromJson(jsonNode);
                }
            }).build();

    /**
     * Create a new request for the Singularity server.  Each REST call should create a new
     * request.
     *
     * @param client a reference to the Jersey client
     * @param server the base URL of the Terra server API, e.g. "http://terra.eurodir.eu/api"
     * @param user   authenticate each request using this user
     */
    public Request(com.sun.jersey.api.client.Client client, String server, User user) {
        this.client = client;
        this.user = user;

        // It's convenient for constructing REST calls to have the slash on the end
        if (server.endsWith("/")) {
            baseUri = server;
        } else {
            baseUri = server + "/";
        }
    }

    /**
     * Create a new request for the Singularity server.  Each REST call should create a new
     * request.
     *
     * @param client a reference to the Jersey client
     * @param server the base URL of the Terra server API, e.g. "http://terra.eurodir.eu/api"
     */
    public Request(com.sun.jersey.api.client.Client client, String server) {
        this(client, server, null);
    }

    /**
     * This is primarily used for testing.
     *
     * @return the full path generated for the request
     */
    public String getUrl() {
        return url;
    }

    /**
     * Primarily used for testing.
     *
     * @return the API credentials of the user
     */
    public String getCredentials() {
        return credentials;
    }

    /**
     * Indicate the path to request on the server, in segments between slashes.  The segments will
     * be escaped for the URL.
     *
     * For example:
     *
     * <verbatim>
     * to("p", portfolioSlug, "d", definitionName, "m")
     * </verbatim>
     *
     * Stores this as the URL for the request:
     *
     * <verbatim>
     * http://localhost/p/some-portfolio-slug/d/some-definition/m
     * </verbatim>
     *
     * @param segments pieces of the path, that will be escaped and assembled into the full path
     *
     * @return this instance of the SingularityRequest, so you can chain methods together
     *
     * @throws NullPointerException if any of the segments is null
     */
    public Request to(String... segments) {
        url = baseUri + path(segments);
        return this;
    }

    /**
     * Indicate the request is an HTTP GET request (default).
     *
     * @return this SingularityRequest object, so calls may be strung together
     */
    public Request get() {
        requestType = RequestType.GET;
        return this;
    }

    /**
     * Indicate the request is an HTTP POST request.
     *
     * @return this SingularityRequest object, so calls may be strung together
     */
    public Request post() {
        requestType = RequestType.POST;
        return this;
    }

    /**
     * Indicate the request is an HTTP PUT request.
     *
     * @return this SingularityRequest object, so calls may be strung together
     */
    public Request put() {
        requestType = RequestType.PUT;
        return this;
    }

    /**
     * Indicate the request is an HTTP DELETE request.
     *
     * @return this SingularityRequest object, so calls may be strung together
     */
    public Request delete() {
        requestType = RequestType.DELETE;
        return this;
    }

    /**
     * Include the user's authentication credentials with the request, if available.
     *
     * @param credentials the user's API credentials
     *
     * @return this SingularityRequest object, so calls may be strung together
     */
    public Request auth(String credentials) {
        this.credentials = credentials;
        return this;
    }

    /**
     * Request an XML response, instead of the default JSON type.  The primary
     * API uses JSON; this is here to support custom traversal functions that
     * return XML data.
     *
     * @return the request as an XML document
     */
    public Request asXml() {
        this.accepts = XML;
        return this;
    }

    /**
     * Make the request with the given parameters.  The IServiceHandler will deal with the results.
     *
     * If the request generates a Singulariy error, the error value will be set in the request will
     * be set.  Call getError() to get the error message.
     *
     * @param params the map of parameters and their values, to send to the server
     *
     * @return the result of the HTTP REST request, as a JSON object
     *
     * @throws SingularityException unable to contact the server, or the request generated an error from Terra
     */
    public Request send(Map<String, Object> params) {

        // Authenticate automatically if we have a user defined
        if (user != null) {
            auth(user.getCredentials());
        }

        try {
            switch (requestType) {
                case POST:
                    if (log.isDebugEnabled())
                        log.debug(String.format("Sending POST request to %s with parameters %s", url, params));
                    response = request().type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(String.class, asForm(params));
                    break;

                case PUT:
                    if (log.isDebugEnabled())
                        log.debug(String.format("Sending PUT request to %s with parameters %s", url, params));
                    response = request().type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).put(String.class, asForm(params));
                    break;

                case DELETE:
                    if (log.isDebugEnabled())
                        log.debug(String.format("Sending DELETE request to %s with parameters %s", url, params));
                    response = request(asQuery(params)).delete(String.class);
                    break;

                default:
                    if (log.isDebugEnabled())
                        log.debug(String.format("Sending GET request to %s with parameters %s", url, params));
                    response = request(asQuery(params)).get(String.class);
            }
        } catch (UniformInterfaceException e) {
            ClientResponse response = e.getResponse();
            try {
                String errorJson = response.getEntity(String.class);
                if (log.isDebugEnabled())
                    log.debug(String.format("Received error: %s", errorJson));
                SingularityException.SingularityError error = SingularityException.SingularityError.fromJson(mapper.readValue(errorJson, JsonNode.class));
                throw new SingularityException(response.getStatus(), error);
            } catch (IOException e1) {
                throw new SingularityException(500, e1.toString(), e1);
            }
        }

        return this;
    }

    public Request send() {
        return send(new HashMap<String, Object>());
    }

    /**
     * You can send an even numbered list of arguments to the send method.  This
     * will have the effect of creating a map out of these arguments and submitting
     * those to the server.  For example:  send("login", login, "password", password).
     *
     * @param args the parameters to send to Terra; must be an even number
     *
     * @return the service request
     */
    public Request send(Object... args) {
        return send(createMap(args));
    }

    /**
     * Convert the result of the request to an object of the given class.
     *
     * @param valueType the type of object
     *
     * @return the object mapped from the JSON, or null if there was an error in the request
     *
     * @throws SingularityException could not map the result
     */
    public <T> T as(Class<T> valueType) {
        if (response != null) {
            if (log.isDebugEnabled())
                log.debug(String.format("Parsing response: %s", response));
            try {
                return mapper.readValue(response, valueType);
            } catch (IOException e) {
                throw new SingularityException(500, "Failed to parse the JSON response from Terra.", e);
            }
        } else {
            return null;
        }
    }

    /**
     * Convert the result of the request to an object of the given class.  This version handles
     * complex types, like returning lists of a type of node or model.  For example, to convert
     * a list of Definition objects:
     *
     * <verbatim>
     * new SingularityRequest(server).to("d").auth(credentials()).send().as(new TypeReference<List<Definition>>() {});
     * </verbatim>
     *
     * TypeReference is a Jackson JSON library solution to type erasure.
     *
     * @param typeReference the type of object
     *
     * @return the object mapped from the JSON, or null if there was an error in the request
     *
     * @throws SingularityException could not map the result
     */
    @SuppressWarnings("unchecked")
    public <T> T as(TypeReference typeReference) {
        if (response != null) {
            if (log.isDebugEnabled())
                log.debug(String.format("Parsing response: %s", response));
            try {
                return (T) mapper.readValue(response, typeReference);
            } catch (IOException e) {
                throw new SingularityException(500, "Failed to parse the JSON response from Terra.", e);
            }
        } else {
            return null;
        }
    }

    /**
     * Useful if you need to custom parse the results.  See Search.Results for
     * an example.
     *
     * @return the response from the server as a simple JsonNode
     */
    public JsonNode root() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, JsonNode.class);
        } catch (IOException e) {
            throw new SingularityException(500, "Failed to parse the JSON response from Terra.", e);
        }
    }

    /**
     * When you will be receiving a mixed set of results from the Terra server,
     * this method will make sense of the chaos.
     *
     * @return the correct Meme objects
     */
    public List<Meme> asMemes() {
        JsonNode root = root();
        if (root.isArray()) {
            return parseResults(root);
        } else {
            return new ArrayList<Meme>();
        }
    }

    public static List<Meme> parseResults(JsonNode parent) {
        if (parent != null) {
            return ImmutableList.copyOf(Iterators.transform(parent.getElements(), new Function<JsonNode, Meme>() {
                public Meme apply(JsonNode jsonNode) {
                    JsonNode definition = jsonNode.path("definition");
                    if (!definition.isMissingNode()) {
                        Function<JsonNode, Meme> transformer = JSON_FUNCTIONS.get(definition.getTextValue());
                        if (transformer != null) {
                            return transformer.apply(jsonNode);
                        } else {
                            log.warn("Unable to parse JSON object; the definition \"" + definition + "\" is unknown: " + jsonNode.toString());
                            return null;
                        }
                    } else {
                        log.warn("Parsing JSON without a definition property is not supported: " + jsonNode.toString());
                        return null;
                    }
                }
            }));
        } else {
            return null;
        }
    }

    /**
     * @return the response parsed as an XML document
     *
     * @throws org.xml.sax.SAXException there was a problem with the XML parsing
     */
    public Document toXml() throws SAXException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(response)));
        } catch (ParserConfigurationException e) {
            throw new SAXException("There is a problem with the XML configuration.", e);
        } catch (IOException e) {
            throw new SAXException("Failed to parse the XML document.", e);
        }
    }

    /**
     * If you're just expecting a String response, call the toString method
     * after your call.
     *
     * @return the raw response from the server (HTML, XML, JSON, plain string)
     */
    @Override
    public String toString() {
        return response;
    }

    /**
     * Look up a Jersey WebResource to the Singularity server.
     *
     * @param path the full path to a JSON resource
     *
     * @return a Jackson WebResource instance to make the request
     */
    private WebResource resource(String path) {
        return client.resource(path);
    }

    /**
     * Helper to make the REST request.  Set the response type using asXml to indicate XML instead
     * of JSON.  Defaults to JSON.
     *
     * @param params the parameters to send to the server
     *
     * @return the Jersey WebResource (response)
     */
    private WebResource.Builder request(MultivaluedMap<String, String> params) {
        switch (accepts) {
            case XML:
                return resource(url).queryParams(params).accept(MediaType.APPLICATION_XML_TYPE);
            default:
                return resource(url).queryParams(params).accept(MediaType.APPLICATION_JSON_TYPE);
        }
    }

    private WebResource.Builder request() {
        return request(new MultivaluedMapImpl());
    }

    /**
     * Convert a map of parameters into something we can send to a POST or PUT request.
     *
     * @param params the map of key/value pairs to send to the server
     *
     * @return the Form to submit through Jersey
     */
    private Form asForm(Map<String, Object> params) {
        Form form = new Form();
        if (credentials != null) {
            form.add("user_credentials", credentials);
        }

        if (params != null) {
            for (String parameter : params.keySet()) {
                Object values = params.get(parameter);
                if (values instanceof Collection) {
                    parameter = parameter + "[]";
                    for (Object value : (Collection) values) {
                        form.add(parameter, value);
                    }
                } else {
                    form.add(parameter, values);
                }
            }
        }

        return form;
    }

    /**
     * Convert a map of parameters into something we can include in a query string.
     *
     * @param params the parameters to send to the Terra server
     *
     * @return a map of parameters compatible with the Jersey client
     */
    private MultivaluedMap<String, String> asQuery(Map<String, Object> params) {
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();

        if (credentials != null) {
            map.add("user_credentials", credentials);
        }

        if (params != null) {
            for (String parameter : params.keySet()) {
                Object values = params.get(parameter);
                if (values instanceof Collection) {
                    parameter = parameter + "[]";
                    for (Object value : (Collection) values) {
                        map.add(parameter, value.toString());
                    }
                } else {
                    map.add(parameter, values.toString());
                }
            }
        }

        return map;
    }

    /**
     * Parse each segment for URI encoding, and combine them into a path with the server
     * prepended.
     *
     * @param segments pieces of the URL
     *
     * @return the URL combined as a full path to the RESTful resource
     *
     * @throws NullPointerException if any of the segments is null
     */
    private String path(String... segments) {
        return Joiner.on("/").join(
                Collections2.transform(Arrays.asList(segments), new Function<String, String>() {
                    public String apply(String s) {
                        try {
                            return URIUtil.encodePath(s, "UTF-8");
                        } catch (URIException e) {
                            return null;
                        }
                    }
                }));
    }

    /**
     * Create a map out of a list of values.  Null values for keys will be
     * ignored, i.e. they will not be sent to the server.  To clear a value,
     * send an empty string (this is a Singularity standard).
     *
     * @param keysAndValues the keys and values, alternating; must be an even number
     *
     * @return a Map composed of the keys and values
     */
    public static Map<String, Object> createMap(Object... keysAndValues) {
        Map<String, Object> params = new HashMap<String, Object>();

        String key = null;

        for (Object keyOrValue : keysAndValues) {
            if (key == null) {
                key = keyOrValue.toString();
            } else {
                if (keyOrValue != null) params.put(key, keyOrValue);
                key = null;
            }
        }

        return params;
    }

    private synchronized static ObjectMapper createMapper() {
        return new ObjectMapper();
    }
}
