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
package de.craftinc.borderprotection.util;

import de.craftinc.borderprotection.Messages;
import de.craftinc.borderprotection.Plugin;
import de.craftinc.borderprotection.borders.Border;
import net.minecraft.server.v1_5_R3.ChunkProviderServer; // NOTE: this will break with any new Bukkit/Minecraft version!
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld; // NOTE: this will break with any new Bukkit/Minecraft version!

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkGenerator
{
    protected static Map<World, Integer[]> chunkGenerationStatus = new HashMap<World, Integer[]>();
    protected static boolean isPaused = true;
    protected static ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>();

    public static void pause()
    {
        isPaused = true;
    }

    public static void resume()
    {
        if (isPaused)
        {
            isPaused = false;

            for (World w : chunkGenerationStatus.keySet())
            {
                loadNextChunk(w);
            }
        }
    }

    public static void cancelRender(World w)
    {
        if (w == null)
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        chunkGenerationStatus.remove(w);
    }

    public static boolean isGenerating(World w)
    {
        if (w == null)
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        return chunkGenerationStatus.containsKey(w);
    }

    /**
     * Starts the generation of all chunks inside a border.
     * @param w The world in which chunks will be generated. Must not be 'null'. An exception will be thrown otherwise!
     * @return A boolean indicating if the generation was successfully started. Will return false if no border exists
     * for a given world. Will return true if the generation was already running but will not restart the generation.
     */
    public static boolean generate(World w)
    {
        if (w == null)
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        Border border = Border.getBorders().get(w);

        if (border == null)
        {
            return false;
        }

        Location[] borderRect = border.getSurroundingRect();

        int firstChunkX = Math.min(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4;
        int firstChunkZ = Math.min(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4;

        firstChunkX--;

        chunkGenerationStatus.put(w, new Integer[]{firstChunkX, firstChunkZ});
        // the actual generation will start when resume is called!

        return true;
    }

    public static void handleChunkLoad(Chunk c, boolean isPopulated)
    {
        if (c == null)
        {
            throw new IllegalArgumentException("Chunk 'c' must not be null!");
        }

        if (isPaused)
        {
            return;
        }

        World w = c.getWorld();
        Integer[] currentGenerationChunk = chunkGenerationStatus.get(w);

        if ((currentGenerationChunk != null) && c.getX() == currentGenerationChunk[0] && c.getZ() == currentGenerationChunk[1])
        {
            if (!isPopulated)
            {
//                Plugin.instance.getLogger().info("Trying to get the chunk to get populated");

                ChunkProviderServer cps = ((CraftWorld) w).getHandle().chunkProviderServer;
                cps.chunkProvider.getOrCreateChunk(c.getX(), c.getZ());


            }

            DelayedCall delayedCall = new DelayedCall();
            delayedCall.w = w;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.instance, delayedCall, 10L);


//                loadNextChunk(w);
        }
    }

//    protected static void loadSurroundingChunks(int x, int z, World w)
//    {
//        int radius = 2;
//
//        for (int i=-radius; i<radius; i++)
//        {
//            for (int j=-radius; j<radius; j++)
//            {
//                if (j == 0  && i == 0)
//                {
//                    continue;
//                }
//
//                w.loadChunk(i+x, j+z, false);
//            }
//        }
//    }
//
//    protected static void unloadLoadedChunks()
//    {
//        for (Chunk c : loadedChunks)
//        {
//            c.getWorld().unloadChunk(c);
//        }
//    }


    /**
     * Will only load chunks inside the border of the given world. Will stop if no border exists.
     */
    protected static void loadNextChunk(World w)
    {
        Border border = Border.getBorders().get(w);

        if (border == null)
        {
            return;
        }

        Integer[] lastGeneratedChunk = chunkGenerationStatus.get(w);

        if (lastGeneratedChunk == null)
        {
            return; // the generation got most likely canceled
        }

        int chunkX = lastGeneratedChunk[0];
        int chunkZ = lastGeneratedChunk[1];

        Location[] borderRect = border.getSurroundingRect();
        int minChunkX = Math.min(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4;
        int maxChunkX = Math.max(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4;
        int maxChunkZ = Math.max(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4;

//        System.out.println("minChunkX: " + minChunkX);
//        System.out.println("maxChunkX: " + maxChunkX);
//        System.out.println("maxChunkZ: " + maxChunkZ);

        chunkX++;

        while (!chunkIsInsideBorder(chunkX, chunkZ, w, border)
                && chunkZ <= maxChunkZ
                /*&& !w.isChunkLoaded(chunkX, chunkZ)*/)
        {
            chunkX++;

            if (chunkX > maxChunkX)
            {
                chunkZ++;
                chunkX = minChunkX;
            }
        }

        if (chunkZ <= maxChunkZ)
        {
            chunkGenerationStatus.put(w, new Integer[]{chunkX, chunkZ});
            Plugin.instance.getLogger().info("Loading/Generating Chunk ( x=" + chunkX + " z=" + chunkZ + " world=" + w.getName() + " )");
            w.loadChunk(chunkX, chunkZ, true);
        }
        else
        {
            Plugin.instance.getLogger().info("Finished generating Chunks for world " + w.getName());
            chunkGenerationStatus.remove(w);
        }
    }

    protected static boolean chunkIsInsideBorder(int x, int z, World w, Border b)
    {
        Location chunkLocation = new Location(w, (double)(x << 4), (double)100, (double)(z << 4));
        return b.checkBorder(chunkLocation) == null;
    }
}


class DelayedCall implements Runnable
{
    public World w;

    @Override
    public void run() {
        ChunkGenerator.loadNextChunk(w);
    }
}
