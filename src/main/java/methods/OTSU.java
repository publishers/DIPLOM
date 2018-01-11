package methods;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Admin on 15.11.15.
 * <p>
 * Array colors must be white and black
 */

public class OTSU {
  private int L;
  private int N;
  private Color[][] colors;
  private int[] histogram;
  private double[] Pi;

  public OTSU(Color[][] colors) {
    this.L = 256;
    this.N = colors.length * colors[0].length;
    this.histogram = new int[L];
    this.Pi = new double[L];
    this.colors = colors.clone();
  }

  public OTSU() throws IOException {
    setColors();
    this.L = 256;
    this.N = colors.length * colors[0].length;
    this.histogram = new int[L];
    this.Pi = new double[L];
  }

  private void setColors() throws IOException {
    BufferedImage img = ImageIO.read(new File("image.jpg"));
    colors = new Color[img.getWidth()][img.getHeight()];
    for (int i = 0; i < img.getWidth(); i++) {
      for (int j = 0; j < img.getHeight(); j++) {
        colors[i][j] = new Color(img.getRGB(i, j));
      }
    }
  }

  public void start() {
    clear();
    createHistogram();
    doPi();
  }

  public void createHistogram() {
    for (int i = 0; i < colors.length; i++) {
      for (int j = 0; j < colors[i].length; j++) {
        int level = colors[i][j].getBlue();
        histogram[level]++;
      }
    }

  }

  /**
   * variant threshold is 1
   */
  public int threshold1() {
    double w = 0;
    double u = 0;
    double uT = uT();
    double oB;
    double threshold = 0;
    double max = 0;
    for (int i = 1; i < L; i++) {
      w += Pi[i - 1];
      u += (double) i * Pi[i - 1];
      oB = Math.pow(uT * w - u, 2) / (w * (1 - w));
      if (oB > max) {
        max = oB;
        threshold = i;
      }
    }
    return (int) threshold;
  }

  private double uT() {
    double uT = 0;
    for (int i = 1; i < L; i++) {
      uT += i * Pi[i - 1];
    }
    return uT;
  }

  /**
   * variant threshold is 2
   */
  public int threshold2() {
    int[] histogram = getHistogram();

    int size = N;

    float sum = 0;
    for (int i = 0; i < 256; i++) sum += i * histogram[i];

    float sumB = 0;
    int w0 = 0;
    int w1 = 0;

    float varMax = 0;
    int threshold = 0;

    for (int t = 0; t < L; t++) {
      w0 += histogram[t];
      if (w0 == 0) continue;
      w1 = size - w0;

      if (w1 == 0) break;

      sumB += (float) (t * histogram[t]);
      float mB = sumB / w0;
      float mF = (sum - sumB) / w1;

      float varBetween = (float) w0 * (float) w1 * (mB - mF) * (mB - mF);

      if (varBetween > varMax) {
        varMax = varBetween;
        threshold = t;
      }
    }
    return threshold;
  }

  /**
   * variant threshold is 3
   */
  public int threshold3() {
    int threshold = 0;
    double max = 0;
    for (int t = 1; t < L; t++) {
      double Topt = (P(t) * (1 - P(t)) * (mf(t) - mb(t)) * (mf(t) - mb(t))) / (P(t) * of(t) + (1 - P(t)) * ob(t));
      if (max < Topt) {
        max = Topt;
        threshold = t;
      }
    }
    return threshold;
  }

  /***/
  private double ob(int T) {
    double sum = 0;
    for (int i = T + 1; i < L; i++) {
      sum += (i - mb(T)) * (i - mb(T)) * Pi[i];
    }
    return sum;
  }

  private double of(int T) {
    double sum = 0;
    for (int i = 0; i <= T; i++) {
      sum += (i - mf(T)) * (i - mf(T)) * Pi[i];
    }
    return sum;
  }

  private double mb(int T) {
    double sum = 0;
    for (int i = T + 1; i < L; i++) {
      sum += i * Pi[i];
    }
    return sum;
  }

  private double mf(int T) {
    double sum = 0;
    for (int i = 0; i <= T; i++) {
      sum += i * Pi[i];
    }
    return sum;
  }

  private double P(int T) {
    double sum = 0;
    for (int i = 0; i <= T; i++) {
      sum += Pi[i];
    }
    return sum;
  }

