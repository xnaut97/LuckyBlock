package net.tez.luckyblocks.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.commands.executor.AbstractCommand;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.constants.LuckyItems;
import net.tez.luckyblocks.manager.ItemManager;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class SetItemCommand extends AbstractCommand{

	private ItemManager itemManager;
	
	private Language language;

	public SetItemCommand(LuckyBlocks plugin) {
		super(plugin);
		this.itemManager = plugin.getItemManager();
		this.language = plugin.getCurrentLanguage();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "setitem";
	}

	@Override
	public String getPermissions() {
		// TODO Auto-generated method stub
		return "luckyblock.command.setitem";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Changes item by specific registry name.";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "&7» Syntax: &6/lb setitem <registry-name> <amount>";
	}

	@Override
	public boolean allowConsole() {
		// TODO Auto-generated method stub
		return false;
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
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_AMOUNT_EMPTY));
		}
		else if(args.length == 3) {
			final String registryName = args[1];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}
			
			int amount = this.parseAmount(player, args[2]);
			if(amount == -1)
				return;
			
			ItemStack inHandItem = this.parseItem(player).clone();
			if(inHandItem == null)
				return;
			inHandItem.setAmount(amount);
			
			LuckyItems cloneItem = LuckyItems.copy(optItem.get());
			LuckyItems newItem = LuckyItems.copy(optItem.get());
			newItem.setItem(inHandItem);
			
			this.itemManager.updateItem(optItem.get(), newItem);
			
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_SETITEM_SENDER)
					.replace("%registry-name%", registryName));
			
			Bukkit.getOnlinePlayers().stream()
			.filter(others -> !others.getUniqueId().equals(player.getUniqueId())
					&& others.hasPermission("luckyblock.notify.setitem"))
			.forEach(others -> StringUtils.messages(others, language.get(ConfigSection.COMMAND_SETITEM_OTHERS)
					.replace("%player-name%", player.getName())
					.replace("%registry-name%", registryName)));
			
			player.getInventory().setItemInHand(cloneItem.getItem());
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length == 2) {
			return this.itemManager.getItems().stream()
					.filter(item -> item.getRegistryName().startsWith(args[1]))
					.map(item -> item.getRegistryName())
					.collect(Collectors.toList());
		}else if(args.length == 3) {
			return Collections.singletonList("<amount>");
		}
		return null;
	}

	private ItemStack parseItem(Player player) {
		ItemStack inHandItem = player.getInventory().getItemInHand();
		if(inHandItem == null || inHandItem.getType() == Material.AIR) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_ITEM_EMPTY));
			return null;
		}
		return inHandItem;
	}
	
	private int parseAmount(Player player, String str) {
		try {
			int value = Integer.parseInt(str);
			if(value < 1) {
				StringUtils.console(language.get(ConfigSection.COMMAND_AMOUNT_NEGATIVE_AMOUNT));
				return -1;
			}else if(value > 64) {
				StringUtils.console(language.get(ConfigSection.COMMAND_CHANCE_LIMIT));
				return -1;
			}

			return value;
		} catch (Exception e) {
			StringUtils.console(language.get(ConfigSection.COMMAND_CHANCE_INVALID));
			return -1;
		}
	}

}