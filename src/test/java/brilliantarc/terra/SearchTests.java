package brilliantarc.terra;

import brilliantarc.terra.node.Category;
import brilliantarc.terra.node.Meme;
import brilliantarc.terra.server.Status;
import brilliantarc.terra.service.Search;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Test;

import static brilliantarc.terra.DefaultSettings.terra;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SearchTests {

    @Test
    public void simpleQuery() {
        Category category = null;

        try {
            category = terra.categories().create("DGS", "Searchable Boogley Category",
                    "searchable-category-test", "en");
        } catch (SingularityException e) {
            if (e.getStatus() == Status.DUPLICATE.code) {
                category = (Category) e.getDuplicate();
            } else {
                throw e;
            }
        }

        Search.Results results = terra.search().query("en", "boogley", "DGS");
        assertNotNull(results);
        assertNotNull(results.getMemes());
        assertNotNull(results.getRefinements());
        assertTrue(results.getMemes().size() > 0);
        assertNotNull(results.getTotal() > 0);

        assertTrue(Iterables.any(results.getMemes(), new Predicate<Meme>() {
            public boolean apply(Meme meme) {
                return "searchable-category-test".equals(meme.getSlug());
            }
        }));

        if (category != null) {
            terra.categories().delete(category);
        }
    }

}
