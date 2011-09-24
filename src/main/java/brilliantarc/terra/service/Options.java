package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.SingularityException;
import brilliantarc.terra.node.*;
import brilliantarc.terra.server.Request;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Modify and interact with options on a category or taxonomy.  Much of the
 * functionality for mapping options to categories and taxonomies resides
 * in those services, e.g. categories.addOption. Functionality here focuses on
 * creating, updating, and deleting options.
 *
 * Service requests related to options.  Don't instantiate this directly; use
 * Client.Service.options() instead.
 */
public class Options extends Base {

    public Options(Client.Services services) {
        super(services);
    }

    /**
     * Look up an option by its slug.  Useful for checking to see if an
     * option exists.
     *
     * @param opco The three or four letter code for the opco
     * @param slug The options's slug
     *
     * @return An Option object
     *
     * @throws SingularityException The operating company or option does not exist
     */
    public Option option(String opco, String slug) {
        return get("option").send("opco", opco, "slug", slug).as(Option.class);
    }

    /**
     * Create a new option.
     *
     * If the server is unable to create the option for some reason, a
     * SingularityException will be thrown, likely due to one of the
     * following:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the slug already exists, returns a status of Conflict.
     * * If the operating company, relatedTo, or relatedBy does not exist, returns a status of Not Found.
     *
     * @param opco      The three or four letter code for the operating company
     * @param name      The human-readable name for this option
     * @param slug      A unique slug for this option; optional, will be generated from the name if not provided
     * @param external  An third-party external identifier for this option (optional)
     * @param language  The two-letter ISO language for the option's name; defaults to the opco's language
     * @param relatedTo The category or taxonomy using this option (optional)
     * @param relatedBy The property by which this option is related to the relatedTo value
     *
     * @return The newly created Option
     *
     * @throws SingularityException see above
     */
    public Option create(String opco, String name, String slug, String external, String language,
                         Meme relatedTo, Property relatedBy) {
        Map<String, Object> params = Request.createMap("opco", opco, "name", name, "slug", slug,
                "external", external, "lang", language);

        if (relatedTo != null && relatedBy != null) {
            if (relatedTo instanceof Taxonomy) {
                params.put("taxonomy", relatedTo.getSlug());
            } else if (relatedTo instanceof Category) {
                params.put("category", relatedTo.getSlug());
            }
            params.put("property", relatedBy.getSlug());
        }

        return post("option").send(params).as(Option.class);
    }

    /**
     * Create a new option without relating it to anything.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The human-readable name for this option
     * @param slug     A unique slug for this option; optional, will be generated from the name if not provided
     * @param external An third-party external identifier for this option (optional)
     * @param language The two-letter ISO language for the option's name; defaults to the opco's language
     *
     * @return The newly created Option
     *
     * @throws SingularityException see above
     */
    public Option create(String opco, String name, String slug, String external, String language) {
        return create(opco, name, slug, external, language, null, null);
    }

    /**
     * Create a new option without a third-party external identifier and without
     * relating it to anything.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The human-readable name for this option
     * @param slug     A unique slug for this option; optional, will be generated from the name if not provided
     * @param language The two-letter ISO language for the option's name; defaults to the opco's language
     *
     * @return The newly created Option
     *
     * @throws SingularityException see above
     */
    public Option create(String opco, String name, String slug, String language) {
        return create(opco, name, slug, null, language, null, null);
    }

    /**
     * Create an option using the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The human-readable name for this option
     * @param slug A unique slug for this option; optional, will be generated from the name if not provided
     *
     * @return The newly created Option
     *
     * @throws SingularityException see above
     */
    public Option create(String opco, String name, String slug) {
        return create(opco, name, slug, null, null, null, null);
    }

    /**
     * Create an option with a generated SEO-compliant slug, based on the name,
     * in the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The human-readable name for this option
     *
     * @return The newly created Option
     *
     * @throws SingularityException see above
     */
    public Option create(String opco, String name) {
        return create(opco, name, null, null, null, null, null);
    }

    /**
     * Update the name, external identifier, or language for this option.
     * Neither the operating company nor the slug may be modified.
     *
     * If the option is unable to be updated, one of these SingularityExceptions
     * will be thrown:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the existing option could not be found on the server, returns a status of Not Found
     * * If the local option is older than the version on the server, a Precondition Failed status is returned
     *
     * @param option An option with the updated information
     *
     * @return A new Option object, with the updates in place
     *
     * @throws SingularityException see above
     */
    public Option update(Option option) {
        return put("option").send("opco", option.getOpco(), "name", option.getName(), "slug", option.getSlug(),
                "external", option.getExternal(), "lang", option.getLanguage(), "v", option.getVersion()).as(Option.class);
    }

    /**
     * Completely delete an option from Terra.  This will remove the option
     * from every category and taxonomy in the operating company.
     *
     * @param option The option to delete
     *
     * @throws SingularityException The option does not exist
     */
    public void delete(Option option) {
        delete("option").send("opco", option.getOpco(), "slug", option.getSlug());
    }

