package resources;

/**
 *
 * @author Wamdue
 * @version 1.0
 */

public class MoveEfficiency implements Comparable<MoveEfficiency>{
    private int numberOfEmptyTiles;
    private Move move;
    private int score;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move)
    {
        this.move = move;
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        if (numberOfEmptyTiles != o.numberOfEmptyTiles) {
            return Integer.compare(numberOfEmptyTiles, o.numberOfEmptyTiles);
        } else {
            return Integer.compare(score, o.score);
        }
    }
}