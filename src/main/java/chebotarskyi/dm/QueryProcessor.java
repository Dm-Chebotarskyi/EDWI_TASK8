package chebotarskyi.dm;

import chebotarskyi.dm.http.TextProcessingService;
import chebotarskyi.dm.http.model.TextProcessingServiceResponse;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.nio.file.Paths;


public class QueryProcessor {

    private final StandardAnalyzer analyzer;
    private FSDirectory index;
    private final int buffer = 200;

    public QueryProcessor() {
        analyzer = new StandardAnalyzer();
        try {
            index = FSDirectory.open(Paths.get(IndexUtils.INDEX_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String processQuery(String querystr) throws ParseException, NotFoundException {
        int hitsPerPage = 5;

        Query q = new QueryParser("body", analyzer).parse(querystr);

        try {
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            if (hits.length > 0) {
                Document d = searcher.doc(hits[0].doc);

                Formatter formatter = new Formatter() {
                    @Override
                    public String highlightTerm(String s, TokenGroup tokenGroup) {
                        return tokenGroup.getTotalScore() <= 0 ? s : s.toUpperCase();
                    }
                };

                //It scores text fragments by the number of unique query terms found
                //Basically the matching score in layman terms
                QueryScorer scorer = new QueryScorer(q);

                //used to markup highlighted terms found in the best sections of a text
                Highlighter highlighter = new Highlighter(formatter, scorer);

                //It breaks text up into same-size texts but does not split up spans
                Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 250);

                highlighter.setTextFragmenter(fragmenter);

                StringBuilder builder = new StringBuilder();

                //Iterate over found results
                for (int i = 0; i < hits.length; i++) {
                    int docid = hits[i].doc;
                    Document doc = searcher.doc(docid);

                    //Get stored text from found document
                    String text = doc.get("body");

                    //Create token stream
                    TokenStream stream = TokenSources.getAnyTokenStream(reader, docid, "body", analyzer);

                    //Get highlighted text fragments
                    String[] frags = highlighter.getBestFragments(stream, text, 1);
                    for (String frag : frags) {
                        System.out.println("=======================");
                        System.out.println(frag);
                        builder.append(makeRequest(frag));
                        builder.append("\n").append("\n");
                    }

                }

                return builder.toString();
            } else {
                throw new NotFoundException();
            }
        } catch (IOException e)

        {
            return e.getMessage();
        } catch (InvalidTokenOffsetsException e) {
            return e.getMessage();
        }
    }

    private String makeRequest(String fragment) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://text-processing.com/api/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        TextProcessingService service = retrofit.create(TextProcessingService.class);

        Call<TextProcessingServiceResponse> call = service.sentimentAnalysis(fragment);
        Response<TextProcessingServiceResponse> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder builder = new StringBuilder();

        if (response.isSuccessful()) {
            builder.append("Label: ")
                    .append(response.body().label())
                    .append("\n")
                    .append("Probability: {\n\tnegative: ")
                    .append(response.body().probability().neg())
                    .append("\n\tneutral: ")
                    .append(response.body().probability().neutral())
                    .append("\n\tpositive: ")
                    .append(response.body().probability().pos())
                    .append("\n}");

            return builder.toString();
        } else {
            throw new RuntimeException(response.errorBody().string());
        }
    }

    public void closeIndex() {
        try {
            index.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
