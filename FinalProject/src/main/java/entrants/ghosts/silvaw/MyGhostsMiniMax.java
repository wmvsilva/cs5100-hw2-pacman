package entrants.ghosts.silvaw;


import com.google.common.base.Optional;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;
import project.MiniMax;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by pwillic on 25/02/2016.
 */
public class MyGhostsMiniMax extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(Game game, long timeDue)
    {
        Map<Constants.GHOST, Constants.MOVE> ghostMoves =
                MiniMax.createMiniMaxTreeAndGetBestMove(game, 4, false, Optional.<Integer>absent(), Optional.<Integer>absent()).ghostMoves;

        EnumMap<Constants.GHOST, Constants.MOVE> enumMap = new EnumMap<>(Constants.GHOST.class);
        for (Map.Entry<Constants.GHOST, Constants.MOVE> ghostMove : ghostMoves.entrySet()) {
            enumMap.put(ghostMove.getKey(), ghostMove.getValue());
        }
        return enumMap;
    }
}
