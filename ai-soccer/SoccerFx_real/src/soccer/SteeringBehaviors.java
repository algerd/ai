
package soccer;

import common.D2.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SteeringBehaviors {
    
    private enum BehaviorType {
        NONE(0x0000),
        SEEK(0x0001),
        ARRIVE(0x0002),
        PURSUIT(0x0004),
        INTERPOSE(0x0008);
        
        private int flag;
        BehaviorType(int flag) {
            this.flag = flag;
        }
        public int flag() {
            return this.flag;
        }
    }
    
    private PlayerBase player;
    private SoccerBall ball;
    private Vector acceleration = new Vector();
    /**
     * Определяет направление движения и направление поворота игрока.
     * !!!! target никогда не должен быть пустым, если нет точки движения(она уже была достигнута),
     * то туда заносить положение мяча !!!!!.
     */
    private Vector target = new Vector();
    //the dist the player tries to interpose from the target
    private double interposeDist;
    //binary flags to indicate whether or not a behavior should be active
    private int flag;
    
    public SteeringBehaviors(PlayerBase agent, SoccerPitch world, SoccerBall ball) {
        this.player = agent;
        this.ball = ball;
    }
    
    /**
     * calculates the overall steering force based on the currently active steering behaviors. 
     */
    public void calculateAcceleration() {
        acceleration.zero();
        if (on(BehaviorType.SEEK)) {
            accumulateAcceleration(seek());
        }
        if (on(BehaviorType.ARRIVE)) {
            accumulateAcceleration(arrive(Params.ARRIVE_DIST_DECELERATION, Params.ARRIVE_COEFF_REMAINING_VELOCITY));
        }
        if (on(BehaviorType.PURSUIT)) {
            accumulateAcceleration(pursuit());
        }       
        //??? target???
        if (on(BehaviorType.INTERPOSE)) {
            accumulateAcceleration(interpose(ball, target, interposeDist));
        }
    }
    
    private void accumulateAcceleration(Vector accel) {
        double remainingAceleration = player.getMaxAcceleration() - acceleration.length();       
        acceleration.add((accel.length() < remainingAceleration) ? accel : accel.normalizen().mul(remainingAceleration));                 
    }
    /**
     * Получить разницу между вектором желаемой скорости и вектором действительной скорости.
     * Величина желаемой скорости задаётся player.getMaxSpeed(), а направление - вектором от player до target.
     * @return (VtargetPos - VplayerPos)/length * maxSpeed - Vvelocity
     */
    private Vector seek() {      
        Vector desiredVelocity = target.subn(player.getPosition()).normalize().mul(player.getMaxSpeed());       
        Vector accel = desiredVelocity.sub(player.getVelocity());                      
        return accel;
    }
   
    /**
	 * Темп замедления будет определяться дистанцией до точки и максимальной скоростью игрока.
	 * Замедление происходит равномерно с дистанции distDeceleration до точки до скорости coeffRemainingVelocity * player.getMaxSpeed().
	 * @param distDeceleration - в общем случае равен ARRIVE_DIST_DECELERATION, но в разных игровых ситуациях может меняться(задаваться)
	 * @param coeffRemainingVelocity - в общем случае равен ARRIVE_COEFF_REMAINING_VELOCITY = 0.1, но в разных игровых ситуациях может меняться(задаваться)
	 */
	private Vector arrive(double distDeceleration, double coeffRemainingVelocity) {	
		Vector toTarget = target.subn(player.getPosition());
        Vector toTargetNormalize = toTarget.normalizen();
        
		double distToTarget = toTarget.length();
		double distDecelerationPlus = coeffRemainingVelocity * distDeceleration;
		
		// Условие достижения дистанции замедления
		if (distToTarget < distDeceleration) {
			double maxVelocity = player.getMaxSpeed() * (distToTarget + distDecelerationPlus)/(distDeceleration + distDecelerationPlus);
			// Условие замедления:
			if (player.getVelocity().length() > maxVelocity) {
                return toTargetNormalize.mul(maxVelocity).sub(player.getVelocity());
			}
		}	
		// в противном случе seek(target) - движение в точку с максимальной скоростью без замедления:	
		return toTargetNormalize.mul(player.getMaxSpeed()).sub(player.getVelocity());	
	}
      
    /**
     * This behavior predicts where its prey will be and seeks to that location.
     * This behavior creates a acceleration with that player steers towards the ball.
     */
    private Vector pursuit() {
        int timeTact = 0;
        Vector toBall = ball.getPosition().subn(player.getPosition());  
        double speed = ball.getVelocity().length();
        //calculate where the ball will be at this time in the future
        if (speed > 0) {
            timeTact = (int)(toBall.length() / speed);         
            target = ball.getFuturePosition(timeTact);
        }
        else {
            target = ball.getPosition();
        }
        return arrive(Params.ARRIVE_DIST_DECELERATION/2, Params.ARRIVE_COEFF_REMAINING_VELOCITY*2);
    }

  
    /**
     * Given an opponent and an object position this method returns a force that attempts to position the agent between them.
     */
    private Vector interpose(final SoccerBall ball, Vector target, double distFromTarget) {
        this.target = ball.getPosition().subn(target).normalize().mul(distFromTarget).add(target);      
        return arrive(Params.ARRIVE_DIST_DECELERATION, Params.ARRIVE_COEFF_REMAINING_VELOCITY);
    }  

    /**
     ************************** Behavior flags ****************************.
     */   
    /**
     * this function tests if a specific bit of m_iFlags is set.
     */
    private boolean on(BehaviorType bt) {
        return (flag & bt.flag()) == bt.flag();
    }
    
    public void seekOn() {
        flag |= BehaviorType.SEEK.flag();
    }

    public void arriveOn() {
        flag |= BehaviorType.ARRIVE.flag();
    }

    public void pursuitOn() {
        flag |= BehaviorType.PURSUIT.flag();
    }

    public void interposeOn(double d) {
        flag |= BehaviorType.INTERPOSE.flag();
        interposeDist = d;
    }

    public void seekOff() {
        if (on(BehaviorType.SEEK)) {
            flag ^= BehaviorType.SEEK.flag();
        }
    }

    public void arriveOff() {
        if (on(BehaviorType.ARRIVE)) {
            flag ^= BehaviorType.ARRIVE.flag();
        }
    }

    public void pursuitOff() {
        if (on(BehaviorType.PURSUIT)) {
            flag ^= BehaviorType.PURSUIT.flag();
        }
    }

    public void interposeOff() {
        if (on(BehaviorType.INTERPOSE)) {
            flag ^= BehaviorType.INTERPOSE.flag();
        }
    }

    public boolean seekIsOn() {
        return on(BehaviorType.SEEK);
    }

    public boolean arriveIsOn() {
        return on(BehaviorType.ARRIVE);
    }

    public boolean pursuitIsOn() {
        return on(BehaviorType.PURSUIT);
    }

    public boolean interposeIsOn() {
        return on(BehaviorType.INTERPOSE);
    }
    
    /**
     ************************ Getters & Setters *******************************.
     */
    
    public Vector getAcceleration() {
        return new Vector(acceleration);
    }
    public Vector getTarget() {
        return new Vector(target);
    }
    public void setTarget(final Vector t) {
        target = new Vector(t);
    }
    
    /**
     * renders visual aids and info for seeing how each behavior is calculated.
     * render the steering force
     */
    public void render(GraphicsContext gc) {         
        gc.setStroke(Color.RED);
        gc.strokeLine(player.getPosition().x, 
            player.getPosition().y, 
            acceleration.muln(20).addn(player.getPosition()).x,
            acceleration.muln(20).addn(player.getPosition()).y);
    }

}
