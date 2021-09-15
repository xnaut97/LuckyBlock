package net.tez.luckyblocks.commands;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.commands.executor.AbstractCommand;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.constants.LuckyItems;
import net.tez.luckyblocks.manager.ItemManager;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class RemoveCommand extends AbstractCommand{

	private ItemManager itemManager;
	
	private Language language;

	public RemoveCommand(LuckyBlocks plugin) {
		super(plugin);
		this.itemManager = plugin.getItemManager();
		this.language = plugin.getCurrentLanguage();
	}

	@Override
	public String getName() {
		return "remove";
	}

	@Override
	public String getPermissions() {
		// TODO Auto-generated method stub
		return "luckyblock.command.remove";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Removes item with specific registry name.";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "&7» Syntax: &6/lb remove <registry-name>";
	}

	@Override
	public boolean allowConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if(args.length > 2) {
			StringUtils.messages(player, this.getUsage());
			return;
		}
		else if(args.length == 1) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		else if(args.length == 2) {
			final String registryName = args[1];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}
			
			LuckyItems item = optItem.get();
			this.itemManager.unregisterItem(item);
			
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_REMOVE_SENDER)
					.replace("%registry-name%", registryName));
			
			Bukkit.getOnlinePlayers().stream()
			.filter(others -> !others.getUniqueId().equals(player.getUniqueId()) && others.hasPermission("luckyblock.notify.remove"))
			.forEach(others -> StringUtils.messages(others, language.get(ConfigSection.COMMAND_REMOVE_OTHERS)
					.replace("%registry-name%", registryName)
					.replace("%player-name%", player.getName())));
		}	
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		if(args.length > 2) {
			StringUtils.console( this.getUsage());
			return;
		}
		else if(args.length == 1) {
			StringUtils.console( language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		else if(args.length == 2) {
			final String registryName = args[1];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.console( language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}
			
			LuckyItems item = optItem.get();
			this.itemManager.unregisterItem(item);
			
			StringUtils.console( language.get(ConfigSection.COMMAND_REMOVE_SENDER)
					.replace("%registry-name%", registryName));
			
			Bukkit.getOnlinePlayers().stream()
			.filter(others -> others.hasPermission("luckyblock.notify.remove"))
			.forEach(others -> StringUtils.messages(others, language.get(ConfigSection.COMMAND_REMOVE_OTHERS)
					.replace("%registry-name%", registryName)
					.replace("%player-name%", "&cConsole")));
		}	
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length == 2) {
			return this.itemManager.getItems().stream()
					.filter(item -> item.getRegistryName().startsWith(args[1]))
					.map(item -> item.getRegistryName())
					.collect(Collectors.toList());
		}
		return null;
	}

}
