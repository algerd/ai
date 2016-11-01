
package common.misc;

public class StreamUtilityFunction {
    /**
     * convert a type to a string.
     */
    public static String ttos(final int t) {
        return ""+t;
    }
    
    public static <T extends Number> String ttos(final T t) {
        return ttos(t,2);
    }
    
    public static <T extends Number> String ttos(final T t, int precision) {
       if(precision == 1) return "" + t;
       double multipicationFactor = Math.pow(10, precision);
       double interestedInZeroDPs = t.doubleValue() * multipicationFactor;
       return "" + (Math.round(interestedInZeroDPs) / multipicationFactor);
    }

    /**
     *  convert a bool to a string
    */
    public static String btos(boolean b) {
      if (b) return "true";
      return "false";
    }

}
