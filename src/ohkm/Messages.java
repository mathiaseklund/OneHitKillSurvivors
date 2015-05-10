package ohkm;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Messages {

	private static Main plugin = Main.getMain();
	private static Messages instance = new Messages();

	private static Utils util = Utils.getInstance();

	public static Messages getInstance() {
		return instance;
	}

	public void onlyplayer(CommandSender sender) {
		cmsg(sender, plugin.config.getString("messages.onlyplayer"));
	}

	public void noperm(Player player) {
		msg(player, plugin.config.getString("messages.noperm"));
	}

	public void msg(Player player, String msg) {
		player.sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', msg));
	}

	public void cmsg(CommandSender sender, String msg) {
		sender.sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', msg));
	}

	public void brdcst(String msg) {
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public void sendTitle(Player player, String subtitle) {
		//title = util.colorString(title);
		subtitle = util.colorString(subtitle);
		CraftPlayer craftplayer = (CraftPlayer) player;
		PlayerConnection connection = craftplayer.getHandle().playerConnection;
		IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + "" + "'}");
		IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subtitle + "'}");
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, 10, 20, 10);
		PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
		connection.sendPacket(titlePacket);
		connection.sendPacket(subtitlePacket);
	}
}
