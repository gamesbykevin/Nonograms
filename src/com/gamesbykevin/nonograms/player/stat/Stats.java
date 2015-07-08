package com.gamesbykevin.nonograms.player.stat;

import com.gamesbykevin.framework.awt.CustomImage;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.shared.IElement;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Image;

/**
 * This class will contain the player's stats
 * @author GOD
 */
public final class Stats extends CustomImage implements IElement
{
    //dimensions
    private static final int WIDTH = 175;
    private static final int HEIGHT = 88;
    
    //the location of the background
    private static final int START_X = 12;
    private static final int START_Y = 31;
    
    //the location of the timer description
    private static final int TIMER_X = 10;
    private static final int TIMER_Y = 50;
    
    //the location of the level description
    private static final int LEVEL_DESC_X = 10;
    private static final int LEVEL_DESC_Y = 25;
    
    //the location for a misc description
    private static final int MISC_X = 10;
    private static final int MISC_Y = 75;
    
    //miscallaneous description
    private String miscDesc;
    
    //default font size
    private static final float DEFAULT_FONT_SIZE = 16f;
    
    //the timer
    private Timer timer;
    
    //keep track of previous update
    private long previous = System.nanoTime() - Timers.NANO_SECONDS_PER_SECOND;
    
    //did we yet assign a font
    private boolean assignedFont = false;
    
    //the level number
    private int level = 1;
    
    //is this timed mode
    private boolean timed = false;
    
    public Stats(final Image image)
    {
        //set dimensions
        super(WIDTH, HEIGHT);
        
        //set location
        super.setX(START_X);
        super.setY(START_Y);
        
        //store background image
        super.setImage(image);
    }
    
    @Override
    public void dispose()
    {
        if (timer != null)
            timer = null;
        
        super.dispose();
    }
    
    /**
     * Set timed mode.
     * @param timed true=yes, false=no
     */
    public void setTimed(final boolean timed)
    {
        this.timed = timed;
    }
    
    /**
     * Is timed mode enabled?
     * @return true=yes, false=no
     */
    public boolean hasTimed()
    {
        return this.timed;
    }
    
    /**
     * Set the miscellaneous description to be displayed
     * @param miscDesc The text description
     */
    public void setMiscDesc(final String miscDesc)
    {
        this.miscDesc = miscDesc;
    }
    
    /**
     * Change the level description to the next level
     */
    public void nextLevel()
    {
        this.level++;
    }
    
    /**
     * Setup timer if we want to set a time limit
     * @param time The time limit (nano-seconds)
     */
    public void setupTimer(final long time)
    {
        if (timer == null)
            timer = new Timer();
        
        this.timer.setReset(time);
        this.timer.setRemaining(time);
        this.timer.reset();
    }
    
    /**
     * Setup timer if we want to track amount of time passed
     */
    public void setupTimer()
    {
        this.setupTimer(0);
    }
    
    public Timer getTimer()
    {
        return this.timer;
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update timer
        getTimer().update(engine.getMain().getTime());
    }
    
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        //if we have not yet assigned a font
        if (!assignedFont)
        {
            //flag we now have assigned font
            assignedFont = true;
            
            //set custom image font
            super.setFont(graphics.getFont().deriveFont(DEFAULT_FONT_SIZE));
            
            //set font color as well
            super.getGraphics2D().setColor(Color.BLACK);
        }
        
        //the current time in nano-seconds
        final long current = System.nanoTime();
        
        //determine if another image needs to be rendered
        if (current - previous >= Timers.NANO_SECONDS_PER_SECOND)
        {
            //render a new image
            render();
            
            //store the new time
            previous = current;
        }
        
        //draw buffered image
        super.draw(graphics, super.getBufferedImage());
    }
    
    @Override
    public void render()
    {
        //clear image
        super.clear();
        
        //draw background
        super.getGraphics2D().drawImage(getImage(), 0, 0, null);
        
        if (hasTimed())
        {
            //draw time remaining
            super.getGraphics2D().drawString("Time : " + getTimer().getDescRemaining(Timers.FORMAT_8), TIMER_X, TIMER_Y);
        }
        else
        {
            //draw time passed
            super.getGraphics2D().drawString("Time : " + getTimer().getDescPassed(Timers.FORMAT_8), TIMER_X, TIMER_Y);
        }
        
        //draw level desc
        super.getGraphics2D().drawString("Level: " + level, LEVEL_DESC_X, LEVEL_DESC_Y);
        
        //if a misc desc exists
        if (this.miscDesc != null)
            getGraphics2D().drawString(this.miscDesc, MISC_X, MISC_Y);
    }
}