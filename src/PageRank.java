import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PageRank {
  /* mapping from end to start */
  protected Map<String, List<String>> edges = null;

  /* mapping from start to num of ends */
  protected Map<String, Integer> sizes = null;

  /* docs that has no outlink */
  protected List<String> noOutLink = null;

  /* the damping factor */
  protected static double alpha = 0.15;

  /* the distance for detecting convergence */
  protected static double DIST = 0.00000001;

  /* total number of docs in the dataset */
  protected int numofdocs = 0;

  /**
   * constructor method, initialize data structures
   */
  public PageRank() {
    /* store the edges */
    edges = new HashMap<String, List<String>>();
    sizes = new HashMap<String, Integer>();
    /* store doc id that has no outlink */
    noOutLink = new ArrayList<String>();
  }

  /**
   * read edges in the graph
   * 
   * @param epath
   *          file path to edges
   */
  protected void readEdges(String epath) {
    /* store the doc id */
    Set<String> docs = new HashSet<String>();

    /* store doc id that has outlink */
    Set<String> outs = new HashSet<String>();

    /* start reading edge file */
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(epath));
      String line = null;
      while ((line = br.readLine()) != null) {
        /* get the start and end of edge */
        String[] tmp = line.split(" ");
        String start = tmp[0];
        String end = tmp[1];
        /* update size of ending docs for 'start' */
        if (!sizes.containsKey(start))
          sizes.put(start, 1);
        else
          sizes.put(start, sizes.get(start) + 1);
        /* update edges */
        if (!edges.containsKey(end)) {
          List<String> list = new ArrayList<String>();
          list.add(start);
          edges.put(end, list);
        } else {
          edges.get(end).add(start);
        }
        /* update outs */
        outs.add(start);
        /* update docs */
        docs.add(start);
        docs.add(end);
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
    this.numofdocs = docs.size();
    /* update docs that has no out link */
    for (String doc : docs) {
      if (!outs.contains(doc))
        noOutLink.add(doc);
    }
  }

  /**
   * compute the distance between two pagerank score vector the distance is computed as the
   * Euclidean distance normalized by total number of docs
   * 
   * @param score1
   * @param score2
   * @return distance
   */
  protected double getDist(Map<String, Double> score1, Map<String, Double> score2) {
    double dist = 0.0;
    int size = score1.size();
    for (String doc : score1.keySet()) {
      dist += Math.pow(score1.get(doc) - score2.get(doc), 2);
    }
    dist = Math.sqrt(dist) / size;
    return dist;
  }
}
