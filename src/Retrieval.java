import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

public class Retrieval {
  /* different pagerank types */
  public enum Type {
    GPR, QTSPR, PTSPR
  }

  Type type;

  /* use user:doc as the key */
  Map<String, double[]> topicDistr;

  /* total number of topics */
  private static int numoftopics = 12;

  /* the weight for page rank score */
  private static double WS = 0.4;

  /* the scores of topic sensitive pagerank */
  private Map<Integer, Map<String, Double>> scores = null;

  /* the score of global pagerank */
  private Map<String, Double> score = null;

  /* file path to edges */
  public static String edgepath = "/Users/huanchen/Desktop/ir_hw5/data/transition.txt";

  /* file path to document topical information */
  public static String topicpath = "/Users/huanchen/Desktop/ir_hw5/data/doc_topics.txt";

  /* file path to query topical information */
  public static String querypath = "/Users/huanchen/Desktop/ir_hw5/data/query_topics.txt";

  /* file path to user topical information */
  public static String userpath = "/Users/huanchen/Desktop/ir_hw5/data/interest.txt";

  /* the text based score */
  public static String searchRelevancePath = "/Users/huanchen/Desktop/ir_hw5/data/indri-lists";

  /* the output path */
  public static String output = "/Users/huanchen/Desktop/ir_hw5/output";

  /**
   * contructor method
   * 
   * @param t
   *          : type of pagerank
   */
  public Retrieval(Type t) {
    type = t;
    /* initialize data structures according to different pagerank types */
    if (type == Type.QTSPR) {
      topicDistr = new HashMap<String, double[]>();
      readTopicDistr(querypath);
      TopicSensitivePR tspr = new TopicSensitivePR(edgepath, topicpath, numoftopics);
      scores = tspr.getScores();
    } else if (type == Type.PTSPR) {
      topicDistr = new HashMap<String, double[]>();
      readTopicDistr(userpath);
      TopicSensitivePR tspr = new TopicSensitivePR(edgepath, topicpath, numoftopics);
      scores = tspr.getScores();
    } else {
      GlobalPR gpr = new GlobalPR(edgepath);
      score = gpr.getScore();
    }
  }

