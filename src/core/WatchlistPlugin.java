package core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import commands.WatchlistCommand;
import listener.PlayerJoining;
import sql.SQLSetup;
import sql.WatchlistSQL;

public class WatchlistPlugin extends JavaPlugin {

	public static WatchlistPlugin plugin;
	public SQLSetup SQL;
	public WatchlistSQL watchlist;

	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	@Override
	public void onEnable() {
		// Initiating the plugin instance to this class
		plugin = this;
		// Enable Message
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lPlugin is enabled!"));

		// initiates the SQL setup and the watchList Table SQL set up
		SQL = new SQLSetup();
		watchlist = new WatchlistSQL(this);

		// setting the command executor
		getCommand("watchlist").setExecutor(new WatchlistCommand());

		// setting up eventlistener for on join
		Bukkit.getPluginManager().registerEvents(new PlayerJoining(), this);

		//
		List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());

		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);

			if (p.hasPermission("watchlist") && !PlayerJoining.staffPlayers.contains(p)) {
				PlayerJoining.staffPlayers.add(p);
			}

		}

		// attempting to connect to the SQL database and creating the table
		try {
			SQL.connect();
			watchlist.createTable();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		// Checks if we connected to the SQL datasbase
		if (SQL.isConnected()) {
			console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDatabase is connected!"));
		}

	}

	@Override
	public void onDisable() {
		// disconnects from SQL database
		SQL.disconnect();
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lPlugin is disabled!"));

	}

	public static WatchlistPlugin getInstance() {
		return plugin;
	}
}
