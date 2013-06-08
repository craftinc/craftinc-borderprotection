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
package de.craftinc.borderprotection.events;

import de.craftinc.borderprotection.Plugin;
import de.craftinc.borderprotection.util.ChunkGenerator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

public class ChunkLoadingListener implements Listener
{
//    @SuppressWarnings("unused")
//    @EventHandler(priority = EventPriority.NORMAL)
//    public void onChunkPopulate( ChunkPopulateEvent e )
//    {
//        System.out.println("populate: " + e.getChunk());
//
//        ChunkGenerator.handleChunkLoad(e.getChunk(), true);
//    }


    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkLoad( ChunkLoadEvent e )
    {
        boolean populated = !e.isNewChunk();
        ChunkGenerator.handleChunkLoad(e.getChunk(), populated);
    }
}
