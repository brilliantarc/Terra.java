package brilliantarc.terra.node;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * An secondary, alternate term for any piece of information in Terra, be
 * it a category, property, option, or taxonomy.  Even headings and 
 * superheadings may have synonyms.
 *
 * Synonyms may also be used as simple translations of a term, given that
 * the synonym's language may freely differ from that of the meme to which
 * it is related.  Ideally, it is better, for example, to map a category in
 * one language to its equivalent in another, but for simple uses a synonym
 * may suffice.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Synonym implements Meme {
    
    private String name;
    private String slug;
    private String external;
    private String language;
    private String opco;
    private String version;

    /**
     * @return  the localized name of the synonym; not necessarily unique
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  an SEO-compliant (preferably) identifier for the synonym,
     *          unique in the context of the operating company
     */
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return  a third-party identifier for the synonym; may be null, and is
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
     *          synonym is named
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return  the three or four letter operating company code that owns this
     *          synonym
     */
    public String getOpco() {
        return opco;
    }

    public void setOpco(String opco) {
        this.opco = opco;
    }

    /**
     * @return  an internal tracking code to ensure the version of this synonym
     *          on the client is identical to the one on the server before
     *          updating; ensures that updates to the synonym won't overwrite
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

        Synonym synonym = (Synonym) o;

        if (!opco.equals(synonym.opco)) return false;
        if (!slug.equals(synonym.slug)) return false;

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
        return "Synonym{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", external='" + external + '\'' +
                ", language='" + language + '\'' +
                ", opco='" + opco + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * Used internally to convert a JSON object into a Synonym.
     *
     * @param node the JSON object received from the server
     *
     * @return the Synonym object
     */
    public static Synonym fromJson(JsonNode node) {
        Synonym synonym = new Synonym();
        synonym.setName(node.path("name").getTextValue());
        synonym.setSlug(node.path("slug").getTextValue());
        synonym.setExternal(node.path("external").getTextValue());
        synonym.setLanguage(node.path("language").getTextValue());
        synonym.setOpco(node.path("opco").getTextValue());
        synonym.setVersion(node.path("version").getTextValue());
        return synonym;
    }
    
}
