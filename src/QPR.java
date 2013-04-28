import java.util.List;

public class QPR extends PageRankTest {

  public QPR(String epath) {
    super(epath);
  }

  public void load() {
    /* set the default value int the sparse matrix as alph/n */
    B = new MyMatrix2D(n, n, alph / n);
    for (int i : edges.keySet()) {
      List<Integer> list = edges.get(i);
      int size = list.size();
      for (int j : list) {
        /* set j,i because B needs to be transposed */
        B.set(j, i, (1 - alph) / size);
      }
    }
    /* build initial r */
    r = new MyMatrix1D(n, 1.0);
  }

  public static void main(String[] args) {
    String epath = "/Users/huanchen/Desktop/ir_hw5/data/transition.txt";
    QPR qpr = new QPR(epath);
    String output = null;
    qpr.load();
    qpr.pagerank(output + "/qpr.out");

  }

}
