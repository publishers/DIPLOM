package model;

import java.awt.Point;
import java.util.TreeSet;
import java.util.Set;

/**
 * Created by sergey on 10.04.16.
 */
public class CenterOfClassters {

    private Set<Point> centersOfClassters;

    public void init(){
        centersOfClassters = new TreeSet<Point>();
    }

    public Set<Point> getCentersOfClassters() {
        return centersOfClassters;
    }

    public void setCentersOfClassters(Set<Point> centersOfClassters) {
        this.centersOfClassters = centersOfClassters;
    }

    public boolean isClasstersContainPoint(Point p){
        return centersOfClassters.contains(p);
    }

    public void addPoint(Point p){
        centersOfClassters.add(p);
    }

}
