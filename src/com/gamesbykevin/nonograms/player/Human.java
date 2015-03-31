package com.gamesbykevin.nonograms.player;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.puzzles.Puzzles;
import com.gamesbykevin.nonograms.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;

/**
 * This is where we will manage the human input
 * @author GOD
 */
public final class Human extends Player
{
    public Human(final Image image)
    {
        super(image);
    }
    
    @Override
    public void dispose()
    {
        
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if the human does not have a puzzle, create one from the current in play
        if (getPuzzle() == null)
            create(engine.getManager().getPuzzles().getPuzzle());
    
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
                    
                    //right click
                    if (mouseRightClick)
                    {
                        switch (getPuzzle().getKeyValue(col, row))
                        {
                            case Puzzles.KEY_EMPTY:
                                getPuzzle().setKeyValue(col, row, Puzzles.KEY_MARK);
                                break;
                                
                            case Puzzles.KEY_MARK:
                                getPuzzle().setKeyValue(col, row, Puzzles.KEY_EMPTY);
                                break;
                        }
                    }
                    else if (mouseLeftClick)
                    {
                        switch (getPuzzle().getKeyValue(col, row))
                        {
                            case Puzzles.KEY_EMPTY:
                                getPuzzle().setKeyValue(col, row, Puzzles.KEY_FILL);
                                break;
                                
                            case Puzzles.KEY_FILL:
                                getPuzzle().setKeyValue(col, row, Puzzles.KEY_EMPTY);
                                break;
                                
                            case Puzzles.KEY_MARK:
                            default:
                                break;
                        }
                    }
                }
            }
            
            //check if the board has been solved
            super.checkComplete(engine);
        }
        
        //reset mouse events
        engine.getMouse().reset();
    }
}