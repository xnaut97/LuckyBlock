package net.tez.luckyblocks.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

public class ModifyCommand extends AbstractCommand{

	private ItemManager itemManager;
	
	private Language language;

	public ModifyCommand(LuckyBlocks plugin) {
		super(plugin);
		this.itemManager = plugin.getItemManager();
		this.language = plugin.getCurrentLanguage();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "modify";
	}

	@Override
	public String getPermissions() {
		// TODO Auto-generated method stub
		return "luckyblock.command.modify";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Modify chance/amount of lucky item";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "&7» Syntax: &6/lb modify <registry-name> chance/amount <value>";
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
			StringUtils.messages(player, this.getUsage());
		}
		else if(args.length == 1) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		else if(args.length == 2) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_MODIFY_TYPE));
		}
		else if(args.length == 3) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_MODIFY_EMPTY_VALUE));
		}
		else if(args.length == 4) {
			final String registryName = args[1];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}
			
			final String type = args[2];
			final String value = args[3];
			LuckyItems newItem = LuckyItems.copy(optItem.get());
			double oldChance = newItem.getChance();
			int oldAmount = newItem.getItem().getAmount();
			
			if(type.equalsIgnoreCase("chance")) {
				double chance = MathUtils.roundDouble(this.parseDouble(player, value));
				if(chance == -1)
					return;
				
				newItem.setChance(chance);
				
				this.itemManager.updateItem(optItem.get(), newItem);
				
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_MODIFY_CHANCE)
						.replace("%old-chance%", "" + oldChance)
						.replace("%new-chance%", "" + chance));
			}else if(type.equalsIgnoreCase("amount")) {
				int amount = this.parseAmount(player, value);
				if(amount == -1)
					return;
				
				ItemStack item = newItem.getItem().clone();
				item.setAmount(amount);
				newItem.setItem(item);
				
				this.itemManager.updateItem(optItem.get(), newItem);
				
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_MODIFY_AMOUNT)
						.replace("%old-amount%", "" + oldAmount)
						.replace("%new-amount%", "" + amount));
			}
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		if(args.length > 4) {
			StringUtils.console(this.getUsage());
		}
		else if(args.length == 1) {
			StringUtils.console( language.get(ConfigSection.COMMAND_NAME_EMPTY));
		}
		else if(args.length == 2) {
			StringUtils.console( language.get(ConfigSection.COMMAND_MODIFY_TYPE));
		}
		else if(args.length == 3) {
			StringUtils.console( language.get(ConfigSection.COMMAND_MODIFY_EMPTY_VALUE));
		}
		else if(args.length == 4) {
			final String registryName = args[1];
			Optional<LuckyItems> optItem = this.itemManager.getItem(registryName);
			if(!optItem.isPresent()) {
				StringUtils.console(language.get(ConfigSection.COMMAND_NAME_NOT_FOUND)
						.replace("%registry-name%", registryName));
				return;
			}
			
			final String type = args[2];
			final String value = args[3];
			LuckyItems newItem = LuckyItems.copy(optItem.get());
			double oldChance = newItem.getChance();
			int oldAmount = newItem.getItem().getAmount();
			
			if(type.equalsIgnoreCase("chance")) {
				double chance = this.parseDouble(value);
				if(chance == -1)
					return;
				
				newItem.setChance(chance);
				
				this.itemManager.updateItem(optItem.get(), newItem);
				
				StringUtils.console(language.get(ConfigSection.COMMAND_MODIFY_CHANCE)
						.replace("%old-chance%", "" + oldChance)
						.replace("%new-chance%", "" + chance));
			}else if(type.equalsIgnoreCase("amount")) {
				int amount = this.parseAmount(value);
				if(amount == -1)
					return;
				
				ItemStack item = newItem.getItem().clone();
				item.setAmount(amount);
				newItem.setItem(item);
				
				this.itemManager.updateItem(optItem.get(), newItem);
				
				StringUtils.console(language.get(ConfigSection.COMMAND_MODIFY_AMOUNT)
						.replace("%old-amount%", "" + oldAmount)
						.replace("%new-amount%", "" + amount));
			}
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
			return Arrays.asList("amount","chance");
		}else if(args.length == 4) {
			return Collections.singletonList("<value>");
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
	
	private double parseDouble(String str) {
		try {
			double value = Integer.parseInt(str);
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
