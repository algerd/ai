
package SimpleSoccer;

import common.D2.Vector;
import common.Game.Region;
import common.Time.Regulator;
import common.misc.Cgdi;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Class to determine the best spots for a suppoting soccermplayer to move to.
 */
public class SupportSpotCalculator {

    //a data structure to hold the values and positions of each spot
    private class SupportSpot {
        Vector pos;
        double score;

        SupportSpot(Vector pos, double value) {
            this.pos = new Vector(pos);
            this.score = value;
        }
    }
    private SoccerTeam team;
    private List<SupportSpot> spotList = new ArrayList<>();
    //a pointer to the highest valued spot from the last update
    private SupportSpot bestSupportingSpot;
    //this will regulate how often the spots are calculated (default is one update per second)
    private Regulator regulator;

    public SupportSpotCalculator(int numX, int numY, SoccerTeam team) {
        this.team = team;
        final Region playingField = team.getPitch().getPlayingArea();

        //calculate the positions of each sweet spot, create them and store them in m_Spots
        double heightOfSSRegion = playingField.height() * 0.8;
        double widthOfSSRegion = playingField.width() * 0.9;
        double sliceX = widthOfSSRegion / numX;
        double sliceY = heightOfSSRegion / numY;

        double left = playingField.left() + (playingField.width() - widthOfSSRegion) / 2.0 + sliceX / 2.0;
        double right = playingField.right() - (playingField.width() - widthOfSSRegion) / 2.0 - sliceX / 2.0;
        double top = playingField.top() + (playingField.height() - heightOfSSRegion) / 2.0 + sliceY / 2.0;

        for (int x = 0; x < (numX / 2) - 1; ++x) {
            for (int y = 0; y < numY; ++y) {
                if (team.getColor() == SoccerTeam.blue) {
                    spotList.add(new SupportSpot(new Vector(left + x * sliceX, top + y * sliceY), 0.0));
                } else {
                    spotList.add(new SupportSpot(new Vector(right - x * sliceX, top + y * sliceY), 0.0));
                }
            }
        }
        regulator = new Regulator(ParamLoader.getInstance().SupportSpotUpdateFreq);
    }

    /**
     * draws the spots to the screen as a hollow circles. The higher the 
     * score, the bigger the circle. The best supporting spot is drawn in bright green.
     */
    public void render() {
        Cgdi.gdi.HollowBrush();
        Cgdi.gdi.GreyPen();
        for (int spt = 0; spt < spotList.size(); ++spt) {
            Cgdi.gdi.Circle(spotList.get(spt).pos, spotList.get(spt).score);
        }
        if (bestSupportingSpot != null) {
            Cgdi.gdi.GreenPen();
            Cgdi.gdi.Circle(bestSupportingSpot.pos, bestSupportingSpot.score);
        }
    }

    /**
     * this method iterates through each possible spot and calculates its
     * score.
     */
    public Vector determineBestSupportingPosition() {
        //only update the spots every few frames                              
        if (!regulator.isReady() && bestSupportingSpot != null) {
            return bestSupportingSpot.pos;
        }
        //reset the best supporting spot
        bestSupportingSpot = null;

        double BestScoreSoFar = 0.0;
        ListIterator<SupportSpot> it = spotList.listIterator();
        while (it.hasNext()) {
            SupportSpot curSpot = it.next();
            //first remove any previous score. (the score is set to one so that
            //the viewer can see the positions of all the spots if he has the aids turned on)
            curSpot.score = 1.0;

            //Test 1. is it possible to make a safe pass from the ball's position to this position?
            if (team.isPassSafeFromAllOpponents(
                    team.getControllingPlayer().getPosition(),
                    curSpot.pos,
                    null,
                    ParamLoader.getInstance().MaxPassingForce)) 
            {
                curSpot.score += ParamLoader.getInstance().Spot_PassSafeScore;
            }

            //Test 2. Determine if a goal can be scored from this position.  
            if (team.canShoot(curSpot.pos, ParamLoader.getInstance().MaxShootingForce)) {
                curSpot.score += ParamLoader.getInstance().Spot_CanScoreFromPositionScore;
            }

            //Test 3. calculate how far this spot is away from the controlling
            //player. The further away, the higher the score. Any distances further
            //away than OptimalDistance pixels do not receive a score.
            if (team.getSupportingPlayer() != null) { //TODO: nema tu byt m_pTeam.getControllingPlayer()??
                final double OptimalDistance = 200.0;
                double temp = Math.abs(OptimalDistance - Vector.dist(team.getControllingPlayer().getPosition(), curSpot.pos));
                if (temp < OptimalDistance) {
                    //normalize the dist and add it to the score
                    curSpot.score += ParamLoader.getInstance().Spot_DistFromControllingPlayerScore * (OptimalDistance - temp) / OptimalDistance;
                }
            }
            //check to see if this spot has the highest score so far
            if (curSpot.score > BestScoreSoFar) {
                BestScoreSoFar = curSpot.score;
                bestSupportingSpot = curSpot;
            }
        }
        return bestSupportingSpot.pos;
    }

    /**
     * returns the best supporting spot if there is one. If one hasn't been
     * calculated yet, this method calls determineBestSupportingPosition and returns the result.
     */
    public Vector getBestSupportingSpot() { 
        return (bestSupportingSpot != null) ? bestSupportingSpot.pos : determineBestSupportingPosition();
    }
    
}
