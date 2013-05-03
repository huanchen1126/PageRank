import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalPR extends PageRank {

  /* mapping from node to pagerank value */
  protected Map<String, Double> score = null;

  /**
   * @param epath
   *          : file path of edges
   */
  public GlobalPR(String epath) {
    /* initialize score */
    score = new HashMap<String, Double>();
    readEdges(epath);
    pagerank();
  }

  /**
   * run global pagerank
   */
  private void pagerank() {
    /* set the initail pagerank score */
    double initialScore = 1.0 / numofdocs;
    for (int i = 1; i <= numofdocs; i++) {
      score.put(String.valueOf(i), initialScore);
    }
    /* start iteration */
    double dist = Double.MAX_VALUE;
    int iteration = 1;
    while (dist > DIST) {
      // System.out.println("Iteration " + iteration++ + ", distance " + dist);
      /* one iteration */
      /* in each iteration, each node first has an initial damping score */
      double dampScore = alpha / numofdocs;

      /* also each node will get a score from nodes with no outlink */
      double outScore = 0.0;
      for (String doc : noOutLink) {
        outScore += score.get(doc);
      }
      outScore = outScore * (1 - alpha) / numofdocs;

      /* start updating scores based on the graph structure */
      Map<String, Double> newScore = new HashMap<String, Double>();
      for (String doc : score.keySet()) {
        double s = 0.0;
        /* update new score based on transition matrix */
        if (edges.containsKey(doc)) {
          List<String> list = edges.get(doc);
          for (String start : list) {
            s += score.get(start) / sizes.get(start);
          }
          /* alpha is damping factor */
          s *= (1 - alpha);
        }
        /* for all docs, add damping score */
        s += dampScore + outScore;
        newScore.put(doc, s);
      }
      /* get the distance between current score to previous score */
      dist = getDist(score, newScore);
      score = newScore;
    }
  }

  public Map<String, Double> getScore() {
    return this.score;
  }

}
