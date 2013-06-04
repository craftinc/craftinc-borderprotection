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
package de.craftinc.borderprotection;

import de.craftinc.borderprotection.borders.Border;
import de.craftinc.borderprotection.borders.CircBorder;
import de.craftinc.borderprotection.borders.RectBorder;
import de.craftinc.borderprotection.util.UpdateHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Commands implements CommandExecutor
{
    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
    {
        // Check if command comes from a player.
        if ( !( sender instanceof Player ) )
        {
            sender.sendMessage(Messages.commandIssuedByNonPlayer);
            return true;
        }

        // command for all actions
        if ( command.getName().equalsIgnoreCase("cibp") )
        {
            // help
            if ( args.length == 0 || ( args.length > 0 && args[0].equalsIgnoreCase("help") ) )
            {
                sender.sendMessage(Messages.helpGeneral);
                return true;
            }

            // checkversion
            if ( args.length > 0 && args[0].equalsIgnoreCase("checkversion") )
            {
                if ( !sender.hasPermission("craftinc.borderprotection.update") )
                {
                    sender.sendMessage(Messages.noPermissionCheckversion);
                    return false;
                }

                if ( UpdateHelper.newVersionAvailable() )
                {
                    sender.sendMessage(
                            Messages.updateMessage(UpdateHelper.cachedLatestVersion, UpdateHelper.getCurrentVersion()));
                    return true;
                }
                else
                {
                    sender.sendMessage(Messages.noUpdateAvailable);
                    return true;
                }

            }

            // set
            if ( ( args.length == 3 || args.length == 4 ) && args[0].equalsIgnoreCase("set") )
            {
                if ( !sender.hasPermission("craftinc.borderprotection.set") )
                {
                    sender.sendMessage(Messages.noPermissionSet);
                    return false;
                }
                World world = ( (Player) sender ).getWorld();

                // set [r|c] <distance>
                if ( args.length == 3 )
                {
                    try
                    {
                        Double distance = Double.parseDouble(args[2]);
                        Border newBorder = null;

                        // rect border
                        if ( args[1].equalsIgnoreCase("r") )
                        {
                            newBorder = new RectBorder(new Location(world, distance, 0, distance),
                                                       new Location(world, -distance, 0, -distance));
                        }
                        // circ border
                        else if ( args[1].equalsIgnoreCase("c") )
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
                        if ( args[1].equalsIgnoreCase("r") )
                        {
                            Double p1X = Double.parseDouble(args[2].split(",")[0]);
                            Double p1Z = Double.parseDouble(args[2].split(",")[1]);
                            Double p2X = Double.parseDouble(args[3].split(",")[0]);
                            Double p2Z = Double.parseDouble(args[3].split(",")[1]);

                            newBorder = new RectBorder(new Location(world, p1X, 0, p1Z),
                                                       new Location(world, p2X, 0, p2Z));
                        }
                        // circ border
                        else if ( args[1].equalsIgnoreCase("c") )
                        {
                            Double centerX = Double.parseDouble(args[2].split(",")[0]);
                            Double centerZ = Double.parseDouble(args[2].split(",")[1]);
                            Double radius = Double.parseDouble(args[3]);

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

            // get
            if ( args.length == 1 && ( args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("info") ) )
            {
                World world = ( (Player) sender ).getWorld();

                // exit and send the player a message if no border is set
                if ( !Border.getBorders().containsKey(world) )
                {
                    sender.sendMessage(Messages.borderInfoNoBorderSet);
                    return true;
                }

                Border border = Border.getBorders().get(world);

                sender.sendMessage(Messages.borderInfo(world.getName(), border));
                return true;
            }

            // on
            if ( args.length == 1 && ( args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off") ) )
            {
                if ( !sender.hasPermission("craftinc.borderprotection.set") )
                {
                    sender.sendMessage(Messages.noPermissionSet);
                    return false;
                }

                World world = ( (Player) sender ).getWorld();
                Border border = Border.getBorders().get(world);

                if ( border != null )
                {
                    if ( args[0].equalsIgnoreCase("on") )
                    {
                        border.enable();
                        sender.sendMessage(Messages.borderEnabled);
                    }
                    else
                    {
                        border.disable();
                        sender.sendMessage(Messages.borderDisabled);
                    }
                }
                else
                {
                    sender.sendMessage(Messages.borderInfoNoBorderSet);
                }

                // save the changed border
                try
                {
                    Border.saveBorders();
                }
                catch ( IOException e )
                {
                    sender.sendMessage(Messages.borderEnableDisableException);
                }
                return true;
            }
        }

        sender.sendMessage(Messages.helpGeneral);
        return false;
    }
}
