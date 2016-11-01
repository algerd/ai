
package Steering;

public class BehaviorFlag {
       
    private enum BehaviorType {
        SEEK(0x00002),
        FLEE(0x00004),
        ARRIVE(0x00008),
        WANDER(0x00010),
        COHESIAN(0x00020),
        SEPARATION(0x00040),
        ALIGNMENT(0x00080),       
        OBSTACLE_AVOIDANCE(0x00100),
        WALL_AVOIDANCE(0x00200),
        FOLLOW_PATH(0x00400),
        PURSUIT(0x00800),
        EVADE(0x01000),
        INTERPOSE(0x02000),
        HIDE(0x04000),
        FLOCK(0x08000),
        OFFSET_PURSUIT(0x10000);
        
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
    public boolean isPursuitOn() {
        return is(BehaviorType.PURSUIT);
    }
    public boolean isObstacleAvoidanceOn() {
        return is(BehaviorType.OBSTACLE_AVOIDANCE);
    }
    public boolean isWallAvoidanceOn() {
        return is(BehaviorType.WALL_AVOIDANCE);
    }
    public boolean isInterposeOn() {
        return is(BehaviorType.INTERPOSE);
    }
    public boolean isHideOn() {
        return is(BehaviorType.HIDE);
    }
    public boolean isFollowPathOn() {
        return is(BehaviorType.FOLLOW_PATH);
    }
    public boolean isOffsetPursuitOn() {
        return is(BehaviorType.OFFSET_PURSUIT);
    }
    public boolean isSeparationOn() {
        return is(BehaviorType.SEPARATION);
    }
    public boolean isAlignmentOn() {
        return is(BehaviorType.ALIGNMENT);
    }
    public boolean isCohesionOn() {
        return is(BehaviorType.COHESIAN);
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
    public void pursuitOn() {
        on(BehaviorType.PURSUIT);      
    }
    public void obstacleAvoidanceOn() {
        on(BehaviorType.OBSTACLE_AVOIDANCE);
    }
    public void wallAvoidanceOn() {
        on(BehaviorType.WALL_AVOIDANCE);
    }
    public void interposeOn() {
        on(BehaviorType.INTERPOSE);
    }
    public void hideOn() {
        on(BehaviorType.HIDE);
    }
    public void followPathOn() {
        on(BehaviorType.FOLLOW_PATH);
    }
    public void offsetPursuitOn() {
        on(BehaviorType.OFFSET_PURSUIT);
    }
    public void separationOn() {
        on(BehaviorType.SEPARATION);
    }
    public void alignmentOn() {
        on(BehaviorType.ALIGNMENT);
    }
    public void cohesionOn() {
        on(BehaviorType.COHESIAN);
    }
    public void flockingOn() {
        cohesionOn();
        alignmentOn();
        separationOn();
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
    public void pursuitOff() {
        off(BehaviorType.PURSUIT);
    }
    public void obstacleAvoidanceOff() {
        off(BehaviorType.OBSTACLE_AVOIDANCE);
    }
    public void wallAvoidanceOff() {
        off(BehaviorType.WALL_AVOIDANCE);
    }
    public void interposeOff() {
        off(BehaviorType.INTERPOSE);
    } 
    public void hideOff() {
        off(BehaviorType.HIDE);
    } 
    public void followPathOff() {
        off(BehaviorType.FOLLOW_PATH);
    }
    public void offsetPursuitOff() {
        off(BehaviorType.OFFSET_PURSUIT);
    }
    public void separationOff() {
        off(BehaviorType.SEPARATION);
    }
    public void alignmentOff() {
        off(BehaviorType.ALIGNMENT);
    }
    public void cohesionOff() {
        off(BehaviorType.COHESIAN);
    }
    public void flockingOff() {
        cohesionOff();
        alignmentOff();
        separationOff();
        wanderOff();
    }
             
}
