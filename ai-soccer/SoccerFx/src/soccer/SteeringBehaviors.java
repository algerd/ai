
package soccer;

import java.util.List;
import common.D2.Vector;
import java.lang.reflect.Array;
import java.util.Arrays;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SteeringBehaviors {
    
    private enum BehaviorType {
        NONE(0x0000),
        SEEK(0x0001),
        ARRIVE(0x0002),
        SEPARATION(0x0004),
        PURSUIT(0x0008),
        INTERPOSE(0x0010);
        
        private int flag;
        BehaviorType(int flag) {
            this.flag = flag;
        }
        public int flag() {
            return this.flag;
        }
    }
    
    private enum DecelerationType {
        SLOW(3), NORMAL(2), FAST(1);
        private int dec;

        DecelerationType(int d) {
            this.dec = d;
        }
        public int value() {
            return dec;
        }
    }

    private PlayerBase player;
    private SoccerBall ball;
    private Vector steeringForce = new Vector();
    //the current target (usually the ball or predicted ball position)
    private Vector target = new Vector();
    //the dist the player tries to interpose from the target
    private double interposeDist;
    //multipliers. 
    private double multSeparation;
    //how far it can 'see'
    private double viewDistance;
    //binary flags to indicate whether or not a behavior should be active
    private int flag;
    //used by group behaviors to tag neighbours
    private boolean tagged;
    //a vertex buffer to contain the feelers rqd for dribbling
    private List<Vector> antenna;
    
    public SteeringBehaviors(PlayerBase agent, SoccerPitch world, SoccerBall ball) {
        this.player = agent;
        this.multSeparation = ParamLoader.getInstance().SeparationCoefficient;
        this.viewDistance = ParamLoader.getInstance().ViewDistance;
        this.ball = ball;
        this.antenna = Arrays.asList((Vector[]) Array.newInstance(Vector.class, 5));
    }
    
    /**
     * calculates the overall steering force based on the currently active steering behaviors. 
     */
    public Vector calculate() {
        //reset the force
        steeringForce.zero();
        sumForces();
        //make sure the force doesn't exceed the vehicles maximum allowable
        steeringForce.truncate(player.getMaxForce());
        return new Vector(steeringForce);
    }

    /**
     * this method calls each active steering behavior and acumulates their
     * forces until the max steering force magnitude is reached at which
     * time the function returns the steering force accumulated to that point.
     */
    private void sumForces() {
        Vector force = new Vector();

        //the soccer players must always tag their neighbors
        findNeighbours();

        if (on(BehaviorType.SEPARATION)) {
            force.add(separation().muln(multSeparation));
            if (!AccumulateForce(force)) {
                return;
            }
        }
        if (on(BehaviorType.SEEK)) {
            force.add(seek());
            if (!AccumulateForce(force)) {
                return;
            }
        }
        if (on(BehaviorType.ARRIVE)) {
            force.add(arrive(target, DecelerationType.FAST));
            if (!AccumulateForce(force)) {
                return;
            }
        }
        if (on(BehaviorType.PURSUIT)) {
            force.add(pursuit());
            if (!AccumulateForce(force)) {
                return;
            }
        }
        if (on(BehaviorType.INTERPOSE)) {
            force.add(interpose(ball, target, interposeDist));
            if (!AccumulateForce(force)) {
                return;
            }
        }
    }
    
    /**
     *  This function calculates how much of its max steering force the 
     *  vehicle has left to apply and then applies that amount of the force to add.
     */
    private boolean AccumulateForce(Vector force) {
        //first calculate how much steering force we have left to use
        double MagnitudeSoFar = steeringForce.length();
        double magnitudeRemaining = player.getMaxForce() - MagnitudeSoFar;

        //return false if there is no more force left to use
        if (magnitudeRemaining <= 0.0) {
            return false;
        }
        //calculate the magnitude of the force we want to add
        double MagnitudeToAdd = force.length();

        //now calculate how much of the force we can really add  
        if (MagnitudeToAdd > magnitudeRemaining) {
            MagnitudeToAdd = magnitudeRemaining;
        }
        //add it to the steering force
        steeringForce.add(force.normalizen().muln(MagnitudeToAdd));
        return true;
    }
    
    /**
     *  tags any vehicles within a predefined radius.
     */
    private void findNeighbours() {
        for (PlayerBase pl : PlayerBase.playerList) {      
            //first clear any current tag
            pl.getSteering().tagOff();
            //work in dist squared to avoid sqrts
            Vector to = pl.getPosition().subn(player.getPosition());
            if (to.lengthSq() < (viewDistance * viewDistance)) {
                pl.getSteering().tagOn();
            }
        }
    }
    
     /**
     * calculates the component of the steering force that is parallel with the vehicle heading.
     */
    public double headingComponent() {
        return player.getHeading().dot(steeringForce);
    }

    /**
     * calculates the component of the steering force that is perpendicuar with the vehicle heading.
     */
    public double perpComponent() {
        return player.getHeading().perp().dot(steeringForce) * player.getMaxTurnRate();      
    }

    /**
     * renders visual aids and info for seeing how each behavior is calculated.
     * render the steering force
     */
    public void render(GraphicsContext gc) {         
        gc.setStroke(Color.RED);
        gc.strokeLine(
            player.getPosition().x, 
            player.getPosition().y, 
            steeringForce.muln(20).addn(player.getPosition()).x,
            steeringForce.muln(20).addn(player.getPosition()).y);
    }
    
    /**
     ****************************** Behaviors ********************************.
     */
     
    /**
     * Given a target, this behavior returns a steering force which will
     * allign the agent with the target and move the agent in the desired direction.
     */
    private Vector seek() {
        Vector desiredVelocity = target.subn(player.getPosition()).mul(player.getMaxSpeed()).normalize();
        return desiredVelocity.sub(player.getVelocity());
    }

    /**
     * This behavior is similar to seek but it attempts to arrive at the target with a zero velocity.
     */
    private Vector arrive(Vector target,DecelerationType deceleration) {
        Vector toTarget = target.subn(player.getPosition());

        //calculate the dist to the target
        double dist = toTarget.length();
        if (dist > 0) {
            //because Deceleration is enumerated as an int, this value is required to provide fine tweaking of the deceleration..
            final double decelerationTweaker = 0.3;

            //calculate the speed required to reach the target given the desired deceleration
            double speed = dist / ((double)deceleration.value() * decelerationTweaker);

            //make sure the velocity does not exceed the max
            speed = Math.min(speed, player.getMaxSpeed());

            //from here proceed just like Seek except we don't need to normalize 
            //the ToTarget vector because we have already gone to the trouble of calculating its length: dist. 
            return toTarget.mul(speed / dist).sub(player.getVelocity());
        }
        return new Vector(0, 0);
    }

    /**
     * This behavior predicts where its prey will be and seeks to that location.
     * This behavior creates a force that steers the agent towards the ball.
     */
    private Vector pursuit() {
        Vector toBall = ball.getPosition().subn(player.getPosition());
        //the lookahead time is proportional to the dist between the ball and the pursuer; 
        double lookAheadTime = 0.0;
        if (ball.speed() != 0.0) {
            lookAheadTime = toBall.length() / ball.speed();
        }
        //calculate where the ball will be at this time in the future
        target = ball.futurePosition(lookAheadTime);
        //now seek to the predicted future position of the ball
        return arrive(target, DecelerationType.FAST);
    }

    /**
     *
     * this calculates a force repelling from the other neighbors.
     */
    private Vector separation() {
        Vector force = new Vector();        
        for (PlayerBase curPlayer : PlayerBase.playerList) {   
            //make sure this agent isn't included in the calculations and that the agent is close enough
            if ((curPlayer != player) && curPlayer.getSteering().isTagged()) {
                Vector toAgent = player.getPosition().subn(curPlayer.getPosition());
                //scale the force inversely proportional to the agents dist  from its neighbor
                force.add(toAgent.normalizen().divn(toAgent.length()));
            }
        }
        return force;
    }

    /**
     * Given an opponent and an object position this method returns a force that attempts to position the agent between them.
     */
    private Vector interpose(final SoccerBall ball, Vector target, double distFromTarget) {
        return arrive(ball.getPosition().subn(target).normalize().mul(distFromTarget).add(target), DecelerationType.NORMAL);
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

    public void separationOn() {
        flag |= BehaviorType.SEPARATION.flag();
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

    public void separationOff() {
        if (on(BehaviorType.SEPARATION)) {
            flag ^= BehaviorType.SEPARATION.flag();
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

    public boolean separationIsOn() {
        return on(BehaviorType.SEPARATION);
    }

    public boolean interposeIsOn() {
        return on(BehaviorType.INTERPOSE);
    }
    
    /**
     ************************ Getters & Setters *******************************.
     */
    
    public Vector getSteeringForce() {
        return steeringForce;
    }
    public Vector getTarget() {
        return new Vector(target);
    }

    public void setTarget(final Vector t) {
        target = new Vector(t);
    }

    public double getInterposeDist() {
        return interposeDist;
    }

    public void setInterposeDist(double d) {
        interposeDist = d;
    }

    public boolean isTagged() {
        return tagged;
    }

    public void tagOn() {
        tagged = true;
    }

    public void tagOff() {
        tagged = false;
    }
   
}
