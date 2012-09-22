package de.craftinc.borderprotection;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BorderManager
{
    /**
     * *********************************************************
     * GLOBAL VARIABLES
     * **********************************************************
     */

    private final String dataFileName = "data.json";

    /**
     * Borders of all Worlds. String is World.getName(). Location is one point of the border. A border
     * consists of two points which create a rectangle.
     */
    private HashMap<String, ArrayList<Location>> borders = null;

    /**
     * the buffer in blocks which applies when a player is teleported inside the border. 0 means the player
     * will be teleported directly to the border.
     */
    private double buffer = 0.5;

    public Serializer getSerializer()
    {
        return serializer;
    }

    /**
     * Serializer, which is used for loading and saving data to harddisk
     */
    private Serializer serializer;

    /**
     * *********************************************************
     * CONSTRUCTOR
     * **********************************************************
     */
    public BorderManager()
    {
        // initialize Serializer and load data file
        serializer = new Serializer(new File(Plugin.getPlugin().getDataFolder(), dataFileName));
        borders = serializer.loadDataFile();
    }


    /**
     * *********************************************************
     * GETTERS AND SETTERS
     * **********************************************************
     */

    public double getBuffer()
    {
        return buffer;
    }

    public HashMap<String, ArrayList<Location>> getBorders()
    {
        return borders;
    }

    public void setBorder( String worldName, double border )
    {
        if ( borders == null )
        {
            borders = new HashMap<String, ArrayList<Location>>();
        }

        World world = Plugin.getPlugin().getServer().getWorld(worldName);

        // set two points which define a square
        borders.put(worldName, new ArrayList<Location>(Arrays.asList(
                new Location(world, border, 0, border),
                new Location(world, -border, 0, -border)
                                                                    )));
    }

    public void setBorder( String worldName, String[] borderPoints )
    {
        if ( borders == null )
        {
            borders = new HashMap<String, ArrayList<Location>>();
        }

        ArrayList<Location> locations = new ArrayList<Location>();
        World world = Plugin.getPlugin().getServer().getWorld(worldName);

        for ( String borderPoint : borderPoints )
        {
            String[] point = borderPoint.split(",");
            locations.add(new Location(world, Double.parseDouble(point[0]), 0, Double.parseDouble(point[1])));
        }

        borders.put(worldName, locations);
    }
}