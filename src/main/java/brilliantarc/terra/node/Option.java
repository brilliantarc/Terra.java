package brilliantarc.terra.node;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * An idea or keyword associated with one or more categories via a
 * property.  The combination of an option and its property provide
 * a meaningful piece of information about a category, which is then
 * inherited by any child categories or mapped headings.
 *
 * While an option's slug must be unique to an operating company, its
 * name need not be.  This can be used to keep an option distinct in
 * meaning that shares common spelling with other options (homynyms).
 * For example, two options may be named "Helmet", while one has the
 * slug "bicycle-helmet" and the other has the slug "motorcycle-helmet".
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Option implements Meme {

    private String name;
    private String slug;
    private String external;
    private String language;
    private String opco;
    private String version;

    /**
     * @return  the localized name of the option; not necessarily unique
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  an SEO-compliant (preferably) identifier for the option,
     *          unique in the context of the operating company
     */
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return  a third-party identifier for the option; may be null, and is
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
     *          option is named
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return  the three or four letter operating company code that owns this
     *          option
     */
    public String getOpco() {
        return opco;
    }

    public void setOpco(String opco) {
        this.opco = opco;
    }

    /**
     * @return  an internal tracking code to ensure the version of this option
     *          on the client is identical to the one on the server before
     *          updating; ensures that updates to the option won't overwrite
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

        Option option = (Option) o;

        if (!opco.equals(option.opco)) return false;
        if (!slug.equals(option.slug)) return false;

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
        return "Option{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", external='" + external + '\'' +
                ", language='" + language + '\'' +
                ", opco='" + opco + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * Used internally to convert a JSON object into a Option.
     *
     * @param node the JSON object received from the server
     *
     * @return the Option object
     */
    public static Option fromJson(JsonNode node) {
        Option option = new Option();
        option.setName(node.path("name").getTextValue());
        option.setSlug(node.path("slug").getTextValue());
        option.setExternal(node.path("external").getTextValue());
        option.setLanguage(node.path("language").getTextValue());
        option.setOpco(node.path("opco").getTextValue());
        option.setVersion(node.path("version").getTextValue());
        return option;
    }
    
}

