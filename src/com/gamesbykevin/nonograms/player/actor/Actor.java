package com.gamesbykevin.nonograms.player.actor;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will manage the actor that fills out the board
 * @author GOD
 */
public final class Actor extends Sprite implements Disposable, IElement
{
    //the dimensions of the actor
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;
    
    private static final int START_X = 0;
    private static final int START_Y = 0;
    
    //0 delay
    private static final long NO_DELAY = 0;
    
    //default delay for each animation
    private static final long DEFAULT_DELAY = Timers.toNanoSeconds(200L);
    
    //the list of destinations for this actor
    private List<Destination> destinations;
    
    //the rate at which the actor can move
    private static final double VELOCITY = 1;
    
    private enum AnimationKey
    {
        Idle, Walk, Fill, Mark
    }
    
    public Actor(final Image image)
    {
        //set the dimensions
        super.setDimensions(WIDTH, HEIGHT);
        
        //set the starting location
        super.setLocation(START_X, START_Y);
        
        //assign the spritesheet
        super.setImage(image);
        
        //create our spritesheet
        super.createSpriteSheet();
        
        //add the animations
        addAnimations();
        
        //create new list
        this.destinations = new ArrayList<>();
    }
    
    /**
     * Add this animation to the sprite sheet
     * @param count How many frames
     * @param row The row the animation is on
     * @param key The unique key for identification
     */
    private void addAnimation(final int count, final int startCol, final int row, final long delay, final boolean loop, final AnimationKey key)
    {
        Animation animation = new Animation();
        
        for (int i = 0; i < count; i++)
        {
            animation.add((startCol + i) * WIDTH, row * HEIGHT, WIDTH, HEIGHT, delay);
        }
        
        //do we loop
        animation.setLoop(loop);
        
        //add animation
        super.getSpriteSheet().add(animation, key);
    }
    
    /**
     * Create the animations for this actor
     */
    private void addAnimations()
    {
        addAnimation(6, 0, 0, DEFAULT_DELAY, false, AnimationKey.Fill);
        addAnimation(5, 0, 1, DEFAULT_DELAY, false, AnimationKey.Mark);
        addAnimation(8, 0, 2, DEFAULT_DELAY, true, AnimationKey.Walk);
        addAnimation(1, 4, 2, NO_DELAY, false, AnimationKey.Idle);
        
        //set the default animation
        setAnimation(AnimationKey.Idle);
    }
    
    private void setAnimation(final AnimationKey key)
    {
        super.getSpriteSheet().setCurrent(key);
    }
    
    private boolean hasAnimationCompleted()
    {
        //has the current animation completed
        return super.getSpriteSheet().hasFinished();
    }
    
    private void resetAnimation()
    {
        //reset the current animation
        super.getSpriteSheet().reset();
    }
    
    @Override
    public void dispose()
    {
        if (destinations != null)
        {
            destinations.clear();
            destinations = null;
        }
        
        super.dispose();
    }
    
    public void addDestination(double x, double y, final int col, final int row, final int key)
    {
        determine the previous destination to see if we need to stop on right or left side
        
        x = x - (getWidth() / 2);
        y = y - getHeight();
        
        destinations.add(new Destination(x, y, col, row, key));
    }
    
    public boolean hasDestinations()
    {
        return (!destinations.isEmpty());
    }
    
    private void removeDestination()
    {
        //remove the current destination
        destinations.remove(0);
    }
    
    /**
     * Get the destination
     * @return The current destination, if none exist null is returned
     */
    private Destination getDestination()
    {
        if (!hasDestinations())
            return null;
        
        return destinations.get(0);
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update the animation
        super.getSpriteSheet().update(engine.getMain().getTime());
        
        //make sure we generally have a destination first
        if (hasDestinations())
        {
            //get the current destination
            final Destination destination = getDestination();
            
            //if we are currently not at our destination
            if (!destination.hasLocation(this))
            {
                //reset the velocity
                resetVelocity();
                
                //start walking
                setAnimation(AnimationKey.Walk);
                
                if (getX() < destination.getX())
                {
                    //face the correct way
                    setHorizontalFlip(false);
                    
                    if (getX() + VELOCITY < destination.getX())
                    {
                        setVelocityX(VELOCITY);
                    }
                    else
                    {
                        setX(destination.getX());
                    }
                }
                else if (getX() > destination.getX())
                {
                    //face the correct way
                    setHorizontalFlip(true);
                    
                    if (getX() - VELOCITY > destination.getX())
                    {
                        setVelocityX(-VELOCITY);
                    }
                    else
                    {
                        setX(destination.getX());
                    }
                }
                
                if (getY() < destination.getY())
                {
                    if (getY() + VELOCITY < destination.getY())
                    {
                        setVelocityY(VELOCITY);
                    }
                    else
                    {
                        setY(destination.getY());
                    }
                }
                else if (getY() > destination.getY())
                {
                    if (getY() - VELOCITY > destination.getY())
                    {
                        setVelocityY(-VELOCITY);
                    }
                    else
                    {
                        setY(destination.getY());
                    }
                }
                
                //update location
                update();
            }
            else
            {
                //hasAnimationCompleted();
                
                //reset the velocity
                resetVelocity();
                
                //reset the current animation
                resetAnimation();
                
                //update the player board?
                engine.getManager().getHuman().getPuzzle().setKeyValue(
                    (int)destination.getCol(), 
                    (int)destination.getRow(), 
                    destination.getKey());

                //determine next steps
                
                //remove the destination
                removeDestination();
                
                //set animation
                setAnimation(AnimationKey.Idle);
            }
        }
    }
    
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        final double x = getX();
        final double y = getY();
        
        //setX();
        //setY();
        
        //draw the current animation
        super.draw(graphics);
        
        setX(x);
        setY(y);
    }
}
