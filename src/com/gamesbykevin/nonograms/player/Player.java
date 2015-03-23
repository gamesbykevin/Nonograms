package com.gamesbykevin.nonograms.player;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.puzzles.Puzzle;
import com.gamesbykevin.nonograms.shared.IElement;

import java.awt.Graphics;

/**
 * This class will represent a player attempting to solve a puzzle
 * @author GOD
 */
public abstract class Player implements IElement, Disposable
{
    //the board where the player makes their selections
    private Puzzle board;
    
    protected Player()
    {
        
    }
    
    @Override
    public void dispose()
    {
        if (board != null)
        {
            board.dispose();
            board = null;
        }
    }
    
    public Puzzle getPuzzle()
    {
        return this.board;
    }
    
    /**
     * Create a new puzzle board
     * @param puzzle The puzzle containing the desired dimensions
     */
    public void create(final Puzzle puzzle)
    {
        create(puzzle.getCols(), puzzle.getRows());
    }
    
    /**
     * Create a new puzzle board
     * @param cols Dimensions of the puzzle board
     * @param rows Dimensions of the puzzle board
     */
    public void create(final int cols, final int rows)
    {
        board = new Puzzle(cols, rows);
    }
}