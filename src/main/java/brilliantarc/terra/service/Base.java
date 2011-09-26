package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.server.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a few helper methods.
 */
public class Base {

    private static Log log = LogFactory.getLog("brilliantarc.terra");

    protected Client.Services services;

    public Base(Client.Services services) {
        this.services = services;
    }

    /**
     * Generate a GET request to the given path (not the full URL, but the server
     * path).
     *
     * The Request object is a builder, and may be appended to directly.
     *
     * @param resource the path to the resource, e.g. "taxonomy/children"
     *
     * @return the server request object, for additional information
     */
    public Request get(String resource) {
        return services.request().to(resource).get();
    }

    /**
     * Generate a POST request to the given path (not the full URL, but the server
     * path).
     *
     * The Request object is a builder, and may be appended to directly.
     *
     * @param resource the path to the resource, e.g. "taxonomy/children"
     *
     * @return the server request object, for additional information
     */
    public Request post(String resource) {
        return services.request().to(resource).post();
    }

    /**
     * Generate a PUT request to the given path (not the full URL, but the server
     * path).
     *
     * The Request object is a builder, and may be appended to directly.
     *
     * @param resource the path to the resource, e.g. "taxonomy/children"
     *
     * @return the server request object, for additional information
     */
    public Request put(String resource) {
        return services.request().to(resource).put();
    }

    /**
     * Generate a DELETE request to the given path (not the full URL, but the server
     * path).
     *
     * The Request object is a builder, and may be appended to directly.
     *
     * @param resource the path to the resource, e.g. "taxonomy/children"
     *
     * @return the server request object, for additional information
     */
    public Request delete(String resource) {
        return services.request().to(resource).delete();
    }

}
