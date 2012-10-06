/*  CraftInc BorderProtection
    Copyright (C) 2012  Paul Schulze

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.craftinc.borderprotection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
     * For every player save the time when he got the last borderMessage
     */
    public HashMap<String, Long> lastBorderMessage = new HashMap<String, Long>();

    /**
     * The buffer in blocks which applies when a player is teleported inside the border. 0 means the player
     * will be teleported directly to the border.
     */
    private double buffer = 0.5;

    /**
     * A timeout for the border message. When a player tries to cross the border and sees the border message,
     * the earliest possible time the message will show up again is after <code>timeout</code> milliseconds.
     */
    private Long timeout = 10000L;

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

    public Serializer getSerializer()
    {
        return serializer;
    }

    public double getBuffer()
    {
        return buffer;
    }

    public Long getTimeout()
    {
        return timeout;
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


    /**
     * Checks if the given location is inside the border rectangle. Returns null if yes, otherwise new coordinates.
     *
     * @param location     location to check
     * @param borderPoints points which define the border rectangle
     * @param buffer       if the player will be teleported back, then he will be <code>buffer</code> far away
     *                     from the border he tried to cross
     * @return null if the player is inside, otherwise a new player location
     */
    public Double[] checkBorder( Location location, ArrayList<Location> borderPoints, double buffer )
    {
        // New x and z: null by default
        Double[] newXZ = { null, null };

        // check if player is withing the X borders
        newXZ[0] = _checkBorder(location.getX(), borderPoints.get(0).getX(), borderPoints.get(1).getX(), buffer);
        // check if player is withing the Z borders
        newXZ[1] = _checkBorder(location.getZ(), borderPoints.get(0).getZ(), borderPoints.get(1).getZ(), buffer);

        // Do nothing, if no new coordinates have been calculated.
        if ( newXZ[0] == null && newXZ[1] == null )
        {
            return null;
        }
        return newXZ;
    }


    /**
     * Checks if the given location is between one specific border pair.
     *
     * @param location part of the location coordinates
     * @param border1  one side of the rectangle
     * @param border2  opposite side of the rectangle
     * @return null if the location is inside, otherwise a new location
     */
    public Double _checkBorder( double location, double border1, double border2, double buffer )
    {
        double bigBorder = Math.max(border1, border2);
        double smallBorder = Math.min(border1, border2);

        // if location is between borders do nothing
        if ( location >= smallBorder && location <= bigBorder )
        {
            return null;
        }
        else
        {
            if ( location > bigBorder )
            {
                // if location is outside of the bigBorder, teleport to the bigBorder
                return bigBorder - buffer;
            }
            else
            {
                // if location is outside of the smallBorder, teleport to the smallBorder
                return smallBorder + buffer;
            }
        }
    }


    /**
     * Show the border message to a player and respect the timeout.
     *
     * @param player Player who will see the border message.
     */
    public void showMessageWithTimeout( Player player, String message )
    {
        // get the current time
        Long now = Calendar.getInstance().getTimeInMillis();

        if ( ( lastBorderMessage.get(player.getName()) != null &&
               now - getTimeout() > lastBorderMessage.get(player.getName()) ) ||
             lastBorderMessage.get(player.getName()) == null )
        {
            // show message
            player.sendMessage(message);

            // set last sent message for this player to now
            lastBorderMessage.put(player.getName(), now);
        }
    }
}
