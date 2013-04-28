import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PageRankTest {

  protected MyMatrix2D B = null;

  protected MyMatrix1D r = null;

  protected Map<Integer, List<Integer>> edges = null;

  protected static double alph = 0.05;

  protected static double beta = 0.1;

  private static double DIST = 0.00001;

  /* num of nodes in the graph */
  protected int n = 0;

  public PageRankTest(String epath) {
    readEdges(epath);
  }

  private void readEdges(String epath) {
    /* store the edges */
    edges = new HashMap<Integer, List<Integer>>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(epath));
      String line = null;
      while ((line = br.readLine()) != null) {
        /* get the start and end of edge */
        String[] tmp = line.split(" ");
        int i = Integer.parseInt(tmp[0]);
        int j = Integer.parseInt(tmp[1]);
        /* update num of nodes */
        n = Math.max(n, Math.max(i, j));
        /* update edges */
        if (!edges.containsKey(i)) {
          List<Integer> list = new ArrayList<Integer>();
          list.add(j);
          edges.put(i, list);
        } else {
          edges.get(i).add(j);
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

  public void pagerank(String output) {
    if (B == null || r == null)
      throw new RuntimeException("the matrices cannot be null.");
    int iteration = 0;
    double dist = Double.MAX_VALUE;
    while (dist > DIST) {
      long a = System.currentTimeMillis();
      System.out.println("Iteration " + iteration++ + ", distance " + dist);
      MyMatrix1D curR = B.times1D(r);
      dist = curR.getNormEucDist(r);
      r = curR;
      System.out.println("\r<br>time : " + (System.currentTimeMillis() - a) / 1000f + " seconds");
    }
//    BufferedWriter bw = null;
//    try {
//      bw = new BufferedWriter(new FileWriter(output));
//      int size = r.getSize();
//      for (int i = 0; i < size; i++) {
//        bw.write(i + " " + r.get(i));
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    } finally {
//      try {
//        bw.close();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
  }
}
