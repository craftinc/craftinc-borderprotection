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
package de.craftinc.borderprotection.commands;

import de.craftinc.borderprotection.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandSwitch implements CommandExecutor
{
    private final Map<String, SubCommand> subCommandsMap = new HashMap<String, SubCommand>();

    public CommandSwitch()
    {
        registerCommand(new CancelGenerateCommand());
        registerCommand(new GenerateCommand());
        registerCommand(new GetCommand());
        registerCommand(new HelpCommand());
        registerCommand(new OnOffCommand());
        registerCommand(new SetCommand());
    }

    private void registerCommand( SubCommand command )
    {
        for ( String commandName : command.commandNames() )
        {
            subCommandsMap.put(commandName, command);
        }
    }

    public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
    {
        // Check if command comes from a player.
        if ( !( sender instanceof Player ) )
        {
            sender.sendMessage(Messages.commandIssuedByNonPlayer);
            return true;
        }

        boolean success = false;

        // command for all actions
        if ( command.getName().equalsIgnoreCase("cibp") )
        {
            if ( args.length > 0 )
            {
                String lowerCaseSubCommandName = args[0].toLowerCase();
                SubCommand subCommand = subCommandsMap.get(lowerCaseSubCommandName);

                if ( subCommand != null )
                {
                    success = subCommand.execute(sender, args);
                }
            }

            if ( !success )
            {
                subCommandsMap.get("help").execute(sender, args);
            }

            return success;
        }

        return false;
    }
}
