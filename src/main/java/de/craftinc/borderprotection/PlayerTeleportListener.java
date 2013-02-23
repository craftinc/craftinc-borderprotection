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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener
{
    private BorderManager borderManager;

    public PlayerTeleportListener( BorderManager borderManager )
    {
        this.borderManager = borderManager;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove( PlayerTeleportEvent e )
    {
        // do nothing if the event is already cancelled
        if (e.isCancelled())
        {
            return;
        }

        // do nothing if player has the ignoreborders permission
        if ( e.getPlayer().hasPermission("craftinc.borderprotection.ignoreborders") )
        {
            return;
        }

        // do nothing if there are no border definitions at all
        if ( Border.getBorders().isEmpty() )
        {
            return;
        }

        // target location
        Location targetLocation = e.getTo();

        // world where the player is in
        World world = targetLocation.getWorld();

        // borders of this world
        Border border = Border.getBorders().get(world);

        // do nothing if there are no borders for this specific world
        if ( border == null )
        {
            return;
        }

        // do nothing if border is disabled
        if ( !border.isActive() )
        {
            return;
        }

        // change x or z. default: do not change
        Double[] newXZ;

        // check if target is inside the borders. null if yes, otherwise a tuple which defines the new position
        newXZ = borderManager.checkBorder(targetLocation, border, BorderManager.buffer);


        // Cancel event, if new coordinates have been calculated.
        if ( newXZ != null )
        {
            e.setCancelled(true);
            borderManager.showMessageWithTimeout(e.getPlayer(), Messages.borderTeleportMessage);
        }
    }
}
