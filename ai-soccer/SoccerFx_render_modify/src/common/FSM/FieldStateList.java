
package common.FSM;

import java.util.ArrayList;
import java.util.List;
import soccer.FieldPlayerStates.ChaseBall;
import soccer.FieldPlayerStates.Dribble;
import soccer.FieldPlayerStates.KickBall;
import soccer.FieldPlayerStates.ReceiveBall;
import soccer.FieldPlayerStates.ReturnToHomeRegion;
import soccer.FieldPlayerStates.SupportAttacker;
import soccer.FieldPlayerStates.Wait;
import soccer.GoalKeeperStates.InterceptBall;
import soccer.GoalKeeperStates.PutBallBackInPlay;
import soccer.GoalKeeperStates.ReturnHome;
import soccer.GoalKeeperStates.TendGoal;

public class FieldStateList {
    
    private final static List<Class<?extends State>> stateList = new ArrayList<>();
    
    static {
        stateList.add(0, null);
        stateList.add(ChaseBall.class);
        stateList.add(Dribble.class);
        stateList.add(KickBall.class);
        stateList.add(ReceiveBall.class);
        stateList.add(ReturnToHomeRegion.class);
        stateList.add(SupportAttacker.class);
        stateList.add(Wait.class);
        stateList.add(InterceptBall.class);
        stateList.add(PutBallBackInPlay.class);
        stateList.add(ReturnHome.class);
        stateList.add(TendGoal.class);
    }
       
    public static String getNameFromeKey(int key) {
        return stateList.get(key).getSimpleName();
    }
    
    public static int getKeyFromClass(Class<?extends State> cls) {
        return stateList.indexOf(cls);
    }
    
}
