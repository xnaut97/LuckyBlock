package net.tez.luckyblocks.utils.player;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerUtils {
	
	private String name;
	
	private UUID uuid;

	private SearchType searchType;
	
	private PlayerUtils(String name) {
		this.name = name;
	}
	
	private PlayerUtils(UUID uuid) {
		this.uuid = uuid;
	}

	public static PlayerUtils invoke(String name) {
		return new PlayerUtils(name);
	}
	
	public static PlayerUtils invoke(UUID uuid) {
		return new PlayerUtils(uuid);
	}
	
	public Optional<OfflinePlayer> findOffline() {
		Optional<OfflinePlayer> opt = Optional.empty();
		switch(this.searchType) {
		case NAME:
			opt = Arrays.asList(Bukkit.getOfflinePlayers()).stream()
					.filter(player -> player.getName().equals(this.name))
					.findAny();
			break;
		case UUID:
			opt = Arrays.asList(Bukkit.getOfflinePlayers()).stream()
					.filter(player -> player.getUniqueId().equals(this.uuid))
					.findAny();
			break;
		}
		return opt;
	}
	
	public Optional<Player> findOnline() {
		Optional<Player> opt = Optional.empty();
		switch(this.searchType) {
		case NAME:
			opt = Bukkit.getOnlinePlayers().stream()
					.filter(player -> player.getName().equals(this.name))
					.map(player -> Bukkit.getPlayerExact(player.getName()))
					.findAny();
			break;
		case UUID:
			opt = Bukkit.getOnlinePlayers().stream()
					.filter(player -> player.getUniqueId().equals(this.uuid))
					.map(player -> Bukkit.getPlayerExact(player.getName()))
					.findAny();
			break;
		}
		return opt;
	}
	
	public PlayerUtils setSearchType(SearchType type) {
		this.searchType = type;
		return this;
	}
	
	public enum SearchType {
		UUID,NAME;
	}
}
