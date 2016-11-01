/** 
 *  Desc:   Definition of a simple vehicle that uses steering behaviors
 */
package Chapter3Steering;

import java.util.ArrayList;
import common.misc.SmootherV2;
import common.D2.Vector2D;
import java.util.List;
import common.misc.Cgdi;
import common.D2.Transformation;

public class Vehicle extends MovingEntity {

    //a pointer to the world data. So a vehicle can access any obstacle, path, wall or agent data
    private GameWorld world;
    //the steering behavior class
    private SteeringBehavior steering;
    //some steering behaviors give jerky looking movement. The following members are used to smooth the vehicle's heading
    private SmootherV2<Vector2D> headingSmoother;
    //this vector represents the average of the vehicle's heading vector smoothed over the last few frames
    private Vector2D smoothedHeading;
    //when true, smoothing is active
    private boolean smoothingOn;
    //keeps a track of the most recent update time. (some of the steering behaviors make use of this - see Wander)
    private double timeElapsed;
    //buffer for the vehicle shape
    private List<Vector2D> vecVehicleVB = new ArrayList<Vector2D>();

    /**
     *  fills the vehicle's shape buffer with its vertices
     */
    private void initBuffer() {
        final int NumVehicleVerts = 3;
        Vector2D vehicle[] = {new Vector2D(-1.0f, 0.6f), new Vector2D(1.0f, 0.0f), new Vector2D(-1.0f, -0.6f)};
        //setup the vertex buffers and calculate the bounding radius
        for (int vtx = 0; vtx < NumVehicleVerts; ++vtx) {
            vecVehicleVB.add(vehicle[vtx]);
        }
    }

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
        smoothedHeading = new Vector2D(0, 0);
        initBuffer();
        steering = new SteeringBehavior(this);
        headingSmoother = new SmootherV2<Vector2D>(ParamLoader.prm.NumSamplesForSmoothing, new Vector2D(0.0, 0.0));
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
        if (isSmoothingOn()) {
            smoothedHeading = headingSmoother.Update(getHeading());
        }
    }

    public void render(boolean pr) {
        //render neighboring vehicles in different colors if requested
        if (world.isRenderNeighbors()) {
            if (getId() == 0) {
                Cgdi.gdi.RedPen();
            } else if (isTagged()) {
                Cgdi.gdi.GreenPen();
            } else {
                Cgdi.gdi.BluePen();
            }
        } else {
            Cgdi.gdi.BluePen();
        }
        if (getSteering().isInterposeOn()) {
            Cgdi.gdi.RedPen();
        }
        if (getSteering().isHideOn()) {
            Cgdi.gdi.GreenPen();
        }
        //a vector to hold the transformed vertices
        List<Vector2D> vecVehicleVBTrans;
        if (isSmoothingOn()) {
            vecVehicleVBTrans = Transformation.WorldTransform(vecVehicleVB,
                    getPos(),
                    getSmoothedHeading(),
                    getSmoothedHeading().perp(),
                    getScale());
        } else {
            vecVehicleVBTrans = Transformation.WorldTransform(vecVehicleVB,
                    getPos(),
                    getHeading(),
                    getSide(),
                    getScale());
        }
        Cgdi.gdi.ClosedShape(vecVehicleVBTrans);
        //render any visual aids / and or user options
        if (world.isViewKeys()) {
            getSteering().RenderAids();
        }
    }
 
    public SteeringBehavior getSteering() {
        return steering;
    }

    public GameWorld getWorld() {
        return world;
    }

    public Vector2D getSmoothedHeading() {
        return new Vector2D(smoothedHeading);
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

    public void toggleSmoothing() {
        smoothingOn = !smoothingOn;
    }

    //return time elapsed from last update
    public double getTimeElapsed() {
        return timeElapsed;
    }

}
