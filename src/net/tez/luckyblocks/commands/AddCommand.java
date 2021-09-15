package net.tez.luckyblocks.commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import net.tez.luckyblocks.utils.MathUtils;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class AddCommand extends AbstractCommand{

	private Language language;

	private ItemManager itemManager;

	public AddCommand(LuckyBlocks plugin) {
		super(plugin);
		this.language = plugin.getCurrentLanguage();
		this.itemManager = plugin.getItemManager();
	}

	@Override
	public String getName() {
		return "add";
	}

	@Override
	public String getPermissions() {
		return "luckyblocks.command.add";
	}

	@Override
	public String getDescription() {
		return "Adds reward to lucky block";
	}

	@Override
	public String getUsage() {
		return "&7» Syntax: &6/lb add <registryName> <chance> <amount>";
	}

	@Override
	public boolean allowConsole() {
		return false;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		//Give usage
		if(args.length > 4) {
			StringUtils.messages(player, this.getUsage());
		}
		///lb add
		else if(args.length == 1) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		//lb add <registryName>
		else if(args.length == 2) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_CHANCE_EMPTY));
		}
		//lb add <registryName> <chance>
		else if(args.length == 3) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_AMOUNT_EMPTY));
		}
		//lb add <registryName> <chance> <amount>
		else if(args.length == 4) {
			if(args[0].equalsIgnoreCase("add")) {
				final String registryName = args[1];
				Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
				if(optItem.isPresent()) {
					//Tell player that item is exist with that name.
					StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_ALREADY_TAKEN)
							.replace("%registry-name%", registryName));
					return;
				}
				double chance = MathUtils.roundDouble(this.parseDouble(player, args[2]));
				int amount = this.parseAmount(player, args[3]);
				ItemStack item = this.parseItem(player).clone();
				if(amount == -1 || chance == -1 || item == null)
					return;
				
				item.setAmount(amount);
				LuckyItems luckyItems = LuckyItems.create(registryName, chance, item, System.currentTimeMillis(), player.getUniqueId());
				this.itemManager.registerItem(luckyItems);
				//Tell player that item is created success.
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_ADD_SENDER)
						.replace("%registry-name%", registryName));

				//Tell other player who have notify permissions
				Bukkit.getOnlinePlayers().stream()
				.filter(others -> !others.getUniqueId().equals(player.getUniqueId()) && others.hasPermission("luckyblock.notify.add"))
				.forEach(others -> StringUtils.messages(others, language.get(ConfigSection.COMMAND_ADD_OTHERS)
						.replace("%player-name%", player.getName())
						.replace("%registry-name%", registryName)));
			}
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length == 2) {
			return Collections.singletonList("<registry-name>");
		}else if(args.length == 3) {
			return Collections.singletonList("<chance>");
		}else if(args.length == 4) {
			return Collections.singletonList("<amount>");
		}
		return null;
	}

	private double parseDouble(Player player, String str) {
		try {
			double value = Double.parseDouble(str);
			if(value < 1) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_CHANCE_NEGATIVE_AMOUNT));
				return -1;
			}else if(value > 100) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_CHANCE_LIMIT));
				return -1;
			}

			return value;
		} catch (Exception e) {
			//Tell player that chance must be number.
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_CHANCE_INVALID));
			return -1;
		}
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

	private ItemStack parseItem(Player player) {
		ItemStack inHandItem = player.getInventory().getItemInHand();
		if(inHandItem == null || inHandItem.getType() == Material.AIR) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_ITEM_EMPTY));
			return null;
		}
		return inHandItem;
	}
}
