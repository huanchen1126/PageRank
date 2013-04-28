import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;

public class MyMatrix1D {
  private DoubleMatrix1D matrix;

  private double defaultValue = 0.0;

  private int size;

  public MyMatrix1D(int s) {
    this.size = s;
    DoubleFactory1D factory1D = DoubleFactory1D.sparse;
    matrix = factory1D.make(s);
  }

  public MyMatrix1D(int s, double v) {
    this.size = s;
    this.defaultValue = v;
    DoubleFactory1D factory1D = DoubleFactory1D.sparse;
    matrix = factory1D.make(s);
  }

  public void set(int i, double v) {
    matrix.setQuick(i, v);
  }

  public void add(int i, double v) {
    double cur = matrix.get(i);
    matrix.setQuick(i, v + cur);
  }

  public int getSize() {
    return this.size;
  }

  public double getNormEucDist(MyMatrix1D m) {
    int s = m.getSize();
    if (s != this.size)
      throw new RuntimeException("num of cells does not match");
    double dist = 0.0;
    for (int i = 0; i < s; i++) {
      dist += Math.pow(this.get(i) - m.get(i), 2);
    }
    dist = Math.sqrt(dist);
    return dist / s;
  }

  public double get(int i) {
    double result = matrix.getQuick(i);
    return result + defaultValue;
  }

  public void print() {
    for (int i = 0; i < size; i++) {
      System.out.println(i + " " + this.get(i));
    }
  }
}
