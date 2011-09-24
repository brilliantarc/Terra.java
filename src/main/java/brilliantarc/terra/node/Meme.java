package brilliantarc.terra.node;

/**
 * Most of the Brilliant Arc nodes are stored as "memes" on our graph.  This
 * interface provides for their common functionality.
 */
public interface Meme extends Node {
    
    /**
     * @return  the localized name of the meme; not necessarily unique
     */
    String getName();
    void setName(String name);

    /**
     * @return  an SEO-compliant (preferably) identifier for the meme,
     *          unique in the context of the operating company
     */
    String getSlug();
    void setSlug(String slug);

    /**
     * @return  a third-party identifier for the meme; may be null, and is
     *          not required to be unique
     */
    String getExternal();
    void setExternal(String external);

    /**
     * @return  a two or three letter ISO code for the language in which the
     *          meme is named
     */
    String getLanguage();
    void setLanguage(String language);

    /**
     * @return  the three or four letter operating company code that owns this
     *          meme
     */
    String getOpco();
    void setOpco(String opco);

    /**
     * @return  an internal tracking code to ensure the version of this meme
     *          on the client is identical to the one on the server before
     *          updating; ensures that updates to the meme won't overwrite
     *          changes made by another user/client
     */
    String getVersion();
    void setVersion(String version);
    
}
