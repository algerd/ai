
package Steering.behaviors;

import Steering.Vehicle;
import common.D2.Vector2D;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class BehaviorManager {
    
    public static enum CalculateForceMethod {
        WEIGHTED_AVERAGE, PRIORITIZED, DITHERED;      
    }
    
    private Map<BehaviorType, Behavior> behaviorMap;
    // the steering force created by the combined effect of all the selected behaviors
    private Vector2D steeringForce;   
    // what type of method is used to calculate steering force
    private CalculateForceMethod calculateForceMethod;         
    // max force of vehicle
    private double maxForce;
    private boolean cellSpaceOn;
    
    public BehaviorManager(Vehicle vehicle) {
        // упорядочить по значениям приоритета поведений
        behaviorMap = new TreeMap<>(Comparator.comparing(BehaviorType::getPriority));
        for(BehaviorType behaviorEnum : BehaviorType.values()) {
            behaviorMap.put(behaviorEnum, behaviorEnum.get(vehicle));
        }
        maxForce = vehicle.getMaxForce();
        steeringForce = new Vector2D(0, 0);
        calculateForceMethod = CalculateForceMethod.PRIORITIZED;      
    }
    
    /**
     * calculates the accumulated steering force according to the method set in calculateForceMethod
     */
    public Vector2D calculateForce() {
        //reset the steering force
        steeringForce.zero();
      
        switch (calculateForceMethod) {
            case WEIGHTED_AVERAGE:
                calculateWeightedSum();
                break;
            case PRIORITIZED:
                calculatePrioritized();
                break;
            case DITHERED:
                calculateDithered();
                break;
        }
        return steeringForce;
    }
    
    /**
     *  this method calls each active steering behavior in order of priority
     *  and accumulates their forces until the max steering force magnitude
     *  is reached, at which time the function returns the steering force 
     *  accumulated to that  point
     */
    private void calculatePrioritized() {        
        Set<Map.Entry<BehaviorType, Behavior>> entries = behaviorMap.entrySet();
        for (Map.Entry<BehaviorType, Behavior> entry : entries) {
            Behavior behavior = entry.getValue();
            if (behavior.isOn() && !accumulateForce(behavior.getWeightForce())) {            
                break;
            }
        } 
    }
    /**
     * This function calculates how much of its max steering force the 
     * vehicle has left to apply and then applies that amount of the force to add.
     */
    private boolean accumulateForce(Vector2D plusForce) {
        double remainingForce = maxForce - steeringForce.length();
        if (remainingForce <= 0.0) {
            return false;
        }
        if (plusForce.length() < remainingForce) {
            steeringForce.add(plusForce);
        } else {
            //add as much of the plusForce vector is possible without going over the max.
            steeringForce.add(Vector2D.mul(Vector2D.vec2DNormalize(plusForce), remainingForce));
        }
        return true;
    }
    
    /**
     *  this simply sums up all the active behaviors X their weights and 
     *  truncates the result to the max available steering force before  returning
     */
    private void calculateWeightedSum() {
        Set<Map.Entry<BehaviorType, Behavior>> entries = behaviorMap.entrySet();
        for (Map.Entry<BehaviorType, Behavior> entry : entries) {
            Behavior behavior = entry.getValue();
            if (behavior.isOn()) {
                steeringForce.add(behavior.getWeightForce());
            }
        }
        steeringForce.truncate(maxForce);     
    }
    
    /**
     *  this method sums up the active behaviors by assigning a probability
     *  of being calculated to each behavior. It then tests the first priority
     *  to see if it should be calculated this simulation-step. If so, it
     *  calculates the steering force resulting from this behavior. If it is
     *  more than zero it returns the force. If zero, or if the behavior is
     *  skipped it continues onto the next priority, and so on.
     */
    private void calculateDithered() {
        Set<Map.Entry<BehaviorType, Behavior>> entries = behaviorMap.entrySet();
        for (Map.Entry<BehaviorType, Behavior> entry : entries) {
            Behavior behavior = entry.getValue();
            steeringForce.add(behavior.getDitheredForce());
            if (!steeringForce.isZero()) {
                steeringForce.truncate(maxForce);
                break;
            }
        }       
    }
    
    public Behavior get(BehaviorType behaviorType) {
        return behaviorMap.get(behaviorType);
    }
    
    public void on(BehaviorType behaviorType) {
        get(behaviorType).on();
    }
    
    public void off(BehaviorType behaviorType) {
        get(behaviorType).off();
    }
    
    public boolean isOn(BehaviorType behaviorType) {
        return get(behaviorType).isOn();
    }

    public Map<BehaviorType, Behavior> getBehaviorMap() {
        return behaviorMap;
    }
         
}