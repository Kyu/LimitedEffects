package ng.precious.LimitedEffects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        configureConfig();
        registerCommands();
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {

    }

    private void configureConfig() {
        FileConfiguration config = this.getConfig();
        config.addDefault("limit", 6);
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void registerCommands() {
        this.getCommand("le").setExecutor(new CommandReload(this));
    }
}
