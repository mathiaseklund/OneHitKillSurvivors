package ohkm;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
		player.setMaxHealth(6);
		Lists.id.put(player.getName(), sqlm.getID(player));
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
			Sign sign = (Sign) b.getState();
			// Checks if the signs first line equals [Join], joins game if it
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
		} else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
				final ItemStack is = player.getItemInHand();
				player.setItemInHand(new ItemStack(Material.AIR));
				String classtype = Lists.classtype.get(player.getName());
				if (classtype.equalsIgnoreCase("knight")) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							player.setItemInHand(is);
						}
					}, 20 * 5);
				} else if (classtype.equalsIgnoreCase("warrior")) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							player.setItemInHand(is);
						}
					}, 20 * 4);
				} else if (classtype.equalsIgnoreCase("barbarian")) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							player.setItemInHand(is);
						}
					}, 20 * 5);
				}
			} else {
				event.setCancelled(true);
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
					event.setDamage(2);
					if (player.getVelocity() != null) {
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
					final ItemStack is = player.getItemInHand();
					player.setItemInHand(new ItemStack(Material.AIR));
					String classtype = Lists.classtype.get(player.getName());
					if (classtype.equalsIgnoreCase("knight")) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								player.setItemInHand(is);
							}
						}, 20 * 5);
					} else if (classtype.equalsIgnoreCase("warrior")) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								player.setItemInHand(is);
							}
						}, 20 * 4);
					} else if (classtype.equalsIgnoreCase("barbarian")) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								player.setItemInHand(is);
							}
						}, 20 * 5);
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
			if (event.getCurrentItem() != null || event.getCurrentItem().getType() != Material.AIR) {
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
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Lists.id.remove(player.getName());
	}

	@EventHandler
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (event.getNewSlot() > 0) {
			event.setCancelled(true);
		}
	}
}