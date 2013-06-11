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
package de.craftinc.borderprotection.events;

import de.craftinc.borderprotection.Messages;
import de.craftinc.borderprotection.util.ChunkGenerator;
import de.craftinc.borderprotection.util.UpdateHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener
{
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin( PlayerJoinEvent e )
    {
        final Player player = e.getPlayer();

        if ( e.getPlayer().hasPermission("craftinc.borderprotection.update") )
        {
            if ( UpdateHelper.newVersionAvailable() )
            {
                String updateMessage = Messages.updateMessage(UpdateHelper.cachedLatestVersion, UpdateHelper.getCurrentVersion());
                e.setJoinMessage(e.getJoinMessage() + "\n" + updateMessage);
            }
        }

        System.out.println("pausing generation"); // TODO: send message to player with correct permission about current progress of the generation.
        ChunkGenerator.pause();
    }
}