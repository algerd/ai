
package SimpleSoccer;

import SimpleSoccer.FieldPlayerStates.GlobalPlayerState;
import common.Messaging.Telegram;
import common.misc.AutoList;
import common.D2.Vector;
import common.FSM.State;
import common.FSM.StateMachine;
import common.Game.EntityFunctionTemplates;
import common.Time.Regulator;
import common.misc.Cgdi;
import common.misc.Utils;
import common.misc.StreamUtilityFunction;
import common.D2.Transformation;

public class FieldPlayer extends PlayerBase {
    
    private StateMachine<FieldPlayer> stateMachine;
    //limits the number of kicks a player may take per second
    private Regulator kickLimiter;

    public FieldPlayer(
            SoccerTeam homeTeam,
            int homeRegion,
            State<FieldPlayer> startState,
            Vector heading,
            Vector velocity,
            double mass,
            double maxForce,
            double maxSpeed,
            double maxTurnRate,
            double scale,
            PlayerRoleType role) 
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
            role);

        stateMachine = new StateMachine<FieldPlayer>(this);
        if (startState != null) {
            stateMachine.setCurrentState(startState);
            stateMachine.setPreviousState(startState);
            stateMachine.setGlobalState(GlobalPlayerState.getInstance());
            stateMachine.getCurrentState().enter(this);
        }
        steering.separationOn();
        kickLimiter = new Regulator(ParamLoader.instance.PlayerKickFrequency);
    }

    /**
     * call this to update the player's position and orientation.
     */
    public void update() {
        //run the logic for the current state
        stateMachine.update();

        //calculate the combined steering force
        steering.calculate();

        //if no steering force is produced decelerate the player by applying a braking force
        if (steering.getSteeringForce().isZero()) {
            final double brakingRate = 0.8;
            velocity.mul(brakingRate);
        }

        //the steering force's side component is a force that rotates the 
        //player about its axis. We must limit the rotation so that a player
        //can only turn by PlayerMaxTurnRate rads per update.
        double turningForce = steering.sideComponent();

        turningForce = Utils.clamp(turningForce, -ParamLoader.instance.PlayerMaxTurnRate, ParamLoader.instance.PlayerMaxTurnRate);

        //rotate the heading vector
        Transformation.rotateAroundOrigin(heading, turningForce);

        //make sure the velocity vector points in the same direction as the heading vector
        velocity = Vector.mul(heading, velocity.length());
        
        //and recreate m_vSide
        side = heading.perp();

        //now to calculate the acceleration due to the force exerted by
        //the forward component of the steering force in the direction of the player's heading
        Vector accel = Vector.mul(heading, steering.forwardComponent()/ mass);
        velocity.add(accel);

        //make sure player does not exceed maximum velocity
        velocity.truncate(maxSpeed);
        //update the position
        position.add(velocity);

        //enforce a non-penetration constraint if desired
        if (ParamLoader.instance.bNonPenetrationConstraint) {
            EntityFunctionTemplates.enforceNonPenetrationContraint(this, new AutoList<PlayerBase>().getAllMembers());
        }
    }

    @Override
    public void render() {
        Cgdi.gdi.TransparentText();
        Cgdi.gdi.TextColor(Cgdi.grey);

        //set appropriate team color
        if (getTeam().getColor() == SoccerTeam.blue) {
            Cgdi.gdi.BluePen();
        } else {
            Cgdi.gdi.RedPen();
        }

        //render the player's body
        vecPlayerVBTrans = Transformation.worldTransform(vecPlayerVB,
                getPosition(),
                getHeading(),
                getSide(),
                getScale());
        Cgdi.gdi.ClosedShape(vecPlayerVBTrans);

        //and 'is 'ead
        Cgdi.gdi.BrownBrush();
        if (ParamLoader.instance.bHighlightIfThreatened && (getTeam().getControllingPlayer() == this) && isThreatened()) {
            Cgdi.gdi.YellowBrush();
        }
        Cgdi.gdi.Circle(getPosition(), 6);


        //render the state
        if (ParamLoader.instance.bStates) {
            Cgdi.gdi.TextColor(0, 170, 0);
            Cgdi.gdi.TextAtPos(position.x, position.y - 25, new String(stateMachine.GetNameOfCurrentState()));
        }

        //show IDs
        if (ParamLoader.instance.bIDs) {
            Cgdi.gdi.TextColor(0, 170, 0);
            Cgdi.gdi.TextAtPos(getPosition().x - 20, getPosition().y - 25, StreamUtilityFunction.ttos(getId()));
        }


        if (ParamLoader.instance.bViewTargets) {
            Cgdi.gdi.RedBrush();
            Cgdi.gdi.Circle(getSteering().getTarget(), 3);
            Cgdi.gdi.TextAtPos(getSteering().getTarget(), StreamUtilityFunction.ttos(getId()));
        }
    }

    /**
     * routes any messages appropriately
     */
    @Override
    public boolean handleMessage(final Telegram msg) {
        return stateMachine.handleMessage(msg);
    }
    
     public boolean isReadyForNextKick() {
        return kickLimiter.isReady();
    }

    public StateMachine<FieldPlayer> getStateMachine() {
        return stateMachine;
    }
 
}
