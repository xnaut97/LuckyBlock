package net.tez.luckyblocks.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.commands.executor.AbstractCommand;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;
import net.tez.luckyblocks.utils.xseries.XMaterial;

public class GiveCommand extends AbstractCommand {

	private Language language;

	public GiveCommand(LuckyBlocks plugin) {
		super(plugin);
		this.language = plugin.getCurrentLanguage();
	}

	@Override
	public String getName() {
		return "give";
	}

	@Override
	public String getPermissions() {
		return "luckyblocks.command.give";
	}

	@Override
	public String getDescription() {
		return "Give players specific amount of lucky blocks";
	}

	@Override
	public String getUsage() {
		return "&7» Syntax: &6/lb give <player> <amount>";
	}

	@Override
	public boolean allowConsole() {
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if(args.length > 3) {
			StringUtils.messages(player, this.getUsage());
			return;
		}
		else if(args.length == 1) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_TARGET_EMPTY));
		}
		else if(args.length == 2) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_AMOUNT_EMPTY));
		}
		else if(args.length == 3) {
			if(args[0].equalsIgnoreCase("give")) {
				final String playerName = args[1];
				Optional<OfflinePlayer> optTarget = this.getTarget(playerName);
				if(!optTarget.isPresent()) {
					StringUtils.messages(player, this.language.get(ConfigSection.COMMAND_TARGET_NOT_EXIST)
							.replace("%name%", playerName));
					return;
				}
				OfflinePlayer target = optTarget.get();
				if(!target.isOnline()) {
					StringUtils.messages(player, this.language.get(ConfigSection.COMMAND_TARGET_NOT_ONLINE)
							.replace("%player-name%", playerName));
					return;
				}

				int amount = this.parseAmount(player, args[2]);
				if(amount == -1)
					return;

				StringUtils.messages(player, language.get(ConfigSection.COMMAND_GIVE_SENDER)
						.replace("%lucky-block-display-name%", language.get(ConfigSection.ITEM_DISPLAY_NAME))
						.replace("%amount%", "" + amount)
						.replace("%player-name%", target.getName()));

				if(!target.getUniqueId().equals(player.getUniqueId())) {
					StringUtils.messages(target.getPlayer(), language.get(ConfigSection.COMMAND_GIVE_TARGET)
							.replace("%lucky-block-display-name%", language.get(ConfigSection.ITEM_DISPLAY_NAME))
							.replace("%amount%", "" + amount));
				}

				ItemStack item = this.createItem();
				for(int i = 0 ; i < amount ; i++) {
					if(target.getPlayer().getInventory().addItem(item).size() < 1)
						continue;
					target.getPlayer().getWorld().dropItem(target.getPlayer().getLocation(), item);
				}
			}
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		if(args.length > 3) {
			StringUtils.console( this.getUsage());
			return;
		}
		else if(args.length == 1) {
			StringUtils.console( language.get(ConfigSection.COMMAND_TARGET_EMPTY));
		}
		else if(args.length == 2) {
			StringUtils.console( language.get(ConfigSection.COMMAND_AMOUNT_EMPTY));
		}
		else if(args.length == 3) {
			if(args[0].equalsIgnoreCase("give")) {
				final String playerName = args[1];
				Optional<OfflinePlayer> optTarget = this.getTarget(playerName);
				if(!optTarget.isPresent()) {
					StringUtils.console( this.language.get(ConfigSection.COMMAND_TARGET_NOT_EXIST)
							.replace("%name%", playerName));
					return;
				}
				OfflinePlayer target = optTarget.get();
				if(!target.isOnline()) {
					StringUtils.console( this.language.get(ConfigSection.COMMAND_TARGET_NOT_ONLINE)
							.replace("%player-name%", playerName));
					return;
				}

				int amount = this.parseAmount(args[2]);
				if(amount == -1)
					return;

				StringUtils.console( language.get(ConfigSection.COMMAND_GIVE_SENDER)
						.replace("%lucky-block-display-name%", language.get(ConfigSection.ITEM_DISPLAY_NAME))
						.replace("%amount%", "" + amount)
						.replace("%player-name%", target.getName()));

				StringUtils.messages(target.getPlayer(), language.get(ConfigSection.COMMAND_GIVE_TARGET)
						.replace("%lucky-block-display-name%", language.get(ConfigSection.ITEM_DISPLAY_NAME))
						.replace("%amount%", "" + amount));

				ItemStack item = this.createItem();
				for(int i = 0 ; i < amount ; i++) {
					if(target.getPlayer().getInventory().addItem(item).size() < 1)
						continue;
					target.getPlayer().getWorld().dropItem(target.getPlayer().getLocation(), item);
				}
			}
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length == 1) {
			return Collections.singletonList("give");
		}else if(args.length == 2) {
			String playerName = args[1];
			return Bukkit.getOnlinePlayers().stream()
					.filter(player -> player.getName().startsWith(playerName))
					.map(player -> player.getName())
					.collect(Collectors.toList());
		}else if(args.length == 3) {
			return Collections.singletonList("<amount>");
		}
		return new ArrayList<>();
	}

	private ItemStack createItem() {
		ItemStack item = null;
		XMaterial mat = XMaterial.valueOf(language.get(ConfigSection.ITEM_TYPE).toUpperCase());
		NBTItem nbti = new NBTItem(mat.parseItem());
		nbti.setBoolean("lucky-block", true);
		item = nbti.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(language.get(ConfigSection.ITEM_DISPLAY_NAME).replace("&", "§"));
		List<String> lore = new ArrayList<>();
		for(String str : language.getList(ConfigSection.ITEM_LORE)) {
			str = str.replace("&", "§");
			lore.add(str);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private Optional<OfflinePlayer> getTarget(String name){
		return Arrays.asList(Bukkit.getOfflinePlayers()).stream()
				.filter(player -> player != null && player.getName().equals(name))
				.findAny();
	}

	private int parseAmount(Player player, String str) {
		try {
			int value = Integer.parseInt(str);
			if(value < 1) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_AMOUNT_NEGATIVE_AMOUNT));
				return -1;
			}else if(value > 64) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_CHANCE_LIMIT));
				return -1;
			}

			return value;
		} catch (Exception e) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_CHANCE_INVALID));
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
