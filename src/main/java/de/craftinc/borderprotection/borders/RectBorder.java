/*  Craft Inc. BorderProtection
    Copyright (C) 2013  Paul Schulze, Tobias Ottenweller

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
package de.craftinc.borderprotection.borders;

import de.craftinc.borderprotection.util.LocationSerializer2D;
import de.craftinc.borderprotection.Plugin;
import de.craftinc.borderprotection.util.PlayerMovementUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class RectBorder extends Border implements ConfigurationSerializable
{
    private Location rectPoint1;
    private Location rectPoint2;

    private static String rectPoint1Name = "p1";
    private static String rectPoint2Name = "p2";

    @SuppressWarnings("unchecked unused")
    public RectBorder( Map<String, Object> map )
    {
        super(map);

        try
        {
            rectPoint1 = LocationSerializer2D.deserializeLocation((Map<String, Object>) map.get(rectPoint1Name));
            rectPoint2 = LocationSerializer2D.deserializeLocation((Map<String, Object>) map.get(rectPoint2Name));

            if ( rectPoint1.getWorld().equals(rectPoint2.getWorld()) )
            {
                borders.put(rectPoint1.getWorld(), this);
            }
            else
            {
                throw new Exception("RectBorder points are at different worlds.");
            }
        }
        catch ( Exception e )
        {
            Plugin.instance.getLogger().severe(e.getMessage());
        }
    }

    public RectBorder( Location p1, Location p2 ) throws Exception
    {
        super();

        rectPoint1 = p1;
        rectPoint2 = p2;

        if ( rectPoint1.getWorld().equals(rectPoint2.getWorld()) )
        {
            borders.put(rectPoint1.getWorld(), this);
        }
        else
        {
            throw new Exception("RectBorder points are at different worlds.");
        }
    }

    @SuppressWarnings("unused")
    public Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        super.serialize(map);
        map.put(rectPoint1Name, LocationSerializer2D.serializeLocation(rectPoint1));
        map.put(rectPoint2Name, LocationSerializer2D.serializeLocation(rectPoint2));

        return map;
    }


    public String toString()
    {
        return "RectBorder(" + rectPoint1.getX() + "," + rectPoint1.getZ() + ";" + rectPoint2.getX() + "," +
               rectPoint2.getZ() + ")";
    }

    @Override
    public String getBorderTypeString()
    {
        return "Rectangle";
    }

    @Override
    public String getBorderInfoString()
    {
        return ChatColor.YELLOW + "Point 1: " + ChatColor.WHITE + rectPoint1.getX() + "," + rectPoint1.getZ() + "\n" +
               ChatColor.YELLOW + "Point 2: " + ChatColor.WHITE + rectPoint2.getX() + "," + rectPoint2.getZ();
    }

    /**
     * Checks if the given location is inside the rectBorder rectangle. Returns null if yes, otherwise new coordinates.
     *
     * @param l location to check
     * @return null if the player is inside, otherwise a new player location
     */
    @Override
    public Location checkBorder( Location l )
    {
        // New x and z: null by default
        Double[] newXZ = { null, null };

        // check if player is withing the X borders
        newXZ[0] = _checkBorder(l.getX(), this.rectPoint1.getX(), this.rectPoint2.getX());
        // check if player is withing the Z borders
        newXZ[1] = _checkBorder(l.getZ(), this.rectPoint1.getZ(), this.rectPoint2.getZ());

        // Do nothing, if no new coordinates have been calculated.
        if ( newXZ[0] == null && newXZ[1] == null )
        {
            return null;
        }

        // if one of the coordinates is null, set it to the player's value
        newXZ[0] = newXZ[0] == null ? l.getX() : newXZ[0];
        newXZ[1] = newXZ[1] == null ? l.getZ() : newXZ[1];

        // new location
        Location newLocation = new Location(l.getWorld(), newXZ[0], l.getY(), newXZ[1], l.getYaw(), l.getPitch());

        // change Y if necessary (when there is no free spot)
        newLocation.setY(PlayerMovementUtil.goUpUntilFreeSpot(newLocation));

        return newLocation;
    }


    /**
     * Checks if the given location is between one specific border pair.
     *
     * @param location part of the location coordinates
     * @param border1  one side of the rectangle
     * @param border2  opposite side of the rectangle
     * @return null if the location is inside, otherwise a new location
     */
    private static Double _checkBorder( double location, double border1, double border2 )
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
                if ( buffer > bigBorder )
                    return bigBorder;
                return bigBorder - buffer;
            }
            else
            {
                // if location is outside of the smallBorder, teleport to the smallBorder
                if ( buffer > Math.abs(smallBorder) )
                    return smallBorder;
                return smallBorder + buffer;
            }
        }
    }

    @Override
    public Location[] getSurroundingRect()
    {
        return new Location[]{ rectPoint1, rectPoint2 };
    }

    @Override
    public Location getCenter()
    {
        World w = rectPoint1.getWorld();
        double x = Math.abs(rectPoint1.getX() - rectPoint2.getX()) / 2.0 + Math.min(rectPoint1.getX(), rectPoint2.getX());
        double y = rectPoint1.getY();
        double z = Math.abs(rectPoint1.getZ() - rectPoint2.getZ()) / 2.0 + Math.min(rectPoint1.getZ(), rectPoint2.getZ());

        return new Location(w, x, y, z);
    }
}
