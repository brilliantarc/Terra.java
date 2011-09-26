package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.SingularityException;
import brilliantarc.terra.node.*;
import brilliantarc.terra.server.Request;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Service requests related to categories.  Don't instantiate this directly; use
 * Client.Service.categories() instead.
 */
public class Categories extends Base {

    public Categories(Client.Services services) {
        super(services);
    }

    /**
     * Get all the categories defined for the operating company.  This may return
     * categories that are not in use, i.e. not in any taxonomies, but are still
     * defined in the system.
     *
     * @param opco the three or four letter code for the operating company
     *
     * @return the full, flat list of categories for the operating company
     *
     * @throws SingularityException if the operating company doesn't exist
     */
    public List<Category> all(String opco) {
        return get("categories").send("opco", opco).as(new TypeReference<List<Category>>() {});
    }

    /**
     * Get the top-level categories for the given taxonomy.  Note that this
     * does not return all the categories for a taxonomy.
     *
     * @param taxonomy The taxonomy of categories
     *
     * @return A list of Category objects
     *
     * @throws SingularityException The given taxonomy does not exist
     */
    public List<Category> children(Taxonomy taxonomy) {
        return get("taxonomy/categories").send("opco", taxonomy.getOpco(),
                "slug", taxonomy.getSlug()).as(new TypeReference<List<Category>>() {});
    }

    /**
     * Look for details about a specific category.  Note that this call
     * simply returns the basic information, such as the name and language
     * of the category.  It does not return child categories or mappings.
     *
     * It is useful for confirming the existence of a category.
     *
     * @param opco The three or four letter code for the operating company, e.g. "PKT"
     * @param slug The slug identifier for the category, unique to the operating company
     *
     * @return A category
     *
     * @throws SingularityException Either the operating company or category does not exist
     */
    public Category category(String opco, String slug) {
        return get("category").send("opco", opco, "slug", slug).as(Category.class);
    }

    /**
     * Create a new category for the given operating company.  The category
     * name need not be unique, but its (optional) slug must be.
     *
     * If the slug is not provided (either null or an empty string), it will
     * be created by transforming the name of the category into an SEO-ready
     * value.  For example, the name "Mexican Restaurants" would be transformed
     * into "mexican-restaurants".
     *
     * If language is not supplied it will default to the language of the
     * operating company.
     *
     * If there is a problem creating the category, a SingularityException
     * is thrown.  The following conditions may occur:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the slug already exists, returns a status of Conflict.
     * * If the operating company or parent does not exist, returns a status of Not Found.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The name of the category
     * @param slug     A unique identifier for the category, or null to have one generated
     * @param external A third-party identifier for the category
     * @param language The language of the category, or null to use the opco's language
     * @param parent   May be either a category or a taxonomy; if a taxonomy, becomes a top-level category in that taxonomy
     *
     * @return The newly created Category object
     *
     * @throws SingularityException see above
     */
    public Category create(String opco, String name, String slug, String external, String language, Meme parent) {
        Map<String, Object> params = Request.createMap("opco", opco, "name", name, "slug", slug,
                "external", external, "lang", language);

        if (parent instanceof Taxonomy) {
            params.put("taxonomy", parent.getSlug());
        } else if (parent instanceof Category) {
            params.put("category", parent.getSlug());
        }

        return post("category").send(params).as(Category.class);
    }

    /**
     * Create a new category.  See the primary create() method for details.
     * Defaults to no parent meme.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The name of the category
     * @param slug     A unique identifier for the category, or null to have one generated
     * @param external A third-party identifier for the category
     * @param language The language of the category, or null to use the opco's language
     *
     * @return The newly created Category object
     *
     * @throws SingularityException see the full create() method
     */
    public Category create(String opco, String name, String slug, String external, String language) {
        return create(opco, name, slug, external, language, null);
    }

    /**
     * Create a new category.  See the primary create() method for details.
     * Defaults to no parent meme and no external identifier.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The name of the category
     * @param slug     A unique identifier for the category, or null to have one generated
     * @param language The language of the category, or null to use the opco's language
     *
     * @return The newly created Category object
     *
     * @throws SingularityException see the full create() method
     */
    public Category create(String opco, String name, String slug, String language) {
        return create(opco, name, slug, null, language, null);
    }