    /**
     * Get the list of synonyms associated with this option.
     *
     * @param option The option
     *
     * @return A list of synonyms
     *
     * @throws SingularityException The option does not exist
     */
    public List<Synonym> synonyms(Option option) {
        return get("option/synonyms").send("opco", option.getOpco(),
                "slug", option.getSlug()).as(new TypeReference<List<Synonym>>() {});
    }

    /**
     * Create a new synonym and associate it with this option.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the option.
     *
     * @param option   The option to associate with the synonym
     * @param name     The human-readable name of the synonym
     * @param slug     An SEO-compliant slug for the synonym; generated if not provided
     * @param language The language of the name; defaults to the opco's language
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The option does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Option option, String name, String slug, String language) {
        return post("option/synonym").send("opco", option.getOpco(), "name", name, "slug", slug,
                "language", language, "option", option.getSlug()).as(Synonym.class);
    }

    /**
     * Create a new synonym and associate it with this option.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the option.
     *
     * Defaults to the language of the operating company.
     *
     * @param option The option to associate with the synonym
     * @param name   The human-readable name of the synonym
     * @param slug   An SEO-compliant slug for the synonym; generated if not provided
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The option does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Option option, String name, String slug) {
        return createSynonym(option, name, slug, null);
    }

    /**
     * Create a new synonym and associate it with this option.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the option.
     *
     * Defaults to the language of the operating company, and an SEO-compliant
     * slug based on the synonym's name.
     *
     * @param option The option to associate with the synonym
     * @param name   The human-readable name of the synonym
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The option does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Option option, String name) {
        return createSynonym(option, name, null, null);
    }

    /**
     * Associate an existing synonym with a option.  This is a rare call
     * to make.  Typically you want createSynonym or the addSynonym method
     * that takes a synonym name (String) instead of a Synonym object.
     *
     * Both the synonym and option must belong to the same operating
     * company.
     *
     * @param option  The option with which to associate the synonym
     * @param synonym The synonym for the option
     *
     * @throws SingularityException Either the option or the synonym doesn't exist
     */
    public void addSynonym(Option option, Synonym synonym) {
        put("option/synonym").send("opco", option.getOpco(), "option", option.getSlug(),
                "synonym", synonym.getSlug());
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a option.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the option.
     *
     * @param option   The option to associate with this synonym
     * @param synonym  The new or existing name of a synonym
     * @param language The language of the synonym; defaults to the opco's language
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The option does not exist
     */
    public Synonym addSynonym(Option option, String synonym, String language) {
        String slug = services.slugify(synonym);
        try {
            // Relate an existing synonym to the option?
            Synonym existing = services.synonyms().synonym(option.getOpco(), slug);
            addSynonym(option, existing);
            return existing;
        } catch (SingularityException e) {
            // Create a new synonym with the given name
            if (e.getStatus() == 404) {
                return createSynonym(option, synonym, slug, language);
            } else {
                throw e;
            }
        }
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a option.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the option.
     *
     * Defaults to the language of the operating company.
     *
     * @param option  The option to associate with this synonym
     * @param synonym The new or existing name of a synonym
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The option does not exist
     */
    public Synonym addSynonym(Option option, String synonym) {
        return addSynonym(option, synonym, null);
    }

    /**
     * Remove the synonym with from the option.  Note that this doesn't
     * delete the synonym, simply removes its association from this
     * option.
     *
     * @param option  The option with which to disassociate the synonym
     * @param synonym The synonym for the option
     *
     * @throws SingularityException Either the option or the synonym doesn't exist
     */
    public void removeSynonym(Option option, Synonym synonym) {
        delete("option/synonym").send("opco", option.getOpco(), "option", option.getSlug(), "slug", synonym.getSlug());
    }

    /**
     * Get the "sub" options for this option.  Options can be related to other
     * options, as sort of "child" options.  This functionality isn't widely
     * used, and if you're considering using suboptions, you should first
     * consider using synonyms instead.
     *
     * @param option the parent option
     *
     * @return the options related to the given parent option
     *
     * @throws SingularityException the parent option doesn't exist
     */
    public List<Option> suboptions(Option option) {
        return get("option/sub").send("opco", option.getOpco(), "slug", option.getSlug()).as(new TypeReference<List<Option>>() {});
    }

    /**
     * Associate an option as a "suboption" of a parent option.  This
     * functionality isn't widely used, and if you're considering using
     * suboptions, you should first consider using synonyms instead.
     *
     * @param option    the parent option
     * @param suboption the option to relate to the parent
     *
     * @throws SingularityException either the parent or the suboption doesn't exist
     */
    public void addSuboption(Option option, Option suboption) {
        put("option/sub").send("opco", option.getOpco(), "option", option.getSlug(), "suboption", suboption.getSlug());
    }

    /**
     * Remove the association between an option and its "suboption".  This
     * functionality isn't widely used, and if you're considering using
     * suboptions, you should first consider using synonyms instead.
     *
     * @param option    the parent option
     * @param suboption the option to remove (does not delete the suboption)
     *
     * @throws SingularityException either the parent or the suboption doesn't exist
     */
    public void removeSuboption(Option option, Option suboption) {
        delete("option/sub").send("opco", option.getOpco(), "option", option.getSlug(), "suboption", suboption.getSlug());
    }
}
