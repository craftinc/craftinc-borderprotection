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

import de.craftinc.borderprotection.Plugin;
import de.craftinc.borderprotection.borders.Border;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;

public class ChunkGenerator
{
    protected static HashMap<World, Integer[]> chunkGenerationStatus = new HashMap<World, Integer[]>();
    protected static boolean isPaused = true;

    public static long waitTicks = 5; // TODO: make adjustable via config file
    public static int batchGenerationSize = 5; //TODO: make adjustable via config file
    public static int paddingChunksAroundBorder = 15; // TODO: make adjustable via config file

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
                slowLoadNextChunk(w);
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

        int firstChunkX = (Math.min(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4) - paddingChunksAroundBorder;
        int firstChunkZ = (Math.min(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4) - paddingChunksAroundBorder;

        firstChunkX--;

        chunkGenerationStatus.put(w, new Integer[]{firstChunkX, firstChunkZ});
        // the actual generation will start when resume is called!

        return true;
    }

    protected static void slowLoadNextChunk(World w)
    {
        if (w == null)
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        if (isPaused)
        {
            return;
        }

        DelayedCall delayedCall = new DelayedCall();
        delayedCall.w = w;
        delayedCall.batchGenerationSize = batchGenerationSize;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.instance, delayedCall, waitTicks);
    }

    /**
     * Will only load/generate the next chunks inside the border of the given world. Will stop if no border exists.
     */
    protected static void loadNextChunk(World w)
    {
        if (w == null)
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

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
        int minChunkX = (Math.min(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4) - paddingChunksAroundBorder;
        int maxChunkX = (Math.max(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4) + paddingChunksAroundBorder;
        int maxChunkZ = (Math.max(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4) + paddingChunksAroundBorder;

        chunkX++;

        while (!chunkIsInsideBorder(chunkX, chunkZ, w, border)
                && chunkZ <= maxChunkZ)
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

            // TODO: only display a message every 10s or so with a fake percentage number.
            Plugin.instance.getLogger().info("Loading/Generating Chunk ( x=" + chunkX + " z=" + chunkZ + " world=" + w.getName() + " )");

            Chunk chunk = w.getChunkAt(chunkX, chunkZ);
            chunk.load(true);
            loadSurroundingChunks(chunkX, chunkZ, w); // this will get the server to generate trees, … inside the new chunk
        }
        else
        {
            Plugin.instance.getLogger().info("Finished generating Chunks for world " + w.getName());
            chunkGenerationStatus.remove(w);
        }
    }

    protected static boolean chunkIsInsideBorder(int x, int z, World w, Border b)
    {
//        double xLoc = (double)(x << 4);
//        double yLoc = 100.0;
//        double yLoc = 0.0;
//        double zLoc = (double)(z << 4);
//
//        Location center = b.getCenter();
//        double centerX = center.getX();
//        double centerZ = center.getZ();
//
//        double padding = paddingChunksAroundBorder << 4;
//
//        if (Math.abs(xLoc) < padding)
//        {
//            xLoc = centerX;
//        }
//        else
//        {
//            xLoc = centerX < xLoc ? xLoc-padding : xLoc+padding;
//            xLoc = centerX < xLoc ? xLoc-padding : xLoc+padding;
//        }
//
//        if (Math.abs(zLoc) < padding)
//        {
//            zLoc = centerZ;
//        }
//        else
//        {
//            zLoc = centerZ < zLoc ? zLoc-padding : zLoc+padding;
//            zLoc = centerZ < zLoc ? zLoc-padding : zLoc+padding;
//        }
//
//        Location chunkLocation = new Location(w, xLoc, yLoc, zLoc);
//        return b.checkBorder(chunkLocation) == null;
//

        // make the location to check the length of 'paddingChunksAroundBorder' closer to center of the border
        Location center = b.getCenter();
        double vecX = center.getX() - (x << 4);
        double vecZ = center.getZ() - (z << 4);
        double length = Math.sqrt(vecX*vecX + vecZ*vecZ);

        double padding = paddingChunksAroundBorder << 4;

        if (Math.abs(length) < padding)
        {
            vecX = vecZ = 0.0; // avoid 'crossing' the center of the border
        }
        else
        {
            vecX -= vecX / length * padding;
            vecZ -= vecZ / length * padding;
        }

        Location locationMinusPadding = new Location(w, center.getX()+vecX, 0.0, center.getZ()+vecZ);
        return b.checkBorder(locationMinusPadding) == null;
    }

    protected static void loadSurroundingChunks(int x, int z, World w)
    {
        int radius = 1;

        for (int i=-radius; i<radius; i++)
        {
            for (int j=-radius; j<radius; j++)
            {
                if (j == 0 && i == 0)
                {
                    continue;
                }

                w.loadChunk(i+x, j+z, false);
            }
        }
    }
}


class DelayedCall implements Runnable
{
    public World w;
    public int batchGenerationSize;

    @Override
    public void run()
    {
        for (int i=0; i<batchGenerationSize;i++)
        {
            ChunkGenerator.loadNextChunk(w);
        }

        ChunkGenerator.slowLoadNextChunk(w);
    }
}
