/*  Craft Inc. BorderProtection
    Copyright (C) 2016  Paul Schulze, Tobias Ottenweller

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

import de.craftinc.borderprotection.Plugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Border
{
    private static final String dataFileName = "borders.yml";

    private Boolean isActive;

    private static final String isActiveKey = "enabled";
    private static final String bordersKey  = "borders";

    static final HashMap<World, Border> borders = new HashMap<World, Border>();

    private static final File              bordersFile     = new File(Plugin.instance.getDataFolder(), dataFileName);
    private static final FileConfiguration bordersFileConf = YamlConfiguration.loadConfiguration(bordersFile);

    /**
     * The buffer in blocks which applies when a player is teleported inside the border. 0 means the player
     * will be teleported directly to the border.
     */
    static final double buffer = 0.5;

    public static HashMap<World, Border> getBorders()
    {
        return borders;
    }

    /**
     * Returns a String which identifies the type of the border. Example: Rectangle
     */
    public abstract String getBorderTypeString();

    /**
     * Returns a formatted String (colors and newlines) which fits into the borderInfo message and describes
     * the border properties properly.
     */
    public abstract String getBorderInfoString();

    /**
     * Checks if the given location is inside or outside the border. If it is outside a new location (inside the border)
     * is returned, otherwise null.
     *
     * @param l Location to check if inside the border
     * @return null if l is inside the border otherwise a new Location which is inside
     */
    public Location checkBorder( Location l )
    {
        return checkBorder(l, 0.0);
    }

    /**
     * Checks if the given location is inside or outside the border. If it is outside a new location (inside the border)
     * is returned, otherwise null. Applies padding to the border. (Simulates a larger border using padding.)
     *
     * @param l       Location to check if inside the border
     * @param padding number of Blocks of padding applied to the border.
     * @return null if l is inside the border otherwise a new Location which is inside
     */
    public abstract Location checkBorder( Location l, double padding );

    /**
     * Returns an array of two Location objects defining a rectangle bigger or at size of the border. There are no
     * locations outside the rectangle which are inside the border.
     */
    public abstract Location[] getSurroundingRect();

    public Boolean isActive()
    {
        return isActive;
    }

    Border( Map<String, Object> map )
    {
        try
        {
            isActive = (Boolean) map.get(isActiveKey);
        }
        catch ( Exception e )
        {
            // FIXME
            Plugin.instance.getLogger().severe(e.getMessage());
        }
    }

    Border()
    {
        // new borders are enabled by default
        isActive = true;
    }

    @SuppressWarnings("unused")
    public static void loadBorders()
    {
        bordersFileConf.getList(bordersKey);
    }

    public static void saveBorders() throws IOException
    {
        bordersFileConf.set(bordersKey, new ArrayList<Object>(borders.values()));
        bordersFileConf.save(bordersFile);
    }

    public void enable()
    {
        isActive = true;
    }

    public void disable()
    {
        isActive = false;
    }

    void serialize( Map<String, Object> map )
    {
        map.put(isActiveKey, isActive);
    }
}
