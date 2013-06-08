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

        int firstChunkX = Math.min(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4;
        int firstChunkZ = Math.min(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4;

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

        DelayedCall delayedCall = new DelayedCall();
        delayedCall.w = w;
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
        int minChunkX = Math.min(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4;
        int maxChunkX = Math.max(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4;
        int maxChunkZ = Math.max(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4;

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
            Plugin.instance.getLogger().info("Loading/Generating Chunk ( x=" + chunkX + " z=" + chunkZ + " world=" + w.getName() + " )");

            Chunk chunk = w.getChunkAt(chunkX, chunkZ);
            chunk.load(true);

            slowLoadNextChunk(w);
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
