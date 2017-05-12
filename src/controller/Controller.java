package controller;

import model.Model;
import resources.Tile;
import view.View;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Wamdue
 * @version 1.0
 */

public class Controller extends KeyAdapter {
    private Model model;
    private View view;
    private final static int WINNING_TILE = 2048;

    public Controller (Model model)
    {
        this.model = model;
        this.view = new View(this);
    }

    public void resetGame()
    {
        view.setGameLost(false);
        view.setGameWon(false);
        model.setScore(0);
        model.resetGameTiles();
    }

    public int getScore()
    {
        return model.getScore();
    }

    public Tile[][] getGameTiles()
    {
        return model.getGameTiles();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            resetGame();

        if(!model.canMove())
            view.setGameLost(true);

        if(!view.isGameLost() && !view.isGameWon()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    model.left();
                    break;
                case KeyEvent.VK_RIGHT:
                    model.right();
                    break;
                case KeyEvent.VK_UP:
                    model.up();
                    break;
                case KeyEvent.VK_DOWN:
                    model.down();
                    break;
                case KeyEvent.VK_Z:
                    model.rollback();
                    break;
                case KeyEvent.VK_R:
                    model.randomMove();
                    break;
                case KeyEvent.VK_A:
                    model.autoMove();
                    break;
            }
        }

        if(model.getMaxTile() == WINNING_TILE)
            view.setGameWon(true);;

        view.repaint();
    }

    public View getView() {
        return view;
    }
}