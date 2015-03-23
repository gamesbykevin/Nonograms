package com.gamesbykevin.nonograms.puzzles;

import com.gamesbykevin.framework.resources.Disposable;
import java.awt.Color;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a puzzle in the game
 * @author GOD
 */
public final class Puzzle implements Disposable
{
    //the key to our puzzle
    private int[][] key;
    
    //the description of this puzzle
    private final String desc;
    
    //these objects will provide the hints to solving the puzzle
    private HashMap<Integer, List<Integer>> columnHint, rowHint;
    
    //no description will be an empty string
    public static final String NO_DESCIPTION = "";
    
    public Puzzle(final int cols, final int rows)
    {
        this(cols, rows, NO_DESCIPTION);
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
     * @throws Exception if we come across an unexpected key value, or if the puzzles compared do not have the same dimensions
     * @return true if both puzzles have the same filled and empty positions, false otherwise
     */
    public boolean hasMatch(final Puzzle puzzle) throws Exception
    {
        if (getRows() != puzzle.getRows())
            throw new Exception("These puzzles have different row dimensions");
        if (getCols() != puzzle.getCols())
            throw new Exception("These puzzles have different column dimensions");
        
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
                        
                    default:
                        throw new Exception("Puzzle key value (" + value + ") not accounted for.");
                }
            }
        }
        
        //we have a match
        return true;
    }
    
    /**
     * Set all locations in the board to empty
     */
    public final void reset()
    {
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                set(col, row, Puzzles.KEY_EMPTY);
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
    public void set(final int col, final int row, final int key)
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
    
    public void render(final Graphics graphics, final int startX, final int startY)
    {
        render(graphics, startX, startY, Puzzles.CELL_WIDTH, Puzzles.CELL_HEIGHT);
    }
    
    /**
     * Render puzzle
     * @param graphics Object used to draw puzzle
     * @param startX Where our puzzle starts
     * @param startY Where our puzzle starts
     * @param width  Dimensions of each cell
     * @param height Dimensions of each cell
     * @param fontWidth  Dimensions of each character
     * @param fontHeight Dimensions of each character
     */
    public void render(final Graphics graphics, final int startX, final int startY, final int width, final int height)
    {
        graphics.setColor(Color.WHITE);
        
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                final int x = Puzzles.getX(startX, width, col);
                final int y = Puzzles.getY(startY, height, row);
                
                switch (getKeyValue(col, row))
                {
                    case Puzzles.KEY_FILL:
                        graphics.fillRect(x, y, width, height);
                        break;
                        
                    case Puzzles.KEY_MARK:
                        graphics.drawLine(x, y, x + width, y + height);
                        graphics.drawLine(x + width, y, x, y + height);
                        break;
                }
            }
        }
        
        graphics.setColor(Color.RED);
        
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                final int x = Puzzles.getX(startX, width, col);
                final int y = Puzzles.getY(startY, height, row);
                
                graphics.drawRect(x, y, width, height);
            }
        }
    }
    
    public void renderColumnHint(final Graphics graphics, final int startX, final int startY, final int width, final int height, final int fontWidth, final int fontHeight)
    {
        //draw column hint
        for (int col = 0; col < getCols(); col++)
        {
            //get list
            List<Integer> tmp = this.columnHint.get(col);
            
            int y = startY - (tmp.size() * fontHeight);
            
            for (int i = 0; i < tmp.size(); i++)
            {
                graphics.drawString(tmp.get(i) + "", startX + (col * width) + (int)(fontWidth * .25), y + (i * fontHeight));
            }
        }
    }
    
    public void renderRowHint(final Graphics graphics, final int startX, final int startY, final int width, final int height, final int fontWidth, final int fontHeight)
    {
        //draw row hint
        for (int row = 0; row < getRows(); row++)
        {
            //get list
            List<Integer> tmp = this.rowHint.get(row);
            
            int x = startX - (tmp.size() * fontWidth);
            
            for (int i = 0; i < tmp.size(); i++)
            {
                graphics.drawString(tmp.get(i) + "", x + (i * fontWidth), startY + (row * height) + (int)(fontHeight * .75));
            }
        }
    }
}