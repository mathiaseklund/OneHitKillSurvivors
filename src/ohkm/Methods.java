package ohkm;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
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
		int rand = util.randInt(0, (spawns.size() - 1));
		Location loc = util.StringToLoc(spawns.get(rand));
		player.teleport(loc);
		msg.brdcst(plugin.config.getString("message.join").replace("USER", player.getName()));
	}

	public void openClassWindow(Player player) {
		if (!Lists.chooseclass.contains(player.getName())) {
			Lists.chooseclass.add(player.getName());
		}
		Inventory inv = Bukkit.createInventory(player, 9, util.colorString(plugin.config.getString("classwindow.title")));
		// knight icon itemstack
		ItemStack knight = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.knight.icon")));
		ItemMeta knightm = knight.getItemMeta();
		knightm.setDisplayName(util.colorString(plugin.config.getString("classwindow.knight.displayname")));
		List<String> knightlore1 = plugin.config.getStringList("classwindow.knight.lore");
		ArrayList<String> knightlore = new ArrayList<String>();
		for (String s : knightlore1) {
			knightlore.add(util.colorString(s));
		}
		knightm.setLore(knightlore);
		knight.setItemMeta(knightm);
		inv.setItem(0, knight);

		// knight cosmetics icon itemstack
		ItemStack knightcos = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.knightcos.icon")));
		ItemMeta knightcosm = knightcos.getItemMeta();
		knightcosm.setDisplayName(util.colorString(plugin.config.getString("classwindow.knightcos.displayname")));
		List<String> knightcoslore1 = plugin.config.getStringList("classwindow.knightcos.lore");
		ArrayList<String> knightcoslore = new ArrayList<String>();
		for (String s : knightcoslore1) {
			knightcoslore.add(util.colorString(s));
		}
		knightcosm.setLore(knightcoslore);
		knightcos.setItemMeta(knightcosm);
		inv.setItem(1, knightcos);

		// barbarian icon itemstack
		ItemStack barbarian = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.barbarian.icon")));
		ItemMeta barbarianm = barbarian.getItemMeta();
		barbarianm.setDisplayName(util.colorString(plugin.config.getString("classwindow.barbarian.displayname")));
		List<String> barbarianlore1 = plugin.config.getStringList("classwindow.barbarian.lore");
		ArrayList<String> barbarianlore = new ArrayList<String>();
		for (String s : barbarianlore1) {
			barbarianlore.add(util.colorString(s));
		}
		barbarianm.setLore(barbarianlore);
		barbarian.setItemMeta(barbarianm);
		inv.setItem(3, barbarian);

		// barbarian cosmetics icon itemstack
		ItemStack barbariancos = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.barbariancos.icon")));
		ItemMeta barbariancosm = barbariancos.getItemMeta();
		barbariancosm.setDisplayName(util.colorString(plugin.config.getString("classwindow.barbariancos.displayname")));
		List<String> barbariancoslore1 = plugin.config.getStringList("classwindow.barbariancos.lore");
		ArrayList<String> barbariancoslore = new ArrayList<String>();
		for (String s : barbariancoslore1) {
			barbariancoslore.add(util.colorString(s));
		}
		barbariancosm.setLore(barbariancoslore);
		barbariancos.setItemMeta(barbariancosm);
		inv.setItem(4, barbariancos);

		// warrior icon itemstack
		ItemStack warrior = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.warrior.icon")));
		ItemMeta warriorm = warrior.getItemMeta();
		warriorm.setDisplayName(util.colorString(plugin.config.getString("classwindow.warrior.displayname")));
		List<String> warriorlore1 = plugin.config.getStringList("classwindow.warrior.lore");
		ArrayList<String> warriorlore = new ArrayList<String>();
		for (String s : warriorlore1) {
			warriorlore.add(util.colorString(s));
		}
		warriorm.setLore(warriorlore);
		warrior.setItemMeta(warriorm);
		inv.setItem(6, warrior);

		// warrior cosmetics icon itemstack
		ItemStack warriorcos = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.warriorcos.icon")));
		ItemMeta warriorcosm = warriorcos.getItemMeta();
		warriorcosm.setDisplayName(util.colorString(plugin.config.getString("classwindow.warriorcos.displayname")));
		List<String> warriorcoslore1 = plugin.config.getStringList("classwindow.warriorcos.lore");
		ArrayList<String> warriorcoslore = new ArrayList<String>();
		for (String s : warriorcoslore1) {
			warriorcoslore.add(util.colorString(s));
		}
		warriorcosm.setLore(warriorcoslore);
		warriorcos.setItemMeta(warriorcosm);
		inv.setItem(7, warriorcos);

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
				System.out.println("Player Count: " + Lists.ingame.size());
				sign.update();
			}
		}
	}

	public void killPlayer(final Player player, Player killer) {
		player.setHealth(6);
		player.getInventory().clear();
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());
		Lists.ingame.remove(player.getName());
		Lists.classtype.remove(player.getName());
		sqlm.addKill(killer);
		int kills = Lists.kills.get(killer.getName());
		kills++;
		Lists.kills.remove(killer.getName());
		Lists.kills.remove(player.getName());
		Lists.kills.put(killer.getName(), kills);
		updateScoreboard(player, kills);
		msg.brdcst(plugin.config.getString("message.death").replace("TARGET", player.getName()).replace("KILLER", killer.getName()));
		int gold = 5;
		if (kills == 3 || kills == 5) {
			gold = gold + 2;
		} else if (kills == 6 || kills == 9) {
			gold = gold + 5;
		} else if (kills >= 10) {
			gold = gold + 10;
		}
		if (Lists.timedkills.containsKey(player.getName())) {
			int tks = Lists.timedkills.get(player.getName());
			tks++;
			if (tks == 2) {
				gold = gold + 5;
				msg.brdcst(plugin.config.getString("message.timed_kills").replace("USER", killer.getName()).replace("NAMED_KILL", "Double Kill"));
			} else if (tks == 3) {
				gold = gold + 10;
				msg.brdcst(plugin.config.getString("message.timed_kills").replace("USER", killer.getName()).replace("NAMED_KILL", "Triple Kill"));
			} else if (tks == 4) {
				gold = gold + 15;
				msg.brdcst(plugin.config.getString("message.timed_kills").replace("USER", killer.getName()).replace("NAMED_KILL", "Quad Kill"));
			}
			final int timekills = tks;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					int timedkills = Lists.timedkills.get(player.getName());
					if (timedkills == timekills) {
						Lists.timedkills.remove(player.getName());
					}
				}
			}, 20 * 7);
		} else {
			Lists.timedkills.put(player.getName(), 1);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					int timedkills = Lists.timedkills.get(player.getName());
					if (timedkills == 1) {
						Lists.timedkills.remove(player.getName());
					}
				}
			}, 20 * 7);
		}
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

	@SuppressWarnings({ "null", "deprecation" })
	public void giveItems(Player player) {
		player.getInventory().clear();
		String classtype = Lists.classtype.get(player.getName());
		if (Lists.item.containsKey(player.getName())) {
			int itemid = Lists.item.get(player.getName());
			ItemStack is = new ItemStack(Material.getMaterial(plugin.iconfig.getInt(itemid + ".item")));
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(util.colorString(plugin.iconfig.getString(itemid + ".name")));
			is.setItemMeta(im);
			player.getInventory().setItem(0, is);
		} else {
			int itemid = plugin.config.getInt(classtype + ".defaultitem");
			ItemStack is = new ItemStack(Material.getMaterial(plugin.iconfig.getInt(itemid + ".item")));
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(util.colorString(plugin.iconfig.getString(itemid + ".name")));
			is.setItemMeta(im);
			player.getInventory().setItem(0, is);
		}
		for (int i = 1; i < player.getInventory().getSize(); i++) {
			if (i == 3) {

			} else {
				ItemStack is = new ItemStack(Material.getMaterial(160), 1, (short) 15);
				player.getInventory().setItem(i, is);
			}
		}
		ItemStack gold = new ItemStack(Material.GOLD_INGOT);
		ItemMeta gm = gold.getItemMeta();
		gm.setDisplayName("Gold: " + sqlm.getGold(player));
		gold.setItemMeta(gm);
		player.getInventory().setItem(3, gold);
	}
}
