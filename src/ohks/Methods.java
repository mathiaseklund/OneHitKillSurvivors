package ohks;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class Methods {

	private static Methods instance = new Methods();

	Main plugin = Main.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();

	public static Methods getInstance() {
		return instance;
	}

	public void spawnPlayer(Player player) {
		// TODO Make the players spawn in the "arena"
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
		
	}
}
