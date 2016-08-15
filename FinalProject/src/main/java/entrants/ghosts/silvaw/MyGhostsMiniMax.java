package entrants.ghosts.silvaw;


import com.google.common.base.Optional;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;
import project.Heuristic;
import project.MiniMax;

import java.util.EnumMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by pwillic on 25/02/2016.
 */
public class MyGhostsMiniMax extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    private MiniMax miniMax;

    public MyGhostsMiniMax(Heuristic miniMax)
    {
        this.miniMax = new MiniMax(miniMax);
    }

    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(Game game, long timeDue)
    {
        MiniMax.MoveNumber moveNumber = miniMax.createMiniMaxTreeAndGetBestMove(game, 4, false, Optional.<Integer>absent(), Optional.<Integer>absent());
        Map<Constants.GHOST, Constants.MOVE> ghostMoves = moveNumber.ghostMoves;
        if (moveNumber.hValue == Integer.MIN_VALUE) {
            System.out.println("Terminal node");
            return null;
        }

        EnumMap<Constants.GHOST, Constants.MOVE> enumMap = new EnumMap<>(Constants.GHOST.class);
        for (Map.Entry<Constants.GHOST, Constants.MOVE> ghostMove : ghostMoves.entrySet()) {
            enumMap.put(ghostMove.getKey(), ghostMove.getValue());
        }
        return enumMap;
    }
}
