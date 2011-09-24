package brilliantarc.terra.node;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Defines a visual grouping of headings.  These superheadings were 
 * designed to limit the number of headings coming back from the server,
 * in order to make them easier to work with, like headings grouped
 * together in rough verticals.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Superheading implements Meme {
    
    private String name;
    private String slug;
    private String external;
    private String language;
    private String opco;
    private String version;

    /**
     * @return  the localized name of the superheading; not necessarily unique
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  an SEO-compliant (preferably) identifier for the superheading,
     *          unique in the context of the operating company
     */
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return  a third-party identifier for the superheading; may be null, and is
     *          not required to be unique
     */
    public String getExternal() {
        return external;
    }

    public void setExternal(String external) {
        this.external = external;
    }

    /**
     * @return  a two or three letter ISO code for the language in which the
     *          superheading is named
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return  the three or four letter operating company code that owns this
     *          superheading
     */
    public String getOpco() {
        return opco;
    }

    public void setOpco(String opco) {
        this.opco = opco;
    }

    /**
     * @return  an internal tracking code to ensure the version of this superheading
     *          on the client is identical to the one on the server before
     *          updating; ensures that updates to the superheading won't overwrite
     *          changes made by another user/client
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Superheading that = (Superheading) o;

        if (!opco.equals(that.opco)) return false;
        if (!slug.equals(that.slug)) return false;

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
        return "Superheading{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", external='" + external + '\'' +
                ", language='" + language + '\'' +
                ", opco='" + opco + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * Used internally to convert a JSON object into a Superheading.
     *
     * @param node the JSON object received from the server
     *
     * @return the Superheading object
     */
    public static Superheading fromJson(JsonNode node) {
        Superheading superheading = new Superheading();
        superheading.setName(node.path("name").getTextValue());
        superheading.setSlug(node.path("slug").getTextValue());
        superheading.setExternal(node.path("external").getTextValue());
        superheading.setLanguage(node.path("language").getTextValue());
        superheading.setOpco(node.path("opco").getTextValue());
        superheading.setVersion(node.path("version").getTextValue());
        return superheading;
    }
    
}

    