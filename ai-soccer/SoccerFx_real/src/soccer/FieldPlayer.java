
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
            double mass,
            double maxAcceleration,
            double maxSpeed,
            double maxTurnRate,
            PlayerRoleType role) 
    {
        super(homeTeam, homeRegion, mass, maxAcceleration, maxSpeed, maxTurnRate, role);
        stateMachine = new StateMachine<>(this);
        if (startState != null) {
            stateMachine.setCurrentState(startState);
            stateMachine.setPreviousState(startState);
            stateMachine.setGlobalState(GlobalPlayerState.getInstance());
            stateMachine.getCurrentState().enter(this);
        }
    }

    /**
     * call this to update the player's position and orientation.
     */
    public void update() {
        ++kickTimer;      
        stateMachine.update();     
        steering.calculateAcceleration();
        
        //if acceleration iz zero then player deceleration (не задано движение в точку)
        if (steering.getAcceleration().isZero()) {
            final double brakingRate = 0.9;
            velocity.mul(brakingRate);
        }               
        boolean changedHeading = rotateHeading(steering.getTarget());
              
        // величина ускорения зависит от угла между heading и вектором ускорения и массой игрока
        Vector acceleration = heading.muln(heading.dot(steering.getAcceleration()) * massForce);
        velocity.add(acceleration);
        
        boolean changedPosition = false;
        if (velocity.x > 0.001 || velocity.x < -0.001 || velocity.y > 0.001 || velocity.y < -0.001) {
            position.add(velocity);  
            if (Params.NON_PENETRATION_CONSTRAINT) {
                Functions.enforceNonPenetrationConstraint(this, PlayerBase.playerList);
            }  
            changedPosition = true;
        }
        positionList.add(changedPosition ? new Vector(position) : null);
        headingList.add(changedHeading ? new Vector(heading) : null);
        
        //Добавить индексы состояний в хранилище (для отладки)
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
