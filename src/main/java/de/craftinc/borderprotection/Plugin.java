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
package de.craftinc.borderprotection;

import de.craftinc.borderprotection.borders.CircBorder;
import de.craftinc.borderprotection.borders.RectBorder;
import de.craftinc.borderprotection.commands.CommandSwitch;
import de.craftinc.borderprotection.events.PlayerJoinListener;
import de.craftinc.borderprotection.events.PlayerMoveListener;
import de.craftinc.borderprotection.events.PlayerQuitListener;
import de.craftinc.borderprotection.events.PlayerTeleportListener;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin
{
    public static Plugin instance;

    @Override
    public void onLoad()
    {
        ConfigurationSerialization.registerClass(RectBorder.class);
        ConfigurationSerialization.registerClass(CircBorder.class);
    }

    @Override
    public void onDisable()
    {
    }

    @Override
    public void onEnable()
    {
        Plugin.instance = this;

        // create listeners
        PlayerMoveListener playerMoveListener = new PlayerMoveListener();
        PlayerTeleportListener playerTeleportListener = new PlayerTeleportListener();
        PlayerJoinListener playerJoinListener = new PlayerJoinListener();
        PlayerQuitListener playerQuitListener = new PlayerQuitListener();

        // commands
        CommandSwitch commandExecutor = new CommandSwitch();
        getCommand("cibp").setExecutor(commandExecutor);

        // register listeners
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(playerMoveListener, this);
        pm.registerEvents(playerTeleportListener, this);
        pm.registerEvents(playerJoinListener, this);
        pm.registerEvents(playerQuitListener, this);
    }
}
