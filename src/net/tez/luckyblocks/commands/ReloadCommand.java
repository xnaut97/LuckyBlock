package net.tez.luckyblocks.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.commands.executor.AbstractCommand;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.utils.PluginUtils;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class ReloadCommand extends AbstractCommand{

	private LuckyBlocks plugin;
	
	private Language language;

	public ReloadCommand(LuckyBlocks plugin) {
		super(plugin);
		this.plugin = plugin;
		this.language = plugin.getCurrentLanguage();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "reload";
	}

	@Override
	public String getPermissions() {
		// TODO Auto-generated method stub
		return "luckyblock.command.reload";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Reload the whole plugin.";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "&7» Syntax: &6/lb reload";
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
			PluginUtils.reload(this.plugin);
			
			StringUtils.messages(player, language.get(ConfigSection.COMMAND_RELOAD));
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
		if(args.length > 1) {
			StringUtils.console(this.getUsage());
		}else if(args.length == 1) {
			PluginUtils.reload(this.plugin);
			
			StringUtils.console(language.get(ConfigSection.COMMAND_RELOAD));
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
