package brilliantarc.terra;

import brilliantarc.terra.node.Category;
import brilliantarc.terra.node.Option;
import brilliantarc.terra.node.Property;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static brilliantarc.terra.DefaultSettings.*;
import static org.junit.Assert.*;

public class CategoryTests {

    @Before
    public void clearTestPortfolio() {
        terra.reset();
    }

    @Test
    public void getCategory() {
        Category category = terra.categories().create(TEST_PORTFOLIO, "A Sample Get Category", "sample-get-category");

        Category check = terra.categories().category(TEST_PORTFOLIO, "sample-get-category");
        assertNotNull(category);
        assertEquals("sample-get-category", check.getSlug());
        assertEquals(TEST_PORTFOLIO, check.getOpco());
        assertEquals("A Sample Get Category", check.getName());

        terra.categories().delete(category);
    }

    @Test
    public void createAndDeleteCategory() {
        Category category = terra.categories().create(TEST_PORTFOLIO, "Sample Name", "sample-slug", "en");
        assertNotNull(category);
        assertEquals(TEST_PORTFOLIO, category.getOpco());
        assertEquals("Sample Name", category.getName());
        assertEquals("sample-slug", category.getSlug());
        assertEquals("en", category.getLanguage());

        Category check = terra.categories().category(TEST_PORTFOLIO, "sample-slug");
        assertNotNull(check);
        assertEquals(category.getOpco(), check.getOpco());
        assertEquals(category.getName(), check.getName());
        assertEquals(category.getSlug(), check.getSlug());
        assertEquals(category.getLanguage(), check.getLanguage());

        terra.categories().delete(category);
        try {
            terra.categories().category(TEST_PORTFOLIO, "sample-slug");
            fail("The sample category identified by sample-slug was not deleted.");
        } catch (SingularityException e) {
            // This is a good thing!
        }
    }

    @Test
    public void updateCategory() {
        Category category = terra.categories().create(TEST_PORTFOLIO, "Sample Name", "sample-slug", "en");

        category.setName("Another Name");
        category.setLanguage("nl");
        category.setExternal("An External");

        Category updated = terra.categories().update(category);
        assertEquals(TEST_PORTFOLIO, updated.getOpco());
        assertEquals("sample-slug", updated.getSlug());
        assertEquals("Another Name", updated.getName());
        assertEquals("nl", updated.getLanguage());
        assertEquals("An External", updated.getExternal());
        assertNotNull(updated.getVersion());

        Category check = terra.categories().category(TEST_PORTFOLIO, "sample-slug");
        assertEquals("Another Name", check.getName());
        assertEquals("nl", check.getLanguage());
        assertEquals("An External", check.getExternal());

        // Check that versions are working...trying to delete the original (sans version) should fail
        try {
            terra.categories().delete(category);
        } catch (SingularityException e) {

        }

        terra.categories().delete(updated);
    }

    @Test
    public void inheritance() {
        // Create a parent category
        Category parent = terra.categories().create(TEST_PORTFOLIO, "Parent Category");

        // Create a child category
        Category child = terra.categories().create(TEST_PORTFOLIO, "Child Category", null, null, null, parent);

        // Attach a property and option to the parent
        Property cuisine = terra.properties().create(TEST_PORTFOLIO, "Cuisine");
        Option seafood = terra.options().create(TEST_PORTFOLIO, "Seafood");
        terra.categories().addOption(parent, cuisine, seafood);

        // Attach a property and option to the child
        Option hamburger = terra.options().create(TEST_PORTFOLIO, "Hamburger");
        terra.categories().addOption(child, cuisine, hamburger);

        // Get the child category's properties and options
        List<Property> properties = terra.categories().options(child);
        assertEquals(1, properties.size());
        assertNotNull(properties.get(0));
        assertNotNull(properties.get(0).getOptions());

        // Should contain just the directly attached property and option
        assertTrue(Iterables.any(properties.get(0).getOptions(), new Predicate<Option>() {
            public boolean apply(Option option) {
                return "hamburger".equals(option.getSlug());
            }
        }));
        assertFalse(Iterables.any(properties.get(0).getOptions(), new Predicate<Option>() {
            public boolean apply(Option option) {
                return "seafood".equals(option.getSlug());
            }
        }));

        // Get the child category's inheritance
        List<Property> inherited = terra.categories().inheritance(child);
        assertEquals(1, inherited.size());
        assertNotNull(inherited.get(0));
        assertNotNull(inherited.get(0).getOptions());

        //// Should contain both parent and child category
        assertTrue(Iterables.any(inherited.get(0).getOptions(), new Predicate<Option>() {
            public boolean apply(Option option) {
                return "hamburger".equals(option.getSlug());
            }
        }));
        assertTrue(Iterables.any(inherited.get(0).getOptions(), new Predicate<Option>() {
            public boolean apply(Option option) {
                return "seafood".equals(option.getSlug());
            }
        }));
    }

}
