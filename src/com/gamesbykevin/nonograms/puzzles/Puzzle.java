package com.gamesbykevin.nonograms.puzzles;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.nonograms.player.Player;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a puzzle in the game
 * @author GOD
 */
public final class Puzzle extends Sprite implements Disposable
{
    //the different sizes of the cells
    public static final int CELL_DIMENSIONS_SMALL = 24;
    public static final int CELL_DIMENSIONS_MEDIUM = 32;
    public static final int CELL_DIMENSIONS_LARGE = 40;
    public static final int CELL_DIMENSIONS_VERY_LARGE = 48;
    
    //the key to our puzzle
    private int[][] key;
    
    //the description of this puzzle
    private final String desc;
    
    //these objects will provide the hints to solving the puzzle
    private HashMap<Integer, List<Integer>> columnHint, rowHint;
    
    //no description will be an empty string
    public static final String NO_DESCIPTION = "";
    
    //the dimensions of each cell in this puzzle
    private int cellDimension;
    
    //has this puzzle been solved
    private boolean solved = false;
    
    public enum AnimationKey
    {
        Empty, Fill, Mark, Highlight, 
        Desc0, Desc1, Desc2, Desc3, Desc4, Desc5, 
        Desc6, Desc7, Desc8, Desc9, Desc10, Desc11, 
        Desc12, Desc13, Desc14, Desc15, Desc16, Desc17, 
        Desc18, Desc19, Desc20, 
    }
    
    public Puzzle(final Puzzle puzzle)
    {
        this(puzzle.getCols(), puzzle.getRows(), NO_DESCIPTION);
    }
    
    public Puzzle(final int cols, final int rows, final String desc)
    {
        //create a new key
        this.key = new int[rows][cols];
        
        //create column hint
        this.columnHint = new HashMap<>();
        
        //create row hint
        this.rowHint = new HashMap<>();
        
        //store the description
        this.desc = desc;
        
        //reset board
        this.reset();
        
        //create the spritesheet
        super.createSpriteSheet();
        
        //add each animation for our board
        super.getSpriteSheet().add(0 * 64, 0 * 64, 64, 64, 0, AnimationKey.Empty);
        super.getSpriteSheet().add(2 * 64, 0 * 64, 64, 64, 0, AnimationKey.Fill);
        super.getSpriteSheet().add(1 * 64, 0 * 64, 64, 64, 0, AnimationKey.Mark);
        super.getSpriteSheet().add(3 * 64, 0 * 64, 64, 64, 0, AnimationKey.Highlight);
        
        //add the numbers as well
        super.getSpriteSheet().add(4 * 64, 2 * 64, 64, 64, 0, AnimationKey.Desc0);
        super.getSpriteSheet().add(0 * 64, 1 * 64, 64, 64, 0, AnimationKey.Desc1);
        super.getSpriteSheet().add(1 * 64, 1 * 64, 64, 64, 0, AnimationKey.Desc2);
        super.getSpriteSheet().add(2 * 64, 1 * 64, 64, 64, 0, AnimationKey.Desc3);
        super.getSpriteSheet().add(3 * 64, 1 * 64, 64, 64, 0, AnimationKey.Desc4);
        super.getSpriteSheet().add(4 * 64, 1 * 64, 64, 64, 0, AnimationKey.Desc5);
        super.getSpriteSheet().add(0 * 64, 2 * 64, 64, 64, 0, AnimationKey.Desc6);
        super.getSpriteSheet().add(1 * 64, 2 * 64, 64, 64, 0, AnimationKey.Desc7);
        super.getSpriteSheet().add(2 * 64, 2 * 64, 64, 64, 0, AnimationKey.Desc8);
        super.getSpriteSheet().add(3 * 64, 2 * 64, 64, 64, 0, AnimationKey.Desc9);
        super.getSpriteSheet().add(4 * 64, 0 * 64, 64, 64, 0, AnimationKey.Desc10);
        super.getSpriteSheet().add(0 * 64, 3 * 64, 64, 64, 0, AnimationKey.Desc11);
        super.getSpriteSheet().add(1 * 64, 3 * 64, 64, 64, 0, AnimationKey.Desc12);
        super.getSpriteSheet().add(2 * 64, 3 * 64, 64, 64, 0, AnimationKey.Desc13);
        super.getSpriteSheet().add(3 * 64, 3 * 64, 64, 64, 0, AnimationKey.Desc14);
        super.getSpriteSheet().add(4 * 64, 3 * 64, 64, 64, 0, AnimationKey.Desc15);
        super.getSpriteSheet().add(0 * 64, 4 * 64, 64, 64, 0, AnimationKey.Desc16);
        super.getSpriteSheet().add(1 * 64, 4 * 64, 64, 64, 0, AnimationKey.Desc17);
        super.getSpriteSheet().add(2 * 64, 4 * 64, 64, 64, 0, AnimationKey.Desc18);
        super.getSpriteSheet().add(3 * 64, 4 * 64, 64, 64, 0, AnimationKey.Desc19);
        super.getSpriteSheet().add(4 * 64, 4 * 64, 64, 64, 0, AnimationKey.Desc20);
    }
    
