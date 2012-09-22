package de.craftinc.borderprotection;

import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Serializer
{
    private File dataFile;

    public Serializer( File dataFile )
    {
        this.dataFile = dataFile;
    }

    public HashMap<String, ArrayList<Location>> loadDataFile()
    {
        try
        {
            FileReader fr = new FileReader(dataFile);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder fileContents = new StringBuilder();

            String line;
            while ( ( line = br.readLine() ) != null )
            {
                fileContents.append(line);
            }

            JSONParser jsonParser = new JSONParser();
            JSONArray json = (JSONArray) jsonParser.parse(fileContents.toString());

            return Util.decodeJSON(json);
        }
        catch ( IOException e )
        {
            if ( e instanceof FileNotFoundException )
            {
                Plugin.getPlugin().getLogger().info("Data file not found.");
            }
            else
            {
                e.printStackTrace();
            }
        }
        catch ( ParseException e )
        {
            Plugin.getPlugin().getLogger()
                  .severe("Could not parse json data file. When you set up a new border, the corrupt data file will be overwritten!");
            e.printStackTrace();
        }
        return null;
    }

    public void saveDataFile( HashMap<String, ArrayList<Location>> data )
    {
        // if there is not data, do nothing and log it to console
        if ( data == null )
        {
            Plugin.getPlugin().getLogger().severe("Could not save data, because it is null");
            return;
        }

        // create plugin directory if it doesn't exists
        if ( !Plugin.getPlugin().getDataFolder().exists() )
        {
            Plugin.getPlugin().getLogger().info("Creating plugin directory...");
            if ( !Plugin.getPlugin().getDataFolder().mkdir() )
            {
                Plugin.getPlugin().getLogger().severe("Could not create plugin directory");
            }
            else
            {
                Plugin.getPlugin().getLogger().info("Plugin directory created successfully");
            }
        }

        JSONArray json = Util.encodeJSON(data);

        // Write to file
        try
        {
            FileWriter fw = new FileWriter(dataFile);
            fw.write(json.toJSONString());
            fw.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
}