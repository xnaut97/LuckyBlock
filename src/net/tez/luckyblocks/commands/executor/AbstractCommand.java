package net.tez.luckyblocks.commands.executor;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.tez.luckyblocks.LuckyBlocks;

public abstract class AbstractCommand {
	
	private LuckyBlocks plugin;
	
	private FileConfiguration config;

	public abstract String getName();
	
	public abstract String getPermissions();
	
	public abstract String getDescription();
	
	public abstract String getUsage();
	
	public abstract boolean allowConsole();
	
	public abstract void execute(CommandSender sender, String[] args);

	public abstract void consoleExecute(CommandSender sender, String[] args);
	
	public abstract List<String> tabComplete(CommandSender sender, String[] args);
	
	public AbstractCommand(LuckyBlocks plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}
	
	public LuckyBlocks getPlugin() {
		return plugin;
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
}