    /**
     * Create a new category.  See the primary create() method for details.
     * Defaults to no parent meme and no external identifier.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The name of the category
     * @param slug     A unique identifier for the category, or null to have one generated
     *
     * @return The newly created Category object
     *
     * @throws SingularityException see the full create() method
     */
    public Category create(String opco, String name, String slug) {
        return create(opco, name, slug, null, null, null);
    }

    /**
     * Create a new category.  See the primary create() method for details.
     * Defaults to no parent meme, the language of the operating company, and
     * an SEO-compliant slug based on the name of the category.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The name of the category
     *
     * @return The newly created Category object
     *
     * @throws SingularityException see the full create() method
     */
    public Category create(String opco, String name) {
        return create(opco, name, null, null, null, null);
    }

    /**
     * Update's the name and language of the given category with the server.
     * Will not change the slug or operating company however.  If you modify
     * either of those, mostly likely you will receive a Not Found status in
     * the ServerException, indicating the category object could not be found
     * on the Terra server.
     *
     * Note that this method will return a new Category object.  The original
     * object will not be modified.
     *
     * If the server is unable to update the category, it will return one
     * of the following problems:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the existing category could not be found on the server, returns a status of Not Found
     * * If the local category is older than the version on the server, a Precondition Failed status is returned
     *
     * @param category An existing category with its name or language modified
     *
     * @return A new Category object with the updated information, as confirmation
     *
     * @throws SingularityException see above
     */
    public Category update(Category category) {
        return put("category").send("opco", category.getOpco(),
                "name", category.getName(), "slug", category.getSlug(),
                "external", category.getExternal(), "lang", category.getLanguage(),
                "v", category.getVersion()).as(Category.class);
    }

    /**
     * Completely deletes the given category from the Terra server.  If
     * there are any categories associated with the category, they are
     * orphaned, as are any properties and options associated with the
     * category.
     *
     * This method returns nothing.  If the delete is unsuccessful, an
     * exception will be raised.  Otherwise it completed successfully.
     *
     * @param category The category to delete; only Opco and Slug are used
     *
     * @throws SingularityException Raises a Not Found exception if the category does not exist, or
     *                              a Precondition Failed if the category on the server is newer than
     *                              the one submitted.
     */
    public void delete(Category category) {
        delete("category").send("opco", category.getOpco(),
                "slug", category.getSlug(), "v", category.getVersion());
    }

    /**
     * Get the list of synonyms associated with this category.
     *
     * @param category The category
     *
     * @return A list of synonyms
     *
     * @throws SingularityException The category does not exist
     */
    public List<Synonym> synonyms(Category category) {
        return get("category/synonyms").send("opco", category.getOpco(),
                "slug", category.getSlug()).as(new TypeReference<List<Synonym>>() {});
    }

