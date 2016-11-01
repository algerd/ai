
package soccer;

import soccer.FieldPlayerStates.GlobalPlayerState;
import common.Messaging.Telegram;
import common.D2.Vector;
import common.FSM.State;
import common.FSM.StateMachine;
import common.Game.Functions;
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
