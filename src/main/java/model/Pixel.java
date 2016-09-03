package model;


import java.awt.*;

/**
 * Created by Admin on 18.11.15.
 */
public class Pixel {
    private int X;
    private int Y;
    private Color color;
    public int MARKER = -1;
    public Color markerColor = new Color(0,0,0);

    public Pixel(){}

    public Pixel(int x, int y, Color color) {
        X = x;
        Y = y;
        this.color = color;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String toString(){
        return MARKER + " " + markerColor;
    }
}
