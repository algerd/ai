
package Steering;

import java.util.ArrayList;
import common.D2.Vector2D;
import java.util.List;
import common.D2.Transformation;

public class Vehicle extends MovingEntity {

    //a pointer to the world data. So a vehicle can access any obstacle, path, wall or agent data
    private GameWorld world;
    //the steering behavior class
    private SteeringBehavior steering;
    //keeps a track of the most recent update time. (some of the steering behaviors make use of this - see Wander)
    private double timeElapsed;
    //buffer for the vehicle shape
    private List<Vector2D> vecVehicleVB = new ArrayList<Vector2D>();

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
        initBuffer();
        steering = new SteeringBehavior(this);
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
              
        //update the velocity
        Vector2D acceleration = Vector2D.div(steering.calculateForce(), mass);  //a = f/m
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
    }

    
    public List<Vector2D> getTransformVehicle() {
        return Transformation.worldTransform(vecVehicleVB, getPos(), getHeading(), getSide(),getScale());
    }
 
    public SteeringBehavior getSteering() {
        return steering;
    }

    public GameWorld getWorld() {
        return world;
    }

    //return time elapsed from last update
    public double getTimeElapsed() {
        return timeElapsed;
    }

}
