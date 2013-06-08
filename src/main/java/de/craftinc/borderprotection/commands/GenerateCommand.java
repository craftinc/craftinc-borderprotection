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
import de.craftinc.borderprotection.util.ChunkGenerator;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GenerateCommand implements SubCommand
{
    @Override
    public boolean execute(CommandSender sender, String[] parameters)
    {
        if ( !sender.hasPermission("craftinc.borderprotection.set") ) // TODO: a new/different permission?
        {
            sender.sendMessage(Messages.noPermissionSet);
            return false;
        }

        World world = ( (Player) sender ).getWorld();

        if (ChunkGenerator.isGenerating(world))
        {
            sender.sendMessage("already generating! will resume on logout"); // TODO: put better message into Message class
        }
        else
        {
            if (ChunkGenerator.generate(world))
            {
                sender.sendMessage("world marked for generation! will start on logout"); // TODO: put better message into Message class
            }
            else
            {
                sender.sendMessage("could not start generation. is there a border?"); // TODO: put better message into Message class
            }

        }

        return true;
    }

    @Override
    public List<String> commandNames()
    {
        ArrayList<String> names = new ArrayList<String>();
        names.add("generate");

        return names;
    }
}
