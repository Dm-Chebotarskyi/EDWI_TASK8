package chebotarskyi.dm;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class IndexUtils {

    public static final String INDEX_PATH = "/home/dima/index_task8.lucene";
    private final Supplier<IndexWriter> w;

    IndexUtils() {
        w = Suppliers.memoize(this::getWriter);
    }

    private IndexWriter getWriter() {
        IndexWriter w = null;
        try {
            StandardAnalyzer analyzer = new StandardAnalyzer();
            FSDirectory index = FSDirectory.open(Paths.get(INDEX_PATH));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            w = new IndexWriter(index, config);
        } catch (IOException e) {
            System.out.println("Cannot create writer: " + e.getMessage());
        }
        return w;
    }

    public void addDoc(String url, String body) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("url", url, Field.Store.YES));
        doc.add(new TextField("body", body, Field.Store.YES));
        w.get().addDocument(doc);
    }

    public void closeWriter() {
        try {
            w.get().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 

}
