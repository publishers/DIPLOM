package methods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import javax.imageio.ImageIO;

public class Watershed {
    //JFrame frame;
    int g_w;
    int g_h;

//    public static void main(String[] args) {
////        if (args.length!=5) {
////            System.out.println("Usage: java popscan.Watershed"
////                            + " [source image filename]"
////                            + " [destination image filename]"
////                            + " [flood point count (1-256)]"
////                            + " [minimums window width (8-256)]"
////                            + " [connected pixels (4 or 8)]"
////            );
////            return;
////        }
//
//        int floodPoints = 255; //уровень заполнения водой 1 to 256
//        int windowWidth = 10;//это интенсивность заполнения соседних регионов
//
//        int connectedPixels = 8;//количество пикселей что будет объеденино
//
//        Watershed watershed = new Watershed();
//        String imageName = "kmeans.png";
//
//        BufferedImage dstImage = watershed.calculate(loadImage(imageName),
//                floodPoints,
//                windowWidth, connectedPixels);
//
//        // save the resulting image
//
//        dstImage = makeImage(imageName, dstImage);
//        saveImage("Water.png", dstImage);
//    }

    public static BufferedImage makeImage(String nameImage, BufferedImage buff) {
        BufferedImage tmp = loadImage(nameImage);
        int white = new Color(255, 255, 255).getRGB();
        for (int i = 0; i < tmp.getHeight(); i++) {
            for (int j = 0; j < tmp.getWidth(); j++) {
                if (buff.getRGB(j, i) == white) {
                    tmp.setRGB(j, i, white);
                }
            }
        }
        return tmp;
    }

    public BufferedImage calculate(BufferedImage image,
                                   int floodPoints,
                                   int windowWidth,
                                   int connectedPixels) {

        /*
        // frame for real time view for the process
        frame = new JFrame();
        frame.setSize(image.getWidth(),image.getHeight());
        frame.setVisible(true);
        */

        g_w = image.getWidth();
        g_h = image.getHeight();
        // height map is the gray color image
        int[] map = image.getRGB(0, 0, g_w, g_h, null, 0, g_w);
        // LUT is the lookup table for the processed pixels
        int[] lut = new int[g_w * g_h];
        // fill LUT with ones
        Arrays.fill(lut, 1);
        Vector<FloodPoint> minimums = new Vector<FloodPoint>();
        // loop all the pixels of the image until
        // a) all the required minimums have been found
        // OR
        // b) there are no more unprocessed pixels left
        int foundMinimums = 0;
        while (foundMinimums < floodPoints) {
            int minimumValue = 256;
            int minimumPosition = -1;
            for (int i = 0; i < lut.length; i++) {
                if ((lut[i] == 1) && (map[i] < minimumValue)) {
                    minimumPosition = i;
                    minimumValue = map[i];
                }
            }
            // check if minimum was found
            if (minimumPosition != -1) {
                // add minimum to found minimum vector
                int x = minimumPosition % g_w;
                int y = minimumPosition / g_w;
                int grey = map[x + g_w * y] & 0xff;
                int label = foundMinimums;
                minimums.add(new FloodPoint(x, y,
                        label, grey));
                // remove pixels around so that the next minimum
                // must be at least windowWidth/2 distance from
                // this minimum (using square, could be circle...)
                int half = windowWidth / 2;
                fill(x - half, y - half, x + half, y + half, lut, 0);
                lut[minimumPosition] = 0;
                foundMinimums++;
            } else {
                // stop while loop
                System.out.println("Out of pixels. Found "
                        + minimums.size()
                        + " minimums of requested "
                        + floodPoints + ".");
                break;
            }
        }

        // create image with minimums only
//        for (int i=0;i<minimums.size();i++) {
//            FloodPoint p = minimums.elementAt(i);
//            Graphics g = image.getGraphics();
//            g.setColor(Color.red);
//            g.drawRect(p.x, p.y, 2, 2);
//        }
//        saveImage("minimums.png", image);


        // start flooding from minimums
        lut = flood(map, minimums, connectedPixels);

        // return flooded image
        image.setRGB(0, 0, g_w, g_h, lut, 0, g_w);
        // create image with boundaries also
//        for (int i=0;i<minimums.size();i++) {
//            FloodPoint p = minimums.elementAt(i);
//            Graphics g = image.getGraphics();
//            g.setColor(Color.red);
//            g.drawRect(p.x, p.y, 2, 2);
//        }


        saveImage("minimums_and_boundaries.png", image);
        return image;
    }

