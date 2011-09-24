package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.node.*;
import org.codehaus.jackson.type.TypeReference;
import brilliantarc.terra.SingularityException;

import java.util.List;

/**
 * Some basic functionality in support of synonyms.  Similar to options,
 * most of the true functionality useful surrounding synonyms resides with
 * the other services, such as Terra.Service.Categories.  This service
 * is primarily for updating and mass-deleting synonyms.
 *
 * Service requests related to synonyms.  Don't instantiate this directly; use
 * Client.Service.synonyms() instead.
 */
public class Synonyms extends Base {

    public Synonyms(Client.Services services) {
        super(services);
    }

    /**
     * Look for the given synonym.
     *
     * It is useful for confirming the existence of a synonym.
     *
     * @param opco The three or four letter code for the operating company, e.g. "PKT"
     * @param slug The slug identifier for the synonym, unique to the operating company
     *
     * @return a Synonym
     *
     * @throws SingularityException Either the operating company or synonym does not exist
     */
    public Synonym synonym(String opco, String slug) {
        return services.request().to("synonym").send("opco", opco, "slug", slug).as(Synonym.class);
    }

    /**
     * Create a new synonym.
     *
     * This will create a "free" synonym in the operating company's
     * portfolio; the majority of the time this is not what you want.
     * Instead, see the individual category, taxonomy, etc. services for
     * methods to create synonyms and attach them to another meme in the
     * same call.
     *
     * If there's a problem creating a synonym, a SingularityException is
     * thrown:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the slug already exists, returns a status of Conflict.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The human-readable name for this synonym
     * @param slug     A unique slug for this synonym; synonymal, will be generated from the name if not provided
     * @param external An third-party external identifier for this synonym (synonymal)
     * @param language The two-letter ISO language for the synonym's name; defaults to the opco's language
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException see above
     * @see Categories#createSynonym
     * @see Taxonomies#createSynonym
     * @see Properties#createSynonym
     * @see Options#createSynonym
     * @see Headings#createSynonym
     * @see Superheadings#createSynonym
     */
    public Synonym create(String opco, String name, String slug, String external, String language) {
        return post("synonym").send("opco", opco, "name", name, "slug", slug, "external", external,
                "lang", language).as(Synonym.class);
    }

    /**
     * Create an option using the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The human-readable name for this option
     * @param slug A unique slug for this option; optional, will be generated from the name if not provided
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException see above
     */
    public Synonym create(String opco, String name, String slug) {
        return create(opco, name, slug, null, null);
    }

    /**
     * Create an option with a generated SEO-compliant slug, based on the name,
     * in the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The human-readable name for this option
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException see above
     */
    public Synonym create(String opco, String name) {
        return create(opco, name, null, null, null);
    }

    /**
     * Update the name, external identifier, or language for this synonym.
     * Neither the operating company nor the slug may be modified.
     *
     * If there's a problem updating the synonym, will raise a
     * SingularityException:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the existing synonym could not be found on the server, returns a status of Not Found
     * * If the local synonym is older than the version on the server, a Precondition Failed status is returned
     *
     * @param synonym An synonym with the updated information
     *
     * @return A new Synonym object, with the updates in place
     *
     * @throws SingularityException see above
     */
    public Synonym update(Synonym synonym) {
        return put("synonym").send("opco", synonym.getOpco(), "name", synonym.getName(), "slug", synonym.getSlug(),
                "external", synonym.getExternal(), "lang", synonym.getLanguage(), "v", synonym.getVersion()).as(Synonym.class);
    }

    /**
     * Completely delete an synonym from Terra.  This will remove the synonym
     * from every meme it is related to in the operating company.  In most
     * cases, you'll likely want something along the lines of
     * Categories.removeSynonym instead.
     *
     * @param synonym The synonym to delete
     *
     * @throws SingularityException The synonym does not exist
     * @see Categories#removeSynonym
     * @see Taxonomies#removeSynonym
     * @see Properties#removeSynonym
     * @see Options#removeSynonym
     * @see Headings#removeSynonym
     * @see Superheadings#removeSynonym
     */
    public void delete(Synonym synonym) {
        delete("synonym").send("opco", synonym.getOpco(), "slug", synonym.getSlug());
    }
}
