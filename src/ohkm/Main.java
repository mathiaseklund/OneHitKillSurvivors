package ohkm;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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

	public void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.getInventory().clear();
			player.kickPlayer("Server Restart");
		}
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
		config.addDefault("warriorcosmetics.title", "Warrior Cosmetics");
		config.addDefault("barbariancosmetics.title", "Barbarian Cosmetics");
		config.addDefault("knightcosmetics.title", "Knight Cosmetics");
		config.addDefault("cosmeticsmenu.knight.lore", lore);
		config.addDefault("cosmeticsmenu.knight.displayname", "Knight Cosmetics");
		config.addDefault("cosmeticsmenu.knight.item", 1);
		config.addDefault("cosmeticsmenu.barbarian.lore", lore);
		config.addDefault("cosmeticsmenu.barbarian.displayname", "barbarian Cosmetics");
		config.addDefault("cosmeticsmenu.barbarian.item", 1);
		config.addDefault("cosmeticsmenu.warrior.lore", lore);
		config.addDefault("cosmeticsmenu.warrior.displayname", "warrior Cosmetics");
		config.addDefault("cosmeticsmenu.warrior.item", 1);
		config.addDefault("cosmeticsmenu.back.lore", lore);
		config.addDefault("cosmeticsmenu.back.displayname", "Go Back");
		config.addDefault("cosmeticsmenu.back.item", 1);
		config.addDefault("cosmeticsmenu.title", "Cosmetics Menu");
		iconfig.addDefault("4.item", 272);
		iconfig.addDefault("4.name", "Knights Stone Sword");
		iconfig.addDefault("4.class", "knight");
		iconfig.addDefault("4.price", 150);
		iconfig.addDefault("5.item", 273);
		iconfig.addDefault("5.name", "Warriors Stone Spear");
		iconfig.addDefault("5.class", "warrior");
		iconfig.addDefault("5.price", 150);
		iconfig.addDefault("6.item", 275);
		iconfig.addDefault("6.name", "Barbarians Stone Axe");
		iconfig.addDefault("6.class", "barbarian");
		iconfig.addDefault("6.price", 150);
		config.addDefault("message.block", "You've BLOCKED USER's Attack!");
		config.addDefault("message.disarm", "You've been DISARMED by USER.");
		config.addDefault("message.dodge", "You've DODGED USER's Attack!");
		config.addDefault("message.gold", "Earned +## Gold");
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
		config.addDefault("classwindow.cosmetics.lore", lore);
		config.addDefault("classwindow.cosmetics.displayname", "Cosmetics");
		config.addDefault("classwindow.cosmetics.icon", 1);
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
