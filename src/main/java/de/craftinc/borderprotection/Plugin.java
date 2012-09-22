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
        PlayerMoveListener eventListener = new PlayerMoveListener(borderManager);
        Commands commandExecutor = new Commands(borderManager);

        PluginManager pm = this.getServer().getPluginManager();
        getCommand("cibp").setExecutor(commandExecutor);
        pm.registerEvents(eventListener, this);
    }
}
