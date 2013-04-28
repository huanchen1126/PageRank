import java.util.Set;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class MyMatrix2D {
  private DoubleMatrix2D matrix;

  private double defaultValue = 0.0;

  private Set<Integer> rowWithDefault;

  private double rowDefaultValue = 0.0;

  private int colDim;

  private int rowDim;

  public MyMatrix2D(int m, int n, double v) {
    DoubleFactory2D factory2D = DoubleFactory2D.rowCompressed;
    matrix = factory2D.make(m, n);
    defaultValue = v;
    rowDim = m;
    colDim = n;
  }

  /* one time set */
  public void set(int i, int j, double v) {
    matrix.setQuick(i, j, v);
  }

//  public void add(int i, int j, double v) {
//    matrix.setQuick(i, j, matrix.getQuick(i, j) + v);
//  }

  public void setRowDefault(Set<Integer> r, double v) {
    this.rowWithDefault = r;
    this.rowDefaultValue = v;
  }

  public double get(int i, int j) {
    double result = matrix.getQuick(i, j);
    result += defaultValue;
    if (this.rowWithDefault != null && this.rowWithDefault.contains(i))
      result += this.rowDefaultValue;
    return result;
  }

  public int getColDim() {
    return this.colDim;
  }

  public int getRowDim() {
    return this.rowDim;
  }

  public MyMatrix1D times1D(MyMatrix1D m) {
    int size = m.getSize();
    if (this.colDim != size)
      throw new RuntimeException("size does not match");
    MyMatrix1D result = new MyMatrix1D(size);
    for (int i = 0; i < rowDim; i++) {
      // System.out.println("num of row mul:" + numofrow++);
      double r = 0.0;
      for (int j = 0; j < colDim; j++) {
        r += this.get(i, j) * m.get(j);
      }
      result.set(i, r);
    }
    return result;
  }
}
