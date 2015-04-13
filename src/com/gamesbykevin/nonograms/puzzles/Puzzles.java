package com.gamesbykevin.nonograms.puzzles;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Text;

import com.gamesbykevin.nonograms.engine.Engine;
import com.gamesbykevin.nonograms.resources.GameAudio;
import com.gamesbykevin.nonograms.resources.GameText.Keys;
import com.gamesbykevin.nonograms.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
    
    /**
     * Get the column position based on the x coordinatE
     * @param x x-coordinate
     * @param puzzle The puzzle containing the dimensions
     * @return The column position
     */
    public static double getColumn(final double x, final Puzzle puzzle)
    {
        return (x - Puzzles.START_X) / puzzle.getCellDimensions();
    }
    
    /**
     * Get the row position based on the y coordinate
     * @param y y-coordinate
     * @param puzzle The puzzle containing the dimensions
     * @return The row position
     */
    public static double getRow(final double y, final Puzzle puzzle)
    {
        return (y - Puzzles.START_Y) / puzzle.getCellDimensions();
    }
    
    /**
     * Get the x-coordinate based on the column
     * @param col column
     * @param puzzle The puzzle containing the dimensions
     * @return The x-coordinate
     */
    public static double getCoordinateX(final double col, final Puzzle puzzle)
    {
        return (Puzzles.START_X + (col * puzzle.getCellDimensions()));
    }
    
    /**
     * Get the y-coordinate based on the row
     * @param row Row
     * @param puzzle The puzzle containing the dimensions
     * @return The y-coordinate
     */
    public static double getCoordinateY(final double row, final Puzzle puzzle)
    {
        return (Puzzles.START_Y + (row * puzzle.getCellDimensions()));
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
        
        //first check if there is a mismatch
        if (cols != rows)
        {
            //check for any puzzles close to the same dimensions
            for (int extra = 1; extra <= 2; extra++)
            {
                //if dimensions are close enough, make them  match
                if (cols - extra == rows || cols + extra == rows)
                {
                    if (cols == DIMENSIONS_HARD || rows == DIMENSIONS_HARD)
                    {
                        cols = DIMENSIONS_HARD;
                        rows = DIMENSIONS_HARD;
                    }
                    else if (cols == DIMENSIONS_MEDIUM || rows == DIMENSIONS_MEDIUM)
                    {
                        cols = DIMENSIONS_MEDIUM;
                        rows = DIMENSIONS_MEDIUM;
                    }
                    else if (cols == DIMENSIONS_EASY || rows == DIMENSIONS_EASY)
                    {
                        cols = DIMENSIONS_EASY;
                        rows = DIMENSIONS_EASY;
                    }
                    else if (cols == DIMENSIONS_VERY_EASY || rows == DIMENSIONS_VERY_EASY)
                    {
                        cols = DIMENSIONS_VERY_EASY;
                        rows = DIMENSIONS_VERY_EASY;
                    }
                }
                
                //exit loop
                break;
            }
        }
        
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
                //make sure in bounds
                if (col >= puzzle.getCols() || row >= puzzle.getRows())
                    continue;
                
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
        
        //the list for a specified difficulty
        List<Puzzle> list = null;
                
        if (puzzle.getCols() == DIMENSIONS_VERY_EASY)
        {
            list = getPuzzleList(Difficulty.VeryEasy);
        }
        else if (puzzle.getCols() <= DIMENSIONS_EASY)
        {
            list = getPuzzleList(Difficulty.Easy);
        }
        else if (puzzle.getCols() <= DIMENSIONS_MEDIUM)
        {
            list = getPuzzleList(Difficulty.Medium);
        }
        else if (puzzle.getCols() <= DIMENSIONS_HARD)
        {
            list = getPuzzleList(Difficulty.Hard);
        }
        
        //make sure the list exists
        if (list != null)
        {
            //does any other puzzle match the current one we are adding
            boolean match = false;
            
            //make sure the puzzle hasn't already been added first
            for (int i = 0; i < list.size(); i++)
            {
                //get the current puzzle
                final Puzzle tmp = list.get(i);
                
                //only check if the dimensions match
                if (tmp.getCols() != puzzle.getCols() || tmp.getRows() != puzzle.getRows())
                    continue;
                
                //if the puzzle matches
                if (tmp.hasMatch(puzzle))
                {
                    match = true;
                    break;
                }
            }
            
            //if no match was found add to the list
            if (!match)
                list.add(puzzle);
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
            //create array list containing the puzzles for each difficulty
            for (int i = 0; i < Difficulty.values().length; i++)
            {
                puzzles.put(Difficulty.values()[i], new ArrayList<Puzzle>());
            }
            
            //load from text file
            load(engine.getResources().getGameText(Keys.Puzzles));
            
            //set the random level
            setRandomLevel(engine.getRandom());
            
            //create the human puzzle
            engine.getManager().getHuman().create(getPuzzle());
            
            //stop all sound
            engine.getResources().stopAllSound();
            
            //play main theme
            engine.getResources().playGameAudio(GameAudio.Keys.Theme, true);
            
            //if hints are enabled, apply them
            if (engine.getManager().getHuman().hasHintEnabled())
                engine.getManager().getHuman().applyHint(getPuzzle(), engine.getRandom());
        }
    }

    /**
     * Do puzzles exist?
     * @return true if puzzles exist for the current assigned difficulty, false otherwise
     */
    public boolean hasPuzzles()
    {
        return (!getPuzzleList().isEmpty());
    }
    
    /**
     * Remove the current puzzle from the list of the current assigned difficulty.<br>
     */
    public void removeCurrent()
    {
        getPuzzleList().remove(current);
    }
    
    /**
     * Pick a random level of the current assigned difficulty.<br>
     * Nothing will happen if no puzzles exist for the assigned difficulty.
     * @param random Object used to make random decisions
     */
    public void setRandomLevel(final Random random)
    {
        //don't continue if no puzzles left
        if (!hasPuzzles())
            return;
        
        //pick random puzzle of assigned difficulty
        this.current = random.nextInt(getPuzzleList().size());
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