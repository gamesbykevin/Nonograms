package com.gamesbykevin.nonograms.engine;

import com.gamesbykevin.nonograms.resources.Resources;
import com.gamesbykevin.nonograms.main.Main;
import com.gamesbykevin.nonograms.manager.Manager;
import com.gamesbykevin.nonograms.menu.CustomMenu;
import com.gamesbykevin.nonograms.shared.Shared;

import com.gamesbykevin.framework.input.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public final class Engine implements KeyListener, MouseMotionListener, MouseListener, IEngine 
{
    //our Main class has important information in it so we need a reference here
    private final Main main;
    
    //access this menu here
    private CustomMenu menu;
    
    //our object that will contain all of the game resources
    private Resources resources;
    
    //mouse object that will be recording mouse input
    private Mouse mouse;
    
    //keyboard object that will be recording key input
    private Keyboard keyboard;
    
    //object containing all of the game elements
    private Manager manager;
    
    //object used to make random decisions
    private Random random;
    
    //default font
    private Font font;
    
    //seed for the Random object
    private final long seed = System.nanoTime();
    
    //the game font size
    private static final float GAME_FONT_SIZE = 64f;
    
    /**
     * The Engine that contains the game/menu objects
     * 
     * @param main Main object that contains important information so we need a reference to it
     * @throws CustomException 
     */
    public Engine(final Main main) throws Exception
    {
        //reference to parent class
        this.main = main;
        
        //object used to track mouse input
        this.mouse = new Mouse();
        
        //object used to track keyboard input
        this.keyboard = new Keyboard();
        
        //create new Random object
        random = new Random(seed);
        
        //display seed if debugging
        if (Shared.DEBUG)
            System.out.println("Seed = " + seed);
    }
    
    /**
     * Proper house-keeping
     */
    @Override
    public void dispose()
    {
        try
        {
            if (resources != null)
            {
                resources.dispose();
                resources = null;
            }
            
            if (menu != null)
            {
                menu.dispose();
                menu = null;
            }
            
            if (mouse != null)
            {
                mouse.dispose();
                mouse = null;
            }
            
            if (keyboard != null)
            {
                keyboard.dispose();
                keyboard = null;
            }
            
            if (manager != null)
            {
                manager.dispose();
                manager = null;
            }
            
            random = null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Main main) throws Exception
    {
        if (menu == null)
        {
            //create new menu
            menu = new CustomMenu(this);

            //reset mouse and keyboard input
            resetInput();
        }
        else
        {
            //does the menu have focus
            if (!menu.hasFocus())
            {
                //reset mouse and keyboard input
                resetInput();
            }

            //update the menu
            menu.update(this);

            //if the menu is finished and the window has focus
            if (menu.hasFinished() && menu.hasFocus())
            {
                //if our resources object is empty create a new one
                if (resources == null)
                    this.resources = new Resources();

                //check if we are still loading resources
                if (resources.isLoading())
                {
                    //load resources
                    resources.update(main.getContainerClass());
                }
                else
                {
                    //create new manager because at this point our resources have loaded
                    if (manager == null)
                    {
                        manager = new Manager(this);
                        manager.reset(this);
                    }

                    //update main game logic
                    manager.update(this);
                }
            }

            //if the mouse is released reset all mouse events
            if (mouse.isMouseReleased())
                mouse.reset();
        }
    }
    
    public long getSeed()
    {
        return this.seed;
    }
    
    /**
     * Reset keyboard and mouse input.<br>
     * Stop any existing sound from playing.<br>
     * Reset our game manager object
     * @throws Exception if there is an error resetting the game manager
     */
    public void reset() throws Exception
    {
        //reset mouse and keyboard input
        resetInput();
        
        //if our resources object exists, stop any existing sound from playing
        if (this.resources != null)
            this.resources.stopAllSound();
        
        if (manager != null)
        {
            manager.dispose();
            manager = null;
        }
    }
    
    /**
     * Reset the keyboard and mouse inputs
     */
    private void resetInput()
    {
        //reset mouse and keyboard input
        getMouse().reset();
        getKeyboard().reset();
    }
    
    public Main getMain()
    {
        return main;
    }
    
    /**
     * Get our object used to make random decisions
     * @return Random
     */
    public Random getRandom()
    {
        return this.random;
    }
    
    /**
     * Draw our game to the Graphics object whether resources are still loading or the game is intact
     * @param graphics
     * @return Graphics
     * @throws Exception 
     */
    @Override
    public void render(Graphics graphics) throws Exception
    {
        //get default font
        if (font == null)
            font = graphics.getFont().deriveFont(Font.BOLD, GAME_FONT_SIZE);
        
        if (getMenu() != null)
        {
            //if the menu is finished and the window has focus
            if (getMenu().hasFinished() && getMenu().hasFocus())
            {
                //before we start game we need to load the resources
                if (resources.isLoading())
                {
                    //set default font
                    graphics.setFont(font);
                    
                    //draw loading screen
                    resources.render(graphics, main.getScreen());
                }
            }
            
            //draw game elements
            if (manager != null)
            {
                //set default font
                graphics.setFont(font);
                
                manager.render(graphics);
            }
            
            //draw menu on top of the game if visible
            renderMenu(graphics);
        }
    }
    
    /**
     * Draw the Game Menu
     * 
     * @param graphics Graphics object where Images/Objects will be drawn to
     * @throws Exception 
     */
    private void renderMenu(Graphics graphics) throws Exception
    {
        //if menu is setup draw menu
        if (getMenu().isSetup() && !getMenu().hasFinished())
            getMenu().render(graphics);

        //if menu is finished and we don't want to hide the mouse cursor then draw it, or if the menu is not finished draw it
        if (getMenu().hasFinished() && !Shared.HIDE_MOUSE || !getMenu().hasFinished())
        {
            //draw the mouse
            getMenu().renderMouse(graphics, mouse);
        }
    }
    
    /**
     * Object that contains all of the game elements
     * @return Manager
     */
    public Manager getManager()
    {
        return this.manager;
    }
    
    public CustomMenu getMenu()
    {
        return this.menu;
    }
    
    public Resources getResources()
    {
        return resources;
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        keyboard.addKeyReleased(e.getKeyCode());
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        keyboard.addKeyPressed(e.getKeyCode());
    }
    
    @Override
    public void keyTyped(KeyEvent e)
    {
        //do nothing here for now
        //keyboard.addKeyTyped(e.getKeyChar());
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        mouse.setMouseClicked(e);
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        mouse.setMousePressed(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        mouse.setMouseReleased(e);
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        mouse.setMouseEntered(e.getPoint());
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        mouse.setMouseExited(e.getPoint());
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouse.setMouseMoved(e.getPoint());
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
        mouse.setMouseDragged(e.getPoint());
    }
    
    public Mouse getMouse()
    {
        return mouse;
    }
    
    public Keyboard getKeyboard()
    {
        return keyboard;
    }
}