    /**
     * Create a new synonym and associate it with this category.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the category.
     *
     * @param category The category to associate with the synonym
     * @param name     The human-readable name of the synonym
     * @param slug     An SEO-compliant slug for the synonym; generated if not provided
     * @param language The language of the name; defaults to the opco's language
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The category does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Category category, String name, String slug, String language) {
        return post("category/synonym").send("opco", category.getOpco(), "name", name, "slug", slug,
                "language", language, "category", category.getSlug()).as(Synonym.class);
    }

    /**
     * Create a new synonym and associate it with this category.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the category.
     *
     * Defaults to the language of the operating company.
     *
     * @param category The category to associate with the synonym
     * @param name     The human-readable name of the synonym
     * @param slug     An SEO-compliant slug for the synonym; generated if not provided
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The category does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Category category, String name, String slug) {
        return createSynonym(category, name, slug, null);
    }

    /**
     * Create a new synonym and associate it with this category.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the category.
     *
     * Defaults to the language of the operating company, and an SEO-compliant
     * slug based on the synonym's name.
     *
     * @param category The category to associate with the synonym
     * @param name     The human-readable name of the synonym
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The category does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Category category, String name) {
        return createSynonym(category, name, null, null);
    }

    /**
     * Associate an existing synonym with a category.  This is a rare call
     * to make.  Typically you want createSynonym or the addSynonym method
     * that takes a synonym name (String) instead of a Synonym object.
     *
     * Both the synonym and category must belong to the same operating
     * company.
     *
     * @param category The category with which to associate the synonym
     * @param synonym  The synonym for the category
     *
     * @throws SingularityException Either the category or the synonym doesn't exist
     */
    public void addSynonym(Category category, Synonym synonym) {
        put("category/synonym").send("opco", category.getOpco(), "category", category.getSlug(),
                "synonym", synonym.getSlug());
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a category.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the category.
     *
     * @param category The category to associate with this synonym
     * @param synonym  The new or existing name of a synonym
     * @param language The language of the synonym; defaults to the opco's language
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The category does not exist
     */
    public Synonym addSynonym(Category category, String synonym, String language) {
        String slug = services.slugify(synonym);
        try {
            // Relate an existing synonym to the category?
            Synonym existing = services.synonyms().synonym(category.getOpco(), slug);
            addSynonym(category, existing);
            return existing;
        } catch (SingularityException e) {
            // Create a new synonym with the given name
            if (e.getStatus() == 404) {
                return createSynonym(category, synonym, slug, language);
            } else {
                throw e;
            }
        }
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a category.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the category.
     *
     * Defaults to the language of the operating company.
     *
     * @param category The category to associate with this synonym
     * @param synonym  The new or existing name of a synonym
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The category does not exist
     */
    public Synonym addSynonym(Category category, String synonym) {
        return addSynonym(category, synonym, null);
    }

    /**
     * Remove the synonym with from the category.  Note that this doesn't
     * delete the synonym, simply removes its association from this
     * category.
     *
     * @param category The category with which to disassociate the synonym
     * @param synonym  The synonym for the category
     *
     * @throws SingularityException Either the category or the synonym doesn't exist
     */
    public void removeSynonym(Category category, Synonym synonym) {
        delete("category/synonym").send("opco", category.getOpco(),
                "category", category.getSlug(), "slug", synonym.getSlug());
    }

    /**
     * Get the direct child categories of the given category.
     *
     * @param category The parent category
     *
     * @return A list of Category objects, or an empty list if there are none
     *
     * @throws SingularityException The given category
     */
    public List<Category> children(Category category) {
        return get("category/children").send("opco", category.getOpco(),
                "slug", category.getSlug()).as(new TypeReference<List<Category>>() {});
    }

    /**
     * Get the parent categories or taxonomies for the given category.
     *
     * @param category The child category
     *
     * @return A mixed list of Category and Taxonomy objects
     */
    public List<Meme> parents(Category category) {
        return get("category/parents").send("opco", category.getOpco(), "slug", category.getSlug()).asMemes();
    }

    /**
     * Make the child category a child of the parent category.
     *
     * Please be aware that this will not automatically update any local
     * lists or caches that you may have; the complexity of updating that
     * information is left up to the client software.
     *
     *
     * This method will not return any information upon success.  If it
     * fails for any reason, a Terra.ServerException is thrown.
     *
     * @param parent The parent category
     * @param child  The child category
     *
     * @throws SingularityException Either the parent or child doesn't exist, or they cannot be related
     */
    public void addChild(Category parent, Category child) {
        put("category/children").send("opco", parent.getOpco(), "parent", parent.getSlug(), "child", child.getSlug());
    }

    /**
     * Remove the child category from the parent category.
     *
     * Please be aware that this will not automatically update any local
     * lists or caches that you may have; the complexity of updating that
     * information is left up to the client software.
     *
     *
     * This method will not return any information upon success.  If it
     * fails for any reason, a Terra.ServerException is thrown.
     *
     * @param parent The parent category
     * @param child  The child category
     *
     * @throws SingularityException Either the parent or child doesn't exist, or their relation cannot be broken
     */
    public void removeChild(Category parent, Category child) {
        delete("category/children").send("opco", parent.getOpco(), "parent", parent.getSlug(), "child", child.getSlug());
    }

    /**
     * Look up the properties associated with the given category.  The list
     * returned does not include options, and there may be properties in
     * the list that have no options associated with them anyway.
     *
     * @param category The category with which the properties are associated
     *
     * @return A list of Property objects
     *
     * @throws SingularityException The category does not exist
     */
    public List<Property> properties(Category category) {
        return get("category/properties").send("opco", category.getOpco(),
                "slug", category.getSlug()).as(new TypeReference<List<Property>>() {});
    }

    /**
     * Associate a property with a category.  This let's both users and
     * the software know what properties should be included in queries
     * such as inheritance.  It also serves as a visual tool for users,
     * so they know the list of possible properties from which to select
     * when adding options.
     *
     * @param category The category with which to associate the property
     * @param property The property to associate
     *
     * @throws SingularityException Either the property or the category doesn't exist
     */
    public void addProperty(Category category, Property property) {
        put("category/property").send("opco", category.getOpco(), "category", category.getSlug(),
                "property", property.getSlug());
    }

    /**
     * Remove a property from a category.  Note that this does not remove
     * any of the relations between options and this node.  It does,
     * however, filter the options from any requests that use the properties
     * associated with the node as a filter, such as inheritance.
     *
     * @param category A category from which to remove the property
     * @param property The property to remove
     *
     * @throws SingularityException Either the property or the node doesn't exist
     */
    public void removeProperty(Category category, Property property) {
        delete("category/property").send("opco", category.getOpco(), "category", category.getSlug(),
                "property", property.getSlug());
    }

    /**
     * Find all the options associated with this category.
     *
     * This operation can be a bit slow, as the system has to filter and
     * collate options and properties.  It is typically faster to request
     * a list of properties, then request the options for a selected
     * property (this is how the Terra UI does things).  Of course by
     * "slow", we mean takes around 300ms.
     *
     * @param category The category of options to retrieve
     *
     * @return A list of Property objects with their Options list filled
     */
    public List<Property> options(Category category) {
        return get("category/options").send("opco", category.getOpco(),
                "slug", category.getSlug()).as(new TypeReference<List<Property>>() {});
    }

    /**
     * Find the options associated with this category by the given property.
     *
     * We return the same value as the basic options() method, so that it's
     * consistent, even though only a single Property is ever returned.
     *
     * @param category  The category of options to retrieve
     * @param property  The specific property to restrict the list by
     *
     * @return A list with the matching Options
     */
    public List<Option> options(Category category, Property property) {
        return get("category/options").send("opco", category.getOpco(), "slug", category.getSlug(),
                "property", property.getSlug()).as(new TypeReference<List<Option>>() {});
    }

    /**
     * Add an option to a category.  The category, option, and property
     * must all exist in the same operating company.
     *
     * @param category The category to associate the option
     * @param property The property or "verb" used in the relation
     * @param option   The option being related
     *
     * @throws SingularityException Either the option, property or the category doesn't exist
     */
    public void addOption(Category category, Property property, Option option) {
        put("category/option").send("opco", category.getOpco(), "category", category.getSlug(),
                "property", property.getSlug(), "option", option.getSlug());
    }

    /**
     * Remove an option from a category.  The category, option, and property
     * must all exist in the same operating company.
     *
     * @param category The category from which to remove the option
     * @param property The property or "verb" used in the relation
     * @param option   The option being removed
     *
     * @throws SingularityException Either the option, property or the category doesn't exist
     */
    public void removeOption(Category category, Property property, Option option) {
        delete("category/option").send("opco", category.getOpco(), "category", category.getSlug(),
                "property", property.getSlug(), "option", option.getSlug());
    }

    /**
     * Look for the categories that the given category has been mapped
     * to.  In otherwords, what category-to-category mappings exists for
     * which this category is the subject of the relation?
     *
     * @param category The subject category of the relations
     *
     * @return A list of object categories
     *
     * @throws SingularityException The subject category does not exist
     */
    public List<Category> mappedTo(Category category) {
        return get("category/mappings").send("opco", category.getOpco(), "slug", category.getSlug(),
                "dir", "from").as(new TypeReference<List<Category>>() {});
    }

    /**
     * Look for the categories mapped to the given category.  In otherwords,
     * what category-to-category mappings exists for which this category is
     * the object of the relation?
     *
     * @param category The object category of the relations
     *
     * @return A list of subject categories
     *
     * @throws SingularityException The object category does not exist
     */
    public List<Category> mappedFrom(Category category) {
        return get("category/mappings").send("opco", category.getOpco(), "slug", category.getSlug(),
                "dir", "to").as(new TypeReference<List<Category>>() {});
    }

    /**
     * Find the headings mapped to this category.  Like GetMappedFrom,
     * what heading-to-category mappings exist for which this category
     * is the object of the relation?
     *
     * @param category The object category
     *
     * @return The headings mapped to this category
     *
     * @throws SingularityException The object category does not exist
     */
    public List<Heading> mappedHeadings(Category category) {
        return get("category/headings").send("opco", category.getOpco(), "slug", category.getSlug(),
                "dir", "from").as(new TypeReference<List<Heading>>() {});
    }

    /**
     * Look for the categories to which the given heading has been mapped.
     * In otherwords, what heading-to-category mappings exists for which
     * this heading is the subject of the relation?
     *
     * @param heading The subject heading of the relations
     *
     * @return A list of object categories
     *
     * @throws SingularityException The subject heading does not exist
     */
    public List<Category> mappedTo(Heading heading) {
        return services.headings().mappedTo(heading);
    }

    /**
     * Map two categories together.  Direction matters:  categories in
     * the "from" position will inherit properties and options from the
     * "to" category.
     *
     * Note: a future release of the Terra API will support mapping custom
     * relation types between categories, in support of new products and
     * services.  For now, the relation type is "mapped-to".
     *
     * @param from The subject category, to inherit from the object category
     * @param to   The object category
     *
     * @throws SingularityException Either of the categories does not exist or could not be mapped
     */
    public void mapCategory(Category from, Category to) {
        put("category/mapping").send("opco", from.getOpco(), "from", from.getSlug(), "to_opco", to.getOpco(),
                "to", to.getSlug());
    }

    /**
     * Map a heading to a category.  The heading will inherit the properties
     * and options from the category.  Note that you may map a heading to
     * many different categories, and inherit the sum total of properties
     * and options from those categories.
     *
     * For reference, the relation between a heading and a category is
     * defined as "heading-for".
     *
     * @param from The heading (subject)
     * @param to   The category (object)
     *
     * @throws SingularityException Either the category or heading does not exist or could not be mapped
     */
    public void mapHeading(Heading from, Category to) {
        put("heading/mapping").send("opco", from.getOpco(), "from", from.getPid(), "to_opco", to.getOpco(),
                "to", to.getSlug());
    }

    /**
     * Remove the relation between two categories.
     *
     * Note: a future release of the Terra API will support mapping custom
     * relation types between categories, in support of new products and
     * services.  For now, the relation type is "mapped-to".
     *
     * @param from The subject category
     * @param to   The object category
     *
     * @throws SingularityException Either of the categories does not exist or could not be unmapped
     */
    public void unmapCategory(Category from, Category to) {
        delete("category/mapping").send("opco", from.getOpco(), "from", from.getSlug(), "to_opco", to.getOpco(),
                "to", to.getSlug());
    }

    /**
     * Remove the relation between a heading and a category.
     *
     * @param from The heading (subject)
     * @param to   The category (object)
     *
     * @throws SingularityException Either the category or heading does not exist or could not be unmapped
     */
    public void unmapHeading(Heading from, Category to) {
        delete("heading/mapping").send("opco", from.getOpco(), "from", from.getPid(), "to_opco", to.getOpco(),
                "to", to.getSlug());
    }

    /**
     * Retrieve the full inheritance of properties and options for this
     * category, based on the parent-child relations in taxonomies and the
     * mappings to other categories, based on those categories' own
     * inheritance.
     *
     * @param category The category for which to retrieve inheritance
     *
     * @return A list of Property objects, with inherited Options included
     *
     * @throws SingularityException The category does not exist
     */
    public List<Property> inheritance(Category category) {
        return get("category/inheritance").send("opco", category.getOpco(),
                "slug", category.getSlug()).as(new TypeReference<List<Property>> () {});
    }
}
