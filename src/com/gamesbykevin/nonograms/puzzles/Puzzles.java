package com.gamesbykevin.nonograms.puzzles;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.resources.Text;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.resources.GameText.Keys;
import com.gamesbykevin.nonograms.shared.IElement;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class will contain all of the puzzles in the game
 * @author GOD
 */
public final class Puzzles extends Sprite implements IElement
{
    /**
     * Make sure the values are in this specific order
     */
    public enum Difficulty
    {
        Medium, Hard, VeryEasy, Easy, 
    }
    
    //our list of puzzles for each difficulty
    private HashMap<Difficulty, List<Puzzle>> puzzles;
    
    //the dimension requirements for each difficulty
    public static final int DIMENSIONS_VERY_EASY = 5;
    public static final int DIMENSIONS_EASY = 10;
    public static final int DIMENSIONS_MEDIUM = 15;
    public static final int DIMENSIONS_HARD = 20;
    
    //the characters that mean the current puzzle is finished in our text file
    private static final String PUZZLE_FINISH = "==========";
    
    //the fill text in the text file
    private static final String PUZZLE_FILL = "#";
    
    //the different key options for the puzzle
    public static final int KEY_FILL = 0;
    public static final int KEY_EMPTY = 1;
    public static final int KEY_MARK = 2;
    
    //the current puzzle we are playing
    private int current = 0;
    
    //the level of difficutly of puzzles we want to play
    private Difficulty difficulty = Difficulty.Medium;
    
    //starting coordinates of puzzle
    public static final int START_X = 200;
    public static final int START_Y = 150;
    
    public Puzzles(final Image image)
    {
        //store the image
        super.setImage(image);
        
        //create new puzzle list
        this.puzzles = new HashMap<>();
    }
    
    /**
     * Set the difficulty
     * @param difficulty The difficulty of the puzzles we want to play
     */
    public void setDifficulty(final Difficulty difficulty)
    {
        this.difficulty = difficulty;
    }
    
    /**
     * Get the difficulty
     * @return The current difficulty set
     */
    public Difficulty getDifficulty()
    {
        return this.difficulty;
    }
    
    /**
     * Get the puzzle list
     * @return A list of puzzles for the specified difficulty
     */
    private List<Puzzle> getPuzzleList()
    {
        return getPuzzleList(getDifficulty());
    }
    
    /**
     * Get the puzzle list
     * @param difficulty The specified difficulty
     * @return A list of puzzles for the specified difficulty
     */
    private List<Puzzle> getPuzzleList(final Difficulty difficulty)
    {
        return puzzles.get(difficulty);
    }
    
    /**
     * Get the current Puzzle
     * @return The current Puzzle in play for the current difficulty
     */
    public Puzzle getPuzzle()
    {
        return getPuzzleList().get(current);
    }
    
    @Override
    public void dispose()
    {
        if (puzzles != null)
        {
            for (Difficulty diff : puzzles.keySet())
            {
                for (int i=0; i < puzzles.get(diff).size(); i++)
                {
                    puzzles.get(diff).get(i).dispose();
                    puzzles.get(diff).set(i, null);
                }
                
                puzzles.get(diff).clear();
            }
            
            puzzles.clear();
            puzzles = null;
        }
    }
    
    /**
     * Load the puzzles
     * @param text Object containing text file with puzzle solutions
     */
    private void load(final Text text)
    {
        //the starting line
        int start = 0;
        
        for (int i = 0; i < text.getLines().size(); i++)
        {
            //get the current line
            final String line = text.getLine(i);
            
            //if this line means we are done with the current puzzle
            if (line.equals(PUZZLE_FINISH))
            {
                //create a puzzle within this location
                create(start, i, text);
                
                //now the next start will be after this current line
                start = i + 1;
            }
        }
    }
    
