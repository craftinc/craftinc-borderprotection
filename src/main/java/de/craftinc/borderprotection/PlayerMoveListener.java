package de.craftinc.borderprotection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class PlayerMoveListener implements Listener
{

    private BorderManager borderManager;

    public PlayerMoveListener( BorderManager borderManager )
    {
        this.borderManager = borderManager;
    }

    private Double goUpUntilFreeSpot( Player player )
    {
        // go up in height until the player can stand in AIR
        Block footBlock = player.getLocation().getBlock();
        Block headBlock = player.getEyeLocation().getBlock();
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove( PlayerMoveEvent e )
    {
        // do nothing if player has the ignoreborders permission
        if ( e.getPlayer().hasPermission("craftinc.borderprotection.ignoreborders") )
        {
            return;
        }

        // do nothing if there are no border definitions at all
        if ( borderManager.getBorders() == null )
        {
            return;
        }

        // player location
        Location playerLocation = e.getPlayer().getLocation();

        // world where the player is in
        String worldName = e.getPlayer().getWorld().getName();

        // borders of this world
        ArrayList<Location> borderPoints = borderManager.getBorders().get(worldName);

        // do nothing if there are no borders for this specific world
        if ( borderPoints == null )
            return;

        // change x or z. default: do not change
        Double[] newXZ;

        // check if player is inside the borders. null if yes, otherwise a tuple which defines the new player position
        newXZ = borderManager.checkBorder(playerLocation, borderPoints, borderManager.getBuffer());

        // Do nothing, if no new coordinates have been calculated.
        if ( newXZ == null )
        {
            return;
        }

        // if one of the coordinates is null, set it to the player's value
        newXZ[0] = newXZ[0] == null ? playerLocation.getX() : newXZ[0];
        newXZ[1] = newXZ[1] == null ? playerLocation.getZ() : newXZ[1];

        // change Y if necessary (when there is no free spot)
        Double newY = goUpUntilFreeSpot(e.getPlayer());

        // teleport the player to the new X and Z coordinates
        e.getPlayer().teleport(
                new Location(e.getPlayer().getWorld(), newXZ[0], newY, newXZ[1], playerLocation.getYaw(),
                             playerLocation.getPitch()));

        // send a message to the player
        borderManager.showMessageWithTimeout(e.getPlayer(), Messages.borderMessage);
    }
}
