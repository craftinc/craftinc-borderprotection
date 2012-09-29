package de.craftinc.borderprotection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Util
{
    public static HashMap<String, ArrayList<Location>> decodeJSON( JSONArray json )
    {
        HashMap<String, ArrayList<Location>> data = new HashMap<String, ArrayList<Location>>();

        for ( Object jsonEntry : json.toArray() )
        {
            JSONObject j = (JSONObject) jsonEntry;
//            // check if border for this world is enabled. continue if not
//            String enabled = (String) j.get("enabled");
//            if (enabled != "1") {
//                continue;
//            }
            String worldname = (String) j.get("worldname");
            ArrayList<Location> locations = new ArrayList<Location>();
            JSONArray borderPoints = (JSONArray) j.get("borderPoints");

            for ( Object pointObj : borderPoints )
            {
                JSONArray point = (JSONArray) pointObj;

                locations
                        .add(new Location(Bukkit.getWorld(worldname), (Double) point.get(0), 0, (Double) point.get(1)));
            }

            data.put(worldname, locations);
        }

        if ( data.size() > 0 )
        {
            return data;
        }

        return null;
    }

    public static JSONArray encodeJSON( HashMap<String, ArrayList<Location>> data )
    {
        JSONArray json = new JSONArray();
        int i = 0;
        for ( ArrayList<Location> border : data.values() )
        {

            // add point 1 as json array
            JSONArray point1 = new JSONArray();
            point1.add(0, border.get(0).getX());
            point1.add(1, border.get(0).getZ());

            // add point 2 as json array
            JSONArray point2 = new JSONArray();
            point2.add(0, border.get(1).getX());
            point2.add(1, border.get(1).getZ());

            // add both points to points json array
            JSONArray points = new JSONArray();
            points.add(point1);
            points.add(point2);

            // Add points and worldname to world json object
            JSONObject borderOfAWorld = new JSONObject();
            try
            {
                borderOfAWorld.put("worldname", border.get(0).getWorld().getName());
                borderOfAWorld.put("borderPoints", points);
                json.add(i, borderOfAWorld);
                i++;
            }
            catch ( NullPointerException e )
            {
                if ( border.get(0).getWorld() == null )
                {
                    Plugin.getPlugin().getLogger()
                          .warning("A world is null. Ignoring this border (not saving this border).");
                }
            }
        }
        return json;
    }
}
