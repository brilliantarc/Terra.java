package brilliantarc.terra.node;

import brilliantarc.terra.SingularityException;
import brilliantarc.terra.server.Request;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

/**
 * Relates an option to a category in a meaningful way.  For example, a
 * property might be "Cuisine", and tie "Steak" to a restaurant, while
 * another property "Sells" may associate "Steak" with a butcher.  It
 * is the "verb" in any category-option relation.
 *
 * The Property class itself represents a meme that internally is not
 * really the relation between a category or taxonomy and an option.
 * Instead, it provides a human-friendly form of that relation's verb.
 * The relation itself is isolated from the Property meme deep inside
 * of Terra.  In most instances you can treat the Property as the
 * relation, but know there are also relations between objects in
 * Terra which are not defined as properties.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property implements Meme {

    private static Log log = LogFactory.getLog("brilliantarc.terra");

    private String name;
    private String slug;
    private String external;
    private String language;
    private String opco;
    private String version;

    private List<Option> options;

    /**
     * @return the localized name of the option; not necessarily unique
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return an SEO-compliant (preferably) identifier for the option,
     *         unique in the context of the operating company
     */
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return a third-party identifier for the option; may be null, and is
     *         not required to be unique
     */
    public String getExternal() {
        return external;
    }

    public void setExternal(String external) {
        this.external = external;
    }

    /**
     * @return a two or three letter ISO code for the language in which the
     *         option is named
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the three or four letter operating company code that owns this
     *         option
     */
    public String getOpco() {
        return opco;
    }

    public void setOpco(String opco) {
        this.opco = opco;
    }

    /**
     * @return an internal tracking code to ensure the version of this option
     *         on the client is identical to the one on the server before
     *         updating; ensures that updates to the option won't overwrite
     *         changes made by another user/client
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (!opco.equals(property.opco)) return false;
        if (!slug.equals(property.slug)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = slug.hashCode();
        result = 31 * result + opco.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", external='" + external + '\'' +
                ", language='" + language + '\'' +
                ", opco='" + opco + '\'' +
                ", version='" + version + '\'' +
                ", options=" + options +
                '}';
    }

    /**
     * Used internally to convert a JSON object into a Property.
     *
     * @param node the JSON object received from the server
     *
     * @return the Property object
     */
    @SuppressWarnings("unchecked")
    public static Property fromJson(JsonNode node) {
        Property property = new Property();
        property.setName(node.path("name").getTextValue());
        property.setSlug(node.path("slug").getTextValue());
        property.setExternal(node.path("external").getTextValue());
        property.setLanguage(node.path("language").getTextValue());
        property.setOpco(node.path("opco").getTextValue());
        property.setVersion(node.path("version").getTextValue());

        if (node.has("options")) {
            try {
                property.setOptions((List<Option>) Request.mapper.readValue(node.path("options"), new TypeReference<List<Option>>() {}));
            } catch (IOException e) {
                log.warn("Unable to parse options for " + property.getName() + ": " + e.getMessage(), e);
            }
        }

        return property;
    }

    /**
     * Parse an array of properties returned from the server, presumably with
     * options attached.  If options aren't attached, it's typically easier to
     * just use the TypeReference notation.
     *
     * @param node the JSON array returned by the server
     *
     * @return the parsed list of properties
     *
     * @throws SingularityException if the JSON could not be parsed
     */
    public static List<Property> fromJsonArray(JsonNode node) {
        if (node != null && node.isArray()) {
            return ImmutableList.copyOf(Iterators.transform(node.getElements(), new Function<JsonNode, Property>() {
                public Property apply(JsonNode node) {
                    return fromJson(node);
                }
            }));
        } else {
            throw new SingularityException(500, "Expected a JSON array but received " + node);
        }
    }

}

