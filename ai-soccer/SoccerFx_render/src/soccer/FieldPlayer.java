
package soccer;

import soccer.FieldPlayerStates.GlobalPlayerState;
import common.Messaging.Telegram;
import common.D2.Vector;
import common.FSM.State;
import common.FSM.StateMachine;
import common.Game.EntityFunctionTemplates;
import common.misc.Utils;
import common.D2.Transformation;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import common.FSM.FieldStateList;

public class FieldPlayer extends PlayerBase {
    
    private StateMachine<FieldPlayer> stateMachine;
    // количество тактов паузы после удара
    private final int PAUSE_KICK = 4;
    // счётчик тактов после удара
    private int kickTimer;

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

        stateMachine = new StateMachine<>(this);
        if (startState != null) {
            stateMachine.setCurrentState(startState);
            stateMachine.setPreviousState(startState);
            stateMachine.setGlobalState(GlobalPlayerState.getInstance());
            stateMachine.getCurrentState().enter(this);
        }
        steering.separationOn();
    }

    /**
     * call this to update the player's position and orientation.
     */
    public void update() {
        ++kickTimer;
        boolean changedPosition = false;
        boolean changedHeading = false;
        
        //run the logic for the current state
        stateMachine.update();

        //1.calculate the combined steering force
        steering.calculate();
        
        //if no steering force is produced decelerate the player by applying a braking force
        if (steering.getSteeringForce().isZero()) {
            final double brakingRate = 0.8;
            velocity.mul(brakingRate);
        }
        //2. the steering force's side component is a force that rotates the player about its axis. 
        //We must limit the rotation so that a player can only turn by PlayerMaxTurnRate rads per update.
        double turningForce = steering.perpComponent();
        turningForce = Utils.clamp(turningForce, -ParamLoader.instance.PlayerMaxTurnRate, ParamLoader.instance.PlayerMaxTurnRate);
        //rotate the heading vector 
        if (turningForce > 0.01 ||  turningForce < 0.01) {
            Transformation.rotateAroundOrigin(heading, turningForce);
            changedHeading = true;
        }    

        //3. make sure the velocity vector points in the same direction as the heading vector
        velocity = heading.muln(velocity.length());

        //4. now to calculate the acceleration due to the force exerted by
        //the forward component of the steering force in the direction of the player's heading
        Vector accel = heading.muln(steering.headingComponent()/ mass);

        //5.now to calculate the velocity
        velocity.add(accel);
        //make sure player does not exceed maximum velocity
        velocity.truncate(maxSpeed);
        //6.update the position        
        if (velocity.x > 0.01 || velocity.x < -0.01 || velocity.y > 0.01 || velocity.y < -0.01) {
            position.add(velocity);  
            
            //7.enforce a non-penetration constraint if desired
            if (ParamLoader.getInstance().nonPenetrationConstraint) {
                EntityFunctionTemplates.enforceNonPenetrationConstraint(this, PlayerBase.playerList);
            }  
            changedPosition = true;
        }              
        //8. добавить положение и направление движения игрока в хранилища
        if (changedPosition) {
            positionList.add(new Vector(position));
        }
        else {
            positionList.add(null);
        }
        if (changedHeading) {
            headingList.add(new Vector(heading));
        }
        else {
            headingList.add(null);
        }
        //9. Добавить индексы состояний в хранилище (для отладки)
        Class<?extends State> state = stateMachine.getCurrentState().getClass();
        int keyState = FieldStateList.getKeyFromClass(state);
        if (prevState != keyState) {
            stateList.add(keyState);
        }
        else {
            stateList.add(null);
        }
        //System.out.println(Integer.toString(keyState));
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.save();
        gc.setLineWidth(1.0);
        
        //set appropriate team color
        if (getTeam().getColor() == SoccerTeam.blue) {
            gc.setStroke(Color.BLUE);
        } else {
            gc.setStroke(Color.RED);
        }

        //render the player's body
        vecPlayerVBTrans = Transformation.worldTransform(vecPlayerVB, position, heading, scale);               
        int length = vecPlayerVBTrans.size();      
        double[] xPoints = new double[length];
        double[] yPoints = new double[length]; 
        for (int i = 0 ; i < length; i++) {
            xPoints[i] = vecPlayerVBTrans.get(i).x;
            yPoints[i] = vecPlayerVBTrans.get(i).y;
        }
        gc.strokePolygon(xPoints, yPoints, length);
          
        // render Highlight If Threatened
        if (ParamLoader.instance.bHighlightIfThreatened && (getTeam().getControllingPlayer() == this) && isThreatened()) {          
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.BROWN); 
        }
        double radius = 6;
        gc.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);

        //render the state
        if (ParamLoader.instance.bStates) {
            gc.setStroke(Color.rgb(0, 170, 0, 0.5));
            gc.strokeText(stateMachine.GetNameOfCurrentState(), position.x, position.y - 17);           
        }
        //show IDs
        if (ParamLoader.instance.bIDs) {
            gc.setStroke(Color.rgb(0, 170, 0));
            gc.strokeText(Integer.toString(getId()), getPosition().x - 11, getPosition().y - 17);
        }

        if (ParamLoader.instance.bViewTargets) {
            gc.setFill(Color.RED);
            radius = 3;
            gc.fillOval(getSteering().getTarget().x - radius, getSteering().getTarget().y - radius, radius * 2, radius * 2);
            gc.setStroke(Color.GREY);
            gc.strokeText(Integer.toString(getId()), getSteering().getTarget().x, getSteering().getTarget().y);
        }
        gc.restore();
    }

    /**
     * routes any messages appropriately
     */
    @Override
    public boolean handleMessage(final Telegram msg) {
        return stateMachine.handleMessage(msg);
    }
    
    public boolean isReadyForNextKick() {
        return kickTimer > PAUSE_KICK;          
    }
     
    public void resetKickTimer() {
        this.kickTimer = 0;
    }
    
    public StateMachine<FieldPlayer> getStateMachine() {
        return stateMachine;
    }
   
}