    /**
     * Create a puzzle.
     * @param start The starting line
     * @param end The last line
     * @param text Object containing text file with puzzle solutions
     */
    private void create(final int start, final int end, final Text text)
    {
        //the puzzle we are to add
        final Puzzle puzzle;
        
        //the number of rows in this puzzle
        int rows = end - (start + 1);
        
        //the number of columns
        int cols = 0;
        
        //look at the lines
        for (int i = start + 1; i < end; i++)
        {
            //get the current line
            final String line = text.getLine(i);
            
            //if the line is longer than our cols
            if (line.length() > cols)
                cols = line.length();
        }
        
        //make sure we meet the minimum requirements
        if (cols < DIMENSIONS_VERY_EASY)
            cols = DIMENSIONS_VERY_EASY;
        if (rows < DIMENSIONS_VERY_EASY)
            rows = DIMENSIONS_VERY_EASY;
        
        //create a new puzzle of specified size
        puzzle = new Puzzle(cols, rows, text.getLine(start));
        
        //now assign the appropriate values
        for (int i = start + 1; i < end; i++)
        {
            //get the current line
            final String line = text.getLine(i);
            
            //the current row
            final int row = i - (start + 1);
            
            //now check every column in that line
            for (int col = 0; col < cols; col++)
            {
                //default empty value
                puzzle.setKeyValue(col, row, KEY_EMPTY);
                
                //if the column is greater than the current line length
                if (col < line.length())
                {
                    //if the current location contains a fill character '#'
                    if (line.substring(col, col + 1).equals(PUZZLE_FILL))
                        puzzle.setKeyValue(col, row, KEY_FILL);
                }
            }
        }
        
        //calculate the hints
        puzzle.calculateHint();

        //finally add to proper list
        add(puzzle);
    }
    
    /**
     * Add puzzle to our list.<BR>
     * If the puzzle dimensions do not match (columns, rows) it will not be added.
     * Here we add the puzzle to a specific list depending on the puzzle size (columns) for each difficulty
     * @param puzzle The puzzle we want to add
     */
    private void add(final Puzzle puzzle)
    {
        //if dimensions do not match, do not add
        if (puzzle.getCols() != puzzle.getRows())
            return;
        
        if (puzzle.getCols() == DIMENSIONS_HARD)
        {
            getPuzzleList(Difficulty.Hard).add(puzzle);
        }
        else if (puzzle.getCols() >= DIMENSIONS_MEDIUM && puzzle.getCols() < DIMENSIONS_HARD)
        {
            getPuzzleList(Difficulty.Medium).add(puzzle);
        }
        else if (puzzle.getCols() >= DIMENSIONS_EASY && puzzle.getCols() < DIMENSIONS_MEDIUM)
        {
            getPuzzleList(Difficulty.Easy).add(puzzle);
        }
        else if (puzzle.getCols() >= DIMENSIONS_VERY_EASY && puzzle.getCols() < DIMENSIONS_EASY)
        {
            getPuzzleList(Difficulty.VeryEasy).add(puzzle);
        }
    }
    
    public static int getX(final int startX, final int width, final int column)
    {
        return (startX + (width * column));
    }
    
    public static int getY(final int startY, final int height, final int row)
    {
        return (startY + (height * row));
    }
    
    @Override
    public void update(final Engine engine)
    {
        //if the puzzles list is empty
        if (puzzles.isEmpty())
        {
            //create array list for each difficulty
            for (int i = 0; i < Difficulty.values().length; i++)
            {
                puzzles.put(Difficulty.values()[i], new ArrayList<Puzzle>());
            }
            
            //load from text file
            load(engine.getResources().getGameText(Keys.Puzzles));
            
            //pick random puzzle for now
            this.current = engine.getRandom().nextInt(getPuzzleList().size());
        }
    }
    
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        if (!puzzles.isEmpty())
        {
            //draw puzzle hints
            getPuzzle().renderHints(graphics, getImage(), START_X, START_Y);
        }
    }
}