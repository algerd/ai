
package common;

import java.awt.Point;
import java.awt.geom.Point2D;

final public class Windows {

    static public class POINT extends Point2D.Double {

        @Override
        public void setLocation(double x, double y) {
            super.setLocation(Math.round(x), Math.round(y));
        }
    }

    static public class POINTS extends Point {

        public POINTS() {
            this(0, 0);
        }

        public POINTS(int x, int y) {
            super(x, y);
        }

        public POINTS(Point point) {
            super(point);
        }
    }
 
}
