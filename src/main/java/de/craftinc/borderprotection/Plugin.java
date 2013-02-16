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

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin
{
    private static JavaPlugin cibpPlugin;

    public static JavaPlugin getPlugin()
    {
        return cibpPlugin;
    }

    @Override
    public void onLoad()
    {
        ConfigurationSerialization.registerClass(Border.class);
    }

    @Override
    public void onDisable()
    {
    }

    @Override
    public void onEnable()
    {
        Plugin.cibpPlugin = this;

        BorderManager borderManager = new BorderManager();
        PlayerMoveListener playerMoveListener = new PlayerMoveListener(borderManager);
        PlayerTeleportListener playerTeleportListener = new PlayerTeleportListener(borderManager);

        // commands
        Commands commandExecutor = new Commands(borderManager);
        getCommand("cibp").setExecutor(commandExecutor);

        // listeners
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(playerMoveListener, this);
        pm.registerEvents(playerTeleportListener, this);
    }
}
