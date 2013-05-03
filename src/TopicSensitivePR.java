import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TopicSensitivePR extends PageRank {
  /* mapping from topic to list of docs */
  private Map<Integer, Set<String>> topics = null;

  /* mapping from topic id to scores */
  protected Map<Integer, Map<String, Double>> scores = null;

  /* the weight for topic sensitive transition prob */
  private static double beta = 0.1;

  /**
   * constructor method
   * 
   * @param epath
   *          : file path to edges
   * @param tpath
   *          : file path to document topical information
   * @param numoftopics
   *          : total number of topics
   */
  public TopicSensitivePR(String epath, String dpath, int numoftopics) {
    topics = new HashMap<Integer, Set<String>>();
    scores = new HashMap<Integer, Map<String, Double>>();
    readEdges(epath);
    readDocTopic(dpath);
    for (int i = 1; i <= numoftopics; i++) {
      pagerank(i);
    }
  }

  /**
   * read document topical information
   * 
   * @param dpath
   *          : file path to document topical information
   */
  private void readDocTopic(String dpath) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(dpath));
      String line = null;
      while ((line = br.readLine()) != null) {
        /* get the start and end of edge */
        String[] tmp = line.split(" ");
        String doc = tmp[0];
        int topic = Integer.parseInt(tmp[1]);
        /* update topics */
        if (!topics.containsKey(topic)) {
          Set<String> set = new HashSet<String>();
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

  /**
   * 
   * @param topic
   *          : the topic for topic sensitive pagerank
   */
  private void pagerank(int topic) {
    /* topic sensitive pagerank score */
    Map<String, Double> score = new HashMap<String, Double>();

    /* set the initial pagerank score */
    double initialScore = 1.0 / numofdocs;
    for (int i = 1; i <= numofdocs; i++) {
      score.put(String.valueOf(i), initialScore);
    }
    /* set of docs in the topic */
    Set<String> docs = topics.get(topic);
    int topicsize = docs.size();
    /* start pagerank iteration */
    double dist = Double.MAX_VALUE;
    while (dist > DIST) {
      /* one iteration */
      /* in each iteration, each node first has a initial damping score */
      double dampScore = alpha / numofdocs;

      /* docs in the right topic also have a initial topic score */
      double topicScore = beta / topicsize;

      /* also each node will get a score from nodes with no outlink */
      double outScore = 0.0;
      for (String doc : noOutLink) {
        outScore += score.get(doc);
      }
      outScore = outScore * (1 - alpha - beta) / numofdocs;

      /* start updating scores */
      Map<String, Double> newScore = new HashMap<String, Double>();
      for (String doc : score.keySet()) {
        double s = 0.0;
        /* update new score based on trainsion matrix */
        if (edges.containsKey(doc)) {
          List<String> list = edges.get(doc);
          for (String start : list) {
            s += score.get(start) / sizes.get(start);
          }
          s *= (1 - alpha - beta);
        }
        /* for all docs, add damping score */
        s += dampScore + outScore;
        /* for docs with right topic, add topic score */
        if (docs.contains(doc))
          s += topicScore;
        /* add to new score */
        newScore.put(doc, s);
      }
      /* get the distance between current score to previous score */
      dist = getDist(score, newScore);
      score = newScore;
    }
    scores.put(topic, score);
  }

  public Map<Integer, Map<String, Double>> getScores() {
    return this.scores;
  }
}
