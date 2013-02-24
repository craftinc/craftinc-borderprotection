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
package de.craftinc.borderprotection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener
{
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin( PlayerLoginEvent e )
    {
        final Player player = e.getPlayer();

        if ( e.getPlayer().hasPermission("craftinc.borderprotection.update") )
        {
            if ( UpdateHelper.newVersionAvailable() )
            {
                // Schedule a task which delays 20 ticks (1 second) and then sends a message to the player
                Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getPlugin(), new Runnable()
                {
                    @Override
                    public void run()
                    {
                        player.sendMessage(Messages.updateMessage(UpdateHelper.cachedLatestVersion,
                                                                  UpdateHelper.getCurrentVersion()));
                    }
                }, 20L);
            }
        }
    }
}
