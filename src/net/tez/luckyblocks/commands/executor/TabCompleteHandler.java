package net.tez.luckyblocks.commands.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabCompleteHandler implements TabCompleter{
	
	private List<AbstractCommand> commands;

	public TabCompleteHandler(CommandHandler handler) {
		this.commands = handler.getCommands();
	}

	private List<String> mainSuggestions(CommandSender sender, String starts) {
		
		return this.commands.stream()
				.filter(command -> command.getName().startsWith(starts)
						&& sender.hasPermission(command.getPermissions()))
				.map(command -> command.getName())
				.collect(Collectors.toList());
	}
	
	private List<String> getSubCommand(CommandSender sender, String[] args) {
		if(args.length == 0)
			return new ArrayList<>();
		if(args[0].isEmpty()) return new ArrayList<>();
		String starts = args[0].toLowerCase();
		Optional<AbstractCommand> opt = this.commands.stream()
				.filter(command -> starts.equalsIgnoreCase(command.getName())
						&& sender.hasPermission(command.getPermissions()))
				.findAny();
		return opt.isPresent() ? opt.get().tabComplete(sender, args) : null;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player))
			return null;
		if(args.length == 1)
			return this.mainSuggestions(sender, args[0]);
		else if(args.length > 1)
			return this.getSubCommand(sender, args);
		return null;
	}
	
}
