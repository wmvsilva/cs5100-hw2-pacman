package minimax;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains a list of all the feature weight names used with {@link SettableHeuristic}
 */
public class FeatureWeightNames
{
    /**
     * @return list of the names of all of the features in {@link SettableHeuristic}
     */
    public static List<String> getNames()
    {
        List<String> names = Lists.newArrayList();

        names.add("pacManEaten");
        names.add("numActivePills");
        names.add("numActivePowerPills");
        names.add("score");
        names.add("pacManDistanceToUnedibleBlinky");
        names.add("pacManDistanceToUnedibleInky");
        names.add("pacManDistanceToUnediblePinky");
        names.add("pacManDistanceToUnedibleSue");
        //names.add("pacManDistanceToNearestGhostIfNotEdible");
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

        names.add("pacManLastMoveLeft");
        names.add("pacManLastMoveDown");

        names.add("pacManDistanceToNearestGhostIfUnder10");
        names.add("pacManDistanceToNearestGhostIfUnder5");

        names.add("likelyNotStuck");
        names.add("reversedDirection");

        names.add("pacManDistanceToNearestGhostEdibleIfUnder10");
        names.add("pacManDistanceToNearestGhostEdibleIfUnder5");

        return names;
    }

    /**
     * @return a map of feature names to feature weights with all feature weights being 0
     */
    public static Map<String, Integer> generateZeroWeights()
    {
        Map<String, Integer> zeroWeightMap = new HashMap<>();
        for (String name : getNames()) {
            zeroWeightMap.put(name, 0);
        }
        return zeroWeightMap;
    }
}
