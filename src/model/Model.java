package model;

import resources.Move;
import resources.MoveEfficiency;
import resources.Tile;

import java.util.*;


/**
 *
 * @author Wamdue
 * @version 1.0
 */

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    private int score;
    private int maxTile;
    private Stack previousStates = new Stack();
    private Stack previousScores = new Stack();
    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
        score = 0;
        maxTile = 2;
    }

    private void addTile() {
        int len = getEmptyTiles().size();
        if(len > 0) {
            int val = (Math.random() < 0.9 ? 2 : 4);
            Tile tile = getEmptyTiles().get((int) (len * Math.random()));
            for (int i = 0; i < FIELD_WIDTH; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++)
                    if (gameTiles[i][j].equals(tile)) {
                        gameTiles[i][j].setValue(val);
                        break;
                    }
            }
        }
    }

    public void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                gameTiles[i][j] = new Tile();
        addTile();
        addTile();
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();

        for (int i = 0; i < FIELD_WIDTH; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                if (gameTiles[i][j].isEmpty())
                    emptyTiles.add(gameTiles[i][j]);

        return emptyTiles;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean isChanging = false;

//        for(int z = 0; z < FIELD_WIDTH; z++) {
        for (int i = 0; i < FIELD_WIDTH - 1; i++) {
            Tile next = tiles[i + 1];
            Tile curr = tiles[i];
            if (curr.isEmpty() && !next.isEmpty()) {
                tiles[i] = next;
                tiles[i + 1] = curr;
                isChanging = true;
            }
        }
        //      }

        return isChanging;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean isChanging = false;

        for (int i = 0; i < FIELD_WIDTH - 1; i++) {
            Tile curr = tiles[i];
            Tile next = tiles[i + 1];
            if (curr.getValue() != 0 && curr.getValue() == next.getValue()) {
                int sum = curr.getValue() + next.getValue();
                tiles[i].setValue(sum);
                tiles[i + 1] = new Tile();

                score += sum;
                if (maxTile < sum) maxTile = sum;
                isChanging = true;
                compressTiles(tiles);
            }
        }
        return isChanging;
    }

    public void left() {
        boolean isChanged = false;
        if(isSaveNeeded)
            saveState(gameTiles);
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) || mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }

        if (isChanged) {
            addTile();
            isSaveNeeded = true;
        }
    }

    public void right()
    {
        saveState(gameTiles);
        rotateToTheRight();
        rotateToTheRight();
        left();
        rotateToTheRight();
        rotateToTheRight();

    }

    public void up()
    {
        saveState(gameTiles);
        rotateToTheRight();
        rotateToTheRight();
        rotateToTheRight();
        left();
        rotateToTheRight();
    }

    public void down()
    {
        saveState(gameTiles);
        rotateToTheRight();
        left();
        rotateToTheRight();
        rotateToTheRight();
        rotateToTheRight();

    }

    private void rotateToTheRight()
    {
        int size = FIELD_WIDTH - 1;
        for(int i = 0; i < FIELD_WIDTH / 2; i++)
        {
            for(int j = i; j < size - i; j++)
            {
                Tile temp = gameTiles[i][j];
                gameTiles[i][j] = gameTiles[size - j][i];
                gameTiles[size - j][i] = gameTiles[size - i][size - j];
                gameTiles[size - i][size - j] = gameTiles[j][size - i];
                gameTiles[j][size - i] = temp;
            }
        }
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove()
    {
        if(!getEmptyTiles().isEmpty())
            return true;

        for(int i = 0; i < FIELD_WIDTH; i++)
            for(int j = 1; j < FIELD_WIDTH; j++)
                if(gameTiles[i][j].getValue() == gameTiles[i][j - 1].getValue())
                    return true;

        for(int i = 1; i < FIELD_WIDTH; i++)
            for(int j = 0;j < FIELD_WIDTH; j++)
                if(gameTiles[i][j].getValue() == gameTiles[i-1][j].getValue())
                    return true;

        return false;
    }

    private void saveState(Tile[][] tiles)
    {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for(int i = 0; i < FIELD_WIDTH; i++)
            for(int j= 0; j < FIELD_WIDTH; j++)
                tempTiles[i][j] = new Tile(tiles[i][j].getValue());

        previousStates.push(tempTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback()
    {
        if(!previousScores.isEmpty() && !previousStates.isEmpty()) {
            score = (int) previousScores.pop();
            gameTiles = (Tile[][]) previousStates.pop();
        }
    }

    public void randomMove()
    {
        //if(canMove()) {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0: left(); break;
            case 1: right(); break;
            case 2: up(); break;
            case 3: down(); break;
        }
        // }
    }

    public boolean hasBoardChanged()
    {

        if(!previousStates.isEmpty())
        {
            int sum1 = 0;
            int sum2 = 0;
            Tile[][] temp = (Tile[][])previousStates.peek();

            for(int i = 0; i < FIELD_WIDTH; i++)
            {
                for(int j = 0; j < FIELD_WIDTH; j++)
                {
                    sum1 += gameTiles[i][j].getValue();
                    sum2 += temp[i][j].getValue();
                }
            }
            return  sum1 != sum2;
        }
        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move)
    {
        MoveEfficiency me;
        move.move();
        if(hasBoardChanged())
        {
            me = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }else
        {
            me = new MoveEfficiency(-1, 0, move);
        }

        rollback();
        return me;
    }

    public void autoMove()
    {
        PriorityQueue<MoveEfficiency> prior = new PriorityQueue(4, Collections.reverseOrder());
        prior.add(getMoveEfficiency(this::left));
        prior.add(getMoveEfficiency(this::right));
        prior.add(getMoveEfficiency(this::up));
        prior.add(getMoveEfficiency(this::down));

        prior.poll().getMove().move();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMaxTile() {
        return maxTile;
    }

    public void setMaxTile(int maxTile) {
        this.maxTile = maxTile;
    }
}