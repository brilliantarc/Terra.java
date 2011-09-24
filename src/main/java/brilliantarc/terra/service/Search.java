package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.node.Meme;
import brilliantarc.terra.server.Request;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Service requests related to free-text searching of content in Terra.  Don't
 * instantiate this directly; use Client.Service.users() instead.
 */
public class Search {

    private Client.Services services;

    /**
     * Searching for information returns a Search.Results object, which includes
     * information about refinements and total memes, along with a set of
     * memes (categories, options, etc.).
     */
    public static class Results {

        private List<Refinement> refinements;
        private List<Meme> memes;
        private int total;

        protected Results(List<Refinement> refinements, List<Meme> memes, int total) {
            this.refinements = refinements;
            this.memes = memes;
            this.total = total;
        }

        /**
         * See the Search.Refinement class for a complete description of
         * refinements.
         *
         * @return a list of refineable definitions and the number of possible memes
         */
        public List<Refinement> getRefinements() {
            return refinements;
        }

        /**
         * A list of categories, properties, headings, etc. will be returned.
         *
         * @return the memes found matching the given search parameters
         */
        public List<Meme> getMemes() {
            return memes;
        }

        /**
         * This value is an estimate by the search engine as to how many memes
         * (categories, properties, headings, etc.) in Terra will match the
         * given search terms.
         *
         * @return the total number of possible memes
         */
        public int getTotal() {
            return total;
        }

        /**
         * @return  some reasonably decent memes printed out, for debugging purposes
         */
        @Override
        public String toString() {
            return "Results{" +
                    "refinements=" + refinements +
                    ", memes=" + memes +
                    ", total=" + total +
                    '}';
        }

        /**
         * The search memes will call this method to transform the response
         * from the Terra server into useful objects.
         *
         * @param node the JSON returned from Terra
         *
         * @return the parsed memes, in a usable format
         */
        public static Results fromJson(JsonNode node) {
            int total = node.path("total").getIntValue();

            List<Refinement> refinements = new ArrayList<Refinement>();
            if (node.has("facets")) {
                final ObjectNode object = (ObjectNode) node.path("facets");
                refinements = ImmutableList.copyOf(Iterators.transform(object.getFieldNames(), new Function<String, Refinement>() {
                    public Refinement apply(String field) {
                        return new Refinement(field, object.get(field).getIntValue());
                    }
                }));
            }

            List<Meme> results = Request.parseResults(node.path("hits"));

            return new Results(refinements, results, total);
        }
    }

    /**
     * A search refinement will indicate a type of meme, such as a "Category",
     * and the estimated number of memes for the search that are of that
     * type.  For example, you might get a refinement of "Option" with the
     * total of 203.  That means that your original query matches on approximately
     * 203 options in the entire set of memes searched.
     *
     * You can turn around and pass in that meme definition into the same query
     * to return only memes of that type.
     */
    public static class Refinement {

        private String definition;
        private int total;

        Refinement(String definition, int total) {
            this.definition = definition;
            this.total = total;
        }

        /**
         * @return  the meme definition name, to be included in future search
         *          requests
         */
        public String getDefinition() {
            return definition;
        }

        /**
         * The search engine will roughly guess at the number of memes defined
         * as this type that would be returned if you returned all possible
         * memes at once.
         *
         * @return  the estimated number of memes that fit this refinement
         *          definition
         */
        public int getTotal() {
            return total;
        }

        @Override
        public String toString() {
            return "Refinement{" +
                    "definition='" + definition + '\'' +
                    ", total=" + total +
                    '}';
        }
    }

    public Search(Client.Services services) {
        this.services = services;
    }

    /**
     * Perform a free-text search for categories, properties, options, etc.
     * in Terra.  Will return a SearchResult object which contains not only
     * the matches for the search, but information about refinements and
     * the total number of memes possible.
     *
     * Searches are localized to the various languages, in order to apply
     * language-specific stemming and other operations.  While you may
     * search across operating companies, you may not search across
     * languages.  Unfortunately this is a limitation of the search engine
     * used in Terra.
     *
     * @param language    The language in which to conduct the search
     * @param terms       A set of keywords or phrases to search for
     * @param definitions Limit the search to memes of this type, e.g. ["Categories", "Taxonomies"]
     * @param opco        Limit the search to a specific opco
     * @param from        Start from this result number (defaults to the beginning, 0; for pagination)
     * @param max         Limit the number of memes to this number, at most (defaults to 10; for pagination)
     *
     * @return A collection of memes (categories, taxonomies, options), in order of search relevance
     */
    public Results query(String language, String terms, List<String> definitions, String opco, int from, int max) {
        String defs = null;
        if (definitions != null) {
            defs = Joiner.on(",").join(definitions);
        }

        return Results.fromJson(services.request().to("search").send("lang", language, "q", terms,
                "from", from, "max", max, "definitions", defs).root());
    }

    /**
     * Default to ten memes.
     *
     * @param language    The language in which to conduct the search
     * @param terms       A set of keywords or phrases to search for
     * @param definitions Limit the search to memes of this type, e.g. ["Categories", "Taxonomies"]
     * @param opco        Limit the search to a specific opco
     * @param from        Start from this result number (defaults to the beginning, 0; for pagination)
     *
     * @return A collection of memes (categories, taxonomies, options), in order of search relevance
     */
    public Results query(String language, String terms, List<String> definitions, String opco, int from) {
        return query(language, terms, definitions, opco, from, 10);
    }

    /**
     * Default to the first ten memes.
     *
     * @param language    The language in which to conduct the search
     * @param terms       A set of keywords or phrases to search for
     * @param definitions Limit the search to memes of this type, e.g. ["Categories", "Taxonomies"]
     * @param opco        Limit the search to a specific opco
     *
     * @return A collection of memes (categories, taxonomies, options), in order of search relevance
     */
    public Results query(String language, String terms, List<String> definitions, String opco) {
        return query(language, terms, definitions, opco, 0, 10);
    }

    /**
     * Default to the first ten memes across all operating companies.
     *
     * @param language    The language in which to conduct the search
     * @param terms       A set of keywords or phrases to search for
     * @param definitions Limit the search to memes of this type, e.g. ["Categories", "Taxonomies"]
     *
     * @return A collection of memes (categories, taxonomies, options), in order of search relevance
     */
    public Results query(String language, String terms, List<String> definitions) {
        return query(language, terms, definitions, null, 0, 10);
    }

    /**
     * Default to the first ten memes regardless of definition for the given
     * opco.
     *
     * @param language The language in which to conduct the search
     * @param terms    A set of keywords or phrases to search for
     * @param opco     Limit the search to a specific opco
     *
     * @return A collection of memes (categories, taxonomies, options), in order of search relevance
     */
    public Results query(String language, String terms, String opco) {
        return query(language, terms, null, opco, 0, 10);
    }

    /**
     * Default to the first ten memes across all operating companies, regardless
     * of definition (category, property, heading, etc.).
     *
     * @param language The language in which to conduct the search
     * @param terms    A set of keywords or phrases to search for
     *
     * @return A collection of memes (categories, taxonomies, options), in order of search relevance
     */
    public Results query(String language, String terms) {
        return query(language, terms, null, null, 0, 10);
    }
}

