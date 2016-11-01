
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
            double max_force,
            double max_speed,
            double max_turn_rate,
            double scale) {
        super(position,
                scale,
                velocity,
                max_speed,
                new Vector2D(Math.sin(rotation), -Math.cos(rotation)),
                mass,
                new Vector2D(scale, scale),
                max_turn_rate,
                max_force);

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
        //update the time elapsed
        timeElapsed = time_elapsed;
        //keep a record of its old position so we can update its cell later in this method
        Vector2D oldPos = getPos();
        Vector2D steeringForce;
        //calculate the combined force from each steering behavior in the vehicle's list
        steeringForce = steering.calculate();
        //Acceleration = Force/Mass
        Vector2D acceleration = Vector2D.div(steeringForce, mass);
        //update velocity
        velocity.add(Vector2D.mul(acceleration, time_elapsed));
        //make sure vehicle does not exceed maximum velocity
        velocity.truncate(maxSpeed);
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
        if (getSteering().isSpacePartitioningOn()) {
            getWorld().getCellSpace().updateEntity(this, oldPos);
        }
    }

    public List<Vector2D> render() {
        //a vector to hold the transformed vertices
        List<Vector2D> vec = Transformation.worldTransform(vecVehicleVB, getPos(), getHeading(), getSide(),getScale());
        return vec;
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
