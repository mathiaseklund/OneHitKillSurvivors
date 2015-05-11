package ohkm;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener {

	private static PlayerListener instance = new PlayerListener();

	Main plugin = Main.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();
	Methods methods = Methods.getInstance();
	SQLMethods sqlm = SQLMethods.getInstance();

	public static PlayerListener getInstance() {
		return instance;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.setMaxHealth(2);
		Lists.id.put(player.getName(), sqlm.getID(player));
		player.teleport(player.getWorld().getSpawnLocation());
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("ohks.admin")) {
			// Detectorline is the line the plugin will look for when a sign is
			// created in order to activate a sign.
			if (event.getLine(0).equalsIgnoreCase(plugin.config.getString("sign.detectorline"))) {
				event.setLine(0, "[Join]");
				event.setLine(1, "-=Players=-");
				event.setLine(2, "" + Lists.ingame.size());
				ArrayList<String> locs = new ArrayList<String>();
				// Store locations of the signs in order to update player
				// amount.
				locs.addAll(plugin.config.getStringList("sign.locations"));
				locs.add(util.LocToString(event.getBlock().getLocation()));
				plugin.config.set("sign.locations", locs);
				plugin.savec();
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) b.getState();
				// Checks if the signs first line equals [Join], joins game if
				// it
				// is.
				if (sign.getLine(0).equalsIgnoreCase("[Join]")) {
					if (Lists.ingame.contains(player.getName())) {
						Lists.ingame.remove(player.getName());
					}
					Lists.ingame.add(player.getName());
					methods.updateScoreboard(player, 0);
					methods.updateSigns();
					methods.openClassWindow(player);
				}
			}
		} else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
				boolean lookingAt = false;
				List<Entity> nearby = player.getNearbyEntities(4, 4, 4);
				for (Entity ent : nearby) {
					if (ent instanceof Player) {
						lookingAt = true;
					}
				}
				if (!lookingAt) {
					ItemStack is = player.getItemInHand();
					String classtype = Lists.classtype.get(player.getName());
					int durability = is.getDurability();
					if (!Lists.regen.contains(player.getName())) {
						Lists.regen.add(player.getName());
						if (classtype.equalsIgnoreCase("knight") || classtype.equalsIgnoreCase("barbarian")) {
							methods.ItemCD5(player, 6, 1);
						} else {
							methods.ItemCD4(player, 5, 1);
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			if (event.getDamager() instanceof Player) {
				Player damager = (Player) event.getDamager();
				if (damager.getItemInHand() != null && damager.getItemInHand().getType() != Material.AIR) {
					ItemStack is = damager.getItemInHand();
					String classtype = Lists.classtype.get(damager.getName());
					int durability = is.getDurability();
					if (!Lists.regen.contains(damager.getName())) {
						Lists.regen.add(damager.getName());
						if (classtype.equalsIgnoreCase("knight") || classtype.equalsIgnoreCase("barbarian")) {
							methods.ItemCD5(damager, 6, 1);
						} else {
							methods.ItemCD4(damager, 5, 1);
						}
						event.setDamage(2);
						if (!Lists.still.contains(player.getName())) {
							int rand = util.randInt(1, 2);
							if (rand == 1) {
								event.setDamage(0);
								msg.msg(player, plugin.config.getString("message.dodge").replace("USER", damager.getName()));
							}
						}
						double health = player.getHealth();
						double damage = event.getDamage();
						if ((health - damage) <= 0) {
							event.setDamage(0);
							methods.killPlayer(player, damager);
						}
					} else {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (Lists.chooseclass.contains(player.getName())) {
			methods.openClassWindow(player);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!player.isOp()) {
			event.setCancelled(true);
		}
		Inventory inv = event.getInventory();
		String title = inv.getTitle();
		if (title.equalsIgnoreCase(util.colorString(plugin.config.getString("classwindow.title")))) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				ItemStack is = event.getCurrentItem();
				if (is.hasItemMeta()) {
					ItemMeta im = is.getItemMeta();
					String dname = im.getDisplayName();
					if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("classwindow.knight.displayname")))) {
						Lists.chooseclass.remove(player.getName());
						Lists.classtype.put(player.getName(), "knight");
						methods.giveItems(player);
						methods.spawnPlayer(player);
					} else if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("classwindow.barbarian.displayname")))) {
						Lists.chooseclass.remove(player.getName());
						Lists.classtype.put(player.getName(), "barbarian");
						methods.giveItems(player);
						methods.spawnPlayer(player);
					} else if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("classwindow.warrior.displayname")))) {
						Lists.chooseclass.remove(player.getName());
						Lists.classtype.put(player.getName(), "warrior");
						methods.giveItems(player);
						methods.spawnPlayer(player);
					} else if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("classwindow.cosmetics.displayname")))) {
						methods.openCosmeticsMenu(player);
					}
				}
			}
		} else if (title.equalsIgnoreCase(util.colorString(plugin.config.getString("cosmeticsmenu.title")))) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				ItemStack is = event.getCurrentItem();
				if (is.hasItemMeta()) {
					ItemMeta im = is.getItemMeta();
					String dname = im.getDisplayName();
					if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("cosmeticsmenu.knight.displayname")))) {
						methods.openKnightCosmeticsMenu(player);
					} else if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("cosmeticsmenu.barbarian.displayname")))) {
						methods.openBarbarianCosmeticsMenu(player);
					} else if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("cosmeticsmenu.warrior.displayname")))) {
						methods.openWarriorCosmeticsMenu(player);
					} else if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("cosmeticsmenu.back.displayname")))) {
						methods.openClassWindow(player);
					}
				}
			}
		} else if (title.equalsIgnoreCase(util.colorString(plugin.config.getString("knightcosmetics.title")))) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				ItemStack is = event.getCurrentItem();
				if (is.hasItemMeta()) {
					ItemMeta im = is.getItemMeta();
					String dname = im.getDisplayName();
					if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("cosmeticsmenu.back.displayname")))) {
						methods.openCosmeticsMenu(player);
					} else {
						String state = ChatColor.stripColor(im.getLore().get(0));
						if (state.equalsIgnoreCase("SELECT")) {
							int id = Integer.parseInt(ChatColor.stripColor(im.getLore().get(1)));
							Lists.item.remove(player.getName());
							Lists.item.put(player.getName(), id);
							methods.openKnightCosmeticsMenu(player);
						} else if (state.equalsIgnoreCase("SELECTED")) {
							Lists.item.remove(player.getName());
							methods.openKnightCosmeticsMenu(player);
						} else if (state.contains("PURCHASE")) {
							String sid = im.getLore().get(1).replace("§", "");
							int id = Integer.parseInt(sid);
							int cost = Integer.parseInt(state.replaceAll("[\\D]", ""));
							int gold = sqlm.getGold(player);
							if (gold >= cost) {
								// PLAYER PURCHASES AND SELECTS ITEM
								gold = (gold - cost);
								sqlm.setGold(player, gold);
								sqlm.addItem(player, id);
								Lists.item.remove(player.getName());
								Lists.item.put(player.getName(), id);
								methods.openKnightCosmeticsMenu(player);
							} else {
								msg.msg(player, "&4ERROR: You don't have enough gold to purchase this item.");
							}
						}
					}
				}
			}
		} else if (title.equalsIgnoreCase(util.colorString(plugin.config.getString("barbariancosmetics.title")))) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				ItemStack is = event.getCurrentItem();
				if (is.hasItemMeta()) {
					ItemMeta im = is.getItemMeta();
					String dname = im.getDisplayName();
					if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("cosmeticsmenu.back.displayname")))) {
						methods.openCosmeticsMenu(player);
					} else {
						String state = ChatColor.stripColor(im.getLore().get(0));
						if (state.equalsIgnoreCase("SELECT")) {
							int id = Integer.parseInt(ChatColor.stripColor(im.getLore().get(1)));
							Lists.item.remove(player.getName());
							Lists.item.put(player.getName(), id);
							methods.openBarbarianCosmeticsMenu(player);
						} else if (state.equalsIgnoreCase("SELECTED")) {
							Lists.item.remove(player.getName());
							methods.openBarbarianCosmeticsMenu(player);
						} else if (state.contains("PURCHASE")) {
							int id = Integer.parseInt(ChatColor.stripColor(im.getLore().get(1)));
							int cost = Integer.parseInt(state.replaceAll("[\\D]", ""));
							int gold = sqlm.getGold(player);
							if (gold >= cost) {
								// PLAYER PURCHASES AND SELECTS ITEM
								gold = (gold - cost);
								sqlm.setGold(player, gold);
								sqlm.addItem(player, id);
								Lists.item.remove(player.getName());
								Lists.item.put(player.getName(), id);
								methods.openBarbarianCosmeticsMenu(player);
							} else {
								msg.msg(player, "&4ERROR: You don't have enough gold to purchase this item.");
							}
						}
					}
				}
			}
		} else if (title.equalsIgnoreCase(util.colorString(plugin.config.getString("warriorcosmetics.title")))) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				ItemStack is = event.getCurrentItem();
				if (is.hasItemMeta()) {
					ItemMeta im = is.getItemMeta();
					String dname = im.getDisplayName();
					if (dname.equalsIgnoreCase(util.colorString(plugin.config.getString("cosmeticsmenu.back.displayname")))) {
						methods.openCosmeticsMenu(player);
					} else {
						String state = ChatColor.stripColor(im.getLore().get(0));
						if (state.equalsIgnoreCase("SELECT")) {
							int id = Integer.parseInt(ChatColor.stripColor(im.getLore().get(1)));
							Lists.item.remove(player.getName());
							Lists.item.put(player.getName(), id);
							methods.openWarriorCosmeticsMenu(player);
						} else if (state.equalsIgnoreCase("SELECTED")) {
							Lists.item.remove(player.getName());
							methods.openWarriorCosmeticsMenu(player);
						} else if (state.contains("PURCHASE")) {
							int id = Integer.parseInt(ChatColor.stripColor(im.getLore().get(1)));
							int cost = Integer.parseInt(state.replaceAll("[\\D]", ""));
							int gold = sqlm.getGold(player);
							if (gold >= cost) {
								// PLAYER PURCHASES AND SELECTS ITEM
								gold = (gold - cost);
								sqlm.setGold(player, gold);
								sqlm.addItem(player, id);
								Lists.item.remove(player.getName());
								Lists.item.put(player.getName(), id);
								methods.openWarriorCosmeticsMenu(player);
							} else {
								msg.msg(player, "&4ERROR: You don't have enough gold to purchase this item.");
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Lists.id.remove(player.getName());
		Lists.ingame.remove(player.getName());
		Lists.classtype.remove(player.getName());
		Lists.kills.remove(player.getName());
		Lists.item.remove(player.getName());
		Lists.still.remove(player.getName());
		methods.updateSigns();
		player.getInventory().clear();
	}

	@EventHandler
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (event.getNewSlot() > 0) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (Lists.chooseclass.contains(player.getName())) {
			methods.openClassWindow(player);
		}
		if (event.getTo().getX() != event.getFrom().getX() || event.getTo().getZ() != event.getFrom().getZ()) {
			if (Lists.still.contains(player.getName())) {
				Lists.still.remove(player.getName());
			}
		} else {
			if (!Lists.still.contains(player.getName())) {
				Lists.still.add(player.getName());
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!player.isOp()) {
			event.setCancelled(true);
		}
	}
}