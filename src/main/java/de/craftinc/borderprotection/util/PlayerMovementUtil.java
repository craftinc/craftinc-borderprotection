/*  Craft Inc. BorderProtection
    Copyright (C) 2016  Paul Schulze, Tobias Ottenweller

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
package de.craftinc.borderprotection.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class PlayerMovementUtil
{
    public static Double goUpUntilFreeSpot( Location l )
    {
        // FIXME: Player should not be placed above lava or other harmful Materials
        // go up in height until the player can stand in AIR
        Block footBlock = l.getBlock();
        Block headBlock = l.getBlock().getRelative(BlockFace.UP);
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
        // return the y value to a spot where the player can stand free
        return (double) footBlock.getY();
    }
}
