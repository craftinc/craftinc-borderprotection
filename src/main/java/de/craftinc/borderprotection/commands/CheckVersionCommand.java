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
import de.craftinc.borderprotection.util.UpdateHelper;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CheckVersionCommand  implements SubCommand
{
    @Override
    public boolean execute(CommandSender sender, String[] parameters)
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

    @Override
    public List<String> commandNames()
    {
        ArrayList<String> names = new ArrayList<String>();
        names.add("checkversion");

        return names;
    }
}
