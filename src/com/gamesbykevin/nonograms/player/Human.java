package com.gamesbykevin.nonograms.player;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.puzzles.Puzzles;
import com.gamesbykevin.nonograms.shared.IElement;

import java.awt.Graphics;

/**
 * This is where we will manage the human input
 * @author GOD
 */
public final class Human extends Player
{
    public Human()
    {
        super();
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
        
        if (mouseRightClick || mouseLeftClick)
        {
            //determine where the mouse is
            final int col = (engine.getMouse().getLocation().x - Puzzles.START_X) / Puzzles.CELL_WIDTH;
            final int row = (engine.getMouse().getLocation().y - Puzzles.START_Y) / Puzzles.CELL_HEIGHT;
            
            //make sure within column range
            if (col >= 0 && col <= getPuzzle().getCols() - 1)
            {
                //make sure within row range
                if (row >= 0 && row <= getPuzzle().getRows() - 1)
                {
                    //right click
                    if (mouseRightClick)
                    {
                        switch (getPuzzle().getKeyValue(col, row))
                        {
                            case Puzzles.KEY_EMPTY:
                            case Puzzles.KEY_FILL:
                                getPuzzle().set(col, row, Puzzles.KEY_MARK);
                                break;
                                
                            case Puzzles.KEY_MARK:
                            default:
                                getPuzzle().set(col, row, Puzzles.KEY_EMPTY);
                                break;
                        }
                    }
                    else if (mouseLeftClick)
                    {
                        switch (getPuzzle().getKeyValue(col, row))
                        {
                            case Puzzles.KEY_EMPTY:
                                getPuzzle().set(col, row, Puzzles.KEY_FILL);
                                break;
                                
                            case Puzzles.KEY_FILL:
                                getPuzzle().set(col, row, Puzzles.KEY_EMPTY);
                                break;
                                
                            case Puzzles.KEY_MARK:
                            default:
                                break;
                        }
                    }
                }
            }
        }
        
        //reset mouse events
        engine.getMouse().reset();
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        getPuzzle().render(graphics, Puzzles.START_X, Puzzles.START_Y);
    }
    
}