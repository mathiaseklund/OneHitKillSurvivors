package ohks;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

	private static PlayerListener instance = new PlayerListener();

	Main plugin = Main.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();
	Methods methods = Methods.getInstance();

	public static PlayerListener getInstance() {
		return instance;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
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
		Player player = event.getPlayer();
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
				methods.spawnPlayer(player);
				methods.updateSigns();
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getDamager() instanceof Player) {
				Player damager = (Player) event.getDamager();
				double health = player.getHealth();
				double damage = event.getDamage();
				if ((health - damage) <= 0) {
					methods.killPlayer(player, damager);
				}
			}
		}
	}
}