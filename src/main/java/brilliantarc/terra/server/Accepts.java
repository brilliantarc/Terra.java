package brilliantarc.terra.server;

/**
 * Types of results we accept.  Primarily JSON, but there are a few calls that
 * may return XML, particularly custom traversal functions.
 */
public enum Accepts {
    JSON, XML, STRING
}
