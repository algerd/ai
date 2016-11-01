
package render.renderfx;

import javafx.scene.paint.Color;

public class ParamsRenderFX {
    
    public static final double SCALE_ANIMATION = 7.0;
    
    public static final Color COLOR_BALL = Color.BLACK;
    public static final double RADIUS_BALL_MAX = 6.0;   // максимальный радиус мяча на высоте HEIGHT_BALL_MAX
    public static final double RADIUS_BALL_MIN = 2.0;   // минимальный радиус мяча на высоте 0 м 
    public static final double HEIGHT_BALL_MAX = 12;    // условная максимальная высота подъёма мяча, метров        
        
    //these values control what debug info you can see
    final public static boolean RENDER_STATE = true;
    final public static boolean RENDER_ID = true;
       
}