    private int[] flood(int[] map, Vector<FloodPoint> minimums,
                        int connectedPixels) {
        SortedVector queue = new SortedVector();
        //BufferedImage result = new BufferedImage(g_w, g_h,
        //        BufferedImage.TYPE_INT_RGB);
        int[] lut = new int[g_w * g_h];
        int[] inqueue = new int[g_w * g_h];
        // not processed = -1, processed >= 0
        Arrays.fill(lut, -1);
        Arrays.fill(inqueue, 0);
        // Initialize queue with each found minimum
        for (int i = 0; i < minimums.size(); i++) {
            FloodPoint p = minimums.elementAt(i);
            int label = p.label;
            // insert starting pixels around minimums
            addPoint(queue, inqueue, map, p.x, p.y - 1, label);
            addPoint(queue, inqueue, map, p.x + 1, p.y, label);
            addPoint(queue, inqueue, map, p.x, p.y + 1, label);
            addPoint(queue, inqueue, map, p.x - 1, p.y, label);
            if (connectedPixels == 8) {
                addPoint(queue, inqueue, map, p.x - 1, p.y - 1, label);
                addPoint(queue, inqueue, map, p.x + 1, p.y - 1, label);
                addPoint(queue, inqueue, map, p.x + 1, p.y + 1, label);
                addPoint(queue, inqueue, map, p.x - 1, p.y + 1, label);
            }
            int pos = p.x + p.y * g_w;
            lut[pos] = label;
            inqueue[pos] = 1;
        }

        // start flooding
        while (queue.size() > 0) {
            // find minimum
            FloodPoint extracted = null;
            // remove the minimum from the queue
            extracted = (FloodPoint) queue.remove(0);
            int x = extracted.x;
            int y = extracted.y;
            int label = extracted.label;
            // check pixels around extracted pixel
            int[] labels = new int[connectedPixels];
            labels[0] = getLabel(lut, x, y - 1);
            labels[1] = getLabel(lut, x + 1, y);
            labels[2] = getLabel(lut, x, y + 1);
            labels[3] = getLabel(lut, x - 1, y);
            if (connectedPixels == 8) {
                labels[4] = getLabel(lut, x - 1, y - 1);
                labels[5] = getLabel(lut, x + 1, y - 1);
                labels[6] = getLabel(lut, x + 1, y + 1);
                labels[7] = getLabel(lut, x - 1, y + 1);
            }
            boolean onEdge = isEdge(labels, extracted);
            if (onEdge) {
                // leave edges without label
            } else {
                // set pixel with label
                lut[x + g_w * y] = extracted.label;
            }
            if (!inQueue(inqueue, x, y - 1)) {
                addPoint(queue, inqueue, map, x, y - 1, label);
            }
            if (!inQueue(inqueue, x + 1, y)) {
                addPoint(queue, inqueue, map, x + 1, y, label);
            }
            if (!inQueue(inqueue, x, y + 1)) {
                addPoint(queue, inqueue, map, x, y + 1, label);
            }
            if (!inQueue(inqueue, x - 1, y)) {
                addPoint(queue, inqueue, map, x - 1, y, label);
            }
            if (connectedPixels == 8) {
                if (!inQueue(inqueue, x - 1, y - 1)) {
                    addPoint(queue, inqueue, map, x - 1, y - 1, label);
                }
                if (!inQueue(inqueue, x + 1, y - 1)) {
                    addPoint(queue, inqueue, map, x + 1, y - 1, label);
                }
                if (!inQueue(inqueue, x + 1, y + 1)) {
                    addPoint(queue, inqueue, map, x + 1, y + 1, label);
                }
                if (!inQueue(inqueue, x - 1, y + 1)) {
                    addPoint(queue, inqueue, map, x - 1, y + 1, label);
                }
            }
            // draw the current pixel set to frame, WARNING: slow...
            //result.setRGB(0, 0, g_w, g_h, lut, 0, g_w);
            //frame.getGraphics().drawImage(result,
            //     0, 0, g_w, g_h, null);
        }
        return lut;
    }

    private boolean inQueue(int[] inqueue, int x, int y) {
        if (x < 0 || x >= g_w || y < 0 || y >= g_h) {
            return false;
        }
        if (inqueue[x + g_w * y] == 1) {
            return true;
        }
        return false;
    }

    private boolean isEdge(int[] labels, FloodPoint extracted) {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != extracted.label && labels[i] != -1) {
                return true;
            }
        }
        return false;
    }

    private int getLabel(int[] lut, int x, int y) {
        if (x < 0 || x >= g_w || y < 0 || y >= g_h) {
            return -2;
        }
        return lut[x + g_w * y];
    }

    private void addPoint(SortedVector queue,
                          int[] inqueue, int[] map,
                          int x, int y, int label) {
        if (x < 0 || x >= g_w || y < 0 || y >= g_h) {
            return;
        }
        queue.add(new FloodPoint(x, y, label, map[x + g_w * y] & 0xff));
        inqueue[x + g_w * y] = 1;
    }

    private void fill(int x1, int y1, int x2, int y2,
                      int[] array, int value) {
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                // clip to boundaries
                if (y >= 0 && x >= 0 && y < g_h && x < g_w) {
                    array[x + g_w * y] = value;
                }
            }
        }
    }

    public static void saveImage(String filename, BufferedImage image) {
        File file = new File(filename);
        try {
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            System.out.println(e.toString() + " Image '" + filename
                    + "' saving failed.");
        }
    }

    public static BufferedImage loadImage(String filename) {
        BufferedImage result = null;
        try {
            result = ImageIO.read(new File(filename));
        } catch (Exception e) {
            System.out.println(e.toString() + " Image '"
                    + filename + "' not found.");
        }
        return result;
    }

    class FloodPoint implements Comparable<Object> {
        int x;
        int y;
        int label;
        int grey;

        public FloodPoint(int x, int y, int label, int grey) {
            this.x = x;
            this.y = y;
            this.label = label;
            this.grey = grey;
        }

        @Override
        public int compareTo(Object o) {
            FloodPoint other = (FloodPoint) o;
            if (this.grey < other.grey) {
                return -1;
            } else if (this.grey > other.grey) {
                return 1;
            }
            return 0;
        }
    }

}