    /**
     * Mark this puzzle as solved
     */
    public void markSolved()
    {
        this.solved = true;
    }
    
    /**
     * Has this puzzle been solved?<br>
     * This means have all the correct
     * @return true=yes, false=no
     */
    public boolean hasSolved()
    {
        return this.solved;
    }
    
    public int getCellDimensions()
    {
        return this.cellDimension;
    }
    
    /**
     * Now with the puzzle set, we will create the column, and row keys.<br>
     * This will provide us the  hint to solving the puzzle.
     */
    public void calculateHint()
    {
        this.calculateColumnHint();
        this.calculateRowHint();
    }
    
    /**
     * Calculate the hint for all of the rows
     */
    private void calculateRowHint()
    {
        //clear list
        this.rowHint.clear();
        
        //calculate the row key
        for (int row = 0; row < getRows(); row++)
        {
            //create list for this specific row
            List<Integer> tmp = new ArrayList<>();
            
            //count the groups of blocks
            int count = 0;
            
            for (int col = 0; col < getCols(); col++)
            {
                //if this is a fill or the last row
                if (getKeyValue(col, row) == Puzzles.KEY_FILL)
                {
                    //add to count
                    count++;
                }
                else
                {
                    //only add if greater than 0
                    if (count > 0)
                    {
                        //add this count to the list
                        tmp.add(count);

                        //reset back to 0
                        count = 0;
                    }
                }
            }
            
            //if a count remains, add to list
            if (count > 0)
            {
                tmp.add(count);
            }
            else
            {
                //if the list is empty, add one 0
                if (tmp.isEmpty())
                    tmp.add(0);
            }
            
            //store the list for this column
            this.rowHint.put(row, tmp);
        }
    }
    
    /**
     * Calculate the hint for all of the columns
     */
    private void calculateColumnHint()
    {
        //clear list
        this.columnHint.clear();
        
        //calculate the column key
        for (int col = 0; col < getCols(); col++)
        {
            //create list for this specific column
            List<Integer> tmp = new ArrayList<>();
            
            //count the groups of blocks
            int count = 0;
            
            //check every row in this column
            for (int row = 0; row < getRows(); row++)
            {
                //if this is a fill or the last row
                if (getKeyValue(col, row) == Puzzles.KEY_FILL)
                {
                    //add to count
                    count++;
                }
                else
                {
                    //only add if greater than 0
                    if (count > 0)
                    {
                        //add this count to the list
                        tmp.add(count);

                        //reset back to 0
                        count = 0;
                    }
                }
            }
            
            //if a count remains, add to list
            if (count > 0)
            {
                tmp.add(count);
            }
            else
            {
                //if the list is empty, add one 0
                if (tmp.isEmpty())
                    tmp.add(0);
            }
            
            //store the list for this column
            this.columnHint.put(col, tmp);
        }
    }
    
