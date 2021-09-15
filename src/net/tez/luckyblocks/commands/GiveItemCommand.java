package net.tez.luckyblocks.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
import net.tez.luckyblocks.utils.player.PlayerUtils;
import net.tez.luckyblocks.utils.player.PlayerUtils.SearchType;

public class GiveItemCommand extends AbstractCommand{

	private ItemManager itemManager;

	private Language language;

	public GiveItemCommand(LuckyBlocks plugin) {
		super(plugin);
		this.itemManager = plugin.getItemManager();
		this.language = plugin.getCurrentLanguage();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "giveitem";
	}

	@Override
	public String getPermissions() {
		// TODO Auto-generated method stub
		return "luckyblock.command.giveitem";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Gives item to player with specific registry name.";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "&7» Syntax: &6/lb giveitem <player> <registry-name> <amount>";
	}

	@Override
	public boolean allowConsole() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if(args.length > 4) {
			StringUtils.console(this.getUsage());
		}
		else if(args.length == 1) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_TARGET_EMPTY));
		}
		else if(args.length == 2) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_EMPTY));	
		}
		else if(args.length == 3) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_AMOUNT_EMPTY));
		}
		else if(args.length == 4) {
			final String playerName = args[1];
			Optional<OfflinePlayer> optTarget = PlayerUtils.invoke(playerName)
					.setSearchType(SearchType.NAME).findOffline();
			if(!optTarget.isPresent()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_TARGET_NOT_EXIST)
						.replace("%name%", playerName));
				return;
			}
			OfflinePlayer target = optTarget.get();
			if(!target.isOnline()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_TARGET_NOT_ONLINE)
						.replace("%player-name%", target.getName()));
				return;
			}

			final String registryName = args[2];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}

			LuckyItems luckyItem = optItem.get();
			ItemStack toGive = luckyItem.getItem().clone();
			toGive.setAmount(1);

			final int amount = this.parseAmount(player, args[3]);
			if(amount == -1)
				return;

			for(int i = 0 ; i < amount ; i++) {
				if(!target.isOnline())
					break;
				if(target.getPlayer().getInventory().addItem(toGive).size() < 1)
					continue;
				target.getPlayer().getWorld().dropItem(target.getPlayer().getLocation(), toGive);
			}

			String itemName = toGive.getItemMeta().hasDisplayName() ? toGive.getItemMeta().getDisplayName()
					: "&b&l" + StringUtils.formatItemName(toGive);
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_GIVEITEM_SENDER)
					.replace("%item-display-name%", itemName)
					.replace("%amount%", "" + amount)
					.replace("%player-name%", target.getName()));

			if(!target.getUniqueId().equals(player.getUniqueId())) {
				StringUtils.messages(target.getPlayer(), language.get(ConfigSection.COMMAND_GIVEITEM_TARGET)
						.replace("%item-display-name%", itemName)
						.replace("%amount%", "" + amount));
			}
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		if(args.length > 4) {
			StringUtils.console(this.getUsage());
		}
		else if(args.length == 1) {
			StringUtils.console( language.get(ConfigSection.COMMAND_TARGET_EMPTY));
		}
		else if(args.length == 2) {
			StringUtils.console( language.get(ConfigSection.COMMAND_NAME_EMPTY));	
		}
		else if(args.length == 3) {
			StringUtils.console( language.get(ConfigSection.COMMAND_AMOUNT_EMPTY));
		}
		else if(args.length == 4) {
			final String playerName = args[1];
			Optional<OfflinePlayer> optTarget = PlayerUtils.invoke(playerName)
					.setSearchType(SearchType.NAME).findOffline();
			if(!optTarget.isPresent()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_TARGET_NOT_EXIST)
						.replace("%name%", playerName));
				return;
			}
			OfflinePlayer target = optTarget.get();
			if(!target.isOnline()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_TARGET_NOT_ONLINE)
						.replace("%player-name%", target.getName()));
				return;
			}

			final String registryName = args[2];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}

			LuckyItems luckyItem = optItem.get();
			ItemStack toGive = luckyItem.getItem().clone();
			toGive.setAmount(1);

			final int amount = this.parseAmount(args[3]);
			if(amount == -1)
				return;

			for(int i = 0 ; i < amount ; i++) {
				if(!target.isOnline())
					break;
				if(target.getPlayer().getInventory().addItem(toGive).size() < 1)
					continue;
				target.getPlayer().getWorld().dropItem(target.getPlayer().getLocation(), toGive);
			}

			String itemName = toGive.getItemMeta().hasDisplayName() ? toGive.getItemMeta().getDisplayName()
					: "&b&l" + StringUtils.formatItemName(toGive);
			StringUtils.console(language.get(ConfigSection.COMMAND_GIVEITEM_SENDER)
					.replace("%item-display-name%", itemName)
					.replace("%amount%", "" + amount)
					.replace("%player-name%", target.getName()));

			StringUtils.messages(target.getPlayer(), language.get(ConfigSection.COMMAND_GIVEITEM_TARGET)
					.replace("%item-display-name%", itemName)
					.replace("%amount%", "" + amount));
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length == 2) {
			return Bukkit.getOnlinePlayers().stream()
					.filter(player -> player.getName().startsWith(args[1]))
					.map(player -> player.getName())
					.collect(Collectors.toList());
		}else if(args.length == 3) {
			return this.itemManager.getItems().stream()
					.filter(item -> item.getRegistryName().startsWith(args[2]))
					.map(item -> item.getRegistryName())
					.collect(Collectors.toList());
		}else if(args.length == 4) {
			return Collections.singletonList("<amount>");
		}
		return null;
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

	private int parseAmount(String str) {
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
