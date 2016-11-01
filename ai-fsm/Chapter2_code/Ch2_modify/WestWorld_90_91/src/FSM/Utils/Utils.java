
package FSM.Utils;

import java.util.Random;

final public class Utils {
    static public final int MaxInt = Integer.MAX_VALUE;
    static public final double MaxDouble = Double.MAX_VALUE;
    static public final double MinDouble = Double.MIN_VALUE;
    static public final float MaxFloat = Float.MAX_VALUE;
    static public final float MinFloat = Float.MIN_VALUE;
    static public final double Pi = Math.PI;
    static public final double TwoPi = Math.PI * 2;
    static public final double HalfPi = Math.PI / 2;
    static public final double QuarterPi = Math.PI / 4;
    static public final double EpsilonDouble = Double.MIN_NORMAL;
    
    static private Random rand = new Random();
    static private double y2 = 0;
    static private boolean use_last = false;

    /**
     * returns true if the value is a NaN.
     */
    public static <T> boolean isNaN(T val) {
        return !(val != null);
    }

    static public double DegsToRads(double degs) {
        return TwoPi * (degs / 360.0);
    }

    /**
     * compares two real numbers. 
     * Returns true if they are equal
     */
    static public boolean isEqual(float a, float b) {
        if (Math.abs(a - b) < 1E-12) {
            return true;
        }

        return false;
    }

    /**
     * returns a random integer between x and y.
     */
    static public int RandInt(int x, int y) {
        return rand.nextInt() % (y - x + 1) + x;
    }

    /**
     * returns a random double between zero and 1.
     */ 
    static public double RandFloat() {
        return rand.nextDouble();
    }

    static public double RandInRange(double x, double y) {
        return x + RandFloat() * (y - x);
    }

    static public boolean RandBool() {
        return RandFloat() > 0.5;
    }

    /**
     * returns a random double in the range -1 < n < 1.
     */
    static public double RandomClamped() {
        return RandFloat() - RandFloat();
    }

    /**
     * returns a random number with a normal distribution. 
     * See method at http://www.taygeta.com/random/gaussian.html
     */
    static public double RandGaussian() {
        return RandGaussian(0, 1);
    }
    
    static public double RandGaussian(double mean, double standard_deviation) {
        double x1, x2, w, y1;
        if (use_last) {
            y1 = y2;
            use_last = false;
        } 
        else {
            do {
                x1 = 2.0 * RandFloat() - 1.0;
                x2 = 2.0 * RandFloat() - 1.0;
                w = x1 * x1 + x2 * x2;
            } 
            while (w >= 1.0);

            w = Math.sqrt((-2.0 * Math.log(w)) / w);
            y1 = x1 * w;
            y2 = x2 * w;
            use_last = true;
        }
        return (mean + y1 * standard_deviation);
    }

    static public boolean isEqual(double a, double b) {
        return Math.abs(a - b) < (1E-12) ;
    }
    
}
