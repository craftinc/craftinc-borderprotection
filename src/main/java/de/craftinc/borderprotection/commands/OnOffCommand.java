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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class OnOffCommand implements SubCommand
{
    public boolean execute( CommandSender sender, String[] parameters )
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
            if ( parameters[0].equalsIgnoreCase("on") )
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

    public List<String> commandNames()
    {
        ArrayList<String> names = new ArrayList<String>();
        names.add("on");
        names.add("off");

        return names;
    }
}
