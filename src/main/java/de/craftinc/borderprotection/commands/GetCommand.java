/*  Craft Inc. BorderProtection
    Copyright (C) 2016  Paul Schulze

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
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class GetCommand implements SubCommand
{
    public boolean execute( CommandSender sender, String[] parameters )
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

    public List<String> commandNames()
    {
        ArrayList<String> names = new ArrayList<String>();
        names.add("get");
        names.add("info");

        return names;
    }
}
