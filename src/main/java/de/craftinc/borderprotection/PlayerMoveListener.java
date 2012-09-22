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

    /**
     * Checks if the player is outside of one specific border.
     *
     * @param player  part of the player coordinates
     * @param border1 one side of the rectangle
     * @param border2 opposite side of the rectangle
     * @return null if the player is inside, otherwise a new player location
     */
    private Double checkBorder( double player, double border1, double border2 )
    {
        double bigBorder = Math.max(border1, border2);
        double smallBorder = Math.min(border1, border2);

        // if player is between borders do nothing
        if ( player >= smallBorder && player <= bigBorder )
        {
            return null;
        }
        else
        {
            if ( player > bigBorder )
            {
                // if player is outside of the bigBorder, teleport him to the bigBorder
                return bigBorder - borderManager.getBuffer();
            }
            else
            {
                // if player is outside of the smallBorder, teleport him to the smallBorder
                return smallBorder + borderManager.getBuffer();
            }
        }
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

        // do nothing if there is no border defined
        if ( borderManager.getBorders() == null )
        {
            return;
        }

        Location pl = e.getPlayer().getLocation();
        String worldName = e.getPlayer().getWorld().getName();
        ArrayList<Location> borderPoints = borderManager.getBorders().get(worldName);

        // do nothing if there are no borders for this specific world
        if ( borderPoints == null )
            return;

        // change x or z. default: do not change
        Double newX, newY, newZ;

        newX = checkBorder(pl.getX(), borderPoints.get(0).getX(), borderPoints.get(1).getX());
        newZ = checkBorder(pl.getZ(), borderPoints.get(0).getZ(), borderPoints.get(1).getZ());

        // Do nothing, if no new coordinates have been calculated.
        if ( newX == null && newZ == null )
        {
            return;
        }

        // if one of the coordinates is null, set it to the player's value
        newX = newX == null ? pl.getX() : newX;
        newZ = newZ == null ? pl.getZ() : newZ;

        // change Y if necessary (when there is no free spot)
        newY = goUpUntilFreeSpot(e.getPlayer());

        // teleport the player to the new X and Z coordinates
        e.getPlayer().teleport(
                new Location(e.getPlayer().getWorld(), newX, newY, newZ, pl.getYaw(), pl.getPitch()));

        // send a message to the player
        e.getPlayer().sendMessage(Messages.borderMessage);
    }
}
