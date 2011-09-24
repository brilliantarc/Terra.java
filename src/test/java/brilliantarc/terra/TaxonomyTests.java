package brilliantarc.terra;
import brilliantarc.terra.node.Taxonomy;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;
import static brilliantarc.terra.DefaultSettings.*;

public class TaxonomyTests {

    @Test
    public void allTaxonomies() {
        List<Taxonomy> taxonomies = terra.taxonomies().all("DGS");
        assertNotNull(taxonomies);
        assertTrue(taxonomies.size() > 0);
        assertTrue(Iterables.all(taxonomies, new Predicate<Taxonomy>() {
            public boolean apply(Taxonomy taxonomy) {
                return "DGS".equals(taxonomy.getOpco());
            }
        }));
    }

    @Test
    public void getTaxonomy() {
        List<Taxonomy> taxonomies = terra.taxonomies().all("DGS");
        String slug = taxonomies.get(0).getSlug();

        Taxonomy taxonomy = terra.taxonomies().taxonomy("DGS", slug);
        assertNotNull(taxonomy);
        assertEquals(slug, taxonomy.getSlug());
        assertEquals("DGS", taxonomy.getOpco());
        assertEquals(taxonomies.get(0).getName(), taxonomy.getName());
    }

    @Test
    public void createAndDeleteTaxonomy() {
        Taxonomy taxonomy = terra.taxonomies().create("DGS", "Sample Name", "sample-slug", "en");
        assertNotNull(taxonomy);
        assertEquals("DGS", taxonomy.getOpco());
        assertEquals("Sample Name", taxonomy.getName());
        assertEquals("sample-slug", taxonomy.getSlug());
        assertEquals("en", taxonomy.getLanguage());

        Taxonomy check = terra.taxonomies().taxonomy("DGS", "sample-slug");
        assertNotNull(check);
        assertEquals(taxonomy.getOpco(), check.getOpco());
        assertEquals(taxonomy.getName(), check.getName());
        assertEquals(taxonomy.getSlug(), check.getSlug());
        assertEquals(taxonomy.getLanguage(), check.getLanguage());

        terra.taxonomies().delete(taxonomy);
        try {
            terra.taxonomies().taxonomy("DGS", "sample-slug");
            fail("The sample taxonomy identified by sample-slug was not deleted.");
        } catch (SingularityException e) {
            // This is a good thing!
        }
    }

    @Test
    public void updateTaxonomy() {
        Taxonomy taxonomy = terra.taxonomies().create("DGS", "Sample Name", "sample-slug", "en");

        taxonomy.setName("Another Name");
        taxonomy.setLanguage("nl");
        taxonomy.setExternal("An External");

        Taxonomy updated = terra.taxonomies().update(taxonomy);
        assertEquals("DGS", updated.getOpco());
        assertEquals("sample-slug", updated.getSlug());
        assertEquals("Another Name", updated.getName());
        assertEquals("nl", updated.getLanguage());
        assertEquals("An External", updated.getExternal());
        assertNotNull(updated.getVersion());

        Taxonomy check = terra.taxonomies().taxonomy("DGS", "sample-slug");
        assertEquals("Another Name", check.getName());
        assertEquals("nl", check.getLanguage());
        assertEquals("An External", check.getExternal());

        // Check that versions are working...trying to delete the original (sans version) should fail
        try {
            terra.taxonomies().delete(taxonomy);
        } catch (SingularityException e) {

        }


        terra.taxonomies().delete(updated);
    }
}
