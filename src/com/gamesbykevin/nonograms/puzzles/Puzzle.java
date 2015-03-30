package com.gamesbykevin.nonograms.puzzles;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.nonograms.puzzles.Puzzles;

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
     * @param startX Where our puzzle starts
     * @param startY Where our puzzle starts
     */
    public void render(final Graphics graphics, final int startX, final int startY)
    {
        //for now the squares will be white
        graphics.setColor(Color.WHITE);
        
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                final int x = Puzzles.getX(startX, getCellDimensions(), col);
                final int y = Puzzles.getY(startY, getCellDimensions(), row);
                
                switch (getKeyValue(col, row))
                {
                    case Puzzles.KEY_FILL:
                        graphics.fillRect(x, y, getCellDimensions(), getCellDimensions());
                        break;
                        
                    case Puzzles.KEY_MARK:
                        graphics.drawLine(x, y, x + getCellDimensions(), y + getCellDimensions());
                        graphics.drawLine(x + getCellDimensions(), y, x, y + getCellDimensions());
                        break;
                }
            }
        }
        
        //if we have not yet solved
        if (!hasSolved())
        {
            //draw the outline of the board
            renderOutline(graphics, startX, startY);
        }
    }
    
    private void renderOutline(final Graphics graphics, final int startX, final int startY)
    {
        //the color for our board
        graphics.setColor(Color.RED);
        
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                final int x = Puzzles.getX(startX, getCellDimensions(), col);
                final int y = Puzzles.getY(startY, getCellDimensions(), row);
                
                graphics.drawRect(x, y, getCellDimensions(), getCellDimensions());
            }
        }
    }
    
    public void renderHighlight(final Graphics graphics, final int startX, final int startY, final int highlightCol, final int highlightRow)
    {
        //if we have not yet solved
        if (!hasSolved())
        {
            graphics.setColor(Color.YELLOW);

            for (int row = 0; row < getRows(); row++)
            {
                for (int col = 0; col < getCols(); col++)
                {
                    final int x = Puzzles.getX(startX, getCellDimensions(), col);
                    final int y = Puzzles.getY(startY, getCellDimensions(), row);

                    if (row == highlightRow || col == highlightCol)
                        graphics.fillRect(x, y, getCellDimensions(), getCellDimensions());
                }
            }
        }
    }
    
    /**
     * Draw the puzzle hints
     * @param graphics Object used to render graphics
     * @param startX Start x-coordinate
     * @param startY Start y-coordinate
     * @param fontWidth The dimensions of the font
     * @param fontHeight The dimensions of the font
     */
    public void renderHints(final Graphics graphics, final int startX, final int startY, final int fontWidth, final int fontHeight)
    {
        //if we have not yet solved
        if (!hasSolved())
        {
            //draw the column hints
            renderColumnHint(graphics,  startX, startY, fontWidth, fontHeight);

            //draw the row hints
            renderRowHint(graphics,     startX, startY, fontWidth, fontHeight);
        }
    }
    
    private void renderColumnHint(final Graphics graphics, final int startX, final int startY, final int fontWidth, final int fontHeight)
    {
        //draw column hint
        for (int col = 0; col < getCols(); col++)
        {
            //get list
            List<Integer> tmp = this.columnHint.get(col);
            
            int y = startY - (tmp.size() * fontHeight);
            
            for (int i = 0; i < tmp.size(); i++)
            {
                graphics.drawString(tmp.get(i) + "", startX + (col * getCellDimensions()) + (int)(fontWidth * .25), y + (i * fontHeight));
            }
        }
    }
    
    private void renderRowHint(final Graphics graphics, final int startX, final int startY, final int fontWidth, final int fontHeight)
    {
        //draw row hint
        for (int row = 0; row < getRows(); row++)
        {
            //get list
            List<Integer> tmp = this.rowHint.get(row);
            
            int x = startX - (tmp.size() * fontWidth);
            
            for (int i = 0; i < tmp.size(); i++)
            {
                graphics.drawString(tmp.get(i) + "", x + (i * fontWidth), startY + (row * getCellDimensions()) + (int)(fontHeight * .75));
            }
        }
    }
}