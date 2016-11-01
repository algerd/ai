
package soccer;

import soccer.FieldPlayerStates.GlobalPlayerState;
import common.Messaging.Telegram;
import common.D2.Vector;
import common.FSM.State;
import common.FSM.StateMachine;
import common.Game.Functions;
import common.D2.Transformation;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import common.FSM.FieldStateList;
import java.util.List;
import render.ParamsRender;
import render.PlayerRender;

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
        turningForce = Functions.clamp(turningForce, -Params.PLAYER_MAX_TURN_RATE, Params.PLAYER_MAX_TURN_RATE);
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
            if (Params.NON_PENETRATION_CONSTRAINT) {
                Functions.enforceNonPenetrationConstraint(this, PlayerBase.playerList);
            }  
            changedPosition = true;
        }              
        //8. добавить положение и направление движения игрока в хранилища        
        positionList.add(changedPosition ? new Vector(position) : null);
        headingList.add(changedHeading ? new Vector(heading) : null);
        
        //9. Добавить индексы состояний в хранилище (для отладки)
        Class<?extends State> state = stateMachine.getCurrentState().getClass();
        int keyState = FieldStateList.getKeyFromClass(state);
        stateList.add((prevState != keyState) ? keyState : null);      
    }

    @Override
    public void render(GraphicsContext gc) {
        Vector posRender = position.muln(ParamsRender.SCALE_ANIMATION);
        
        gc.save();
        gc.setLineWidth(1.0);
        
        //set appropriate team color
        if (getTeam().getColor() == SoccerTeam.blue) {
            gc.setStroke(Color.BLUE);
        } else {
            gc.setStroke(Color.RED);
        }

        //render the player's body
        List<Vector> transVertexList = Transformation.worldTransform(PlayerRender.PLAYER_VERTEXES, posRender, heading);               
        int length = transVertexList.size();      
        double[] xPoints = new double[length];
        double[] yPoints = new double[length]; 
        for (int i = 0 ; i < length; i++) {
            xPoints[i] = transVertexList.get(i).x;
            yPoints[i] = transVertexList.get(i).y;
        }
        gc.strokePolygon(xPoints, yPoints, length);
          
        // render Highlight If Threatened
        if (ParamsRender.RENDER_HIGHLIGHT_IF_THREATENED && (getTeam().getControllingPlayer() == this) && isThreatened()) {          
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.BROWN); 
        }
        double radius = 6;
        gc.fillOval(posRender.x - radius, posRender.y - radius, radius * 2, radius * 2);

        //render the state
        if (ParamsRender.RENDER_STATE) {
            gc.setStroke(Color.rgb(0, 170, 0, 0.5));
            gc.strokeText(stateMachine.GetNameOfCurrentState(), posRender.x, posRender.y - 17);           
        }
        //show IDs
        if (ParamsRender.RENDER_ID) {
            gc.setStroke(Color.rgb(0, 170, 0));
            gc.strokeText(Integer.toString(getId()), posRender.x - 11, posRender.y - 17);
        }

        if (ParamsRender.RENDER_TARGETS) {
            gc.setFill(Color.RED);
            radius = 3;
            Vector target = getSteering().getTarget().muln(ParamsRender.SCALE_ANIMATION);
            gc.fillOval(target.x - radius, target.y - radius, radius * 2, radius * 2);
            gc.setStroke(Color.GREY);
            gc.strokeText(Integer.toString(getId()), target.x, target.y);
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
