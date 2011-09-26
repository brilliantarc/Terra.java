package brilliantarc.terra.node;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * An operating company's business sales heading.  Businesses are sold into
 * a heading, and those headings are then mapped to taxonomic categories
 * for additional information (properties and options).  Headings tend to
 * be broad strokes, sales or market driven, whereas categories follow a
 * stricter physical, ontological definition of information.
 *
 * Note that Headings support both a PID and a Slug value; they are identical.
 * However, PID is the preferred notation for headings, so its use is encouraged
 * instead of slugs.  Slugs are only indicated to support the Meme interface.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Heading implements Meme {

    private String name;
    private String pid;
    private String external;
    private String language;
    private String opco;
    private String version;

    private List<Category> categories;

    /**
     * @return  the localized name of the heading; not necessarily unique
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  the operating company identifier for the heading; must be
     *          unique
     */
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    /**
     * We implement this to satisfy the Meme interface, but using get/setPid
     * is preferred.
     *
     * @return the PID for this heading
     */
    public String getSlug() {
        return getPid();
    }

    /**
     * We implement this to satisfy the Meme interface, but using get/setPid
     * is preferred.
     *
     * @param slug the PID for this heading
     */
    public void setSlug(String slug) {
        setPid(slug);
    }

    /**
     * @return  a third-party identifier for the heading; may be null, and is
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
     *          heading is named
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return  the three or four letter operating company code that owns this
     *          heading
     */
    public String getOpco() {
        return opco;
    }

    public void setOpco(String opco) {
        this.opco = opco;
    }

    /**
     * @return  an internal tracking code to ensure the version of this heading
     *          on the client is identical to the one on the server before
     *          updating; ensures that updates to the heading won't overwrite
     *          changes made by another user/client
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Note that this information is never filled in by the Terra API, but you
     * are welcome to use this slot to hold information in your own client
     * applications.
     *
     * @return the categories mapped to this heading, presumably
     */
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Heading heading = (Heading) o;

        if (!opco.equals(heading.opco)) return false;
        if (!pid.equals(heading.pid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pid.hashCode();
        result = 31 * result + opco.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Heading{" +
                "name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", external='" + external + '\'' +
                ", language='" + language + '\'' +
                ", opco='" + opco + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * Used internally to convert a JSON object into a Heading.
     *
     * @param node the JSON object received from the server
     *
     * @return the Heading object
     */
    public static Heading fromJson(JsonNode node) {
        Heading heading = new Heading();
        heading.setName(node.path("name").getTextValue());
        heading.setPid(node.path("pid").getTextValue());
        heading.setExternal(node.path("external").getTextValue());
        heading.setLanguage(node.path("language").getTextValue());
        heading.setOpco(node.path("opco").getTextValue());
        heading.setVersion(node.path("version").getTextValue());
        return heading;
    }
    
}

