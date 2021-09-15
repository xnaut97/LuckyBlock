package net.tez.luckyblocks.commands.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.commands.AddCommand;
import net.tez.luckyblocks.commands.ClearCommand;
import net.tez.luckyblocks.commands.GiveCommand;
import net.tez.luckyblocks.commands.GiveItemCommand;
import net.tez.luckyblocks.commands.HelpCommand;
import net.tez.luckyblocks.commands.ItemsCommand;
import net.tez.luckyblocks.commands.ModifyCommand;
import net.tez.luckyblocks.commands.ReloadCommand;
import net.tez.luckyblocks.commands.RemoveCommand;
import net.tez.luckyblocks.commands.RenameCommand;
import net.tez.luckyblocks.commands.SetItemCommand;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class CommandHandler implements CommandExecutor{

	private List<AbstractCommand> commands = new ArrayList<>();

	private Language language;

	public CommandHandler(LuckyBlocks plugin) {
		this.language = plugin.getCurrentLanguage();
		this.registerMainCommand(plugin);
		this.registerSubCommands(plugin);
	}

	private void registerMainCommand(LuckyBlocks plugin) {
		plugin.getCommand("luckyblock").setAliases(Arrays.asList("lb"));
		plugin.getCommand("luckyblock").setExecutor(this);
		plugin.getCommand("luckyblock").setTabCompleter(new TabCompleteHandler(this));
	}

	private void registerSubCommands(LuckyBlocks plugin) {
		this.register(new AddCommand(plugin));
		this.register(new ClearCommand(plugin));
		this.register(new GiveCommand(plugin));
		this.register(new GiveItemCommand(plugin));
		this.register(new HelpCommand(plugin, this));
		this.register(new ItemsCommand(plugin));
		this.register(new ModifyCommand(plugin));
		this.register(new ReloadCommand(plugin));
		this.register(new RemoveCommand(plugin));
		this.register(new RenameCommand(plugin));
		this.register(new SetItemCommand(plugin));
		this.register(new GiveCommand(plugin));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Iterator<AbstractCommand> iterator = commands.iterator();
		//Console
		if(!(sender instanceof Player)) {
			if(args.length > 0) {
				do {
					AbstractCommand commands = iterator.next();

					if(!args[0].equalsIgnoreCase(commands.getName()))
						continue;
					if(!commands.allowConsole()) {
						StringUtils.console(this.language.get("command.consoleNotAllowed",
								"&cThis command can be executed by player only!"));
						return true;
					}
					commands.consoleExecute(sender, args);
					return true;

				}while(iterator.hasNext());
			}
			return true;
		}
		//Player
		Player player = (Player) sender;

		if(args.length > 0) {
			boolean validArgument = this.getCommands().stream()
					.anyMatch(command -> command.getName().equalsIgnoreCase(args[0]));
			if(!validArgument) {
				StringUtils.messages(player, language.get("command.invalid", 
						"&cWrong command, Please use &6/lb help &cfor available commands!"));
				return true;
			}
			
			do {
				AbstractCommand commands = iterator.next();

				if(!args[0].equalsIgnoreCase(commands.getName()))
					continue;
				
				if(!player.hasPermission(commands.getPermissions())) {
					StringUtils.messages(player, language.get("command.noPermission", 
							"&cYou don't have permissions to use this command!"));
					return true;
				}
				
				commands.execute(sender, args);
				return true;
				
			} while (iterator.hasNext());
		}
		return true;
	}

	public List<AbstractCommand> getCommands() {
		return commands;
	}

	private void register(AbstractCommand cmd) {
		this.commands.add(cmd);
	}
}
