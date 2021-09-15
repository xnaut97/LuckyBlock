package net.tez.luckyblocks.commands;

import java.util.Collections;
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

public class RenameCommand extends AbstractCommand{

	private ItemManager itemManager;
	
	private Language language;

	public RenameCommand(LuckyBlocks plugin) {
		super(plugin);
		this.itemManager = plugin.getItemManager();
		this.language = plugin.getCurrentLanguage();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "rename";
	}

	@Override
	public String getPermissions() {
		// TODO Auto-generated method stub
		return "luckyblock.command.rename";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Changes registry name of specific item.";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "&7» Syntax: &6/lb rename <old-name> <new-name>";
	}

	@Override
	public boolean allowConsole() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if(args.length > 3) {
			StringUtils.messages(player, this.getUsage());
		}
		else if(args.length == 1) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		else if(args.length == 2) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		else if(args.length == 3) {
			final String registryName = args[1];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}
			
			final String newRegistryName = args[2];
			
			if(newRegistryName.equals(registryName)) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_DUPLICATE));
				return;
			}
			
			Optional<LuckyItems> optFound = this.itemManager.getItem(newRegistryName);
			if(optFound.isPresent()) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_ALREADY_TAKEN)
						.replace("%registry-name%", newRegistryName));
				return;
			}
			
			LuckyItems newItem = LuckyItems.copy(optItem.get());
			newItem.setRegistryName(newRegistryName);
			
			this.itemManager.updateItem(optItem.get(), newItem);
			
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_RENAME_SENDER)
					.replace("%old-registry-name%", registryName)
					.replace("%new-registry-name%", newRegistryName));
			
			Bukkit.getOnlinePlayers().stream()
			.filter(others -> !others.getUniqueId().equals(player.getUniqueId()) 
					&& others.hasPermission("luckyblock.notify.rename"))
			.forEach(others -> StringUtils.messages(others, language.get(ConfigSection.COMMAND_RENAME_OTHERS)
					.replace("%old-registry-name%", registryName)
					.replace("%new-registry-name%", newRegistryName)
					.replace("%player-name%", player.getName())));
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		if(args.length > 3) {
			StringUtils.console(this.getUsage());
		}
		else if(args.length == 1) {
			StringUtils.console(language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		else if(args.length == 2) {
			StringUtils.console(language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		else if(args.length == 3) {
			final String registryName = args[1];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}
			
			final String newRegistryName = args[2];
			
			if(newRegistryName.equals(registryName)) {
				StringUtils.console(language.get(ConfigSection.COMMAND_NAME_DUPLICATE));
				return;
			}
			
			Optional<LuckyItems> optFound = this.itemManager.getItem(newRegistryName);
			if(optFound.isPresent()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_NAME_ALREADY_TAKEN)
						.replace("%registry-name%", newRegistryName));
				return;
			}
			
			LuckyItems newItem = LuckyItems.copy(optItem.get());
			newItem.setRegistryName(newRegistryName);
			
			this.itemManager.updateItem(optItem.get(), newItem);
			
			StringUtils.console(language.get(ConfigSection.COMMAND_RENAME_SENDER)
					.replace("%old-registry-name%", registryName)
					.replace("%new-registry-name%", newRegistryName));
			
			Bukkit.getOnlinePlayers().stream()
			.filter(others -> others.hasPermission("luckyblock.notify.rename"))
			.forEach(others -> StringUtils.messages(others, language.get(ConfigSection.COMMAND_RENAME_OTHERS)
					.replace("%old-registry-name%", registryName)
					.replace("%new-registry-name%", newRegistryName)
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
		}else if(args.length == 3) {
			return Collections.singletonList("<new-name>");
		}
		return null;
	}

}
