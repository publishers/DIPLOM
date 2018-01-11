package methods;


/**
 * @author nayef
 */
public class BernsenLocalThresholdService {

  public int[][] getBernsenLocalThreshold(int[][] grayScale2DImage, boolean removeGhosts) {

    int[][] fMax = this.getFMax(grayScale2DImage);
    int[][] fMin = this.getFMin(grayScale2DImage);
    int[][] g = this.getG(fMax, fMin);
    int[][] b = this.getB(grayScale2DImage, g, removeGhosts, fMax, fMin);
    return b;

  }

  private int[][] getFMax(int[][] grayScale2DImage) {

    int[][] fMax = new int[grayScale2DImage.length][grayScale2DImage[0].length];

    for (int y = 0; y < fMax.length; y++) {
      for (int x = 0; x < fMax[y].length; x++) {
        fMax[y][x] = getMaxNeighbour(grayScale2DImage, y, x);
      }
    }
    return fMax;
  }

  private int getMaxNeighbour(int[][] grayScale2DImage, int y, int x) {
    int max = -1;
    //upper left
    if (y - 1 >= 0 && x - 1 >= 0 && max < grayScale2DImage[y - 1][x - 1]) {
      max = grayScale2DImage[y - 1][x - 1];
    }
    //directly up
    if (y - 1 >= 0 && max < grayScale2DImage[y - 1][x]) {
      max = grayScale2DImage[y - 1][x];
    }
    //uppper right
    if (y - 1 >= 0 && x + 1 < grayScale2DImage[y].length && max < grayScale2DImage[y - 1][x + 1]) {
      max = grayScale2DImage[y - 1][x + 1];
    }

    //right
    if (x + 1 < grayScale2DImage[y].length && max < grayScale2DImage[y][x + 1]) {
      max = grayScale2DImage[y][x + 1];
    }

    //lower right
    if (y + 1 < grayScale2DImage.length && x + 1 < grayScale2DImage[y].length && max < grayScale2DImage[y + 1][x + 1]) {
      max = grayScale2DImage[y + 1][x + 1];
    }

    //below
    if (y + 1 < grayScale2DImage.length && max < grayScale2DImage[y + 1][x]) {
      max = grayScale2DImage[y + 1][x];
    }

    //lower left
    if (y + 1 < grayScale2DImage.length && x - 1 >= 0 && max < grayScale2DImage[y + 1][x - 1]) {
      max = grayScale2DImage[y + 1][x - 1];
    }
    //left
    if (x - 1 >= 0 && max < grayScale2DImage[y][x - 1]) {
      max = grayScale2DImage[y][x - 1];
    }
    return max;
  }

  private int getMinNeighbour(int[][] grayScale2DImage, int y, int x) {
    int min = 256 + 100;
    //upper left
    if (y - 1 >= 0 && x - 1 >= 0 && min > grayScale2DImage[y - 1][x - 1]) {
      min = grayScale2DImage[y - 1][x - 1];
    }

    //directly up
    if (y - 1 >= 0 && min > grayScale2DImage[y - 1][x]) {
      min = grayScale2DImage[y - 1][x];
    }

    //uppper right
    if (y - 1 >= 0 && x + 1 < grayScale2DImage[y].length && min > grayScale2DImage[y - 1][x + 1]) {
      min = grayScale2DImage[y - 1][x + 1];
    }

    //right
    if (x + 1 < grayScale2DImage[y].length && min > grayScale2DImage[y][x + 1]) {
      min = grayScale2DImage[y][x + 1];
    }

    //lower right
    if (y + 1 < grayScale2DImage.length && x + 1 < grayScale2DImage[y].length && min > grayScale2DImage[y + 1][x + 1]) {
      min = grayScale2DImage[y + 1][x + 1];
    }

    //below
    if (y + 1 < grayScale2DImage.length && min > grayScale2DImage[y + 1][x]) {
      min = grayScale2DImage[y + 1][x];
    }

    //lower left
    if (y + 1 < grayScale2DImage.length && x - 1 >= 0 && min > grayScale2DImage[y + 1][x - 1]) {
      min = grayScale2DImage[y + 1][x - 1];
    }

    //left
    if (x - 1 >= 0 && min > grayScale2DImage[y][x - 1]) {
      min = grayScale2DImage[y][x - 1];
    }

    return min;
  }

  private int[][] getFMin(int[][] grayScale2DImage) {
    int[][] fMin = new int[grayScale2DImage.length][grayScale2DImage[0].length];

    for (int y = 0; y < fMin.length; y++) {
      for (int x = 0; x < fMin[y].length; x++) {
        fMin[y][x] = getMinNeighbour(grayScale2DImage, y, x);
      }
    }

    return fMin;
  }

  private int[][] getG(int[][] fMax, int[][] fMin) {
    int[][] g = new int[fMax.length][fMax[0].length];

    for (int y = 0; y < fMax.length; y++) {
      for (int x = 0; x < fMax[y].length; x++) {
        g[y][x] = (fMax[y][x] + fMin[y][x]) / 2;
        //notice,fMax[y][x]+fMin[y][x]
      }
    }
    return g;

  }

  private int[][] getB(int[][] grayScale2DImage, int[][] g, boolean removeGhosts, int[][] fMax, int[][] fMin) {
    if (removeGhosts) {
      return getBWithGhostRemoval(grayScale2DImage, g, fMax, fMin);
    } else {
      return getBWithoutGhostRemoval(grayScale2DImage, g);
    }
  }

  private int[][] getBWithGhostRemoval(int[][] grayScale2DImage, int[][] g, int[][] fMax, int[][] fMin) {

    otsuThresholdingService thresholdingService = new otsuThresholdingService();
    int[][] b = new int[grayScale2DImage.length][grayScale2DImage[0].length];
    int c[][] = getC(fMax, fMin);

    int cStar = (int) thresholdingService.getOtsuThreshold(from2Dto1D(c));
    int fStar = (int) thresholdingService.getOtsuThreshold(from2Dto1D(g));

    for (int y = 0; y < grayScale2DImage.length; y++) {
      for (int x = 0; x < grayScale2DImage[y].length; x++) {
        if ((grayScale2DImage[y][x] < g[y][x] || c[y][x] > cStar)
            || (grayScale2DImage[y][x]) < fStar || c[y][x] <= cStar) {
          b[y][x] = 1;
        } else {
          b[y][x] = 0;
        }
      }
    }

    return b;

  }

  private int[][] getBWithoutGhostRemoval(int[][] grayScale2DImage, int[][] g) {

    int[][] b = new int[grayScale2DImage.length][grayScale2DImage[0].length];

    for (int y = 0; y < grayScale2DImage.length; y++) {
      for (int x = 0; x < grayScale2DImage[y].length; x++) {
        if (grayScale2DImage[y][x] < g[y][x]) {
          b[y][x] = 1;
        } else {
          b[y][x] = 0;
        }
      }
    }

    return b;
  }

  private int[] from2Dto1D(int[][] array) {

    int[] oneD = new int[array.length * array[0].length];
    int index = 0;
    for (int y = 0; y < array.length; y++) {
      for (int x = 0; x < array[y].length; x++) {
        oneD[index++] = array[y][x];
      }
    }

    return oneD;
  }

  private int[][] getC(int[][] fMax, int[][] fMin) {
    int[][] c = new int[fMax.length][fMax[0].length];

    for (int y = 0; y < fMax.length; y++) {
      for (int x = 0; x < fMax[y].length; x++) {
        c[y][x] = fMax[y][x] - fMin[y][x];
      }
    }

    return c;
  }
}