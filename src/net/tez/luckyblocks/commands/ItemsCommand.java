package net.tez.luckyblocks.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.commands.executor.AbstractCommand;
import net.tez.luckyblocks.inventory.ItemsBrowser;
import net.tez.luckyblocks.utils.StringUtils;

public class ItemsCommand extends AbstractCommand{

	public ItemsCommand(LuckyBlocks plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "items";
	}

	@Override
	public String getPermissions() {
		return "luckyblock.command.items";
	}

	@Override
	public String getDescription() {
		return "Shows available items on lucky block";
	}

	@Override
	public String getUsage() {
		return "&7» Syntax: /lb items <page>";
	}

	@Override
	public boolean allowConsole() {
		return false;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if(args.length > 1) {
			StringUtils.messages(player, this.getUsage());
			return;
		}else if(args.length == 1) {

			player.openInventory(new ItemsBrowser(player, 0).getInventory());
		}
	}

	@Override
	public void consoleExecute(CommandSender sender, String[] args) {
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}

}
