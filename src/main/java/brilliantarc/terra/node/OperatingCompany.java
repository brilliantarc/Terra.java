package brilliantarc.terra.node;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A European Directories operating company, such as PKT or Fonecta.  Each
 * operating company is identified by a three or four letter code, such as
 * "PKT" or "FON", which is used in most requests to the server.
 *
 * This object is here merely as a convenience.  Typically you just pass
 * around the operating company's slug/code.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OperatingCompany implements Node {

    private String name;
    private String slug;
    private String language;

    /**
     * @return  the unique, full name of the operating company
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  a three or four letter unique identifier for the operating
     *          company, e.g. "PKT" or "EDSA"
     */
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return  the default language ISO code for the operating company
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperatingCompany that = (OperatingCompany) o;

        if (!slug.equals(that.slug)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return slug.hashCode();
    }

    @Override
    public String toString() {
        return "OperatingCompany{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
