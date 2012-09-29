package de.craftinc.borderprotection;

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
