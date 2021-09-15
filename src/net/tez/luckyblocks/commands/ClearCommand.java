package net.tez.luckyblocks.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.commands.executor.AbstractCommand;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.manager.ItemManager;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class ClearCommand extends AbstractCommand{

	private Language language;
	
	private ItemManager itemManager;

	public ClearCommand(LuckyBlocks plugin) {
		super(plugin);
		this.language = plugin.getCurrentLanguage();
		this.itemManager = plugin.getItemManager();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "clear";
	}

	@Override
	public String getPermissions() {
		// TODO Auto-generated method stub
		return "luckyblock.command.clear";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Clear all items from lucky block.";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "&7» Syntax: &6/lb clear";
	}

	@Override
	public boolean allowConsole() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if(args.length > 1) {
			StringUtils.messages(player, this.getUsage());
		}else if(args.length == 1) {
			if(itemManager.getItems().size() < 1) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_CLEAR_NO_ITEMS));
				return;
			}
			int amount = itemManager.getItems().size();
			itemManager.clearItems();
			
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_CLEAR_SENDER)
					.replace("%amount%", "" + amount));
			
			Bukkit.getOnlinePlayers().stream()
			.filter(others -> !others.getUniqueId().equals(player.getUniqueId()) 
					&& others.hasPermission("luckyblock.notify.clear"))
			.forEach(others -> StringUtils.messages(others, language.get(ConfigSection.COMMAND_CLEAR_OTHERS)
					.replace("%amount%", "" + amount)
					.replace("%player-name%", player.getName())));
		}
		
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		if(args.length > 1) {
			StringUtils.console( this.getUsage());
		}else if(args.length == 1) {
			if(itemManager.getItems().size() < 1) {
				StringUtils.console( language.get(ConfigSection.COMMAND_CLEAR_NO_ITEMS));
				return;
			}
			int amount = itemManager.getItems().size();
			itemManager.clearItems();
			
			StringUtils.console( language.get(ConfigSection.COMMAND_CLEAR_SENDER)
					.replace("%amount%", "" + amount));
			
			Bukkit.getOnlinePlayers().stream()
			.filter(others -> others.hasPermission("luckyblock.notify.clear"))
			.forEach(others -> StringUtils.messages(others, language.get(ConfigSection.COMMAND_CLEAR_OTHERS)
					.replace("%amount%", "" + amount)
					.replace("%player-name%", "&cConsole")));
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
