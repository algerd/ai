/**
 *  Desc:   class to define, manage, and traverse a path (defined by a series of 2D vectors)
 */
package Steering;

import common.D2.Vector2D;
import java.util.List;
import java.util.ListIterator;
import common.misc.Utils;
import common.D2.Transformation;
import common.misc.Cgdi;
import java.util.ArrayList;

public class Path implements Cloneable {
    
    private List<Vector2D> waypointList = new ArrayList<>();
    //points to the current waypoint
    private ListIterator<Vector2D> waypointIterator;
    private Vector2D currentWaypoint = null;
    //flag to indicate if the path should be looped (The last waypoint connected to the first)
    boolean looped;
    
    public Path() {
        looped = false;
    }

    //constructor for creating a path with initial random waypoints. MinX/Y & MaxX/Y define the bounding box of the path.
    public Path(int NumWaypoints, double MinX, double MinY, double MaxX, double MaxY, boolean looped) {       
        this.looped = looped;  
        
        double midX = (MaxX + MinX) / 2.0;
        double midY = (MaxY + MinY) / 2.0;       
        double smaller = Math.min(midX, midY);        
        double spacing = Utils.TwoPi / (double) NumWaypoints;
        
        for (int i = 0; i < NumWaypoints; ++i) {
            double RadialDist = Utils.RandInRange(smaller * 0.2f, smaller);       
            Vector2D temp = new Vector2D(RadialDist, 0.0f);           
            Transformation.vec2DRotateAroundOrigin(temp, i * spacing);          
            temp.x += midX;
            temp.y += midY;           
            waypointList.add(temp);           
        }       
        waypointIterator = waypointList.listIterator();
        if (waypointIterator.hasNext()) {
            currentWaypoint = waypointIterator.next();
        }   
    }
    
    @Override
	public Path clone() {
		Path copy = null;
		try {	     
			copy = (Path)super.clone();
            copy.waypointList = new ArrayList<>();
            for(Vector2D point : this.waypointList) {
                copy.waypointList.add(point.clone());
            }
            copy.waypointIterator = copy.waypointList.listIterator();
            if (copy.waypointIterator.hasNext()) {
                copy.currentWaypoint = copy.waypointIterator.next();
            } 
		} 
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}	
		return copy;
	}
       
    //returns true if the end of the list has been reached
    public boolean finished() {
        return !(waypointIterator.hasNext());
    }

    //moves the iterator on to the next waypoint in the list
    public void setNextWaypoint() {   
        if (!waypointIterator.hasNext()) {
            if (looped) {
                waypointIterator = waypointList.listIterator();
            }
        }
        if (waypointIterator.hasNext()) {
            currentWaypoint = waypointIterator.next();
        }
    }   
    
    public void clearWaypointList() {
        waypointList.clear();
    }
    
    public void render(Cgdi cgdi) {      
        ListIterator<Vector2D> it = waypointList.listIterator();      
        Vector2D wp = it.next();       
        while (it.hasNext()) {
            Vector2D n = it.next();
            cgdi.drawLine(wp, n);           
            wp = n;
        }      
        if (looped) {
            cgdi.drawLine(wp, waypointList.get(0));
        }
    }
    
    public void setWaypointList(List<Vector2D> new_path) {
        waypointList = new_path;
        waypointIterator = waypointList.listIterator();
        currentWaypoint = waypointIterator.next();
    }
   
    public List<Vector2D> getWaypointList() {
        return waypointList;
    }
    
    public Vector2D getCurrentWaypoint() {
        return currentWaypoint;
    }
    
}
