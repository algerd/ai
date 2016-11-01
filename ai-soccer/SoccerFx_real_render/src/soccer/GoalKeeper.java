
package soccer;

import soccer.GoalKeeperStates.GlobalKeeperState;
import common.Messaging.Telegram;
import common.D2.Vector;
import common.FSM.FieldStateList;
import common.FSM.State;
import common.FSM.StateMachine;
import common.Game.Functions;

public class GoalKeeper extends PlayerBase {

    private StateMachine<GoalKeeper> stateMachine;

    public GoalKeeper(
            SoccerTeam homeTeam,
            int homeRegion,
            State<GoalKeeper> startState,
            double mass,
            double maxAcceleration,
            double maxSpeed,
            double maxTurnRate) 
    {
        super(homeTeam, homeRegion, mass, maxAcceleration, maxSpeed, maxTurnRate, PlayerBase.PlayerRoleType.GOAL_KEEPER);
        stateMachine = new StateMachine<>(this);
        stateMachine.setCurrentState(startState);
        stateMachine.setPreviousState(startState);
        stateMachine.setGlobalState(GlobalKeeperState.getInstance());
        stateMachine.getCurrentState().enter(this);
    }
   
    @Override
    public void update() {
        boolean changedPosition = false;
        boolean changedHeading = false;
        
        /*
        Использовать аналогичную FieldPlayer схему расчёта, но с условием:
        - если dist < 2m до target, вратарь должен иметь heading на мяч, при большем расстоянии на targret
            как в FieldPlayer. Это будет приводить, что вратарь будет разворачиваться к мячу за 2м до цели движения
        */ 
        
        
        //run the logic for the current state
        stateMachine.update();

        //calculate the combined acceleration from each steering behavior 
        steering.calculateAcceleration();   
        velocity.add(steering.getAcceleration().muln(massForce));
        velocity.truncate(maxSpeed);
        
        //update the position
        if (velocity.x > 0.01 || velocity.x < -0.01 || velocity.y > 0.01 || velocity.y < -0.01) {         
            position.add(velocity);
            //enforce a non-penetration constraint if desired
            if (Params.NON_PENETRATION_CONSTRAINT) {
                Functions.enforceNonPenetrationConstraint(this, PlayerBase.playerList);
            } 
            changedPosition = true;
        }
        
        // если изменилось положение вратаря или изменилось положение мяча - расчитать направление вратаря
        if (changedPosition || getBall().getPositionList().get(getBall().getPositionList().size() - 1) != null) {
            // вратарь всегда находится лицом к мячу, даже когда возвращается назад к воротам
            heading = getBall().getPosition().subn(getPosition()).normalizen();
            changedHeading = true;
        }
        //добавить положение и направление движения игрока в хранилища
        positionList.add(changedPosition ? new Vector(position) : null);
        headingList.add(changedHeading ? new Vector(heading) : null);
        
        //Добавить индексы состояний в хранилище (для отладки)
        Class<?extends State> state = stateMachine.getCurrentState().getClass();
        int keyState = FieldStateList.getKeyFromClass(state);
        stateList.add((prevState != keyState) ? keyState : null);    
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
        return getTeam().getHomeGoal().getCenter().distSq(getBall().getPosition()) <= Params.GOALKEEPER_INTERCEPT_RANGE_SQ;
    }

    /**
     * return true if the keeper has ventured too far away from the goalmouth.
     */
    public boolean isTooFarFromGoalMouth() {
        return getPosition().distSq(getRearInterposeTarget()) > Params.GOALKEEPER_INTERCEPT_RANGE_SQ;
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
            Field.WIDTH_FIELD_CENTER - Field.WIDTH_GATE / 2 + getBall().getPosition().y * Field.WIDTH_GATE / Field.WIDTH_FIELD;
        return new Vector(xPosTarget, yPosTarget);
    }

    public StateMachine<GoalKeeper> getStateMachine() {
        return stateMachine;
    }

}
