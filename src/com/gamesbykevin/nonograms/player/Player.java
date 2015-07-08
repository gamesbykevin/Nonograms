package com.gamesbykevin.nonograms.player;


import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.player.stat.Stats;
import com.gamesbykevin.nonograms.puzzles.Puzzle;
import com.gamesbykevin.nonograms.puzzles.Puzzles;
import com.gamesbykevin.nonograms.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

/**
 * This class will represent a player attempting to solve a puzzle
 * @author GOD
 */
public abstract class Player extends Sprite implements IElement, Disposable
{
    //the board where the player makes their selections
    private Puzzle board;
    
    //the game stats
    private Stats stats;
    
    //the location we will highlight to make the puzzle more friendly for the user
    private int highlightCol = 0, highlightRow = 0;
    
    //do we highlight the player current location
    private boolean showHighlight = true;
    
    //has hint enabled
    private boolean hintEnabled = false;
    
    protected Player(final Image image, final Image backgroundStatImage)
    {
        super.setImage(image);
        
        //create stat object
        this.stats = new Stats(backgroundStatImage);
    }
    
    public void setHintEnabled(final boolean hintEnabled)
    {
        this.hintEnabled = hintEnabled;
    }
    
    public boolean hasHintEnabled()
    {
        return this.hintEnabled;
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
     * Do we highlight the location?<br>
     * The location specified will only return true if highlight is enabled and matches the player highlight location
     * @param col The column we want to check
     * @param row The row we want to check
     * @return if highlight is enabled and the column or row match the location return true, otherwise return false
     */
    public boolean hasHighlight(final int col, final int row)
    {
        //if we have highlight enabled
        if (this.showHighlight)
        {
            //now check if the location has a match
            if (col == getHighlightCol() || row == getHighlightRow())
                return true;
        }
        
        //we don't need to highlight
        return false;
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
    private int getHighlightCol()
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
    private int getHighlightRow()
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
        
        if (stats != null)
        {
            stats.dispose();
            stats = null;
        }
    }
    
    public Puzzle getPuzzle()
    {
        return this.board;
    }
    
    public Stats getStats()
    {
        return this.stats;
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
    public void update(final Engine engine) throws Exception
    {
        //update stats
        getStats().update(engine);
    }
    
    /**
     * Perform the necessary steps to setup the next level
     * @param engine Object containing all game elements
     */
    protected void setupNextLevel(final Engine engine)
    {
        //next level
        getStats().nextLevel();

        //set the misc message based on the difficulty
        switch (engine.getManager().getPuzzles().getDifficulty())
        {
            case VeryEasy:
                getStats().setMiscDesc("Difficulty: Very Easy");
                break;
                
            case Easy:
                getStats().setMiscDesc("Difficulty: Easy");
                break;
                
            case Medium:
                getStats().setMiscDesc("Difficulty: Medium");
                break;
                
            case Hard:
                getStats().setMiscDesc("Difficulty: Hard");
                break;
        }

        //render new image
        getStats().render();

        //reset timer
        getStats().getTimer().reset();

        //reset player board
        getPuzzle().reset();

        //remove this puzzle
        engine.getManager().getPuzzles().removeCurrent();

        //pick next random level
        engine.getManager().getPuzzles().setRandomLevel(engine.getRandom());
        
        //if hint enabled, apply hint
        if (hasHintEnabled())
            applyHint(engine.getManager().getPuzzles().getPuzzle(), engine.getRandom());
        
        //start highlighting again
        setHighlight(true);
    }
    
    /**
     * Apply the hint to the player board.<br>
     * A random location will be picked.<br>
     * All vertical and horizontal columns, rows will be revealed
     * @param puzzle The current puzzle
     * @param random Object used to make random decisions
     */
    public void applyHint(final Puzzle puzzle, final Random random)
    {
        //pick random column
        final int randCol = random.nextInt(puzzle.getCols());
        
        //pick random row
        final int randRow = random.nextInt(puzzle.getRows());
        
        //reveal column
        for (int col = 0; col < puzzle.getCols(); col++)
        {
            switch (puzzle.getKeyValue(col, randRow))
            {
                case Puzzles.KEY_EMPTY:
                    getPuzzle().setKeyValue(col, randRow, Puzzles.KEY_MARK);
                    break;
                    
                default:
                    getPuzzle().setKeyValue(col, randRow, puzzle.getKeyValue(col, randRow));
                    break;
            }
        }
        
        //reveal row
        for (int row = 0; row < puzzle.getRows(); row++)
        {
            switch (puzzle.getKeyValue(randCol, row))
            {
                case Puzzles.KEY_EMPTY:
                    getPuzzle().setKeyValue(randCol, row, Puzzles.KEY_MARK);
                    break;
                    
                default:
                    getPuzzle().setKeyValue(randCol, row, puzzle.getKeyValue(randCol, row));
                    break;
            }
        }
    }
    
    public void checkComplete(final Engine engine) throws Exception
    {
        final Puzzle current = engine.getManager().getPuzzles().getPuzzle();
        
        if (current.hasMatch(getPuzzle()))
        {
            //mark the current puzzle as solved
            current.markSolved();
            
            //mark ours solved as well
            getPuzzle().markSolved();
            
            //remove all existing marks to display the full picture
            getPuzzle().remove(Puzzles.KEY_MARK);
            
            //stop highlighting
            setHighlight(false);
        }
    }
    
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        //then draw our puzzle
        getPuzzle().render(graphics, this, Puzzles.START_X, Puzzles.START_Y);
        
        //draw the stats
        getStats().render(graphics);
    }
    
    public void renderDesc(final Graphics graphics, final String desc)
    {
        //draw description
        graphics.drawString(desc, Puzzles.START_X, Puzzles.START_Y - 1);
    }
}