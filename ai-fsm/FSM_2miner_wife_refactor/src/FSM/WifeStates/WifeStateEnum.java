
package FSM.WifeStates;

import FSM.State;
import FSM.Wife;

public enum WifeStateEnum {
    HOUSE_WORK() {
        public HouseWork get(Wife wife){
            return new HouseWork(wife);
        }
    },
    VISIT_BATHROOM() {
        public VisitBathroom get(Wife wife){
            return new VisitBathroom(wife);
        }
    };
            
    public abstract State get(Wife wife);
}
