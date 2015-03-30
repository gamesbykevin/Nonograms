package com.gamesbykevin.nonograms.player;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.puzzles.Puzzle;
import com.gamesbykevin.nonograms.puzzles.Puzzles;
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
    
    //the location we will highlight to make the puzzle more friendly for the user
    private int highlightCol = 0, highlightRow = 0;
    
    //do we highlight the player current location
    private boolean showHighlight = true;
    
    protected Player()
    {
        
    }
    
    /**
     * Set the highlight
     * @param showHighlight true if we want to highlight the players current location, false otherwise
     */
    public void setHighlight(final boolean showHighlight)
    {
        this.showHighlight = showHighlight;
    }
    
    /**
     * Do we highlight the players current location
     * @return true=yes, false=no
     */
    public boolean hasHighlight()
    {
        return this.showHighlight;
    }
    
    /**
     * Set the highlighted Column of the current location for this player
     * @param highlightCol The current column location of the player
     */
    protected void setHighlightCol(final int highlightCol)
    {
        this.highlightCol = highlightCol;
    }
    
    /**
     * Get the highlighted column
     * @return The column where our player is currently located
     */
    protected int getHighlightCol()
    {
        return this.highlightCol;
    }
    
    /**
     * Set the highlighted Row of the current location for this player
     * @param highlightRow The current row location of the player
     */
    protected void setHighlightRow(final int highlightRow)
    {
        this.highlightRow = highlightRow;
    }
    
    /**
     * Get the highlighted row
     * @return The row where our player is currently located
     */
    protected int getHighlightRow()
    {
        return this.highlightRow;
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
     * @param puzzle The puzzle we want to copy for this player
     */
    public void create(final Puzzle puzzle)
    {
        board = new Puzzle(puzzle);
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (hasHighlight())
        {
            //draw highlight first of our location
            getPuzzle().renderHighlight(graphics, Puzzles.START_X, Puzzles.START_Y, getHighlightCol(), getHighlightRow());
        }
        
        //then draw our puzzle
        getPuzzle().render(graphics, Puzzles.START_X, Puzzles.START_Y);
    }
}