package ohks;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddSpawnCommand implements CommandExecutor {

	Main plugin = Main.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("ohks.admin")) {
				ArrayList<String> spawns = new ArrayList<String>();
				spawns.addAll(plugin.config.getStringList("spawns"));
				String location = util.LocToString(player.getLocation());
				spawns.add(location);
				plugin.config.set("spawns", spawns);
				plugin.savec();
				msg.msg(player, plugin.config.getString("message.addspawn").replace("LOC", location));
			} else {
				return false;
			}
		} else {
			msg.onlyplayer(sender);
		}
		return false;
	}

}
