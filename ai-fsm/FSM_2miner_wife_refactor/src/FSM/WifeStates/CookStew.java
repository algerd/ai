
package FSM.WifeStates;

import FSM.LocationEnum;
import FSM.State;
import FSM.Wife;

class CookStew extends State<WifeStateEnum> {
    
    private Wife wife;
    private boolean isCooking;
    private final WifeStateEnum[] transitionArray = new WifeStateEnum[]{
        //WifeStateEnum.VISIT_BATHROOM    
    };
  
    private CookStew() {}
    
    CookStew(Wife wife) {
        this.wife = wife;
    }
    
    @Override
    public boolean enterCondition() {
        return true;
    }

    @Override
    public void enter(State prevState) {
        if (!this.isCooking) {
            System.out.println(wife.getEntity() + ": Putting the stew in the oven");
            this.isCooking = true;
        }
    }

    @Override
    public void execute() {
        System.out.println(wife.getEntity() + ": Fussin' over food");
    }
    
    @Override
    public boolean exitCondition() {
        return true;
    }

    @Override
    public void exit() {
        System.out.println(wife.getEntity() + ": Puttin' the stew on the table");
    }
    
    @Override
    public WifeStateEnum[] getTransitionArray() {
        return this.transitionArray;
    }
    
    public LocationEnum getLocation() { 
        return null;
    } 

}
