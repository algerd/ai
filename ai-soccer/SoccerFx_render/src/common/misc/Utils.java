
package common.misc;

import java.util.Random;

final public class Utils {
    
    static private Random rand = new Random();
    static private double y2 = 0;
    static private boolean useLast = false;

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
        return randFloat() > 0.5;     
    }

    //returns a random double in the range -1 < n < 1
    static public double randomClamped() {
        return randFloat() - randFloat();
    }

    //returns a random number with a normal distribution. See method at http://www.taygeta.com/random/gaussian.html
    static public double randGaussian() {
        return randGaussian(0, 1);
    }
    
    static public double randGaussian(double mean, double standard_deviation) {
        double x1, x2, w, y1;
        if (useLast) /* use value from previous call */ {
            y1 = y2;
            useLast = false;
        } else {
            do {
                x1 = 2.0 * randFloat() - 1.0;
                x2 = 2.0 * randFloat() - 1.0;
                w = x1 * x1 + x2 * x2;
            } while (w >= 1.0);

            w = Math.sqrt((-2.0 * Math.log(w)) / w);
            y1 = x1 * w;
            y2 = x2 * w;
            useLast = true;
        }
        return (mean + y1 * standard_deviation);
    }

    /** 
     * clamps the first argument between the second two.
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
        return Math.abs(a - b) < 1E-12;
    }
    
}
