package model;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sergey on 2/18/16.
 */
public class SpecialPoints implements Serializable {
    private String FName;
    private ArrayList<Point> Adges;
    private ArrayList<Point> Ramifications;
    private byte[][] colors;


    public SpecialPoints(byte[][] colors) {

        this.Adges = new ArrayList<>();
        this.Ramifications = new ArrayList<>();
        this.colors = colors;
        createAS();
    }

    public SpecialPoints() {
        this.Adges = new ArrayList<>();
        this.Ramifications = new ArrayList<>();
    }

    private void createAS(){

        for (int i = 1; i < colors.length-1; i++) {
            for (int j = 1; j < colors[i].length-1; j++) {
                if(colors[i][j] == 1){
                    if( sumPointsAround(i,j) <= 1){
                        addA(i, j);
                    }
                    if(S(i, j) == 3 || S(i, j) == 4){
                        addR(i, j);
                    }
                }
            }
        }
    }

    private int S(int i, int j) {
        int[] p = {
                colors[i - 1][j], colors[i - 1][j + 1],
                colors[i][j + 1], colors[i + 1][j + 1],
                colors[i + 1][j], colors[i + 1][j - 1],
                colors[i][j - 1], colors[i - 1][j - 1]
        };
        int sumS = 0;
        if( p[p.length - 1] < p[0])
            sumS ++;
        for (int t = 0; t < p.length - 1; t++) {
            if(p[t] < p[t+1])
                sumS ++;
        }
        return sumS;
    }

    private int sumPointsAround(int i, int j) {
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
        return sum;
    }

    public void addR(int i, int j){
        Ramifications.add(new Point(i, j));
    }

    public void addA(int i, int j){
        Adges.add(new Point(i, j));
    }

    public ArrayList<Point> getAdges() {
        return Adges;
    }

    public ArrayList<Point> getRamifications() {
        return Ramifications;
    }

    public void setAdges(ArrayList<Point> adges) {
        Adges = adges;
    }

    public void setRamifications(ArrayList<Point> ramifications) {
        Ramifications = ramifications;
    }

    public byte[][] getColors() {
        return colors;
    }

    public void setColors(byte[][] colors) {
        this.colors = colors;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        makeString(Adges, sb);
        sb.append("|");
        makeString(Ramifications, sb);
        sb.append("|");
        sb.append(FName);
        return sb.toString();
    }

    private void makeString(ArrayList<Point> al, StringBuilder sb){
        al.forEach(p ->
                        sb.append(p.getX() + "-" + p.getY() + ";")
        );
        sb.deleteCharAt(sb.length() - 1);
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }
}