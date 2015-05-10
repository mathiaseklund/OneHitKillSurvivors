package ohks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class SQLMethods {

	private static SQLMethods instance = new SQLMethods();

	Main plugin = Main.getMain();
	Messages msg = Messages.getInstance();
	Utils util = Utils.getInstance();

	public static SQLMethods getInstance() {
		return instance;
	}

	public synchronized int getID(Player player) {
		int keyid = 0;
		String query = "SELECT id FROM accounts WHERE uuid = ?";
		try {
			PreparedStatement ps = plugin.c.prepareStatement(query);
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				keyid = newPlayer(player);
			} else {
				keyid = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keyid;
	}

	public synchronized int newPlayer(Player player) {
		int keyid = 0;
		String query = "INSERT INTO accounts (uuid, username,kills) VALUES (?, ?, ?)";
		PreparedStatement ps;
		try {
			ps = plugin.c.prepareStatement(query);
			ps.setString(1, player.getUniqueId().toString());
			ps.setString(2, player.getName());
			ps.setInt(3, 0);
			ps.execute();
			keyid = getID(player);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keyid;

	}

	public synchronized int getKills(Player player) {
		int kills = 0;
		String query = "SELECT kills FROM accounts WHERE id = ?";
		try {
			PreparedStatement ps = plugin.c.prepareStatement(query);
			ps.setInt(1, Lists.id.get(player.getName()));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				kills = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return kills;
	}

	public synchronized void addKill(Player player) {
		int kills = getKills(player) + 1;
		String query = "UPDATE accounts SET kills=? WHERE id=?";
		try {
			PreparedStatement ps = plugin.c.prepareStatement(query);
			ps.setInt(1, kills);
			ps.setInt(2, Lists.id.get(player.getName()));
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
