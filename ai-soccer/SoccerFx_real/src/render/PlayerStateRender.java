
package render;

import common.D2.Vector;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import common.FSM.FieldStateList;
import soccer.PlayerBase;

public class PlayerStateRender implements Renderable {
    
    private GraphicsContext gc;
    private List<Integer> stateList = new ArrayList<>();
    private List<Vector> positionList = new ArrayList<>();
    private Vector position;
    private int state;
    private int id;
     
    public PlayerStateRender(GraphicsContext gc, PlayerBase player) {
        this.gc = gc;
        id = player.getId();
        stateList = player.getStateList();
        positionList = player.getPositionList();
        state = stateList.get(0);
        position = positionList.get(0);
    }
    
    @Override
    public void render(int tact) {
        int idState = stateList.get(tact);
        if (idState != state) {
            state = idState;
        }
        String nameState = FieldStateList.getNameFromeKey(state);
        
        Vector pos = positionList.get(tact);
        if (pos != null) {
            position = pos;
        }  
        
        gc.save(); 
        // show state
        gc.setStroke(Color.WHITE);
        gc.strokeText(nameState, position.x, position.y - 17);           
        
        //show id
        gc.setStroke(Color.WHITE);
        gc.strokeText(Integer.toString(id), position.x - 13, position.y - 17);  
        gc.restore();
    }
    
}