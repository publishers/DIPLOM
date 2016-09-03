import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Admin on 26.11.15.
 */


public class CopyColors {
    public static void main(String[] args){
        double h = 0.25;
        double sigma = 1;
        double q=Math.sin(Math.PI*0.25);
        double w=Math.sin(Math.PI*0.5);
        double e=Math.sin(Math.PI*0.75);
        double z = sigma/(2*h*h);
        double k = 1 + sigma/(h*h);
        double j = 1 - sigma/(h*h);
        double t = e/k;

       double u1 = (k * k * q + e * z * z - q * z * z + k * w * z) /
               (k * k - 2 * k * z * z);
        double u2 = (k * w + e * z + q * z) /
                (k * k - 2 * z * z);
        double u3 = (e * k * k - e * z * z + q * z * z + k * w * z) /
                (k * k - 2 * k * z * z);

        double tmpU1 = 1/k*(z*u2+q);

        System.out.println(u1 + "     "+u2 + "    "+u3);
        System.out.println(z/k+" " +j/k);
    }

}

