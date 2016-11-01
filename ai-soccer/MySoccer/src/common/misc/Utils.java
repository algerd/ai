
package common.misc;

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
     * returns true if the value is a NaN
     */
    public static <T> boolean isNaN(T val) {
        return !(val != null);
    }

    static public double degsToRads(double degs) {
        return TwoPi * (degs / 360.0);
    }

    static public boolean isEqual(float a, float b) {
        if (Math.abs(a - b) < 1E-12) {
            return true;
        }
        return false;
    }
    //----------------------------------------------------------------------------
    //  some random number functions.
    //----------------------------------------------------------------------------
    static public void setSeed(long seed) {
        rand.setSeed(seed);
    }

    //returns a random integer between x and y
    static public int randInt(int x, int y) {
        assert y >= x : "<RandInt>: y is less than x";
        return rand.nextInt(Integer.MAX_VALUE - x) % (y - x + 1) + x;
    }

    //returns a random double between zero and 1
    static public double randFloat() {
        return rand.nextDouble();
    }

    static public double randInRange(double x, double y) {
        return x + randFloat() * (y - x);
    }

    //returns a random bool
    static public boolean randBool() {
        if (randFloat() > 0.5) {
            return true;
        } else {
            return false;
        }
    }

    //returns a random double in the range -1 < n < 1
    static public double randomClamped() {
        return randFloat() - randFloat();
    }

    //returns a random number with a normal distribution. See method at
    //http://www.taygeta.com/random/gaussian.html
    static public double randGaussian() {
        return randGaussian(0, 1);
    }
    
    static public double randGaussian(double mean, double standard_deviation) {
        double x1, x2, w, y1;
        if (use_last) /* use value from previous call */ {
            y1 = y2;
            use_last = false;
        } else {
            do {
                x1 = 2.0 * randFloat() - 1.0;
                x2 = 2.0 * randFloat() - 1.0;
                w = x1 * x1 + x2 * x2;
            } while (w >= 1.0);

            w = Math.sqrt((-2.0 * Math.log(w)) / w);
            y1 = x1 * w;
            y2 = x2 * w;
            use_last = true;
        }
        return (mean + y1 * standard_deviation);
    }

    //-----------------------------------------------------------------------  
    //  some handy little functions
    //-----------------------------------------------------------------------
    public static double sigmoid(double input) {
        return sigmoid(input, 1.0);
    }

    public static double sigmoid(double input, double response) {
        return (1.0 / (1.0 + Math.exp(-input / response)));
    }

    //returns the maximum of two values
    public static <T extends Comparable> T maxOf(T a, T b) {
        if (a.compareTo(b) > 0) {
            return a;
        }
        return b;
    }

    //returns the minimum of two values
    public static <T extends Comparable> T minOf(T a, T b) {
        if (a.compareTo(b) < 0) {
            return a;
        }
        return b;
    }

    /** 
     * clamps the first argument between the second two
     */
    public static <T extends Number> T clamp(final T arg, final T minVal, final T maxVal) {
        assert (minVal.doubleValue() < maxVal.doubleValue()) : "<Clamp>MaxVal < MinVal!";
        if (arg.doubleValue() < minVal.doubleValue()) {
            return  minVal;
        }

        if (arg.doubleValue() > maxVal.doubleValue()) {
            return maxVal;
        }
        return arg;
    }

    static public boolean isEqual(double a, double b) {
        if (Math.abs(a - b) < 1E-12) {
            return true;
        }
        return false;
    }
    
}
