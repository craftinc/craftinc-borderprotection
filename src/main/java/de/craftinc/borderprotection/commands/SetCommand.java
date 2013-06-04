/*  Craft Inc. BorderProtection
    Copyright (C) 2013  Paul Schulze

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
package de.craftinc.borderprotection.commands;


import de.craftinc.borderprotection.Messages;
import de.craftinc.borderprotection.borders.Border;
import de.craftinc.borderprotection.borders.CircBorder;
import de.craftinc.borderprotection.borders.RectBorder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetCommand  implements SubCommand
{
    @Override
    public boolean execute(CommandSender sender, String[] parameters)
    {
        if ( !( parameters.length == 3 || parameters.length == 4 ) )
        {
            return false;
        }

        if ( !sender.hasPermission("craftinc.borderprotection.set") )
        {
            sender.sendMessage(Messages.noPermissionSet);
            return false;
        }

        World world = ( (Player) sender ).getWorld();

        // set [r|c] <distance>
        if ( parameters.length == 3 )
        {
            try
            {
                Double distance = Double.parseDouble(parameters[2]);
                Border newBorder = null;

                // rect border
                if ( parameters[1].equalsIgnoreCase("r") )
                {
                    newBorder = new RectBorder(new Location(world, distance, 0, distance),
                            new Location(world, -distance, 0, -distance));
                }
                // circ border
                else if ( parameters[1].equalsIgnoreCase("c") )
                {
                    newBorder = new CircBorder(new Location(world, 0, 0, 0), distance);
                }

                if ( newBorder != null )
                {
                    sender.sendMessage(Messages.borderCreationSuccessful);
                    sender.sendMessage(
                            Messages.borderInfo(world.getName(), newBorder));
                }
            }
            catch ( Exception e )
            {
                sender.sendMessage(e.getMessage());
            }
        }
        // set r <point1> <point2> | set c <center> <radius>
        else
        {
            try
            {
                Border newBorder = null;

                // rect border
                if ( parameters[1].equalsIgnoreCase("r") )
                {
                    Double p1X = Double.parseDouble(parameters[2].split(",")[0]);
                    Double p1Z = Double.parseDouble(parameters[2].split(",")[1]);
                    Double p2X = Double.parseDouble(parameters[3].split(",")[0]);
                    Double p2Z = Double.parseDouble(parameters[3].split(",")[1]);

                    newBorder = new RectBorder(new Location(world, p1X, 0, p1Z),
                            new Location(world, p2X, 0, p2Z));
                }
                // circ border
                else if ( parameters[1].equalsIgnoreCase("c") )
                {
                    Double centerX = Double.parseDouble(parameters[2].split(",")[0]);
                    Double centerZ = Double.parseDouble(parameters[2].split(",")[1]);
                    Double radius = Double.parseDouble(parameters[3]);

                    newBorder = new CircBorder(new Location(world, centerX, 0, centerZ), radius);
                }

                if ( newBorder != null )
                {
                    sender.sendMessage(Messages.borderCreationSuccessful);
                    sender.sendMessage(
                            Messages.borderInfo(world.getName(), newBorder));
                }
            }
            catch ( Exception e )
            {
                sender.sendMessage(e.getMessage());
            }
        }

        // save the new border
        try
        {
            Border.saveBorders();
        }
        catch ( IOException e )
        {
            sender.sendMessage(Messages.borderSaveException);
        }
        return true;
    }

    @Override
    public List<String> commandNames()
    {
        ArrayList<String> names = new ArrayList<String>();
        names.add("set");

        return names;
    }
}
