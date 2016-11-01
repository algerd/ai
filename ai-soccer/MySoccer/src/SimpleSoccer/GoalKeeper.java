
package SimpleSoccer;

import SimpleSoccer.GoalKeeperStates.GlobalKeeperState;
import common.Messaging.Telegram;
import common.misc.AutoList;
import common.D2.Vector;
import common.D2.Transformation;
import common.FSM.State;
import common.FSM.StateMachine;
import common.misc.Cgdi;
import common.misc.StreamUtilityFunction;
import common.Game.EntityFunctionTemplates;

public class GoalKeeper extends PlayerBase {

    private StateMachine<GoalKeeper> stateMachine;
    //this vector is updated to point towards the ball and is used when
    //rendering the goalkeeper (instead of the underlaying vehicle's heading)
    //to ensure he always appears to be watching the ball
    private Vector lookAt = new Vector();

    public GoalKeeper(
            SoccerTeam homeTeam,
            int homeRegion,
            State<GoalKeeper> startState,
            Vector heading,
            Vector velocity,
            double mass,
            double maxForce,
            double maxSpeed,
            double maxTurnRate,
            double scale) 
    {
        super(
            homeTeam,
            homeRegion,
            heading,
            velocity,
            mass,
            maxForce,
            maxSpeed,
            maxTurnRate,
            scale,
            PlayerBase.PlayerRoleType.GOAL_KEEPER);

        stateMachine = new StateMachine<GoalKeeper>(this);
        stateMachine.setCurrentState(startState);
        stateMachine.setPreviousState(startState);
        stateMachine.setGlobalState(GlobalKeeperState.getInstance());
        stateMachine.getCurrentState().enter(this);
    }
   
    @Override
    public void update() {
        //run the logic for the current state
        stateMachine.update();

        //calculate the combined force from each steering behavior 
        Vector steeringForce = steering.calculate();

        //Acceleration = getSteeringForce/Mass
        Vector acceleration = Vector.div(steeringForce, mass);
        //update velocity
        velocity.add(acceleration);

        //make sure player does not exceed maximum velocity
        velocity.truncate(maxSpeed);

        //update the position
        position.add(velocity);

        //enforce a non-penetration constraint if desired
        if (ParamLoader.instance.bNonPenetrationConstraint) {
            EntityFunctionTemplates.enforceNonPenetrationContraint(this, new AutoList<PlayerBase>().getAllMembers());
        }
        //update the heading if the player has a non zero velocity
        if (!velocity.isZero()) {
            heading = Vector.normalize(velocity);
            side = heading.perp();
        }
        //look-at vector always points toward the ball
        if (!getPitch().getGoalKeeperHasBall()) {
            lookAt = Vector.normalize(Vector.sub(getBall().getPosition(), getPosition()));
        }
    }

    @Override
    public void render() {
        if (getTeam().getColor() == SoccerTeam.blue) {
            Cgdi.gdi.BluePen();
        } else {
            Cgdi.gdi.RedPen();
        }

        vecPlayerVBTrans = Transformation.worldTransform(vecPlayerVB,
                getPosition(),
                lookAt,
                lookAt.perp(),
                getScale());

        Cgdi.gdi.ClosedShape(vecPlayerVBTrans);

        //draw the head
        Cgdi.gdi.BrownBrush();
        Cgdi.gdi.Circle(getPosition(), 6);

        //draw the ID
        if (ParamLoader.instance.bIDs) {
            Cgdi.gdi.TextColor(0, 170, 0);;
            Cgdi.gdi.TextAtPos(getPosition().x - 20, getPosition().y - 25, StreamUtilityFunction.ttos(getId()));
        }

        //draw the state
        if (ParamLoader.instance.bStates) {
            Cgdi.gdi.TextColor(0, 170, 0);
            Cgdi.gdi.TransparentText();
            Cgdi.gdi.TextAtPos(position.x, position.y - 25, new String(stateMachine.GetNameOfCurrentState()));
        }
    }

    /**
     * routes any messages appropriately.
     */
    @Override
    public boolean handleMessage(final Telegram msg) {
        return stateMachine.handleMessage(msg);
    }

    /**
     * return true if the ball comes close enough for the keeper to consider intercepting.
     */
    public boolean isBallWithinRangeForIntercept() {
        return (Vector.distSq(getTeam().getHomeGoal().getCenter(), getBall().getPosition())
                <= ParamLoader.instance.GoalKeeperInterceptRangeSq);
    }

    /**
     * @return true if the keeper has ventured too far away from the goalmouth
     */
    public boolean isTooFarFromGoalMouth() {
        return (Vector.distSq(getPosition(), getRearInterposeTarget())
                > ParamLoader.instance.GoalKeeperInterceptRangeSq);
    }

    /**
     * this method is called by the Intercept state to determine the spot
     * along the goalmouth which will act as one of the interpose targets
     * (the other is the ball).
     * the specific point at the goal line that the keeper is trying to cover
     * is flexible and can move depending on where the ball is on the field.
     * To achieve this we just scale the ball's y value by the ratio of the
     * goal width to playingfield width
     */
    public Vector getRearInterposeTarget() {
        double xPosTarget = getTeam().getHomeGoal().getCenter().x;
        double yPosTarget = 
                getPitch().getPlayingArea().center().y -
                ParamLoader.instance.GoalWidth * 0.5 + 
                (getBall().getPosition().y * ParamLoader.instance.GoalWidth) / getPitch().getPlayingArea().height();
        return new Vector(xPosTarget, yPosTarget);
    }

    public StateMachine<GoalKeeper> getStateMachine() {
        return stateMachine;
    }

    public Vector getLookAt() {
        return new Vector(lookAt);
    }

    public void setLookAt(Vector v) {
        lookAt = new Vector(v);
    }
    
}
