package brilliantarc.terra.node;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A collection of categories within an operating company.  Taxonomies may act
 * like categories in a number of scenarios.  For example, a taxonomy may have
 * properties and options associated with it.  Any options associated with a
 * taxonomy will be inherited by all of the categories in the taxonomy.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Taxonomy implements Meme {
    
    private String name;
    private String slug;
    private String external;
    private String language;
    private String opco;
    private String version;

    /**
     * @return  the localized name of the taxonomy; not necessarily unique
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  an SEO-compliant (preferably) identifier for the taxonomy,
     *          unique in the context of the operating company
     */
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return  a third-party identifier for the taxonomy; may be null, and is
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
     *          taxonomy is named
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return  the three or four letter operating company code that owns this
     *          taxonomy
     */
    public String getOpco() {
        return opco;
    }

    public void setOpco(String opco) {
        this.opco = opco;
    }

    /**
     * @return  an internal tracking code to ensure the version of this taxonomy
     *          on the client is identical to the one on the server before
     *          updating; ensures that updates to the taxonomy won't overwrite
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

        Taxonomy taxonomy = (Taxonomy) o;

        if (!opco.equals(taxonomy.opco)) return false;
        if (!slug.equals(taxonomy.slug)) return false;

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
        return "Taxonomy{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", external='" + external + '\'' +
                ", language='" + language + '\'' +
                ", opco='" + opco + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * Though the API is distinct, sometimes a category may act like a taxonomy
     * and vice-versa.  In those situations, you may trap the exception for
     * not found and try the taxonomy as a category.
     *
     * @return  a basic Category representing the Taxonomy (slug, opco), enough
     *          for an API call
     */
    public Category asCategory() {
        Category category = new Category();
        category.setSlug(slug);
        category.setOpco(opco);
        return category;
    }

    /**
     * Used internally to convert a JSON object into a Taxonomy.
     *
     * @param node the JSON object received from the server
     *
     * @return the Taxonomy object
     */
    public static Taxonomy fromJson(JsonNode node) {
        Taxonomy taxonomy = new Taxonomy();
        taxonomy.setName(node.path("name").getTextValue());
        taxonomy.setSlug(node.path("slug").getTextValue());
        taxonomy.setExternal(node.path("external").getTextValue());
        taxonomy.setLanguage(node.path("language").getTextValue());
        taxonomy.setOpco(node.path("opco").getTextValue());
        taxonomy.setVersion(node.path("version").getTextValue());
        return taxonomy;
    }
    
}