  /**
   * threshold 4 Low
   */
  public int threshold4() {
    int threshold = 0;
    double T;
    double oldT = 0;
    double deltaT = 0.01;
    for (int i = 0; i < L; i++) {
      double m1 = m1(i);
      double m2 = m2(i);
      T = (m1 + m2) / 2;
      if (Math.abs(oldT - T) > deltaT) {
        threshold = (int) T;
        break;
      } else {
        oldT = T;
      }
    }
    return threshold;
  }

  private double m1(int k) {
    double sum = 0;
    for (int i = 0; i <= k; i++) {
      sum += i * Pi[i];
    }
    return sum / P1(k);
  }

  private double m2(int k) {
    double sum = 0;
    for (int i = k + 1; i < L; i++) {
      sum += i * Pi[i];
    }
    return sum / (1d - P1(k));
  }

  private double P1(int k) {
    double sum = 0;
    for (int i = 0; i <= k; i++) {
      sum += Pi[i];
    }
    return sum;
  }


  /**
   * variant threashold is 5
   */
  public int threshold5() {
    int threshold = 0;
    double max = 0;
    double mG = threshold5mG();
    for (int i = 0; i < L; i++) {
      double oB = Math.pow((mG * threshold5P1(i) - threshold5m(i)), 2)
          /
          (threshold5P1(i) * (1 - threshold5P1(i)));
      if (max < oB) {
        max = oB;
        threshold = i;
      }
    }
    return threshold;
  }

  private double threshold5m(int k) {
    double sum = 0;
    for (int i = 0; i <= k; i++) {
      sum += i * Pi[i];
    }
    return sum;
  }

  private double threshold5P1(int T) {
    double sum = 0;
    for (int i = 0; i <= T; i++) {
      sum += Pi[i];
    }
    return sum;
  }

  private double threshold5mG() {
    double sum = 0;
    for (int i = 0; i < L; i++) {
      sum += i * Pi[i];
    }
    return sum;
  }

  /**
   * threshold 4 Hight
   */

  public int threshold4Hight() {
    int threshold = 0;
    double T;
    double oldT = 0;
    double deltaT = 0.01;
    for (int i = L - 1; i > 0; i--) {
      double m1 = m1Hight(i);
      double m2 = m2Hight(i);
      T = (m1 + m2) / 2;
      if (Math.abs(oldT - T) > deltaT) {
        threshold = (int) T;
        break;
      } else {
        oldT = T;
      }
    }
    return threshold;
  }

  private double m1Hight(int k) {
    double sum = 0;
    for (int i = L - 1; i >= k; i--) {
      sum += i * Pi[i];
    }
    return sum / P1Hight(k);
  }

  private double m2Hight(int k) {
    double sum = 0;
    for (int i = k - 1; i > 0; i--) {
      sum += i * Pi[i];
    }
    return sum / (1d - P1Hight(k));
  }

  private double P1Hight(int k) {
    double sum = 0;
    for (int i = L - 1; i >= k; i--) {
      sum += Pi[i];
    }
    return sum;
  }

  /**
   * end methods
   */

  private void doPi() {
    for (int i = 0; i < Pi.length; i++) {
      Pi[i] = (double) histogram[i] / N;
    }
  }

  public void createImage(int threshold) {
    int red;
    int newPixel;

    BufferedImage img = new BufferedImage(colors.length, colors[0].length, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < colors.length; i++) {
      for (int j = 0; j < colors[i].length; j++) {
        red = new Color(colors[i][j].getRGB()).getRed();
        if (red > threshold) {
          newPixel = 255;
        } else {
          newPixel = 0;
        }
        img.setRGB(i, j, new Color(newPixel, newPixel, newPixel).getRGB());
      }
    }

    createIMG(img);
  }

  public void createImage(int thresholdLow, int thresholdHight) {
    int red;
    int newPixel;

    BufferedImage img = new BufferedImage(colors.length, colors[0].length, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < colors.length; i++) {
      for (int j = 0; j < colors[i].length; j++) {
        red = new Color(colors[i][j].getRGB()).getRed();
        if (red < thresholdLow) {
          newPixel = 0;
        } else if (red > thresholdHight) {
          newPixel = 255;
        } else {
          newPixel = 175;
        }
        img.setRGB(i, j, new Color(newPixel, newPixel, newPixel).getRGB());
      }
    }

    createIMG(img);
  }

  private void createIMG(BufferedImage img) {
    File whiteBlackFile = new File("OTSUImage.jpg");
    try {
      ImageIO.write(img, "jpg", whiteBlackFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public double[] getPi() {
    return Pi;
  }

  public int[] getHistogram() {
    return histogram;
  }

  private void clear() {
    this.L = 256;
    this.histogram = new int[L];
    this.Pi = new double[L];
  }

}
