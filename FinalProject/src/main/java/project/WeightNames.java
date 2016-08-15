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
        names.add("pacManDistanceToNearestGhost");
        names.add("pacManNearestGhostEdible");
        names.add("pacManDistanceToNearestGhostUnder20");
        names.add("pacManNearestGhostEdibleAndUnder40");
        names.add("pacManDistanceToNearestPill");

        return names;
    }
}
