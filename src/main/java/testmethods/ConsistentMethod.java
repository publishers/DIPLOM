package testmethods;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
* Created by sergey on 15.05.16.
*/
public class ConsistentMethod {
    private ArrayList<Region> regions = new ArrayList<Region>();
    private int Qregions = 50;
    private int Qpixels = 20;

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

    public void calculation(BufferedImage img){

        Region r = new Region();
        r.arrPixels.add(new Pixel(0, 0, img.getRGB(0,0)));
        regions.add(r);

        System.gc();
        for (int i = 1; i < img.getHeight(); i++) {
            for (int j = 1; j < img.getWidth(); j++) {
                if(
                        (
                                brightness(new Color(img.getRGB(j,i)))
                                        - brightness(new Color(img.getRGB(j - 1,i)))
                ) > Qpixels){
                    r = new Region();
                    r.arrPixels.add(new Pixel(j, i, img.getRGB(j, i)));
                    regions.add(r);
                }else{

                }
            }
        }
        System.gc();

        mergerOfRegions();
    }

    void mergerOfRegions(){

    }

    private double brightness(Color c){
        return 0.59*c.getGreen() + 0.3*c.getRed()+ 0.11*c.getBlue();
    }

//    private void createArrPix(BufferedImage img){
//        for (int i = 0; i < pixels.length; i++) {
//            for (int j = 0; j < pixels[0].length; j++) {
//                Pixel p = new Pixel();
//                p.setX(j);
//                p.setY(i);
//                p.setColor(new Color(img.getRGB(j, i)));
//                pixels[i][j] = p;
//            }
//        }
//    }

    public static void main(String[] args) {
        ConsistentMethod cm = new ConsistentMethod();
        cm.calculation(cm.loadImage("kmeans.png"));
    }
}


