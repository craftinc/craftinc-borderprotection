package de.craftinc.borderprotection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Border
{
    private static final String dataFileName = "borders.json";

    private Location rectPoint1;
    private Location rectPoint2;

    private static String rectPoint1Name = "p1";
    private static String rectPoint2Name = "p2";
    private static String rectBordersKey = "rectBorders";

    private static final HashMap<World, Border> borders = new HashMap<World, Border>();

    private static File              bordersFile     = new File(Plugin.getPlugin().getDataFolder(), dataFileName);
    private static FileConfiguration bordersFileConf = YamlConfiguration.loadConfiguration(bordersFile);

    public static HashMap<World, Border> getBorders()
    {
        return borders;
    }

    public Location getRectPoint1()
    {
        return rectPoint1;
    }

    public Location getRectPoint2()
    {
        return rectPoint2;
    }

    @SuppressWarnings("unchecked")
    public Border( Map<String, Object> map )
    {
        try
        {
            rectPoint1 = LocationSerializer.deserializeLocation((Map<String, Object>) map.get(rectPoint1Name));
            rectPoint2 = LocationSerializer.deserializeLocation((Map<String, Object>) map.get(rectPoint2Name));

            if ( rectPoint1.getWorld().equals(rectPoint2.getWorld()) )
            {
                borders.put(rectPoint1.getWorld(), this);
            }
            else
            {
                throw new Exception("Border points are at different worlds.");
            }
        }
        catch ( Exception e )
        {
            Plugin.getPlugin().getLogger().severe(e.getMessage());
        }
    }

    public Border( Location p1, Location p2 ) throws Exception
    {
        rectPoint1 = p1;
        rectPoint2 = p2;
        if ( rectPoint1.getWorld().equals(rectPoint2.getWorld()) )
        {
            borders.put(rectPoint1.getWorld(), this);
        }
        else
        {
            throw new Exception("Border points are at different worlds.");
        }
    }


    public Map<String, Object> serialize()
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(rectPoint1Name, LocationSerializer.serializeLocation(rectPoint1));
        map.put(rectPoint2Name, LocationSerializer.serializeLocation(rectPoint2));

        return map;
    }

    public static void loadBorders()
    {
        bordersFileConf.getList(rectBordersKey);
    }

    public static void saveBorders()
    {
        bordersFileConf.set(rectBordersKey, borders.values());
    }

    public String toString()
    {
        return rectPoint1.getX() + "," + rectPoint1.getZ() + " " + rectPoint2.getX() + "," + rectPoint2.getZ();
    }
}
