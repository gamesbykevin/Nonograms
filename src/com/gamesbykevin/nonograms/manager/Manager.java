package com.gamesbykevin.nonograms.manager;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.menu.CustomMenu;
import com.gamesbykevin.nonograms.menu.CustomMenu.*;
import com.gamesbykevin.nonograms.player.*;
import com.gamesbykevin.nonograms.puzzles.Puzzles;
import com.gamesbykevin.nonograms.resources.GameAudio;
import com.gamesbykevin.nonograms.resources.GameImages;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

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
    
    //the background image
    private Image background;
    
    //assign the images for win/lose
    private Image imageVictory;
    private Image imageGameover;
    
    //is the game finished
    private boolean finished = false;
    
    //did we win
    private boolean victory = false;
    
    //the default time for each difficulty when playing timed mode
    private static final long DELAY_DIFFICULTY_VERY_EASY = (Timers.NANO_SECONDS_PER_MINUTE * 2)  + Timers.NANO_SECONDS_PER_SECOND;
    private static final long DELAY_DIFFICULTY_EASY      = (Timers.NANO_SECONDS_PER_MINUTE * 7) + Timers.NANO_SECONDS_PER_SECOND;
    private static final long DELAY_DIFFICULTY_MEDIUM    = (Timers.NANO_SECONDS_PER_MINUTE * 15) + Timers.NANO_SECONDS_PER_SECOND;
    private static final long DELAY_DIFFICULTY_HARD      = (Timers.NANO_SECONDS_PER_MINUTE * 30) + Timers.NANO_SECONDS_PER_SECOND;
    
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
        
        if (background == null)
        {
            List<GameImages.Keys> keys = new ArrayList<>();
            
            keys.add(GameImages.Keys.Background1);
            keys.add(GameImages.Keys.Background2);
            keys.add(GameImages.Keys.Background3);
            keys.add(GameImages.Keys.Background4);
            keys.add(GameImages.Keys.Background5);
            keys.add(GameImages.Keys.Background6);
            keys.add(GameImages.Keys.Background7);
            keys.add(GameImages.Keys.Background8);
            keys.add(GameImages.Keys.Background9);
            keys.add(GameImages.Keys.Background10);
            keys.add(GameImages.Keys.Background11);
            keys.add(GameImages.Keys.Background12);
            
            //pick a random background
            background = engine.getResources().getGameImage(keys.get(engine.getRandom().nextInt(keys.size())));
        }
        
        //store the images
        if (imageVictory == null)
            imageVictory = engine.getResources().getGameImage(GameImages.Keys.Victory);
        if (imageGameover == null)
            imageGameover = engine.getResources().getGameImage(GameImages.Keys.Gameover);
    }
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        if (puzzles == null)
            puzzles = new Puzzles(engine.getResources().getGameImage(GameImages.Keys.Board));
        
        //set the difficulty
        getPuzzles().setDifficulty(Puzzles.Difficulty.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Difficulty)]);
        
        if (human == null)
        {
            human = new Human(
                engine.getResources().getGameImage(GameImages.Keys.Board), 
                engine.getResources().getGameImage(GameImages.Keys.BackgroundStat)
            );
            
            //do we have hints enabled
            getHuman().setHintEnabled(engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Hint) == 1);
            
            //set the mode (regular or timed)
            getHuman().getStats().setTimed(engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Mode) == 1);
            
            //time remaining for timer
            long time = 0;

            switch (getPuzzles().getDifficulty())
            {
                case VeryEasy:
                    getHuman().getStats().setMiscDesc("Difficulty: Very Easy");
                    
                    if (getHuman().getStats().hasTimed())
                        time = DELAY_DIFFICULTY_VERY_EASY;
                    break;

                case Easy:
                    getHuman().getStats().setMiscDesc("Difficulty: Easy");
                    
                    if (getHuman().getStats().hasTimed())
                        time = DELAY_DIFFICULTY_EASY;
                    break;

                case Medium:
                    getHuman().getStats().setMiscDesc("Difficulty: Medium");
                    
                    if (getHuman().getStats().hasTimed())
                        time = DELAY_DIFFICULTY_MEDIUM;
                    break;

                case Hard:
                default:
                    getHuman().getStats().setMiscDesc("Difficulty: Hard");
                    
                    if (getHuman().getStats().hasTimed())
                        time = DELAY_DIFFICULTY_HARD;
                    break;
            }
            
            //setup timer
            getHuman().getStats().setupTimer(time);
        }
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
    
    public void setFinished(final boolean finished)
    {
        this.finished = finished;
    }
    
    public void setVictory(final boolean victory)
    {
        this.victory = victory;
    }
    
    /**
     * Update all elements
     * @param engine Our game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        //make sure game isn't finished yet
        if (!finished)
        {
            if (getPuzzles() != null)
                getPuzzles().update(engine);

            if (getHuman() != null)
                getHuman().update(engine);
            
            if (!getPuzzles().hasPuzzles())
            {
                //flag finished
                setFinished(true);

                //flag victory
                setVictory(true);
            }
            
            //if timed mode
            if (getHuman().getStats().hasTimed())
            {
                //if time has passed
                if (getHuman().getStats().getTimer().hasTimePassed())
                {
                    //set time remaining to 0
                    getHuman().getStats().getTimer().setRemaining(0);
                    
                    //flag finished
                    setFinished(true);

                    //flag loss
                    setVictory(false);
                }
            }
            
            //if the game is now finished
            if (finished)
            {
                //stop sound
                engine.getResources().stopAllSound();
                
                if (victory)
                {
                    //play victory music
                    engine.getResources().playGameAudio(GameAudio.Keys.Victory, true);
                }
                else
                {
                    //play game over music
                    engine.getResources().playGameAudio(GameAudio.Keys.Gameover, true);
                }
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        if (getPuzzles() == null || getHuman() == null)
            return;
        
        //if the game is finished
        if (finished)
        {
            //draw the appropriate image
            graphics.drawImage(victory ? imageVictory : imageGameover, 0, 0, null);
            
            //no need to continue
            return;
        }
        
        if (background != null)
            graphics.drawImage(background, 0, 0, null);
        
        //if the human solved the puzzle
        if (getHuman().getPuzzle().hasSolved())
        {
            //draw human board
            getHuman().render(graphics);
            
            //draw puzzle description
            getHuman().renderDesc(graphics, getPuzzles().getPuzzle().getDesc());
        }
        else
        {
            //draw the puzzle with hints
            getPuzzles().render(graphics);
            
            //now draw human board
            getHuman().render(graphics);
        }
    }
}