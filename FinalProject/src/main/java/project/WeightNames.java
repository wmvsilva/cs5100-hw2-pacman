package project;

import pacman.game.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 8/15/2016.
 */
public class WeightNames
{
    public static List<String> getNames()
    {
        List<String> names = new ArrayList<>();

        names.add("pacManEaten");
        names.add("numActivePills");
        names.add("numActivePowerPills");
        names.add("score");
        names.add("pacManDistanceToBlinky");
        names.add("pacManDistanceToInky");
        names.add("pacManDistanceToPinky");
        names.add("pacManDistanceToSue");
        names.add("pacManDistanceToNearestGhostIfNotEdible");
        names.add("pacManNearestGhostEdible");
        names.add("pacManDistanceToNearestGhostUnder20");
        names.add("pacManNearestGhostEdibleAndUnder40");
        names.add("pacManDistanceToNearestPill");

        names.add("numTotalActivePills");
        names.add("numLevel");
        names.add("levelTime");
        names.add("totalGameTime");
        names.add("numGhostsEaten");
        names.add("livesRemaining");
        names.add("gameOver");
        names.add("wasPillEaten");
        names.add("wasPowerPillEaten");

        names.add("pacManNumPossibleMoves");
        names.add("blinkyNumPossibleMoves");
        names.add("inkyNumPossibleMoves");
        names.add("pinkyNumPossibleMoves");
        names.add("sueNumPossibleMoves");
        names.add("distanceToNextNearestPillMOreThan10IfPillJustEaten");
        names.add("distanceToNearestPillAboveFive");

        return names;
    }
}
