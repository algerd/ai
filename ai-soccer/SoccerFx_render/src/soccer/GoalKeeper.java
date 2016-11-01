
package soccer;

import soccer.GoalKeeperStates.GlobalKeeperState;
import common.Messaging.Telegram;
import common.D2.Vector;
import common.D2.Transformation;
import common.FSM.FieldStateList;
import common.FSM.State;
import common.FSM.StateMachine;
import common.Game.EntityFunctionTemplates;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GoalKeeper extends PlayerBase {

    private StateMachine<GoalKeeper> stateMachine;

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
        
        //run the logic for the current state
        stateMachine.update();

        //calculate the combined force from each steering behavior 
        Vector steeringForce = steering.calculate();

        //Acceleration = getSteeringForce/Mass
        Vector acceleration = steeringForce.divn(mass);
        //update velocity
        velocity.add(acceleration);

        //make sure player does not exceed maximum velocity
        velocity.truncate(maxSpeed);
        
        //update the position
        if (velocity.x > 0.01 || velocity.x < -0.01 || velocity.y > 0.01 || velocity.y < -0.01) {         
            position.add(velocity);
            //enforce a non-penetration constraint if desired
            if (ParamLoader.instance.nonPenetrationConstraint) {
                EntityFunctionTemplates.enforceNonPenetrationConstraint(this, PlayerBase.playerList);
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
        //Добавить индексы состояний в хранилище (для отладки)
        Class<?extends State> state = stateMachine.getCurrentState().getClass();
        int keyState = FieldStateList.getKeyFromClass(state);
        if (prevState != keyState) {
            stateList.add(keyState);
        }
        else {
            stateList.add(null);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
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
        
        //draw the head
        gc.setFill(Color.BROWN);
        double radius = 6;
        gc.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);

        //draw the ID
        if (ParamLoader.instance.bIDs) {
            gc.setStroke(Color.rgb(0, 170, 0));
            gc.strokeText(Integer.toString(getId()), getPosition().x - 11, getPosition().y - 17);
        }

        //draw the state
        if (ParamLoader.instance.bStates) {
            gc.setStroke(Color.rgb(0, 170, 0, 0.5));
            gc.strokeText(stateMachine.GetNameOfCurrentState(), position.x, position.y - 17);           
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
        return getTeam().getHomeGoal().getCenter().distSq(getBall().getPosition()) <= ParamLoader.instance.GoalKeeperInterceptRangeSq;
    }

    /**
     * @return true if the keeper has ventured too far away from the goalmouth
     */
    public boolean isTooFarFromGoalMouth() {
        return getPosition().distSq(getRearInterposeTarget()) > ParamLoader.instance.GoalKeeperInterceptRangeSq;
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
            getBall().getPosition().y * ParamLoader.instance.GoalWidth / getPitch().getPlayingArea().height();
        return new Vector(xPosTarget, yPosTarget);
    }

    public StateMachine<GoalKeeper> getStateMachine() {
        return stateMachine;
    }

}
