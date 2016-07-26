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

import de.craftinc.borderprotection.Plugin;
import de.craftinc.borderprotection.borders.Border;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Calendar;
import java.util.HashMap;

public class ChunkGenerator
{
    private static final HashMap<World, Integer[]> chunkGenerationStatus = new HashMap<World, Integer[]>();
    private static       boolean                   isPaused              = true;
    private static final HashMap<World, Long>      lastGenerationLogTime = new HashMap<World, Long>();

    private static final long waitTicks                 = 5; // TODO: make adjustable via config file
    private static final int  batchGenerationSize       = 5; //TODO: make adjustable via config file
    private static final int  paddingChunksAroundBorder = 15; // TODO: make adjustable via config file

    public static void pause()
    {
        isPaused = true;
    }

    public static void resume()
    {
        if ( isPaused )
        {
            isPaused = false;

            for ( World w : chunkGenerationStatus.keySet() )
            {
                slowLoadNextChunk(w);
            }
        }
    }

    public static void cancelRender( World w )
    {
        if ( w == null )
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        chunkGenerationStatus.remove(w);
    }

    public static boolean isGenerating( World w )
    {
        if ( w == null )
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        return chunkGenerationStatus.containsKey(w);
    }

    /**
     * Starts the generation of all chunks inside a border.
     *
     * @param w The world in which chunks will be generated. Must not be 'null'. An exception will be thrown otherwise!
     * @return A boolean indicating if the generation was successfully started. Will return false if no border exists
     * for a given world. Will return true if the generation was already running but will not restart the generation.
     */
    public static boolean generate( World w )
    {
        if ( w == null )
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        Border border = Border.getBorders().get(w);

        if ( border == null )
        {
            return false;
        }

        Location[] borderRect = border.getSurroundingRect();

        int firstChunkX =
                ( Math.min(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4 ) - paddingChunksAroundBorder;
        int firstChunkZ =
                ( Math.min(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4 ) - paddingChunksAroundBorder;

        firstChunkX--;

        chunkGenerationStatus.put(w, new Integer[] { firstChunkX, firstChunkZ });
        // the actual generation will start when resume is called!

        return true;
    }

    static void slowLoadNextChunk( World w )
    {
        if ( w == null )
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        if ( isPaused )
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
    static void loadNextChunk( World w )
    {
        if ( w == null )
        {
            throw new IllegalArgumentException("World 'w' must not be null!");
        }

        Border border = Border.getBorders().get(w);

        if ( border == null )
        {
            return;
        }

        Integer[] lastGeneratedChunk = chunkGenerationStatus.get(w);

        if ( lastGeneratedChunk == null )
        {
            return; // the generation got most likely canceled
        }

        int chunkX = lastGeneratedChunk[0];
        int chunkZ = lastGeneratedChunk[1];

        final Location[] borderRect = border.getSurroundingRect();
        final int minChunkX =
                ( Math.min(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4 ) - paddingChunksAroundBorder;
        final int minChunkZ =
                ( Math.min(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4 ) - paddingChunksAroundBorder;
        final int maxChunkX =
                ( Math.max(borderRect[0].getBlockX(), borderRect[1].getBlockX()) >> 4 ) + paddingChunksAroundBorder;
        final int maxChunkZ =
                ( Math.max(borderRect[0].getBlockZ(), borderRect[1].getBlockZ()) >> 4 ) + paddingChunksAroundBorder;

        chunkX++;

        while ( !chunkIsInsideBorder(chunkX, chunkZ, w, border)
                && chunkZ <= maxChunkZ )
        {
            chunkX++;

            if ( chunkX > maxChunkX )
            {
                chunkZ++;
                chunkX = minChunkX;
            }
        }

        if ( chunkZ <= maxChunkZ )
        {
            chunkGenerationStatus.put(w, new Integer[] { chunkX, chunkZ });

            Chunk chunk = w.getChunkAt(chunkX, chunkZ);
            chunk.load(true);
            loadSurroundingChunks(chunkX, chunkZ,
                                  w); // this will get the server to generate trees, … inside the new chunk

            logProgress(minChunkX, maxChunkX, minChunkZ, maxChunkZ, chunkX, chunkZ, w);
        }
        else
        {
            Plugin.instance.getLogger().info("Finished generating Chunks for world " + w.getName());
            chunkGenerationStatus.remove(w);
        }
    }

    private static boolean chunkIsInsideBorder( int x, int z, World w, Border b )
    {
        double xLoc = (double) ( x << 4 ) + 8.0;
        double yLoc = 0.0;
        double zLoc = (double) ( z << 4 ) + 8.0;

        double padding = (double) ( paddingChunksAroundBorder << 4 );
        Location l = new Location(w, xLoc, yLoc, zLoc);

        return b.checkBorder(l, padding) == null;
    }

    private static void loadSurroundingChunks( int x, int z, World w )
    {
        int radius = 1;

        for ( int i = -radius; i < radius; i++ )
        {
            for ( int j = -radius; j < radius; j++ )
            {
                if ( j == 0 && i == 0 )
                {
                    continue;
                }

                w.loadChunk(i + x, j + z, false);
            }
        }
    }

    private static void logProgress( int minChunkX, int maxChunkX, int minChunkZ, int maxChunkZ, int chunkX,
                                     int chunkZ, World world )
    {
        final Long now = Calendar.getInstance().getTimeInMillis();
        final Long lastLogTime = lastGenerationLogTime.get(world);

        if ( lastLogTime == null || ( now - lastLogTime ) > 30000 )
        {
            lastGenerationLogTime.put(world, now);

            final int numXChunks, offsetX;
            if ( ( ( maxChunkX > 0 ) && ( minChunkX > 0 ) ) || ( ( maxChunkX < 0 ) && ( minChunkX < 0 ) ) )
            {
                numXChunks = minChunkX * maxChunkX;
                offsetX = 0;
            }
            else
            {
                numXChunks = Math.abs(minChunkX) + maxChunkX;
                offsetX = Math.abs(minChunkX);
            }

            final int numZChunks, offsetZ;
            if ( ( ( maxChunkZ > 0 ) && ( minChunkZ > 0 ) ) || ( ( maxChunkZ < 0 ) && ( minChunkZ < 0 ) ) )
            {
                numZChunks = minChunkZ * maxChunkZ;
                offsetZ = 0;
            }
            else
            {
                numZChunks = Math.abs(minChunkZ) + maxChunkZ;
                offsetZ = Math.abs(minChunkZ);
            }

            final int totalNumChunks = numXChunks * numZChunks;
            final int currentChunk = ( ( offsetZ + chunkZ ) * numXChunks ) + offsetX + chunkX;
            Plugin.instance.getLogger()
                           .info("Generation progress: " + currentChunk + "/" + totalNumChunks + " Chunks in World " +
                                 world.getName());
        }
    }
}


class DelayedCall implements Runnable
{
    public World w;
    int batchGenerationSize;

    public void run()
    {
        for ( int i = 0; i < batchGenerationSize; i++ )
        {
            ChunkGenerator.loadNextChunk(w);
        }

        ChunkGenerator.slowLoadNextChunk(w);
    }
}
