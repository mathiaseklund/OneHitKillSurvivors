package ohks;

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
		MySQL = new MySQL(this, config.getString("sql.ip"), config.getString("sql.port"), config.getString("sql.database"), config.getString("sql.user"), config.getString("sql.pass"));
		try {
			c = MySQL.openConnection();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void savec() {
		try {
			config.save(configurationConfig);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadConfig() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Default Lore");
		config.addDefault("sql.pass", "password");
		config.addDefault("sql.user", "minecraft");
		config.addDefault("sql.database", "ohks");
		config.addDefault("sql.port", "3306");
		config.addDefault("sql.ip", "localhost");
		ArrayList<String> ditems = new ArrayList<String>();
		ditems.add("item:269 name:Common_Knife");
		config.addDefault("doctor.items", ditems);
		ArrayList<String> oitems = new ArrayList<String>();
		oitems.add("item:280 name:Taser");
		oitems.add("item:290 name:9mm_Pistol");
		oitems.add("item:351");
		config.addDefault("officer.items", oitems);
		ArrayList<String> fitems = new ArrayList<String>();
		fitems.add("item:271 name:Steel_Axe");
		config.addDefault("fireman.items", fitems);
		ArrayList<String> bitems = new ArrayList<String>();
		bitems.add("item:364");
		bitems.add("item:269 name:Butcher_Shovel lore:im_lazy-as_fuck");
		config.addDefault("butcher.items", bitems);
		ArrayList<String> pitems = new ArrayList<String>();
		pitems.add("item:262 amount:2");
		pitems.add("item:261");
		pitems.add("item:268 name:Machete lore:this_is_the_lore-new_line enchants:16,1-21,1");
		config.addDefault("prepper.items", pitems);
		ArrayList<String> citems = new ArrayList<String>();
		citems.add("item:260 amount:2");
		citems.add("item:270 name:Citizen_Pickaxe lore:This_is_the_lore-this_is_a_new_line enchants:16,1-21,1");
		config.addDefault("citizen.items", citems);
		config.addDefault("classwindow.citizen.lore", lore);
		config.addDefault("classwindow.citizen.displayname", "Citizen");
		config.addDefault("classwindow.citizen.icon", 1);
		config.addDefault("classwindow.prepper.lore", lore);
		config.addDefault("classwindow.prepper.displayname", "prepper");
		config.addDefault("classwindow.prepper.icon", 1);
		config.addDefault("classwindow.butcher.lore", lore);
		config.addDefault("classwindow.butcher.displayname", "butcher");
		config.addDefault("classwindow.butcher.icon", 1);
		config.addDefault("classwindow.fireman.lore", lore);
		config.addDefault("classwindow.fireman.displayname", "fireman");
		config.addDefault("classwindow.fireman.icon", 1);
		config.addDefault("classwindow.officer.lore", lore);
		config.addDefault("classwindow.officer.displayname", "officer");
		config.addDefault("classwindow.officer.icon", 1);
		config.addDefault("classwindow.doctor.lore", lore);
		config.addDefault("classwindow.doctor.displayname", "doctor");
		config.addDefault("classwindow.doctor.icon", 1);
		config.addDefault("classwindow.title", "Class Window");
		config.addDefault("message.noperm", "&4Error: You don't have permission to use this function.");
		config.addDefault("message.onlyplayer", "&4Error: Only players may use this function.");
		config.addDefault("prefix", "");
		config.options().copyDefaults(true);
		savec();
		prefix = config.getString("prefix");

	}

}
