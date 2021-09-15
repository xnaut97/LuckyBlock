package net.tez.luckyblocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.tez.luckyblocks.commands.executor.CommandHandler;
import net.tez.luckyblocks.manager.ItemManager;
import net.tez.luckyblocks.manager.RequestManager;
import net.tez.luckyblocks.utils.inventory.UI;
import net.tez.luckyblocks.utils.language.Language;
import net.tez.luckyblocks.utils.language.LanguageManager;

public class LuckyBlocks extends JavaPlugin{

	private LanguageManager languageManager;

	private ItemManager itemManager;

	private RequestManager requestManager;

	private static LuckyBlocks instance;

	@Override
	public void onEnable() {
		if(Bukkit.getPluginManager().getPlugin("NBTAPI") == null) {
			Bukkit.getLogger().severe("[LuckyBlock] Require dependency plugin 'NBTAPI' to use this plugin!");
			Bukkit.getLogger().severe("[LuckyBlock] Please install plugin 'NBTAPI' then load again.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		else {
			Bukkit.getLogger().info("[LuckyBlock] §2Found §6NBTAPI §2and hooked");
		}
		if(Bukkit.getPluginManager().getPlugin("ClearLag") == null) {
			Bukkit.getLogger().warning("[LuckyBlock] Require soft dependency plugin ClearLag to work functionally");
			Bukkit.getLogger().warning("[LuckyBlock] Reload LuckyBlock plugin after you've installed ClearLag");
			Bukkit.getLogger().warning("[LuckyBlock] to make two plugins are synchronized with each other");
		}else
		{
			Bukkit.getLogger().info("[LuckyBlock] §2Found §6ClearLag §2and hooked");
		}
		enableMessages();
		instance = this;
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		UI.cancelUI(this);
		this.languageManager = new LanguageManager(this);
		this.itemManager = new ItemManager(this);
		this.requestManager = new RequestManager(this);
		this.itemManager.handlerItems(true);
		new CommandHandler(this);
		
	}

	@Override
	public void onDisable() {
		UI.forcePlayerClose();
		this.requestManager.revokeOnDisable();
		if(this.itemManager != null) {
			this.itemManager.handlerItems(false);
		}
	}
	
	public void saveResource(@Nonnull String resourcePath, boolean replace) {
		if (resourcePath == null || resourcePath.equals(""))
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);
		if (in == null)
			throw new IllegalArgumentException(
					"The embedded resource '" + resourcePath + "' cannot be found in " + this.getFile());
		File outFile = new File(this.getDataFolder(), resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(this.getDataFolder(), resourcePath.substring(0, (lastIndex >= 0) ? lastIndex : 0));
		if (!outDir.exists())
			outDir.mkdirs();
		try {
			if (!outFile.exists() || replace) {
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
					out.write(buf, 0, len);
				out.close();
				in.close();
			} else {
			}
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	public static LuckyBlocks instance() {
		return instance;
	}


	public LanguageManager getLanguageManager() {
		return languageManager;
	}

	public Language getCurrentLanguage() {
		return this.languageManager.getCurrentLanguage();
	}

	public ItemManager getItemManager() {
		return itemManager;
	}
	
	public RequestManager getRequestManager() {
		return requestManager;
	}
	
	private void enableMessages() {
		List<String> list = Arrays.asList(
				"§2----------------------------------",
				"§b         _",
				"    §3|   §b|_)    §7Version: §6" + getDescription().getVersion(),
				"    §3|__ §b|_)    §7Author: §6TezVN",
				" ",
				"§2----------------------------------");
		list.forEach(str -> {
			Bukkit.getLogger().info(str);
		});
	}
}
