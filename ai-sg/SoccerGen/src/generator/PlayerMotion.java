
package generator;

import utils.Vector;

public class PlayerMotion {
    
    public enum MotionType {
        NONE,
        RUN;
    }
    
    private Player player;
    private Vector acceleration = new Vector();
    private Vector target = new Vector();
    private double speed;
    private MotionType flag;
    
    public PlayerMotion(Player player) {
        this.player = player;
        speed = player.getMaxSpeed();
    }
       
    private Vector run() { 
         
        Vector desiredVelocity = target.subn(player.getPosition()).normalize().mul(speed);       
        Vector accel = desiredVelocity.sub(player.getVelocity());                      
        return accel;
    }
    
    private void restrictAcceleration(Vector accel) {       
        double remainingAceleration = player.getMaxAcceleration() - acceleration.length();       
        acceleration.add((accel.length() < remainingAceleration) ? accel : accel.normalizen().mul(remainingAceleration));                 
    }
    
    public void calculateAcceleration() {
        acceleration.zero();
        switch(flag) {
            case RUN : 
                restrictAcceleration(run());
                break;                
        }
    }

    /************************** Getters & Setters ********************************/
    
    public Vector getAcceleration() {
        return new Vector(acceleration);
    }
       
    public void setTarget(final Vector t) {
        target.set(t);
    }

    public Vector getTarget() {
        return target;
    }
       
    public void setFlag(MotionType flag) {
        this.flag = flag;
    }
    
}
