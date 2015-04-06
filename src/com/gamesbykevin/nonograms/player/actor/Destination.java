package com.gamesbykevin.nonograms.player.actor;

import com.gamesbykevin.framework.base.Sprite;

/**
 * The destination will direct the actor where to go and what board move to make
 * @author GOD
 */
public final class Destination extends Sprite
{
    //the move on the board to make
    private final int key;
    
    /**
     * Create a new destination
     * @param x x-coordinate
     * @param y y-coordinate
     * @param col column
     * @param row Row
     * @param key Board key
     */
    protected Destination(final double x, final double y, final int col, final int row, final int key)
    {
        //set the location
        setLocation(x, y);
        
        //set column, row location
        setCol(col);
        setRow(row);
        
        //set the board key
        this.key = key;
    }
    
    /**
     * Is the actor at the destination?
     * @param actor The object containing the x,y coordinates
     * @return true=yes, false=no
     */
    protected boolean hasLocation(final Actor actor)
    {
        if ((int)actor.getX() == (int)getX() && (int)actor.getY() == (int)getY())
            return true;
        
        return false;
    }
    
    @Override
    public void setLocation(final double x, final double y)
    {
        super.setX((int)x);
        super.setY((int)y);
    }
    
    /**
     * The key value for this destination
     * @return FILL, EMPTY, MARK, etc....
     */
    public int getKey()
    {
        return this.key;
    }
}