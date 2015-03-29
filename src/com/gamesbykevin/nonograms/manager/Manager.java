package com.gamesbykevin.nonograms.manager;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.menu.CustomMenu;
import com.gamesbykevin.nonograms.menu.CustomMenu.*;
import com.gamesbykevin.nonograms.player.*;
import com.gamesbykevin.nonograms.puzzles.Puzzles;
import com.gamesbykevin.nonograms.resources.GameAudio;
import com.gamesbykevin.nonograms.resources.GameFont;
import com.gamesbykevin.nonograms.resources.GameImages;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * The class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //where gameplay occurs
    private Rectangle window;
    
    //the puzzles in the game
    private Puzzles puzzles;
    
    //object representing the human
    private Human human;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //set the audio depending on menu setting
        engine.getResources().setAudioEnabled(engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Sound) == CustomMenu.SOUND_ENABLED);
        
        //set the game window where game play will occur
        setWindow(engine.getMain().getScreen());
    }
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        if (puzzles == null)
            puzzles = new Puzzles();
        
        //set the difficulty
        getPuzzles().setDifficulty(Puzzles.Difficulty.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Difficulty)]);
        
        if (human == null)
            human = new Human();
        
        //engine.getResources().getGameImage(GameImages.Keys.MapBackground));
        
        /*
        switch (engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Funds))
        {
            default:
                break;
        }
        */
    }
    
    public Human getHuman()
    {
        return this.human;
    }
    
    public Puzzles getPuzzles()
    {
        return this.puzzles;
    }
    
    @Override
    public Rectangle getWindow()
    {
        return this.window;
    }
    
    @Override
    public void setWindow(final Rectangle window)
    {
        this.window = new Rectangle(window);
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (window != null)
            window = null;
        
        if (human != null)
        {
            human.dispose();
            human = null;
        }
        
        if (puzzles != null)
        {
            puzzles.dispose();
            puzzles = null;
        }
        
        try
        {
            //recycle objects
            super.finalize();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Update all elements
     * @param engine Our game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        if (getPuzzles() != null)
        {
            getPuzzles().update(engine);
        }
        
        if (getHuman() != null)
        {
            getHuman().update(engine);
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        if (getPuzzles() != null)
        {
            getPuzzles().render(graphics);
        }
        
        if (getHuman() != null)
        {
            getHuman().render(graphics);
        }
        
        //did we solve the puzzle
        if (getPuzzles().getPuzzle().hasMatch(getHuman().getPuzzle()))
        {
            getHuman().getPuzzle().remove(Puzzles.KEY_MARK);
            graphics.drawString("YOU WIN - " + getPuzzles().getPuzzle().getDesc(), 100, 500);
        }
    }
}