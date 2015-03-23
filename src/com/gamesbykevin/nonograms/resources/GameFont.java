package com.gamesbykevin.nonograms.resources;

import com.gamesbykevin.framework.resources.FontManager;

public final class GameFont extends FontManager
{
    //description for progress bar
    private static final String DESCRIPTION = "Loading Font Resources";
    
    /**
     * These are the keys used to access the resources and need to match the id in the xml file
     */
    public enum Keys
    {
        Default, 
    }
    
    public GameFont() throws Exception
    {
        super(Resources.XML_CONFIG_GAME_FONT);
        
        //the description that will be displayed for the progress bar
        super.setProgressDescription(DESCRIPTION);
        
        if (Keys.values().length < 1)
            super.increase();
    }
}