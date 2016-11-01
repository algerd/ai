
package Steering;

import Steering.behaviors.BehaviorManager;
import java.util.ArrayList;
import common.D2.Vector2D;
import java.util.List;
import common.D2.Transformation;
import common.misc.Cgdi;
import common.misc.SmootherV2;

public class Vehicle extends MovingEntity {

    //a pointer to the world data. So a vehicle can access any obstacle, path, wall or agent data
    private GameWorld world;
    private BehaviorManager behaviorManager;
    //keeps a track of the most recent update time. (some of the steering behaviors make use of this - see Wander)
    private double timeElapsed;
    //buffer for the vehicle shape
    private List<Vector2D> vecVehicleVB = new ArrayList<>();
    
    //some steering behaviors give jerky looking movement. The following members are used to smooth the vehicle's heading
    private SmootherV2 headingSmoother;
    //this vector represents the average of the vehicle's heading vector smoothed over the last few frames
    private Vector2D smoothedHeading;
    //when true, smoothing is active
    private boolean smoothingOn;
       
    public Vehicle(GameWorld world,
            Vector2D position,
            double rotation,
            Vector2D velocity,
            double mass,
            double maxForce,
            double maxSpeed,
            double maxTurnRate,
            double scale) {
        super(position,
                scale,
                velocity,
                maxSpeed,
                new Vector2D(Math.sin(rotation), -Math.cos(rotation)),
                mass,
                new Vector2D(scale, scale),
                maxTurnRate,
                maxForce);

        this.world = world;
        smoothedHeading = new Vector2D(0, 0);
        headingSmoother = new SmootherV2(Params.NUM_SAMPLES_FOR_SMOOTHING, new Vector2D(0.0, 0.0));
        initBuffer();        
        behaviorManager =  new BehaviorManager(this);
    }
    
    /**
     *  fills the vehicle's shape buffer with its vertices
     */
    private void initBuffer() {
        final int numVehicleVerts = 3;
        Vector2D vehicle[] = {
            new Vector2D(-1.0f, 0.6f), 
            new Vector2D(1.0f, 0.0f), 
            new Vector2D(-1.0f, -0.6f)
        };
        //setup the vertex buffers and calculate the bounding radius
        for (int vtx = 0; vtx < numVehicleVerts; ++vtx) {
            vecVehicleVB.add(vehicle[vtx]);
        }
    }

    /**
    *  Updates the vehicle's position and orientation from a series of steering behaviors
    */
    public void update(double time_elapsed) {      
        //update the timeElapsed 
        timeElapsed = time_elapsed;
        //keep a record of its old position so we can update its cell later in this method
        Vector2D oldPos = getPos();
        
        //update the velocity
        //Vector2D acceleration = Vector2D.div(steering.calculateForce(), mass);  //a = f/m
        Vector2D acceleration = Vector2D.div(behaviorManager.calculateForce(), mass);  //a = f/m
        velocity.add(Vector2D.mul(acceleration, time_elapsed)); // v = v + at     
        velocity.truncate(maxSpeed);    //make sure vehicle does not exceed maximum velocity
        
        //update the position
        pos.add(Vector2D.mul(velocity, time_elapsed));
        
        //update the heading if the vehicle has a non zero velocity
        if (velocity.lengthSq() > 0.00000001) {
            heading = Vector2D.vec2DNormalize(velocity);
            side = heading.perp();
        }
        //treat the screen as a toroid
        Vector2D.wrapAround(pos, world.getXClient(), world.getYClient());
        
        //update the vehicle's current cell if space partitioning is turned on
        if (world.isCellSpaceOn()) {
            getWorld().getCellSpace().updateEntity(this, oldPos);
        }
        if (isSmoothingOn()) {
            smoothedHeading = headingSmoother.update(getHeading());
        }
    }
    
    public void render(Cgdi cgdi) {
        List<Vector2D> vertexList;
        if (isSmoothingOn()) {
            vertexList = Transformation.worldTransform(
                    vecVehicleVB,
                    getPos(),
                    smoothedHeading,
                    smoothedHeading.perp(),
                    getScale());
        } 
        else {
            vertexList = Transformation.worldTransform(
                    vecVehicleVB, 
                    getPos(), 
                    getHeading(), 
                    getSide(),
                    getScale());
        }
        cgdi.drawPolygon(vertexList);
    }
    
    public boolean isSmoothingOn() {
        return smoothingOn;
    }

    public void setSmoothingOn() {
        smoothingOn = true;
    }

    public void setSmoothingOff() {
        smoothingOn = false;
    }
 
    public GameWorld getWorld() {
        return world;
    }

    //return time elapsed from last update
    public double getTimeElapsed() {
        return timeElapsed;
    }

    public BehaviorManager getBehaviorManager() {
        return behaviorManager;
    }
    
}
