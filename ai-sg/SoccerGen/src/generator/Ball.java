
package generator;

import java.util.List;
import java.util.ArrayList;
import static java.lang.Math.*;
import utils.Vector;
import utils.Vector3D;

public class Ball implements Updatable {
    
    public static final double G = 9.81;
    private final Vector3D startPos = new Vector3D();
    private final Vector3D prevPos = new Vector3D();
    private final Vector3D pos = new Vector3D();
    private final Vector3D velocity = new Vector3D();
    private final List<Vector3D> positionList = new ArrayList<>();
    
    public Ball() {
        startPos.set(Field.LENGTH_FIELD_CENTER, Field.WIDTH_FIELD_CENTER, 0.0);
        pos.set(startPos);
        prevPos.set(pos);
    }
    
    public void pass(Vector3D target, double angle) { 
        Vector toTargetXY = target.getVectorXY().subn(pos.getVectorXY());
        Vector toTargetXYNorm = toTargetXY.normalizen();
        double distXY = toTargetXY.length();      
        double velocityLength = distXY / cos(angle) * sqrt(G / (2 *(pos.z - target.z + distXY * tan(angle))));
        double cosAngle = cos(angle);
        velocity.set(new Vector3D(
            toTargetXYNorm.x * cosAngle,
            toTargetXYNorm.y * cosAngle,    
            sin(angle)     
        ).mul(velocityLength));
        pos.z += 0.01;  // ball is in the air
    } 
    
    @Override
    public void update() { 
        //System.out.println("Velocity: "+velocity);
        //System.out.println("Position: "+pos);      
        if (velocity.isZero()) {
            positionList.add(null);
            return;
        }      
        prevPos.set(pos);
        // ball is in the air
        if (pos.z > 0) {
            velocity.z -= G*Params.TIME_TACT;                
            pos.add(velocity.muln(Params.TIME_TACT));
            
            if (pos.z < 0) {      
                velocity.mul(Params.BALL_DOWN_SPEED_KICK_GROUND);
                velocity.z = -velocity.z;
                // отскок мяча после удара об землю
                if (velocity.z > Params.BALL_VELOCITY_Z_MIN) {                  
                    pos.z = 0.01;      
                } 
                // мяч опускается на землю и катится
                else {
                    pos.z = 0;         
                    velocity.z = 0;
                } 
            }
        }
        // ball is on the ground
        else {
            double velocityXYLength = velocity.getVectorXY().length() - Params.BALL_DECELERATION_TACT; 
            if (velocityXYLength < 0) {
                velocity.zero();
            }
            else {
                Vector velocityXYNorm = velocity.getVectorXY().normalize();          
                velocity.set(new Vector3D(
                    velocityXYNorm.x,
                    velocityXYNorm.y, 
                    0.0
                ).mul(velocityXYLength));
                pos.add(velocity.muln(Params.TIME_TACT));
            }          
        }            
        positionList.add(pos);            
    }

    /**************************** Getters & Setters. ******************************/

    public Vector3D getPos() {
        return pos;
    }
         
    public Vector3D getPrevPos() {
        return new Vector3D(prevPos);
    }

    public Vector3D getStartPos() {
        return startPos;
    }       

    public List<Vector3D> getPositionList() {
        return positionList;
    }
         
}
