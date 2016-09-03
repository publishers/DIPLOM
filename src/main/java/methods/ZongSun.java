package methods;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by sergey on 2/7/16.
 */
public class ZongSun {
    private byte [][] colors;
    private byte [][] colors2;
    private File file;

    public ZongSun(File file) {
        this.file = file;
    }

    public void init() throws IOException {
        BufferedImage img = ImageIO.read(new File(file.getAbsolutePath()));
        colors = new byte[img.getWidth()][img.getHeight()];
        colors2 = new byte[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                if (i == 0 || j == 0 || i == colors.length-1 || j== colors[i].length){
                    colors[i][j] = 0;
                    continue;
                }
                if(new Color(img.getRGB(i,j)).getRed() <= 150){
                    colors[i][j] = 1;
                }else{
                    colors[i][j] = 0;
                }
            }
        }
    }

    public boolean start() {
        boolean b = false;
        for (int i = 1; i < colors.length-1; i ++) {
            for (int j = 1; j < colors[0].length-1; j ++) {
                if(colors[i][j] == 1)
                    if (goStep1(i, j)) {
                        b = true;
                        colors2[i][j] = 1;
                    }else {
                        colors2[i][j] = 0;
                    }
            }

        }

        reWrite();
        for (int i = 1; i < colors.length-1; i ++) {
            for (int j = 1; j < colors[0].length-1; j ++) {
                if(colors[i][j] == 1)
                    if (goStep2(i, j)) {
                        b = true;
                        colors2[i][j] = 1;
                    }else {
                        colors2[i][j] = 0;
                    }
            }
        }

        reWrite();
        return b;
    }

    private void reWrite(){
        for (int i = 1; i < colors.length-1; i ++) {
            for (int j = 1; j < colors[0].length-1; j ++) {
                if (colors2[i][j] == 1) {
                    colors[i][j] = 0;
                }
            }
        }
    }

    private boolean goStep1(int i, int j) {
        boolean b = false;
        int[] p = {
                colors[i - 1][j], colors[i - 1][j + 1],
                colors[i][j + 1], colors[i + 1][j + 1],
                colors[i + 1][j], colors[i + 1][j - 1],
                colors[i][j - 1], colors[i - 1][j - 1]
        };
        int sum = 0;
        for (int k = 0; k < p.length; k++) {
            sum += p[k];
        }
        int p1 = p[0]*p[2]*p[4];
        int p2 = p[2]*p[4]*p[6];
        if(2 <= sum && sum <= 6 &&
                S(p) == 1 &&
                p1 == 0 && p2 ==0)
        {
            b = true;
        }

        return b;
    }

    private boolean goStep2(int i, int j) {
        boolean b = false;
        int[] p = {
                colors[i - 1][j], colors[i - 1][j + 1],
                colors[i][j + 1], colors[i + 1][j + 1],
                colors[i + 1][j], colors[i + 1][j - 1],
                colors[i][j - 1], colors[i - 1][j - 1]
        };
        int sum = 0;
        for (int k = 0; k < p.length; k++) {
            sum += p[k];
        }
        int p1 = p[0]*p[2]*p[6];
        int p2 = p[0]*p[4]*p[6];
        if(2 <= sum && sum <= 6 &&
                S(p) == 1 &&
                p1 == 0 && p2 ==0)
        {
            b = true;
        }

        return b;
    }

    private int S(int[] p) {
        int sumS = 0;
        if( p[p.length - 1] < p[0])
            sumS ++;
        for (int i = 0; i < p.length - 1; i++) {
            if(p[i] < p[i+1])
                sumS ++;
        }
        return sumS;
    }

    public byte[][] getColors() {
        return colors;
    }
}