  /**
   * read topical distribution
   * 
   * @param tpath
   *          : file path to query topical info if QTSPR or user topical info if PTSPR
   */
  private void readTopicDistr(String tpath) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(tpath));
      String line = null;
      while ((line = br.readLine()) != null) {
        /* get the start and end of edge */
        String[] tmp = line.split(" ");
        String user = tmp[0];
        String doc = tmp[1];
        /* update topics */
        int endindex = 2 + numoftopics;
        /* the array of topic distribution */
        double[] array = new double[numoftopics];
        for (int i = 2, j = 0; i < endindex; i++, j++) {
          int colonindex = tmp[i].indexOf(':');
          double score = Double.parseDouble(tmp[i].substring(colonindex + 1));
          array[j] = score;
        }
        topicDistr.put(user + "-" + doc, array);
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

  /**
   * 
   * read the text based score
   * 
   * @param userquery
   *          should be in format user-query
   * @return
   */
  private Map<String, Double> getSearchRelevanceScore(String userquery) {
    String path = this.searchRelevancePath + "/" + userquery + ".results.txt";
    /* mapping from docuement to score */
    Map<String, Double> result = new HashMap<String, Double>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(path));
      String line = null;
      while ((line = br.readLine()) != null) {
        /* get the start and end of edge */
        String[] tmp = line.split(" ");
        String doc = tmp[2];
        double score = Double.parseDouble(tmp[4]);
        result.put(doc, score);
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
    return result;
  }

  /**
   * weighted sum retrieval
   * 
   * @param userquery
   *          should be in format user-query
   */
  public void retrieveWS(String userquery) {
    Map<String, Double> searchRelevance = getSearchRelevanceScore(userquery);
    Map<String, Double> result = new HashMap<String, Double>();
    /* get the pagerank score according to different pagerank types */
    if (type == Type.GPR) {
      for (String doc : score.keySet()) {
        double s = score.get(doc);
        result.put(doc, s);
      }
    } else { /* if topic sensitive pagerank */
      /* weighted sum based on the topical distribution */
      double[] distribution = this.topicDistr.get(userquery);
      for (int topic : scores.keySet()) {
        Map<String, Double> score = scores.get(topic);
        for (String doc : score.keySet()) {
          double s = score.get(doc);
          if (!result.containsKey(doc)) {
            result.put(doc, s * distribution[topic - 1]);
          } else
            result.put(doc, result.get(doc) + s * distribution[topic - 1]);
        }
      }
    }
    /* get the weighted score */
    for (String doc : result.keySet()) {
      if (searchRelevance.containsKey(doc))
        result.put(doc, WS * result.get(doc) + (1 - WS) * searchRelevance.get(doc));
    }
    // for (String doc : result.keySet()) {
    // System.out.println(doc + " : " + result.get(doc));
    // }
    /* print to file */
    print(userquery, result);
  }

  /**
   * retrieval based only on pagerank score
   * 
   * @param userquery
   *          should be in format user-query
   */
  public void retrievaNS(String userquery) {
    Map<String, Double> result = new HashMap<String, Double>();
    /* get the pagerank score according to different pagerank types */
    if (type == Type.GPR) {
      for (String doc : score.keySet()) {
        double s = score.get(doc);
        result.put(doc, s);
      }
    } else { /* if topic sensitive pagerank */
      double[] distribution = this.topicDistr.get(userquery);
      /* weighted sum based on the topical distribution */
      for (int topic : scores.keySet()) {
        Map<String, Double> score = scores.get(topic);
        for (String doc : score.keySet()) {
          double s = score.get(doc);
          if (!result.containsKey(doc)) {
            result.put(doc, s * distribution[topic - 1]);
          } else
            result.put(doc, result.get(doc) + s * distribution[topic - 1]);
        }
      }
    }
    // for (String doc : result.keySet()) {
    // System.out.println(doc + " : " + result.get(doc));
    // }
    print(userquery, result);
  }

  /**
   * retrieval using the multiplication of pagerank score and text based score
   * 
   * @param userquery
   */
  public void retrievaCM(String userquery) {
    Map<String, Double> searchRelevance = getSearchRelevanceScore(userquery);
    Map<String, Double> result = new HashMap<String, Double>();
    /* get the pagerank score according to different pagerank types */
    if (type == Type.GPR) {
      for (String doc : score.keySet()) {
        double s = score.get(doc);
        result.put(doc, s);
      }
    } else { /* if topic sensitive pagerank */
      /* weighted sum based on the topical distribution */
      double[] distribution = this.topicDistr.get(userquery);
      for (int topic : scores.keySet()) {
        Map<String, Double> score = scores.get(topic);
        for (String doc : score.keySet()) {
          double s = score.get(doc);
          if (!result.containsKey(doc)) {
            result.put(doc, s * distribution[topic - 1]);
          } else
            result.put(doc, result.get(doc) + s * distribution[topic - 1]);
        }
      }
    }
    /* get the customized score */
    for (String doc : result.keySet()) {
      if (searchRelevance.containsKey(doc))
        result.put(doc, result.get(doc) * searchRelevance.get(doc));
    }
    // for (String doc : result.keySet()) {
    // System.out.println(doc + " : " + result.get(doc));
    // }
    print(userquery, result);
  }

  /**
   * print the result to files
   * 
   * @param userquery
   *          user:query
   * @param result
   */
  private void print(String userquery, Map<String, Double> result) {
    int ind = userquery.indexOf('-');
    String user = userquery.substring(0, ind);
    String query = userquery.substring(ind + 1);
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(output + "/" + userquery + ".results.txt", true));
      /* build a minimum heap to get top 100 */
      PriorityQueue<Map.Entry<String, Double>> heap = new PriorityQueue<Map.Entry<String, Double>>(
              100, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                  if (o1.getValue() < o2.getValue())
                    return -1;
                  else
                    return 1;
                }
              });
      /* add to heap */
      for (Map.Entry<String, Double> e : result.entrySet()) {
        if (heap.size() < 100)
          heap.add(e);
        else {
          Map.Entry<String, Double> tmp = heap.peek();
          if (tmp.getValue() < e.getValue()) {
            heap.remove();
            heap.add(e);
          }
        }
      }
      /* put the result to a linked list */
      LinkedList<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>();
      while (heap.size() > 0) {
        Map.Entry<String, Double> e = heap.poll();
        list.addFirst(e);
      }
      /* print top 100 to files */
      int rank = 1;
      for (Map.Entry<String, Double> e : list) {
        bw.write(query + " Q0 " + e.getKey() + " " + rank++ + " " + e.getValue() + " indri\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (bw != null)
        try {
          bw.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }

  public static void main(String[] args) {
    long a = System.currentTimeMillis();
    Retrieval r = new Retrieval(Retrieval.Type.PTSPR);
    r.retrieveWS("19-5");
    // Retrieval r = new Retrieval(Retrieval.Type.GPR);
    // r.retrieveWS("19-5");
    System.out.println("\r<br>time : " + (System.currentTimeMillis() - a) / 1000f + " seconds");
  }
}
