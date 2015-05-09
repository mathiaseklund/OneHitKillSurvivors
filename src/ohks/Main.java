package ohks;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main main;

	File configurationConfig;
	public FileConfiguration config;

	String prefix = "";

	public static Main getMain() {
		return main;
	}

	public void onEnable() {
		main = this;
		configurationConfig = new File(getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configurationConfig);
		loadConfig();
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}

	public void savec() {
		try {
			config.save(configurationConfig);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadConfig() {

		config.addDefault("message.noperm", "&4Error: You don't have permission to use this function.");
		config.addDefault("message.onlyplayer", "&4Error: Only players may use this function.");
		config.addDefault("prefix", "");
		config.options().copyDefaults(true);
		savec();
		prefix = config.getString("prefix");

	}

}
