package brilliantarc.terra;
import brilliantarc.terra.node.Taxonomy;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;
import static brilliantarc.terra.DefaultSettings.*;

public class TaxonomyTests {

    @Before
    public void clearTestPortfolio() {
        terra.reset();
    }

    @Test
    public void allTaxonomies() {
        terra.taxonomies().create(TEST_PORTFOLIO, "Sample Taxonomy");

        List<Taxonomy> taxonomies = terra.taxonomies().all(TEST_PORTFOLIO);
        assertNotNull(taxonomies);
        assertTrue(taxonomies.size() > 0);
        assertTrue(Iterables.all(taxonomies, new Predicate<Taxonomy>() {
            public boolean apply(Taxonomy taxonomy) {
                return TEST_PORTFOLIO.equals(taxonomy.getOpco());
            }
        }));
    }

    @Test
    public void getTaxonomy() {
        terra.taxonomies().create(TEST_PORTFOLIO, "Sample Taxonomy");

        Taxonomy taxonomy = terra.taxonomies().taxonomy(TEST_PORTFOLIO, "sample-taxonomy");
        assertNotNull(taxonomy);
        assertEquals("sample-taxonomy", taxonomy.getSlug());
        assertEquals(TEST_PORTFOLIO, taxonomy.getOpco());
        assertEquals("Sample Taxonomy", taxonomy.getName());
    }

    @Test
    public void createAndDeleteTaxonomy() {
        Taxonomy taxonomy = terra.taxonomies().create(TEST_PORTFOLIO, "Sample Name", "sample-slug", "en");
        assertNotNull(taxonomy);
        assertEquals(TEST_PORTFOLIO, taxonomy.getOpco());
        assertEquals("Sample Name", taxonomy.getName());
        assertEquals("sample-slug", taxonomy.getSlug());
        assertEquals("en", taxonomy.getLanguage());

        Taxonomy check = terra.taxonomies().taxonomy(TEST_PORTFOLIO, "sample-slug");
        assertNotNull(check);
        assertEquals(taxonomy.getOpco(), check.getOpco());
        assertEquals(taxonomy.getName(), check.getName());
        assertEquals(taxonomy.getSlug(), check.getSlug());
        assertEquals(taxonomy.getLanguage(), check.getLanguage());

        terra.taxonomies().delete(taxonomy);
        try {
            terra.taxonomies().taxonomy(TEST_PORTFOLIO, "sample-slug");
            fail("The sample taxonomy identified by sample-slug was not deleted.");
        } catch (SingularityException e) {
            // This is a good thing!
        }
    }

    @Test
    public void updateTaxonomy() {
        Taxonomy taxonomy = terra.taxonomies().create(TEST_PORTFOLIO, "Sample Name", "sample-slug", "en");

        taxonomy.setName("Another Name");
        taxonomy.setLanguage("nl");
        taxonomy.setExternal("An External");

        Taxonomy updated = terra.taxonomies().update(taxonomy);
        assertEquals(TEST_PORTFOLIO, updated.getOpco());
        assertEquals("sample-slug", updated.getSlug());
        assertEquals("Another Name", updated.getName());
        assertEquals("nl", updated.getLanguage());
        assertEquals("An External", updated.getExternal());
        assertNotNull(updated.getVersion());

        Taxonomy check = terra.taxonomies().taxonomy(TEST_PORTFOLIO, "sample-slug");
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
