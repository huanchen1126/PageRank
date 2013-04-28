import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QTSPR extends PageRankTest {
  /* mapping from topic to list of docs */
  private Map<Integer, Set<Integer>> topics = null;

  public QTSPR(String epath, String dpath) {
    super(epath);
    readDocTopic(dpath);
  }

  private void readDocTopic(String dpath) {
    topics = new HashMap<Integer, Set<Integer>>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(dpath));
      String line = null;
      while ((line = br.readLine()) != null) {
        /* get the start and end of edge */
        String[] tmp = line.split(" ");
        int doc = Integer.parseInt(tmp[0]);
        int topic = Integer.parseInt(tmp[1]);
        /* update topics */
        if (!topics.containsKey(topic)) {
          Set<Integer> set = new HashSet<Integer>();
          set.add(doc);
          topics.put(topic, set);
        } else {
          topics.get(topic).add(doc);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null)
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }

  public void load(int topicid) {
    /* set the default value int the sparse matrix as alph/n */
    B = new MyMatrix2D(n, n, alph / n);
    /* set topic teleport */
    // System.out.println("begin topic");
    long a = System.currentTimeMillis();
    int sizeT = topics.get(topicid).size();
    // System.out.println("size of topic:" + sizeT);
    Set<Integer> docs = topics.get(topicid);
    double v = beta / sizeT;
    B.setRowDefault(docs, v);
    // int d = 0;
    // for (int doc : docs) {
    // System.out.println("document: " + d);
    // for (int i = 0; i < n; i++) {
    // B.set(doc, i, v);
    // }
    // d++;
    // }
    // System.out.println("\r<br>time : " + (System.currentTimeMillis() - a) / 1000f + " seconds");
    // System.out.println("end topic");
    for (int i : edges.keySet()) {
      List<Integer> list = edges.get(i);
      int sizeE = list.size();
      for (int j : list) {
        /* set j,i because B needs to be transposed */
        B.set(j, i, (1 - alph - beta) / sizeE);
      }
    }
    /* build initial r */
    r = new MyMatrix1D(n, 1.0);
  }

  public static void main(String[] args) {
    // String epath = "/Users/huanchen/Desktop/ir_hw5/data/transition.txt";
    // String dpath = "/Users/huanchen/Desktop/ir_hw5/data/doc_topics.txt";
    if (args.length != 1)
      return;
    String epath = "data/transition.txt";
    String dpath = "data/doc_topics.txt";
    String output = args[0];
    QTSPR qpr = new QTSPR(epath, dpath);
    for (int i = 1; i <= 12; i++) {
      qpr.load(i);
      qpr.pagerank(output + "/tspr_" + i + ".out");
    }
  }
}
