package com.gamesbykevin.nonograms.player;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.puzzles.Puzzles;
import com.gamesbykevin.nonograms.resources.GameAudio.Keys;

import java.awt.Image;
import java.awt.event.KeyEvent;

/**
 * This is where we will manage the human input
 * @author GOD
 */
public final class Human extends Player
{
    public Human(final Image image, final Image backgroundStatImage)
    {
        super(image, backgroundStatImage);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //don't continue if the puzzle has been solved
        if (engine.getManager().getPuzzles().getPuzzle().hasSolved())
        {
            //if space bar was pressed
            if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_SPACE))
            {
                //reset input
                engine.getKeyboard().reset();
                
                //stop all audio
                engine.getResources().stopAllSound();
                
                //setup next level
                super.setupNextLevel(engine);
                
                //only resume playing main theme, if puzzles still exist
                if (engine.getManager().getPuzzles().hasPuzzles())
                    engine.getResources().playGameAudio(Keys.Theme, true);
            }
            
            return;
        }
    
        //determine mouse input
        final boolean mouseRightClick = engine.getMouse().hitRightButton() && engine.getMouse().isMouseReleased();
        final boolean mouseLeftClick = engine.getMouse().hitLeftButton() && engine.getMouse().isMouseReleased();
        
        if (engine.getMouse().hasMouseMoved())
        {
            //check if the board has been solved
            super.checkComplete(engine);
            
            //if mouse moved update highlighted location if within bounds
            final int col = (engine.getMouse().getLocation().x - Puzzles.START_X) / engine.getManager().getPuzzles().getPuzzle().getCellDimensions();
            final int row = (engine.getMouse().getLocation().y - Puzzles.START_Y) / engine.getManager().getPuzzles().getPuzzle().getCellDimensions();
            
            //make sure within column range
            if (col >= 0 && col <= getPuzzle().getCols() - 1)
            {
                //make sure within row range
                if (row >= 0 && row <= getPuzzle().getRows() - 1)
                {
                    //set the current location of the player
                    super.setHighlightCol(col);
                    super.setHighlightRow(row);
                }
            }
        } 
        else if (mouseRightClick || mouseLeftClick)
        {
            //determine where the mouse is
            final int col = (int)Puzzles.getColumn(engine.getMouse().getLocation().x, engine.getManager().getPuzzles().getPuzzle());
            final int row = (int)Puzzles.getRow(engine.getMouse().getLocation().y, engine.getManager().getPuzzles().getPuzzle());

            //make sure within column range
            if (col >= 0 && col <= getPuzzle().getCols() - 1)
            {
                //make sure within row range
                if (row >= 0 && row <= getPuzzle().getRows() - 1)
                {
                    //set the current location of the player
                    super.setHighlightCol(col);
                    super.setHighlightRow(row);
                    
                    //right click
                    if (mouseRightClick)
                    {
                        switch (getPuzzle().getKeyValue(col, row))
                        {
                            case Puzzles.KEY_EMPTY:
                                getPuzzle().setKeyValue(col, row, Puzzles.KEY_MARK);
                                
                                //play sound effect
                                engine.getResources().playGameAudio(Keys.Mark);
                                break;
                                
                            case Puzzles.KEY_MARK:
                                getPuzzle().setKeyValue(col, row, Puzzles.KEY_EMPTY);
                                
                                //play sound effect
                                engine.getResources().playGameAudio(Keys.UnMark);
                                break;
                                
                            default:
                                
                                //play sound effect
                                engine.getResources().playGameAudio(Keys.Invalid);
                                break;
                        }
                    }
                    else if (mouseLeftClick)
                    {
                        switch (getPuzzle().getKeyValue(col, row))
                        {
                            case Puzzles.KEY_EMPTY:
                                getPuzzle().setKeyValue(col, row, Puzzles.KEY_FILL);
                                
                                //play sound effect
                                engine.getResources().playGameAudio(Keys.Fill);
                                break;
                                
                            case Puzzles.KEY_FILL:
                                getPuzzle().setKeyValue(col, row, Puzzles.KEY_EMPTY);
                                
                                //play sound effect
                                engine.getResources().playGameAudio(Keys.UnFill);
                                break;
                                
                            case Puzzles.KEY_MARK:
                            default:
                                
                                //play sound effect
                                engine.getResources().playGameAudio(Keys.Invalid);
                                break;
                        }
                        
                        //if time mode is enabled
                        if (getStats().hasTimed())
                        {
                            //if the values are not equal
                            if (engine.getManager().getPuzzles().getPuzzle().getKeyValue(col, row) != getPuzzle().getKeyValue(col, row))
                            {
                                //if player just filled the block, it was a bad move
                                if (getPuzzle().getKeyValue(col, row) == Puzzles.KEY_FILL)
                                {
                                    //get the time remaining
                                    final long remaining = getStats().getTimer().getRemaining();
                                    
                                    //deduct 1 minute from timer
                                    getStats().getTimer().setRemaining(remaining - Timers.NANO_SECONDS_PER_MINUTE);
                                }
                            }
                        }
                    }
                }
            }
            
            //check if the board has been solved
            super.checkComplete(engine);
            
            //if the puzzle has been solved
            if (getPuzzle().hasSolved())
            {
                //update misc message
                getStats().setMiscDesc("Hit 'Space Bar'");
                
                //render new image
                getStats().renderImage();
                
                //stop all sound
                engine.getResources().stopAllSound();
                
                //play victory sound
                engine.getResources().playGameAudio(Keys.Solved, true);
            }
        }
        
        //update
        super.update(engine);
        
        //reset mouse events
        engine.getMouse().reset();
    }
}