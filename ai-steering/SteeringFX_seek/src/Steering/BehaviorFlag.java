
package Steering;

public class BehaviorFlag {
       
    private enum BehaviorType {
        SEEK(0x00002),
        FLEE(0x00004),
        ARRIVE(0x00008),
        WANDER(0x00010),
        EVADE(0x01000),
        FLOCK(0x08000);
        
        private int flag;

        BehaviorType(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return this.flag;
        }
    }
    
    //binary flags to indicate whether or not a behavior should be active
    private int flags;
    
    public BehaviorFlag() {}
    
    //this function tests if a specific bit of m_iFlags is set
    private boolean is(BehaviorType bt) {
        return (flags & bt.getFlag()) == bt.getFlag();
    }
    
    private void on(BehaviorType bt) {
        flags |= bt.getFlag();
    }
    
    private void off(BehaviorType bt) {
        if (is(bt)) {
            flags ^= bt.getFlag();
        }
    }
    
    //////////////////// isBehaviorOn
    public boolean isSeekOn() {
        return is(BehaviorType.SEEK);
    }
    public boolean isFleeOn() {
        return is(BehaviorType.FLEE);      
    }
     public boolean isArriveOn() {
        return is(BehaviorType.ARRIVE);      
    }
    public boolean isEvadeOn() {
        return is(BehaviorType.EVADE);      
    }
    public boolean isWanderOn() {
        return is(BehaviorType.WANDER);      
    }
    
    ////////////////// behaviorOn
    public void seekOn() {
        on(BehaviorType.SEEK);
    }
    public void fleeOn() {
        on(BehaviorType.FLEE);      
    }
     public void arriveOn() {
        on(BehaviorType.ARRIVE);      
    }
    public void evadeOn() {
        on(BehaviorType.EVADE);      
    }
    public void wanderOn() {
        on(BehaviorType.WANDER);      
    }
    public void flockingOn() {
        wanderOn();
    }
      
    ////////////////// behaviorOff
     public void seekOff() {
        off(BehaviorType.SEEK);
    }
    public void fleeOff() {
        off(BehaviorType.FLEE);      
    }
     public void arriveOff() {
        off(BehaviorType.ARRIVE);      
    }
    public void evadeOff() {
        off(BehaviorType.EVADE);      
    }
    public void wanderOff() {
        off(BehaviorType.WANDER);      
    }
    public void flockingOff() {
        wanderOff();
    }
                
}
