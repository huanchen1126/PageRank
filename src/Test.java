import java.util.HashSet;
import java.util.Set;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;



public class Test {
  public static void main(String[] args) {
    Set<Integer> set = new HashSet<Integer>();
    set.add(1);
    MyMatrix2D m = new MyMatrix2D(4, 4, 1.0);
    m.set(0, 0, 1.0);
    m.set(2, 2, 3);
    m.setRowDefault(set, 1.0);
    MyMatrix1D v = new MyMatrix1D(4, 1.0);
    MyMatrix1D r = m.times1D(v);
    for( int i=0; i<4;i++)
      System.out.println(r.get(i));
  }
}
