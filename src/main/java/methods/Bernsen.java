package methods;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Admin on 25.11.15.
 */
public class Bernsen {
  private Color[][] colors;
  public static int eps = 15;
  public static int r = 15;

  public Bernsen(Color[][] colors) {
    this.colors = colors.clone();
  }

  public Bernsen() throws IOException {
    setColors();
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
    for (int i = r; i < colors.length; i = (i + r * 2)) {
      for (int j = r; j < colors[0].length; j = (j + r * 2)) {
        t(i, j);
      }
    }

  }

  private void t(int i, int j) {

    int low = colors[i][j].getBlue();
    int hight = colors[i][j].getBlue();

    for (int k = i - r > 0 ? i - r : 0; k <= i + r; k++) {
      for (int l = j - r > 0 ? j - r : 0; l <= j + r; l++) {
        if (l >= colors[0].length || k >= colors.length) {
          continue;
        }
        if (colors[k][l].getBlue() > hight) {
          hight = colors[k][l].getBlue();
        }
        if (colors[k][l].getBlue() < low) {
          low = colors[k][l].getBlue();
        }
      }
    }

    int threshold = (low + hight) / 2;
    int wNew = 0;
    int bNew = 255;
    for (int k = i - r; k < i + r; k++) {
      for (int l = j - r; l < j + r; l++) {
        if (l >= colors[0].length || k >= colors.length) {
          continue;
        }
        if (Math.abs(hight - low) < eps) {
          colors[k][l] = new Color(bNew, bNew, bNew);
        } else if (colors[k][l].getBlue() > threshold) {
          colors[k][l] = new Color(bNew, bNew, bNew);
        } else {
          colors[k][l] = new Color(wNew, wNew, wNew);
        }
      }
    }
  }

  public void createImage() {
    BufferedImage img = new BufferedImage(colors.length, colors[0].length, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < colors.length; i++) {
      for (int j = 0; j < colors[i].length; j++) {
        img.setRGB(i, j, colors[i][j].getRGB());
      }
    }
    File fileIMAGE = new File("BERNSENImage.jpg");
    try {
      ImageIO.write(img, "jpg", fileIMAGE);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Color[][] getColors() {
    return colors;
  }

  public void setColors(Color[][] colors) {
    this.colors = colors;
  }

  public int getEps() {
    return eps;
  }

  public void setEps(int eps) {
    this.eps = eps;
  }

  public int getR() {
    return r;
  }

  public void setR(int r) {
    this.r = r;
  }
}
