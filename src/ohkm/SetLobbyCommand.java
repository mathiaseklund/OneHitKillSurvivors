package ohkm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbyCommand implements CommandExecutor {

	Main plugin = Main.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("ohks.admin") || player.isOp()) {
				player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
				msg.msg(player, plugin.config.getString("message.setlobby").replace("LOC", util.LocToString(player.getLocation())));
			} else {
				return false;
			}
		} else {
			msg.onlyplayer(sender);
		}
		return false;
	}

}
