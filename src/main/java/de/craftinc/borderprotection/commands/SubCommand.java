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


import org.bukkit.command.CommandSender;

import java.util.List;


public interface SubCommand
{
    /**
     *
     * @param sender The sender (player) who executed the command.
     * @param parameters All parameters for executing this subcommand. Includes the subcommand name at index 0.
     * @return Returns an boolean indicating if the subcommand got could be executed.
     */
    public boolean execute(CommandSender sender, String[] parameters);

    /**
     * Returns a list of names of the command. All strings should be lowercase!
     */
    public List<String> commandNames();
}
