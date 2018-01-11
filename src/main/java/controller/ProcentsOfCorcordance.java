package controller;

import model.SpecialPoints;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by sergey on 3/3/16.
 */
public final class ProcentsOfCorcordance {
  public static int MINPROCENTS = 70;
  private static int MININNERPROCENT = 65;
  private static final int DISTANSE_OF_SCATTER = 25;
  private static ArrayList<SpecialPoints> db;

  /**
   * read db from file
   * matches looks like:  digX-digY;...;digX-digY|digX-digY;...;digX-digY|IMGName
   */
  public static void readDB() throws IOException {
    db = new ArrayList();/** Create db*/

    Stream<String> stream = Files.lines(Paths.get("db.txt"));
    stream.forEach(s -> {
      String[] pars = s.split("\\|");
      db.add(createSpecPoints(pars));
    });
  }

  /**
   * parsing string line from reading file
   */
  public static SpecialPoints createSpecPoints(String[] str) {
    SpecialPoints sp = new SpecialPoints();
    sp.setFName(str[2]);
    String[] adge = str[0].split(";");
    String[] ramific = str[1].split(";");
    parsString(sp, adge, 1);
    parsString(sp, ramific, 2);
    return sp;
  }

  private static SpecialPoints parsString(SpecialPoints sp, String[] adge, int mark) {
    for (int i = 0; i < adge.length; i++) {
      String[] dig = adge[i].split("\\-");
      if (mark == 1) {
        sp.addA((int) Double.parseDouble(dig[0]), (int) Double.parseDouble(dig[1]));
      } else if (mark == 2) {
        sp.addR((int) Double.parseDouble(dig[0]), (int) Double.parseDouble(dig[1]));
      }
    }
    return sp;
  }

  /**
   * matches between SpecialPoints
   */
  public static double getProcent(SpecialPoints sp, SpecialPoints sp2) {
    int proAdges = procents(sp.getAdges(), sp2.getAdges());
    int proRam = procents(sp.getRamifications(), sp2.getRamifications());
    System.out.println("getProcent : " + (proRam + " " + proAdges));
    if (proRam < MININNERPROCENT || proAdges < MININNERPROCENT) {
      return 0;
    }

    return (proRam + proAdges) / 2;
  }


  /**
   * count of procent ramification
   */
  public static int procents(ArrayList<Point> spPoints, ArrayList<Point> points) {
    int pro = 0;
    int stoPro = points.size();
    for (int i = 0; i < points.size(); i++) {
      for (int j = 0; j < spPoints.size(); j++) {
        if (dist(spPoints.get(j), points.get(i)) <= DISTANSE_OF_SCATTER) {
          pro++;
          break;
        }
      }
    }


    return (int) Math.round(((double) pro / stoPro) * 100);
  }

  /**
   * distance between both points
   */
  public static double dist(Point p1, Point p2) {
    return p1.distance(p2);
  }

  public static ArrayList<SpecialPoints> getDB() {
    try {
      readDB();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return db;
  }
}



