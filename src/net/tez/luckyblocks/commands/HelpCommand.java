package net.tez.luckyblocks.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.commands.executor.AbstractCommand;
import net.tez.luckyblocks.commands.executor.CommandHandler;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class HelpCommand extends AbstractCommand{

	private Language language;
	
	private List<AbstractCommand> commands;

	public HelpCommand(LuckyBlocks plugin, CommandHandler handler) {
		super(plugin);
		this.language = plugin.getCurrentLanguage();
		this.commands = handler.getCommands();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "help";
	}

	@Override
	public String getPermissions() {
		// TODO Auto-generated method stub
		return "luckyblock.command.help";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Shows available commands.";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "&7» Syntax: &6/lb help <page>";
	}

	@Override
	public boolean allowConsole() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		int value = 5;
		if(args.length > 2) {
			StringUtils.messages(player, this.getUsage());
		}else if(args.length == 1) {
			this.showCommands(player, 0, value);
		}
		else if(args.length == 2) {
			int page = this.parsePage(player, args[1]);
			if(page == -1)
				return;
			this.showCommands(player, 5*(page-1), 5*page);
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		int value = 5;
		if(args.length > 2) {
			StringUtils.console(this.getUsage());
		}else if(args.length == 1) {
			this.showCommands(0, value);
		}
		else if(args.length == 2) {
			int page = this.parsePage(args[1]);
			if(page == -1)
				return;
			this.showCommands(5*(page-1), 5*page);
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> list = new ArrayList<>();
		if(args.length == 2) {
			for(int i = 1 ; i < commands.size()+1 ; i++) {
				if(i%5==0)
					list.add(String.valueOf(i/5));
			}
			return list;
		}
		return null;
	}

	private void showCommands(Player player, int current, int max) {
		for(int i = current ; i < max ; i++) {
			AbstractCommand command = this.commands.get(i);
			if(!player.hasPermission(command.getPermissions()))
				continue;
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_HELP)
					.replace("%command-name%", command.getName())
					.replace("%command-description%", command.getDescription()));
		}
	}
	
	private void showCommands(int current, int max) {
		for(int i = current ; i < max ; i++) {
			AbstractCommand command = this.commands.get(i);
			StringUtils.console(language.get(ConfigSection.COMMAND_HELP)
					.replace("%command-name%", command.getName())
					.replace("%command-description%", command.getDescription()));
		}
	}
	
	private int parsePage(Player player, String str) {
		try {
			int value = Integer.parseInt(str);
			if(value < 1) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_PAGE_NEGATIVE_AMOUNT));
				return -1;
			}else if(value > commands.size()/5) {
				StringUtils.messages(player, language.get(ConfigSection.COMMAND_CHANCE_LIMIT)
						.replace("%max-page%", "" + commands.size()));
				return -1;
			}

			return value;
		} catch (Exception e) {
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_CHANCE_INVALID));
			return -1;
		}
	}

	private int parsePage(String str) {
		try {
			int value = Integer.parseInt(str);
			if(value < 1) {
				StringUtils.console(language.get(ConfigSection.COMMAND_PAGE_NEGATIVE_AMOUNT));
				return -1;
			}else if(value > commands.size()/5) {
				StringUtils.console(language.get(ConfigSection.COMMAND_CHANCE_LIMIT)
						.replace("%max-page%", "" + commands.size()));
				return -1;
			}

			return value;
		} catch (Exception e) {
			StringUtils.console(language.get(ConfigSection.COMMAND_CHANCE_INVALID));
			return -1;
		}
	}
}
