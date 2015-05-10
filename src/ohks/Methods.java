package ohks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Methods {

	private static Methods instance = new Methods();

	Main plugin = Main.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();
	SQLMethods sqlm = SQLMethods.getInstance();

	public static Methods getInstance() {
		return instance;
	}

	public void spawnPlayer(Player player) {
		ArrayList<String> spawns = new ArrayList<String>();
		spawns.addAll(plugin.config.getStringList("spawns"));
		int rand = util.randInt(0, spawns.size());
		Location loc = util.StringToLoc(spawns.get(rand));
		player.teleport(loc);
	}

	public void openClassWindow(Player player) {
		if (!Lists.chooseclass.contains(player.getName())) {
			Lists.chooseclass.add(player.getName());
		}
		Inventory inv = Bukkit.createInventory(player, 9, util.colorString(plugin.config.getString("classwindow.title")));
		// Citizen icon itemstack
		ItemStack citizen = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.citizen.icon")));
		ItemMeta citizenm = citizen.getItemMeta();
		citizenm.setDisplayName(util.colorString(plugin.config.getString("classwindow.citizen.displayname")));
		List<String> citizenlore1 = plugin.config.getStringList("classwindow.citizen.lore");
		ArrayList<String> citizenlore = new ArrayList<String>();
		for (String s : citizenlore1) {
			citizenlore.add(util.colorString(s));
		}
		citizenm.setLore(citizenlore);
		citizen.setItemMeta(citizenm);
		inv.addItem(citizen);

		// prepper icon itemstack
		ItemStack prepper = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.prepper.icon")));
		ItemMeta prepperm = prepper.getItemMeta();
		prepperm.setDisplayName(util.colorString(plugin.config.getString("classwindow.prepper.displayname")));
		List<String> prepperlore1 = plugin.config.getStringList("classwindow.prepper.lore");
		ArrayList<String> prepperlore = new ArrayList<String>();
		for (String s : prepperlore1) {
			prepperlore.add(util.colorString(s));
		}
		prepperm.setLore(prepperlore);
		prepper.setItemMeta(prepperm);
		inv.addItem(prepper);

		// butcher icon itemstack
		ItemStack butcher = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.butcher.icon")));
		ItemMeta butcherm = butcher.getItemMeta();
		butcherm.setDisplayName(util.colorString(plugin.config.getString("classwindow.butcher.displayname")));
		List<String> butcherlore1 = plugin.config.getStringList("classwindow.butcher.lore");
		ArrayList<String> butcherlore = new ArrayList<String>();
		for (String s : butcherlore1) {
			butcherlore.add(util.colorString(s));
		}
		butcherm.setLore(butcherlore);
		butcher.setItemMeta(butcherm);
		inv.addItem(butcher);

		// fireman icon itemstack
		ItemStack fireman = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.fireman.icon")));
		ItemMeta firemanm = fireman.getItemMeta();
		firemanm.setDisplayName(util.colorString(plugin.config.getString("classwindow.fireman.displayname")));
		List<String> firemanlore1 = plugin.config.getStringList("classwindow.fireman.lore");
		ArrayList<String> firemanlore = new ArrayList<String>();
		for (String s : firemanlore1) {
			firemanlore.add(util.colorString(s));
		}
		firemanm.setLore(firemanlore);
		fireman.setItemMeta(firemanm);
		inv.addItem(fireman);

		// officer icon itemstack
		ItemStack officer = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.officer.icon")));
		ItemMeta officerm = officer.getItemMeta();
		officerm.setDisplayName(util.colorString(plugin.config.getString("classwindow.officer.displayname")));
		List<String> officerlore1 = plugin.config.getStringList("classwindow.officer.lore");
		ArrayList<String> officerlore = new ArrayList<String>();
		for (String s : officerlore1) {
			officerlore.add(util.colorString(s));
		}
		officerm.setLore(officerlore);
		officer.setItemMeta(officerm);
		inv.addItem(officer);

		// doctor icon itemstack
		ItemStack doctor = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.doctor.icon")));
		ItemMeta doctorm = doctor.getItemMeta();
		doctorm.setDisplayName(util.colorString(plugin.config.getString("classwindow.doctor.displayname")));
		List<String> doctorlore1 = plugin.config.getStringList("classwindow.doctor.lore");
		ArrayList<String> doctorlore = new ArrayList<String>();
		for (String s : doctorlore1) {
			doctorlore.add(util.colorString(s));
		}
		doctorm.setLore(doctorlore);
		doctor.setItemMeta(doctorm);
		inv.addItem(doctor);

		player.openInventory(inv);
	}

	public void updateSigns() {
		// This functions updates the player amount on the signs.
		ArrayList<String> locs = new ArrayList<String>();
		locs.addAll(plugin.config.getStringList("sign.locations"));
		for (String s : locs) {
			Location loc = util.StringToLoc(s);
			BlockState bs = loc.getBlock().getState();
			if (bs instanceof Sign) {
				Sign sign = (Sign) bs;
				sign.setLine(2, Lists.ingame.size() + "");
			}
		}
	}

	public void killPlayer(Player player, Player killer) {
		player.setHealth(6);
		player.getInventory().clear();
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());
		Lists.ingame.remove(player.getName());
		Lists.classtype.remove(player.getName());
		sqlm.addKill(killer);
		int kills = Lists.kills.get(player.getName());
		kills++;
		Lists.kills.remove(player.getName());
		Lists.kills.put(player.getName(), kills);
		updateScoreboard(player, kills);

	}

	public void updateScoreboard(Player player, int kills) {

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective objective = board.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(util.colorString(plugin.config.getString("scoreboard.displayname")));
		Score score = objective.getScore("Kills:");
		score.setScore(kills);
		player.setScoreboard(board);
	}

	@SuppressWarnings("null")
	public void giveItems(Player player) {
		player.getInventory().clear();
		String classtype = Lists.classtype.get(player.getName());
		ArrayList<String> items = new ArrayList<String>();
		items.addAll(plugin.config.getStringList(classtype + ".items"));
		for (String s : items) {
			String[] strings = s.split(" ");
			for (String str : strings) {
				ItemStack is = null;
				ItemMeta im = null;
				if (str.contains("item:")) {
					int id = Integer.parseInt(str.split(":")[1]);
					is = new ItemStack(Material.getMaterial(id));
				} else if (str.contains("name:")) {
					im = is.getItemMeta();
					im.setDisplayName(util.colorString(str.split(":")[1].replace("_", " ")));
				} else if (str.contains("lore:")) {
					ArrayList<String> lore = new ArrayList<String>();
					if (str.contains("-")) {
						String[] lore1 = str.split(":")[1].split("-");
						for (String string : lore1) {
							lore.add(util.colorString(string.replace("_", " ")));
						}
					} else {
						lore.add(util.colorString(str.replace("_", " ")));
					}
					im.setLore(lore);
				} else if (str.contains("enchants:")) {
					if (str.contains("-")) {
						String[] enchants = str.split(":")[1].split("-");
						for (String string : enchants) {
							is.addEnchantment(Enchantment.getById(Integer.parseInt(string.split(",")[0])), Integer.parseInt(string.split(",")[1]));
						}
					} else {
						String enchant = str.split(":")[1];
						is.addEnchantment(Enchantment.getById(Integer.parseInt(enchant.split(",")[0])), Integer.parseInt(enchant.split(",")[1]));
					}
				} else if (str.contains("amount:")) {
					int amount = Integer.parseInt(str.split(":")[1]);
					is.setAmount(amount);
				}
				is.setItemMeta(im);
				player.getInventory().addItem(is);
			}
		}

		for (int i = 4; i < player.getInventory().getSize(); i++) {
			ItemStack is = new ItemStack(Material.getMaterial(160), 1, (short) 15);
			player.getInventory().setItem(i, is);
		}
	}
}
