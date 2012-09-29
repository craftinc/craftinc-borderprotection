package de.craftinc.borderprotection;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Commands implements CommandExecutor
{
    private BorderManager borderManager;

    public Commands( BorderManager borderManager )
    {
        this.borderManager = borderManager;
    }

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

            // set
            if ( ( args.length == 2 || args.length == 3 ) && args[0].equalsIgnoreCase("set") )
            {
                if ( !sender.hasPermission("craftinc.borderprotection.set") )
                {
                    sender.sendMessage(Messages.noPermissionSet);
                    return false;
                }
                if ( args.length == 2 )
                {
                    borderManager.setBorder(( (Player) sender ).getWorld().getName(), Double.parseDouble(args[1]));
                }
                else if ( args.length == 3 )
                {
                    String[] borderDefinition = { args[1], args[2] };
                    borderManager.setBorder(( (Player) sender ).getWorld().getName(), borderDefinition);
                }

                // save the new border
                borderManager.getSerializer().saveDataFile(borderManager.getBorders());
                return true;
            }

            // get
            if ( args.length == 1 && ( args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("info") ) )
            {
                String worldName = ( (Player) sender ).getWorld().getName();

                // exit and send the player a message if no border is set
                if ( borderManager.getBorders() == null ||
                     borderManager.getBorders().get(worldName) == null )
                {
                    sender.sendMessage(Messages.borderInfoNoBorderSet);
                    return true;
                }

                ArrayList<Location> borderPoints = borderManager.getBorders()
                                                                .get(worldName);
                String borderDef = borderPoints.get(0).getX() + "," + borderPoints.get(0).getZ() + " " +
                                   borderPoints.get(1).getX() + "," + borderPoints.get(1).getZ();

                sender.sendMessage(Messages.borderInfo(worldName, borderDef));
                return true;
            }
        }
        return false;
    }
}
