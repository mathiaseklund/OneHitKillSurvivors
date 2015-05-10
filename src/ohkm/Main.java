package ohkm;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main main;

	MySQL MySQL = null;
	Connection c = null;
	File configurationConfig;
	public FileConfiguration config;
	File itemConfig;
	public FileConfiguration iconfig;

	String prefix = "";

	public static Main getMain() {
		return main;
	}

	public void onEnable() {
		System.out.println("Starting to enable plugin.");
		main = this;
		configurationConfig = new File(getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configurationConfig);
		itemConfig = new File(getDataFolder(), "items.yml");
		iconfig = YamlConfiguration.loadConfiguration(itemConfig);
		loadConfig();
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		getCommand("addspawn").setExecutor(new AddSpawnCommand());
		getCommand("setlobby").setExecutor(new SetLobbyCommand());
		MySQL = new MySQL(this, config.getString("sql.ip"), config.getString("sql.port"), config.getString("sql.database"), config.getString("sql.user"), config.getString("sql.pass"));
		System.out.println("Starting to open MySQL Connection");
		try {
			c = MySQL.openConnection();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OHKS HAS BEEN ENABLED!");
		Methods.getInstance().updateSigns();
	}

	public void savec() {
		try {
			config.save(configurationConfig);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveic() {
		try {
			iconfig.save(itemConfig);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadConfig() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Default Lore");
		config.addDefault("message.block", "You've BLOCKED USER's Attack!");
		config.addDefault("message.disarm", "You've been DISARMED by USER.");
		config.addDefault("message.dodge", "You've DODGED USER's Attack!");
		config.addDefault("message.gold", "You've earned +## Gold.");
		config.addDefault("message.timed_streak", "USER has gotten a NAMED_KILL");
		config.addDefault("message.death", "TARGET was killed by KILLER.");
		config.addDefault("message.join", "USER has joined One-Hit Kill!");
		iconfig.addDefault("1.item", 268);
		iconfig.addDefault("1.name", "Knights Sword");
		iconfig.addDefault("2.item", 271);
		iconfig.addDefault("2.name", "barbarians axe");
		iconfig.addDefault("3.item", 269);
		iconfig.addDefault("3.name", "Warrior's Shovel");
		config.addDefault("scoreboard.displayname", "Survive..");
		config.addDefault("sign.detectorline", "[ohkm]");
		config.addDefault("message.setlobby", "Lobby locations has been set to LOC");
		config.addDefault("message.addspawn", "You've added a spawnpoint at LOC.");
		config.addDefault("sql.pass", "pass");
		config.addDefault("sql.user", "name");
		config.addDefault("sql.database", "dbname");
		config.addDefault("sql.port", "3306");
		config.addDefault("sql.ip", "localhost");
		ArrayList<String> cos = new ArrayList<String>();
		config.addDefault("knight.cosmeticitems", cos);
		config.addDefault("knight.defaultitem", 1);
		config.addDefault("barbarian.cosmeticitems", cos);
		config.addDefault("barbarian.defaultitem", 2);
		config.addDefault("warrior.cosmeticitems", cos);
		config.addDefault("warrior.defaultitem", 3);
		config.addDefault("classwindow.knight.lore", lore);
		config.addDefault("classwindow.knight.displayname", "knight");
		config.addDefault("classwindow.knight.icon", 1);
		config.addDefault("classwindow.barbarian.lore", lore);
		config.addDefault("classwindow.barbarian.displayname", "barbarian");
		config.addDefault("classwindow.barbarian.icon", 1);
		config.addDefault("classwindow.warrior.lore", lore);
		config.addDefault("classwindow.warrior.displayname", "warrior");
		config.addDefault("classwindow.warrior.icon", 1);
		config.addDefault("classwindow.knightcos.lore", lore);
		config.addDefault("classwindow.knightcos.displayname", "Knight Costmetics");
		config.addDefault("classwindow.knightcos.icon", 1);
		config.addDefault("classwindow.barbariancos.lore", lore);
		config.addDefault("classwindow.barbariancos.displayname", "Barbarian Cosmetics");
		config.addDefault("classwindow.barbariancos.icon", 1);
		config.addDefault("classwindow.warriorcos.lore", lore);
		config.addDefault("classwindow.warriorcos.displayname", "Warrior Cosmetics");
		config.addDefault("classwindow.warriorcos.icon", 1);
		config.addDefault("classwindow.title", "Class Window");
		config.addDefault("message.noperm", "&4Error: You don't have permission to use this function.");
		config.addDefault("message.onlyplayer", "&4Error: Only players may use this function.");
		config.addDefault("prefix", "");
		config.options().copyDefaults(true);
		iconfig.options().copyDefaults(true);
		savec();
		saveic();
		prefix = config.getString("prefix");

	}

}
