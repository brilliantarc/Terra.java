package brilliantarc.terra;

import brilliantarc.terra.node.Category;
import org.junit.Test;

import static brilliantarc.terra.DefaultSettings.terra;
import static org.junit.Assert.*;

public class CategoryTests {
    
    @Test
    public void getCategory() {
        Category category = terra.categories().create("DGS", "A Sample Get Category", "sample-get-category");

        Category check = terra.categories().category("DGS", "sample-get-category");
        assertNotNull(category);
        assertEquals("sample-get-category", check.getSlug());
        assertEquals("DGS", check.getOpco());
        assertEquals("A Sample Get Category", check.getName());

        terra.categories().delete(category);
    }

    @Test
    public void createAndDeleteCategory() {
        Category category = terra.categories().create("DGS", "Sample Name", "sample-slug", "en");
        assertNotNull(category);
        assertEquals("DGS", category.getOpco());
        assertEquals("Sample Name", category.getName());
        assertEquals("sample-slug", category.getSlug());
        assertEquals("en", category.getLanguage());

        Category check = terra.categories().category("DGS", "sample-slug");
        assertNotNull(check);
        assertEquals(category.getOpco(), check.getOpco());
        assertEquals(category.getName(), check.getName());
        assertEquals(category.getSlug(), check.getSlug());
        assertEquals(category.getLanguage(), check.getLanguage());

        terra.categories().delete(category);
        try {
            terra.categories().category("DGS", "sample-slug");
            fail("The sample category identified by sample-slug was not deleted.");
        } catch (SingularityException e) {
            // This is a good thing!
        }
    }

    @Test
    public void updateCategory() {
        Category category = terra.categories().create("DGS", "Sample Name", "sample-slug", "en");

        category.setName("Another Name");
        category.setLanguage("nl");
        category.setExternal("An External");

        Category updated = terra.categories().update(category);
        assertEquals("DGS", updated.getOpco());
        assertEquals("sample-slug", updated.getSlug());
        assertEquals("Another Name", updated.getName());
        assertEquals("nl", updated.getLanguage());
        assertEquals("An External", updated.getExternal());
        assertNotNull(updated.getVersion());

        Category check = terra.categories().category("DGS", "sample-slug");
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
    
}
