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
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class CircBorder extends Border implements ConfigurationSerializable
{
    private Double   radius;
    private Location center;

    private static String centerKey = "center";
    private static String radiusKey = "radius";

    @SuppressWarnings("unchecked unused")
    public CircBorder( Map<String, Object> map )
    {
        super(map);
        try
        {
            center = LocationSerializer2D.deserializeLocation((Map<String, Object>) map.get(centerKey));
            radius = (Double) map.get(radiusKey);

            borders.put(center.getWorld(), this);
        }
        catch ( Exception e )
        {
            // FIXME
            Plugin.instance.getLogger().severe(e.getMessage());
        }
    }

    public CircBorder( Location center, Double radius )
    {
        super();

        this.center = center;
        this.radius = radius;

        borders.put(center.getWorld(), this);
    }


    @SuppressWarnings("unused")
    public Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        super.serialize(map);
        map.put(centerKey, LocationSerializer2D.serializeLocation(center));
        map.put(radiusKey, radius);

        return map;
    }

    public String toString()
    {
        return "CircBorder(" + "center: " + center.getX() + "," + center.getZ() + ", radius: " + radius + ")";
    }

    @Override
    public String getBorderTypeString()
    {
        return "Circle";
    }

    @Override
    public String getBorderInfoString()
    {
        return ChatColor.YELLOW + "Center: " + ChatColor.WHITE + center.getX() + "," + center.getZ() + "\n" +
               ChatColor.YELLOW + "Raduis: " + ChatColor.WHITE + radius;
    }

    @Override
    public Location checkBorder( Location l, double padding )
    {
        double paddedRadius = radius + padding;

        double distX = l.getX() - center.getX();
        double distZ = l.getZ() - center.getZ();

        double distanceFromCenterSquared = distX * distX + distZ * distZ;
        double radiusSquared = paddedRadius * paddedRadius;

        // inside the border
        if ( distanceFromCenterSquared <= radiusSquared )
        {
            return null;
        }

        // outside the border: it's ok to use square-root function here, because this only happens very few times
        double ratio = paddedRadius / Math.sqrt(distanceFromCenterSquared);
        double newX = center.getX() + ( ratio * distX );
        double newZ = center.getZ() + ( ratio * distZ );

        Location newLocation = new Location(l.getWorld(), newX, l.getY(), newZ, l.getYaw(), l.getPitch());

        // ensure that the player will not appear in a block
        // TODO: Should hook into another Plugin maybe or implement something better
        newLocation.setY(PlayerMovementUtil.goUpUntilFreeSpot(newLocation));

        return newLocation;
    }

    @Override
    public Location[] getSurroundingRect()
    {
        Location l1 = new Location(center.getWorld(), center.getX()+radius, center.getY(), center.getX()+radius);
        Location l2 = new Location(center.getWorld(), center.getX()-radius, center.getY(), center.getX()-radius);

        return new Location[]{ l1, l2 };
    }
}