    /**
     * Does the specified puzzle match with this one
     * @param puzzle The puzzle we want to check
     * @return true if both puzzles have the same filled and empty positions, false otherwise
     */
    public boolean hasMatch(final Puzzle puzzle)
    {
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                //get the current key value
                final int value = getKeyValue(col, row);
                
                switch (value)
                {
                    case Puzzles.KEY_FILL:
                        
                        //if values not equal, we do not have a match
                        if (puzzle.getKeyValue(col, row) != Puzzles.KEY_FILL)
                            return false;
                        break;
                        
                    case Puzzles.KEY_EMPTY:
                    case Puzzles.KEY_MARK:
                        
                        //if values not equal, we do not have a match
                        if (puzzle.getKeyValue(col, row) != Puzzles.KEY_EMPTY && puzzle.getKeyValue(col, row) != Puzzles.KEY_MARK)
                            return false;
                        break;
                }
            }
        }
        
        //we have a match
        return true;
    }
    
    /**
     * Remove a specific key from the puzzle<br>.
     * The new key will be EMPTY
     * @param keyValue The key we want to remove where found in this puzzle.
     */
    public void remove(final int keyValue)
    {
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                if (getKeyValue(col, row) == keyValue)
                    setKeyValue(col, row, Puzzles.KEY_EMPTY);
            }
        }
    }
    
    /**
     * Set all locations in the board to empty
     */
    public final void reset()
    {
        //determine the size of the puzzle
        if (getCols() >= Puzzles.DIMENSIONS_HARD)
        {
            this.cellDimension = CELL_DIMENSIONS_SMALL;
        }
        else if (getCols() >= Puzzles.DIMENSIONS_MEDIUM && getCols() < Puzzles.DIMENSIONS_HARD)
        {
            this.cellDimension = CELL_DIMENSIONS_MEDIUM;
        }
        else if (getCols() >= Puzzles.DIMENSIONS_EASY && getCols() < Puzzles.DIMENSIONS_MEDIUM)
        {
            this.cellDimension = CELL_DIMENSIONS_LARGE;
        }
        else
        {
            this.cellDimension = CELL_DIMENSIONS_VERY_LARGE;
        }
        
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {                
                setKeyValue(col, row, Puzzles.KEY_EMPTY);
            }
        }
    }
    
    public int getCols()
    {
        return getKey()[0].length;
    }
    
    public int getRows()
    {
        return getKey().length;
    }
    
    /**
     * Get the description of this puzzle
     * @return The description is also the text solution.
     */
    public String getDesc()
    {
        return this.desc;
    }
    
    /**
     * Assign the appropriate key at this location
     * @param col Column
     * @param row Row
     * @param key The solution for this location
     */
    public void setKeyValue(final int col, final int row, final int key)
    {
        getKey()[row][col] = key;
    }
    
    /**
     * Get the value of a specific location in our puzzle
     * @param col Column
     * @param row Row
     * @return The value of the specified location
     */
    public int getKeyValue(final int col, final int row)
    {
        return getKey()[row][col];
    }
    
    /**
     * Get the key for our puzzle
     * @return The solution to this puzzle
     */
    protected int[][] getKey()
    {
        return this.key;
    }
    
    @Override
    public void dispose()
    {
        this.key = null;
    }
    
    /**
     * Render puzzle
     * @param graphics Object used to draw puzzle
     * @param Player our player object
     * @param startX Where our puzzle starts
     * @param startY Where our puzzle starts
     */
    public void render(final Graphics graphics, final Player player, final int startX, final int startY)
    {
        //set the dimensions
        super.setDimensions(getCellDimensions(), getCellDimensions());
        
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                //set the x,y coordinates
                super.setX(Puzzles.getX(startX, getCellDimensions(), col));
                super.setY(Puzzles.getY(startY, getCellDimensions(), row));
                
                switch (getKeyValue(col, row))
                {
                    case Puzzles.KEY_FILL:
                        super.getSpriteSheet().setCurrent(AnimationKey.Fill);
                        break;
                        
                    case Puzzles.KEY_MARK:
                        super.getSpriteSheet().setCurrent(AnimationKey.Mark);
                        break;
                        
                    case Puzzles.KEY_EMPTY:
                    default:
                        //first default to the empty
                        super.getSpriteSheet().setCurrent(AnimationKey.Empty);
                        
                        //if this location matches the players location
                        if (col == player.getHighlightCol() || row == player.getHighlightRow())
                        {
                            if (player.hasHighlight())
                                super.getSpriteSheet().setCurrent(AnimationKey.Highlight);
                        }
                        break;
                }
                
                try
                {
                    //draw the animation
                    super.draw(graphics, player.getImage());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Draw the puzzle hints
     * @param graphics Object used to render graphics
     * @param image Image containing animations
     * @param startX Start x-coordinate
     * @param startY Start y-coordinate
     */
    public void renderHints(final Graphics graphics, final Image image, final int startX, final int startY) throws Exception
    {
        //if we have not yet solved
        if (!hasSolved())
        {
            //set the dimensions
            super.setDimensions(getCellDimensions() * .75, getCellDimensions() * .75);
            
            //draw the column hints
            renderColumnHint(graphics, image, startX, startY);

            //draw the row hints
            renderRowHint(graphics, image, startX, startY);
        }
    }
    
    /**
     * Assign the animation
     * @param key The key we use to determine the animation
     * @throws exception if the key does not have an associated animation
     */
    private void assignAnimation(final int key) throws Exception
    {
        switch (key)
        {
            case 0:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc0);
                break;
            
            case 1:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc1);
                break;
            
            case 2:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc2);
                break;
            
            case 3:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc3);
                break;
            
            case 4:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc4);
                break;
            
            case 5:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc5);
                break;
            
            case 6:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc6);
                break;
            
            case 7:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc7);
                break;
                
            case 8:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc8);
                break;
            
            case 9:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc9);
                break;
            
            case 10:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc10);
                break;
            
            case 11:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc11);
                break;
            
            case 12:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc12);
                break;
            
            case 13:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc13);
                break;
            
            case 14:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc14);
                break;
            
            case 15:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc15);
                break;
            
            case 16:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc16);
                break;
            
            case 17:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc17);
                break;
            
            case 18:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc18);
                break;
            
            case 19:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc19);
                break;
            
            case 20:
                super.getSpriteSheet().setCurrent(AnimationKey.Desc20);
                break;
                
            default:
                throw new Exception("Key not found here - " + key);
        }
    }
    
    private void renderColumnHint(final Graphics graphics, final Image image, final int startX, final int startY) throws Exception
    {
        //draw column hint
        for (int col = 0; col < getCols(); col++)
        {
            //get x-coordinate
            final int x = (int)(Puzzles.getX(startX, getCellDimensions(), col) + (getCellDimensions() / 2) - (getWidth() / 2));
            
            //get list for this column
            List<Integer> tmp = this.columnHint.get(col);
            
            for (int i = 0; i < tmp.size(); i++)
            {
                final int y = (int)(Puzzles.getY(startY, (int)getWidth(), 0) - ((tmp.size() - i) * getWidth()));
                
                //set coordinates
                super.setX(x);
                super.setY(y);

                //assign temporary animation
                assignAnimation(tmp.get(i));

                try
                {
                    //draw animation
                    super.draw(graphics, image);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void renderRowHint(final Graphics graphics, final Image image, final int startX, final int startY) throws Exception
    {
        //draw row hint
        for (int row = 0; row < getRows(); row++)
        {
            final int y = (int)(Puzzles.getY(startY, getCellDimensions(), row) + (getCellDimensions() / 2) - (getWidth() / 2));
            
            //get list for this row
            List<Integer> tmp = this.rowHint.get(row);
            
            for (int i = 0; i < tmp.size(); i++)
            {
                final int x = Puzzles.getX(startX, (int)getWidth(), 0) - ((tmp.size() - i) * (int)getWidth());
                
                //set coordinates
                super.setX(x);
                super.setY(y);

                //assign temporary animation
                assignAnimation(tmp.get(i));

                try
                {
                    //draw animation
                    super.draw(graphics, image);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}