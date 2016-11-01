/**
 * 
 *  Desc: Template class to help calculate the average value of a history
 *        of values. This can only be used with types that have a 'zero'
 *        value and that have the += and / operators overloaded.
 */
package common.misc;

import common.D2.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SmootherV2 {
    //this holds the history
    private List<Vector2D> history = new ArrayList<>();
    private int nextUpdateSlot;
    //an example of the 'zero' value of the type to be smoothed. This would be something like Vector2D(0,0)
    private Vector2D zeroValue;

    //to instantiate a Smoother pass it the number of samples you want to use in the smoothing, and an exampe of a 'zero' type
    public SmootherV2(int sampleSize, Vector2D zeroValue) {
        for(int i = 0; i < sampleSize; i++)
              history.add(zeroValue);
        this.zeroValue = zeroValue;
        nextUpdateSlot = 0;
    }

    //each time you want to get a new average, feed it the most recent value and this method will return an average over the last SampleSize updates
    public Vector2D update(Vector2D mostRecentValue) {  
        //overwrite the oldest value with the newest
        history.set(nextUpdateSlot++, mostRecentValue);

        //make sure m_iNextUpdateSlot wraps around. 
        if (nextUpdateSlot == history.size()) nextUpdateSlot = 0;

        //now to calculate the average of the history list c++ code make a copy here, I use Zero method instead.
        //Another approach could be creating public clone() method in Vector2D ...
        Vector2D sum = zeroValue; 
        sum.zero();
        ListIterator<Vector2D> it = history.listIterator();
        while(it.hasNext()) {
          sum.add(it.next());
        }

        sum.div((double)history.size());
        return sum;
    }
}
