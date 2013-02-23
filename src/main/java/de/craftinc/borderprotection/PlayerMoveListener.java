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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener
{

    private BorderManager borderManager;

    public PlayerMoveListener( BorderManager borderManager )
    {
        this.borderManager = borderManager;
    }

    private Double goUpUntilFreeSpot( Location newLocation )
    {
        // go up in height until the player can stand in AIR
        Block footBlock = newLocation.getBlock();
        Block headBlock = newLocation.getBlock().getRelative(BlockFace.UP);
        while ( footBlock.getType() != Material.AIR || headBlock.getType() != Material.AIR )
        {
            byte offset = 1;
            if ( headBlock.getType() != Material.AIR )
            {
                offset = 2;
            }
            footBlock = footBlock.getRelative(0, offset, 0);
            headBlock = headBlock.getRelative(0, offset, 0);
        }
        // set the y value to a spot where the player can stand free
        return (double) footBlock.getY();
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove( PlayerMoveEvent e )
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

        // player location
        Location playerLocation = e.getPlayer().getLocation();

        // world where the player is in
        World world = e.getPlayer().getWorld();

        // border of this world
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

        // check if player is inside the borders. null if yes, otherwise a tuple which defines the new player position
        newXZ = borderManager.checkBorder(playerLocation, border, BorderManager.buffer);

        // Do nothing, if no new coordinates have been calculated.
        if ( newXZ == null )
        {
            return;
        }

        // if one of the coordinates is null, set it to the player's value
        newXZ[0] = newXZ[0] == null ? playerLocation.getX() : newXZ[0];
        newXZ[1] = newXZ[1] == null ? playerLocation.getZ() : newXZ[1];

        // change Y if necessary (when there is no free spot)
        Double newY = goUpUntilFreeSpot(new Location(world, newXZ[0], e.getPlayer().getLocation().getY(), newXZ[1]));

        // teleport the player to the new X and Z coordinates
        e.getPlayer().teleport(
                new Location(e.getPlayer().getWorld(), newXZ[0], newY, newXZ[1], playerLocation.getYaw(),
                             playerLocation.getPitch()));

        // send a message to the player
        borderManager.showMessageWithTimeout(e.getPlayer(), Messages.borderMessage);
    }
}
