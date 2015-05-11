package ohkm;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

		// cosmetics icon itemstack
		ItemStack cosmetics = new ItemStack(Material.getMaterial(plugin.config.getInt("classwindow.cosmetics.icon")));
		ItemMeta cosmeticsm = cosmetics.getItemMeta();
		cosmeticsm.setDisplayName(util.colorString(plugin.config.getString("classwindow.cosmetics.displayname")));
		List<String> cosmeticslore1 = plugin.config.getStringList("classwindow.cosmetics.lore");
		ArrayList<String> cosmeticslore = new ArrayList<String>();
		for (String s : cosmeticslore1) {
			cosmeticslore.add(util.colorString(s));
		}
		cosmeticsm.setLore(cosmeticslore);
		cosmetics.setItemMeta(cosmeticsm);
		inv.setItem(0, cosmetics);

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
		inv.setItem(2, knight);

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
		inv.setItem(4, barbarian);

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
		player.setHealth(2);
		player.getInventory().clear();
		player.teleport(player.getWorld().getSpawnLocation());
		Lists.ingame.remove(player.getName());
		updateSigns();
		Lists.classtype.remove(player.getName());
		sqlm.addKill(killer);
		int kills = 0;
		if (Lists.kills.containsKey(killer.getName())) {
			kills = Lists.kills.get(killer.getName());
		}
		kills++;
		Lists.kills.remove(killer.getName());
		Lists.kills.remove(player.getName());
		Lists.kills.put(killer.getName(), kills);
		updateScoreboard(killer, kills);
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
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
		sqlm.addGold(killer, gold);
		updateGold(killer);
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

	@SuppressWarnings({ "deprecation" })
	public void giveItems(Player player) {
		player.getInventory().clear();
		String classtype = Lists.classtype.get(player.getName());
		if (Lists.item.containsKey(player.getName())) {
			int itemid = Lists.item.get(player.getName());
			String itemclass = plugin.iconfig.getString(itemid + ".class");
			if (itemclass.equalsIgnoreCase(classtype)) {
				ItemStack is = new ItemStack(Material.getMaterial(plugin.iconfig.getInt(itemid + ".item")));
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(util.colorString(plugin.iconfig.getString(itemid + ".name")));
				is.setItemMeta(im);
				player.getInventory().setItem(0, is);
			} else {
				itemid = plugin.config.getInt(classtype + ".defaultitem");
				ItemStack is = new ItemStack(Material.getMaterial(plugin.iconfig.getInt(itemid + ".item")));
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(util.colorString(plugin.iconfig.getString(itemid + ".name")));
				is.setItemMeta(im);
				player.getInventory().setItem(0, is);
			}
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

	public void updateGold(Player player) {
		ItemStack gold = new ItemStack(Material.GOLD_INGOT);
		ItemMeta gm = gold.getItemMeta();
		gm.setDisplayName("Gold: " + sqlm.getGold(player));
		gold.setItemMeta(gm);
		player.getInventory().setItem(3, gold);
	}

	public void ItemCD4(final Player player, final int i, int time) {
		if (i >= 1) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					ItemStack is = player.getItemInHand();
					double maxdur = is.getType().getMaxDurability();
					double dur = is.getDurability();
					if (i == 5) {
						dur = (maxdur * 0.95);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD4(player, (i - 1), 20);
					} else if (i == 4) {
						dur = (maxdur * 0.75);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD4(player, (i - 1), 20);
					} else if (i == 3) {
						dur = (maxdur * 0.5);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD4(player, (i - 1), 20);
					} else if (i == 2) {
						dur = (maxdur * 0.25);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD4(player, (i - 1), 20);
					} else if (i == 1) {
						dur = (maxdur * 0);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						Lists.regen.remove(player.getName());
					}

				}
			}, time);
		}
	}

	public void ItemCD5(final Player player, final int i, int time) {
		if (i >= 1) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					ItemStack is = player.getItemInHand();
					double maxdur = is.getType().getMaxDurability();
					double dur = is.getDurability();
					if (i == 6) {
						dur = (maxdur * 0.95);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD5(player, (i - 1), 20);
					} else if (i == 5) {
						dur = (maxdur * 0.8);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD5(player, (i - 1), 20);
					} else if (i == 4) {
						dur = (maxdur * 0.6);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD5(player, (i - 1), 20);
					} else if (i == 3) {
						dur = (maxdur * 0.4);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD5(player, (i - 1), 20);
					} else if (i == 2) {
						dur = (maxdur * 0.2);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						ItemCD5(player, (i - 1), 20);
					} else if (i == 1) {
						dur = (maxdur * 0);
						is.setDurability((short) dur);
						player.setItemInHand(is);
						Lists.regen.remove(player.getName());
					}

				}
			}, time);
		}
	}

	public void openCosmeticsMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, util.colorString(plugin.config.getString("cosmeticsmenu.title")));

		// knight cosmetic icon item
		ItemStack knight = new ItemStack(Material.getMaterial(plugin.config.getInt("cosmeticsmenu.knight.item")));
		ItemMeta knightm = knight.getItemMeta();
		knightm.setDisplayName(util.colorString(plugin.config.getString("cosmeticsmenu.knight.displayname")));
		List<String> knightlore1 = plugin.config.getStringList("cosmeticsmenu.knight.lore");
		ArrayList<String> knightlore = new ArrayList<String>();
		for (String s : knightlore1) {
			knightlore.add(util.colorString(s));
		}
		knightm.setLore(knightlore);
		knight.setItemMeta(knightm);
		inv.setItem(0, knight);

		// barbarian cosmetic icon item
		ItemStack barbarian = new ItemStack(Material.getMaterial(plugin.config.getInt("cosmeticsmenu.barbarian.item")));
		ItemMeta barbarianm = barbarian.getItemMeta();
		barbarianm.setDisplayName(util.colorString(plugin.config.getString("cosmeticsmenu.barbarian.displayname")));
		List<String> barbarianlore1 = plugin.config.getStringList("cosmeticsmenu.barbarian.lore");
		ArrayList<String> barbarianlore = new ArrayList<String>();
		for (String s : barbarianlore1) {
			barbarianlore.add(util.colorString(s));
		}
		barbarianm.setLore(barbarianlore);
		barbarian.setItemMeta(barbarianm);
		inv.setItem(3, barbarian);

		// warrior cosmetic icon item
		ItemStack warrior = new ItemStack(Material.getMaterial(plugin.config.getInt("cosmeticsmenu.warrior.item")));
		ItemMeta warriorm = warrior.getItemMeta();
		warriorm.setDisplayName(util.colorString(plugin.config.getString("cosmeticsmenu.warrior.displayname")));
		List<String> warriorlore1 = plugin.config.getStringList("cosmeticsmenu.warrior.lore");
		ArrayList<String> warriorlore = new ArrayList<String>();
		for (String s : warriorlore1) {
			warriorlore.add(util.colorString(s));
		}
		warriorm.setLore(warriorlore);
		warrior.setItemMeta(warriorm);
		inv.setItem(6, warrior);

		// go back icon itemstack
		ItemStack back = new ItemStack(Material.getMaterial(plugin.config.getInt("cosmeticsmenu.back.item")));
		ItemMeta backm = back.getItemMeta();
		backm.setDisplayName(util.colorString(plugin.config.getString("cosmeticsmenu.back.displayname")));
		List<String> backlore1 = plugin.config.getStringList("cosmeticsmenu.back.lore");
		ArrayList<String> backlore = new ArrayList<String>();
		for (String s : backlore1) {
			backlore.add(util.colorString(s));
		}
		backm.setLore(backlore);
		back.setItemMeta(backm);
		inv.setItem(8, back);

		player.openInventory(inv);
	}

	public void openKnightCosmeticsMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 36, util.colorString(plugin.config.getString("knightcosmetics.title")));
		String[] owneditems = sqlm.getItems(player);
		for (int i = 0; i < 99; i++) {
			if (plugin.iconfig.getInt(i + ".item") != 0) {
				if (plugin.iconfig.getString(i + ".class") != null) {
					if (plugin.iconfig.getString(i + ".class").equalsIgnoreCase("knight")) {
						ItemStack is = new ItemStack(Material.getMaterial(plugin.iconfig.getInt(i + ".item")));
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(util.colorString(plugin.iconfig.getString(i + ".name")));
						ArrayList<String> lore = new ArrayList<String>();
						boolean contains = false;
						for (String s : owneditems) {
							if (!s.equalsIgnoreCase("")) {
								int si = Integer.parseInt(s);
								if (si == i) {
									contains = true;
								}
							}
						}
						if (contains) {
							if (Lists.item.get(player.getName()) == i) {
								lore.add(ChatColor.RED + "SELECTED");
							} else {
								lore.add(ChatColor.GREEN + "SELECT");
							}
						} else {
							lore.add(ChatColor.GOLD + "PURCHASE FOR " + plugin.iconfig.getInt(i + ".price") + " GOLD");
						}
						lore.add(ChatColor.COLOR_CHAR + "" + i);
						im.setLore(lore);
						is.setItemMeta(im);
						inv.addItem(is);
					}
				}
			}
		}

		// go back icon itemstack
		ItemStack back = new ItemStack(Material.getMaterial(plugin.config.getInt("cosmeticsmenu.back.item")));
		ItemMeta backm = back.getItemMeta();
		backm.setDisplayName(util.colorString(plugin.config.getString("cosmeticsmenu.back.displayname")));
		List<String> backlore1 = plugin.config.getStringList("cosmeticsmenu.back.lore");
		ArrayList<String> backlore = new ArrayList<String>();
		for (String s : backlore1) {
			backlore.add(util.colorString(s));
		}
		backm.setLore(backlore);
		back.setItemMeta(backm);
		inv.setItem(35, back);

		player.openInventory(inv);
	}

	public void openBarbarianCosmeticsMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 36, util.colorString(plugin.config.getString("barbariancosmetics.title")));
		String[] owneditems = sqlm.getItems(player);
		for (int i = 0; i < 99; i++) {
			if (plugin.iconfig.getInt(i + ".item") != 0) {
				if (plugin.iconfig.getString(i + ".class") != null) {
					if (plugin.iconfig.getString(i + ".class").equalsIgnoreCase("barbarian")) {
						ItemStack is = new ItemStack(Material.getMaterial(plugin.iconfig.getInt(i + ".item")));
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(util.colorString(plugin.iconfig.getString(i + ".name")));
						ArrayList<String> lore = new ArrayList<String>();
						boolean contains = false;
						for (String s : owneditems) {
							if (!s.equalsIgnoreCase("")) {
								int si = Integer.parseInt(s);
								if (si == i) {
									contains = true;
								}
							}
						}
						if (contains) {
							if (Lists.item.get(player.getName()) == i) {
								lore.add(ChatColor.RED + "SELECTED");
							} else {
								lore.add(ChatColor.GREEN + "SELECT");
							}
						} else {
							lore.add(ChatColor.GOLD + "PURCHASE FOR " + plugin.iconfig.getInt(i + ".price") + " GOLD");
						}
						lore.add(ChatColor.COLOR_CHAR + "" + i);
						im.setLore(lore);
						is.setItemMeta(im);
						inv.addItem(is);
					}
				}
			}
		}

		// go back icon itemstack
		ItemStack back = new ItemStack(Material.getMaterial(plugin.config.getInt("cosmeticsmenu.back.item")));
		ItemMeta backm = back.getItemMeta();
		backm.setDisplayName(util.colorString(plugin.config.getString("cosmeticsmenu.back.displayname")));
		List<String> backlore1 = plugin.config.getStringList("cosmeticsmenu.back.lore");
		ArrayList<String> backlore = new ArrayList<String>();
		for (String s : backlore1) {
			backlore.add(util.colorString(s));
		}
		backm.setLore(backlore);
		back.setItemMeta(backm);
		inv.setItem(35, back);
	}

	public void openWarriorCosmeticsMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 36, util.colorString(plugin.config.getString("warriorcosmetics.title")));
		String[] owneditems = sqlm.getItems(player);
		for (int i = 0; i < 99; i++) {
			if (plugin.iconfig.getInt(i + ".item") != 0) {
				if (plugin.iconfig.getString(i + ".class") != null) {
					if (plugin.iconfig.getString(i + ".class").equalsIgnoreCase("warrior")) {
						ItemStack is = new ItemStack(Material.getMaterial(plugin.iconfig.getInt(i + ".item")));
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(util.colorString(plugin.iconfig.getString(i + ".name")));
						ArrayList<String> lore = new ArrayList<String>();
						boolean contains = false;
						for (String s : owneditems) {
							if (!s.equalsIgnoreCase("")) {
								int si = Integer.parseInt(s);
								if (si == i) {
									contains = true;
								}
							}
						}
						if (contains) {
							if (Lists.item.get(player.getName()) == i) {
								lore.add(ChatColor.RED + "SELECTED");
							} else {
								lore.add(ChatColor.GREEN + "SELECT");
							}
						} else {
							lore.add(ChatColor.GOLD + "PURCHASE FOR " + plugin.iconfig.getInt(i + ".price") + " GOLD");
						}
						lore.add(ChatColor.COLOR_CHAR + "" + i);
						im.setLore(lore);
						is.setItemMeta(im);
						inv.addItem(is);
					}
				}
			}
		}

		// go back icon itemstack
		ItemStack back = new ItemStack(Material.getMaterial(plugin.config.getInt("cosmeticsmenu.back.item")));
		ItemMeta backm = back.getItemMeta();
		backm.setDisplayName(util.colorString(plugin.config.getString("cosmeticsmenu.back.displayname")));
		List<String> backlore1 = plugin.config.getStringList("cosmeticsmenu.back.lore");
		ArrayList<String> backlore = new ArrayList<String>();
		for (String s : backlore1) {
			backlore.add(util.colorString(s));
		}
		backm.setLore(backlore);
		back.setItemMeta(backm);
		inv.setItem(35, back);
	}
}
