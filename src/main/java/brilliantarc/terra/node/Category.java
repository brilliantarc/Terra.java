package brilliantarc.terra.node;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A taxonomic category designed around tight ontological principles.  A
 * category tends to have a stricter definition than an operating company
 * heading.  Categories are defined by the properties and options associated
 * with them, and any heading mapped to a category inherits that category's
 * properties and options, as well as the properties and options of the
 * category's parents.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category implements Meme {

    private String name;
    private String slug;
    private String external;
    private String language;
    private String opco;
    private String version;

    /**
     * @return the localized name of the category; not necessarily unique
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return an SEO-compliant (preferably) identifier for the category,
     *         unique in the context of the operating company
     */
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return a third-party identifier for the category; may be null, and is
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
     *         category is named
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the three or four letter operating company code that owns this
     *         category
     */
    public String getOpco() {
        return opco;
    }

    public void setOpco(String opco) {
        this.opco = opco;
    }

    /**
     * @return an internal tracking code to ensure the version of this category
     *         on the client is identical to the one on the server before
     *         updating; ensures that updates to the category won't overwrite
     *         changes made by another user/client
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

        Category category = (Category) o;

        if (!opco.equals(category.opco)) return false;
        if (!slug.equals(category.slug)) return false;

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
        return "Category{" +
                "external='" + external + '\'' +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", language='" + language + '\'' +
                ", opco='" + opco + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * Used internally to convert a JSON object into a Category.
     *
     * @param node the JSON object received from the server
     *
     * @return the Category object
     */
    public static Category fromJson(JsonNode node) {
        Category category = new Category();
        category.setName(node.path("name").getTextValue());
        category.setSlug(node.path("slug").getTextValue());
        category.setExternal(node.path("external").getTextValue());
        category.setLanguage(node.path("language").getTextValue());
        category.setOpco(node.path("opco").getTextValue());
        category.setVersion(node.path("version").getTextValue());
        return category;
    }
}

