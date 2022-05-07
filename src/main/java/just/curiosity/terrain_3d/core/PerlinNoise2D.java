package just.curiosity.terrain_3d.core;

import java.util.Random;

public final class PerlinNoise2D {
  public static final double gradientRange;
  private static final double[][] gradientVectors;
  private static final double[] d0;
  private static final double[] d1;
  private static final double[] d2;
  private static final double[] d3;
  private static final byte[] auxiliaryBytes;

  static {
    gradientRange = 4d;
    gradientVectors = new double[][]{
      {gradientRange, 0}, {-gradientRange, 0},
      {0, gradientRange}, {0, -gradientRange}
    };

    d0 = new double[2];
    d1 = new double[2];
    d2 = new double[2];
    d3 = new double[2];

    auxiliaryBytes = new byte[512];

    genAux();
  }

  /*
   * Generating random auxiliary bytes.
   * */

  public static void genAux() {
    new Random().nextBytes(auxiliaryBytes);
  }

  /*
   * Multi octave noise.
   * */

  public static double calc(double x, double y, int octaves, double persistence) {
    double amplitude = 1;
    double max = 0;
    double result = 0;

    while (octaves-- > 0) {
      max += amplitude;
      result += calc(x, y) * amplitude;
      amplitude *= persistence;
      x *= 2;
      y *= 2;
    }

    return result / max;
  }

  /*
   * Main noise function.
   * */

  public static double calc(double x, double y) {
    /*
     * The top left corner of the square.
     * */

    int tLX = (int) Math.floor(x);
    int tLY = (int) Math.floor(y);

    /*
     * The local position of the point in the current
     * square.
     * */

    double dX = x - tLX;
    double dY = y - tLY;

    /*
     * Calculation of vectors emanating from the vertices
     * of a square to a point.
     * */

    d0[0] = dX;
    d0[1] = dY;

    d1[0] = dX - 1;
    d1[1] = dY;

    d2[0] = dX;
    d2[1] = dY - 1;

    d3[0] = dX - 1;
    d3[1] = dY - 1;

    /*
     * Calculation of gradient vectors for each of
     * the vertices.
     * */

    double[] g0 = gradientVector(tLX, tLY);
    double[] g1 = gradientVector(tLX + 1, tLY);
    double[] g2 = gradientVector(tLX, tLY + 1);
    double[] g3 = gradientVector(tLX + 1, tLY + 1);

    /*
     * Dot product of gradient vectors by relative vectors.
     * */

    double q0 = dotProduct(d0, g0);
    double q1 = dotProduct(d1, g1);
    double q2 = dotProduct(d2, g2);
    double q3 = dotProduct(d3, g3);

    dX = quinticCurve(dX);
    dY = quinticCurve(dY);

    double l1 = linearInterpolation(q0, q1, dX);
    double l2 = linearInterpolation(q2, q3, dX);

    return linearInterpolation(l1, l2, dY);
  }

  private static double[] gradientVector(int x, int y) {
    int hash = ((x * 234231123) ^ (y * 234231123) + 234231123);
    int aux = auxiliaryBytes[hash & (auxiliaryBytes.length - 1)];

    return gradientVectors[aux & (gradientVectors.length - 1)];
  }

  private static double dotProduct(double[] a, double[] b) {
    return a[0] * b[0] + a[1] * b[1];
  }

  private static double linearInterpolation(double a, double b, double t) {
    return a + (b - a) * t;
  }

  static double quinticCurve(double t) {
    return t * t * t * (t * (t * 6 - 15) + 10);
  }
}