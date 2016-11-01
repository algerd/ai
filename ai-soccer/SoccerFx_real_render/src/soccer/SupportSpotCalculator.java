
package soccer;

import common.D2.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Class to determine the best spots for a suppoting soccermplayer to move to.
 */
public class SupportSpotCalculator {

    //a data structure to hold the values and positions of each spot
    public class SupportSpot {
        public Vector pos;
        public double score;

        SupportSpot(Vector pos, double value) {
            this.pos = new Vector(pos);
            this.score = value;
        }
    }
    private SoccerTeam team;
    private List<SupportSpot> spotList = new ArrayList<>();
    //a pointer to the highest valued spot from the last update
    private SupportSpot bestSupportingSpot;   

    public SupportSpotCalculator(int numX, int numY, SoccerTeam team) {
        this.team = team;
        
        //calculate the positions of each sweet spot, create them and store them in m_Spots
        double width = Field.WIDTH_FIELD * 0.95;
        double length = Field.LENGTH_FIELD * 0.95;
        double sliceX = length / numX;
        double sliceY = width / numY;

        double left = (Field.LENGTH_FIELD - length) / 2.0 + sliceX / 2.0;
        double right = Field.LENGTH_FIELD - (Field.LENGTH_FIELD - length) / 2.0 - sliceX / 2.0;
        double top = (Field.WIDTH_FIELD - width) / 2.0 + sliceY / 2.0;

        for (int x = 0; x < (numX / 2) - 1; ++x) {
            for (int y = 0; y < numY; ++y) {
                spotList.add((team.getColor() == SoccerTeam.blue) ?
                    new SupportSpot(new Vector(left + x * sliceX, top + y * sliceY), 0.0) :
                    new SupportSpot(new Vector(right - x * sliceX, top + y * sliceY), 0.0)    
                );
            }
        }
    }

    /**
     * this method iterates through each possible spot and calculates its
     * score.
     */
    public Vector determineBestSupportingPosition() {
        //only update the spots every few frames                              
        if (!team.isReadyForNextSpot() && bestSupportingSpot != null) {
            return bestSupportingSpot.pos;
        }
        //reset the best supporting spot
        bestSupportingSpot = null;

        double bestScoreSoFar = 0.0;
        ListIterator<SupportSpot> it = spotList.listIterator();
        while (it.hasNext()) {
            SupportSpot curSpot = it.next();
            //first remove any previous score. (the score is set to one so that
            //the viewer can see the positions of all the spots if he has the aids turned on)
            curSpot.score = 1.0;

            //Test 1. is it possible to make a safe pass from the ball's position to this position?
            if (team.isPassSafeFromAllOpponents(team.getControllingPlayer().getPosition(),
                    curSpot.pos,
                    null,
                    Params.BALL_VELOCITY_PASS_TACT)) 
            {
                curSpot.score += Params.SPOT_PASS_SAFE;
            }

            //Test 2. Determine if a goal can be scored from this position.  
            if (team.canShoot(curSpot.pos, Params.BALL_VELOCITY_SHOOT_TACT)) {
                curSpot.score += Params.SPOT_CAN_SCORE_FROM_POSITION;
            }

            //Test 3. calculate how far this spot is away from the controlling player. The further away, the higher the score. 
            if (team.getSupportingPlayer() != null) {
                double distToSpot = team.getControllingPlayer().getPosition().dist(curSpot.pos);
                if (distToSpot < Params.MAX_DIST_TO_SPOT) {
                    curSpot.score += Params.SPOT_DIST_FROM_CONTROLLING_PLAYER * (Params.MAX_DIST_TO_SPOT - distToSpot) / Params.MAX_DIST_TO_SPOT;
                }
            }
            //check to see if this spot has the highest score so far
            if (curSpot.score > bestScoreSoFar) {
                bestScoreSoFar = curSpot.score;
                bestSupportingSpot = curSpot;
            }
        }
        team.resetSpotTimer();
        return bestSupportingSpot.pos;
    }

    /**
     * returns the best supporting spot if there is one. If one hasn't been
     * calculated yet, this method calls determineBestSupportingPosition and returns the result.
     */
    public Vector getBestSupportingSpotVector() { 
        return (bestSupportingSpot != null) ? bestSupportingSpot.pos : determineBestSupportingPosition();
    }
     
    public List<SupportSpot> getSpotList() {
        return spotList;
    }

    public SupportSpot getBestSupportingSpot() {
        return bestSupportingSpot;
    }
          
}
