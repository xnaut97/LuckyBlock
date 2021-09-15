package net.tez.luckyblocks.manager;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import de.tr7zw.nbtapi.NBTItem;
import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.constants.LuckyItems;
import net.tez.luckyblocks.constants.LuckyItems.ModifyAction;
import net.tez.luckyblocks.constants.LuckyRequest;
import net.tez.luckyblocks.manager.RequestManager.RequestOperation;
import net.tez.luckyblocks.utils.ItemWrapper;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class ItemManager implements Listener{

	private Set<LuckyItems> items;

	private FileConfiguration config;

	private Language language;

	private LuckyBlocks plugin;

	public ItemManager(LuckyBlocks plugin) {
		this.items = new HashSet<>();
		this.plugin = plugin;
		this.config = plugin.getConfig();
		this.language = plugin.getCurrentLanguage();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void registerItem(LuckyItems item) {
		if(this.hasItems(item))
			return;
		this.items.add(item);
	}
	
	public void registerItems(LuckyItems...luckyItems) {
		if(luckyItems == null || luckyItems.length < 1)
			return;
		List<String> temp = Arrays.asList(luckyItems).stream()
				.map(item -> item.getRegistryName())
				.collect(Collectors.toList());

		Set<LuckyItems> filter = this.getItems().stream()
				.filter(item -> !temp.contains(item.getRegistryName()))
				.collect(Collectors.toSet());
		if(filter.size() < 1)
			return;

		this.getItems().addAll(filter);
	}
	
	public void unregisterItem(LuckyItems item) {
		Optional<LuckyItems> optItem = this.getItem(item.getRegistryName());
		if(!optItem.isPresent())
			return;
		this.items.remove(optItem.get());
	}
	
	public void unregisterItem(String registryName) {
		Optional<LuckyItems> optItem = this.getItem(registryName);
		if(!optItem.isPresent())
			return;
		this.items.remove(optItem.get());
	}
	
	public void unregisterItems(LuckyItems... luckyItems) {
		if(luckyItems == null || luckyItems.length < 1)
			return;
		List<String> temp = Arrays.asList(luckyItems).stream()
				.map(item -> item.getRegistryName())
				.collect(Collectors.toList());

		Set<LuckyItems> filter = this.getItems().stream()
				.filter(item -> temp.contains(item.getRegistryName()))
				.collect(Collectors.toSet());
		if(filter.size() < 1)
			return;

		this.getItems().removeAll(filter);
	}

	public void modifyItems(String registryName, ModifyAction action, double amount) {
		Optional<LuckyItems> optItem = this.getItem(registryName);
		if(!optItem.isPresent())
			throw new IllegalArgumentException("Could not find lucky item with registry name '" + registryName + "'");
		LuckyItems toUnregister = optItem.get();
		LuckyItems toRegister = optItem.get();
		toRegister.modifyChance(action, amount);
		this.unregisterItems(toUnregister);
		this.registerItems(toRegister);

	}

	public void updateItem(LuckyItems oldItem, LuckyItems newItem) {
		Optional<LuckyItems> optItem = this.getItem(oldItem.getRegistryName());
		if(!optItem.isPresent())
			return;
		this.unregisterItem(optItem.get());
		this.registerItem(newItem);
	}
	
	public Optional<LuckyItems> getItem(String registryName){
		return this.getItems().stream()
				.filter(item -> item.getRegistryName().equals(registryName))
				.findAny();
	}

	public Set<LuckyItems> getItems(String... names){
		return this.getItems().stream()
				.filter(item -> Arrays.asList(names).contains(item.getRegistryName()))
				.collect(Collectors.toSet());
	}

	public boolean hasItems(LuckyItems luckyItems) {
		return this.getItems().stream()
				.filter(item -> item.getRegistryName().equals(luckyItems.getRegistryName()))
				.findAny()
				.isPresent();
	}

	public boolean hasItems(String registryName) {
		return this.getItems().stream()
				.filter(item -> item.getRegistryName().equals(registryName))
				.findAny()
				.isPresent();
	}

	public Set<LuckyItems> getItems() {
		return items;
	}

	public void setItems(Set<LuckyItems> items) {
		this.items = items;
	}

	public void handlerItems(boolean handler) {
		try {
			File file = new File("plugins/LuckyBlock/items.yml");
			FileConfiguration config = null;
			//Loading stage
			if(handler) {
				if(!file.exists()) {
					return;
				}
				config = YamlConfiguration.loadConfiguration(file);
				
				if(config.getKeys(false).size() < 1 || config.getKeys(false) == null)
					return;

				Set<LuckyItems> temp = new HashSet<>(); 
				for(String registryName : config.getKeys(false)) {
					if(registryName == null)
						continue;
					UUID creator = UUID.fromString(config.getString(registryName + ".creator"));
					if(creator == null) {
						Bukkit.getLogger().severe("[LuckyBlocks] Could not load item with registry name '" + registryName 
								+ "' due to creator's UUID is empty/not exist");
						continue;
					}
					double percent = config.getDouble(registryName + ".percent",0);
					if(percent < 0)
						percent = 0;
					else if(percent > 100)
						percent = 100;
					long createdDate = config.getLong(registryName + ".createdDate");
					ItemStack item = ItemWrapper.fromBase64(config.getString(registryName + ".itemSerial"));
					LuckyItems luckyItems = LuckyItems.create(registryName, percent, item, createdDate, creator);
					temp.add(luckyItems);
				}

				if(temp.size() < 1)
					return;

				this.getItems().addAll(temp);
				Bukkit.getLogger().info("[LuckyBlocks] Loaded " + this.getItems().size() + " items from local database!");
				return;
			}
			//Check if file exist
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
			if(this.getItems().size() < 1)
				return;

			config = YamlConfiguration.loadConfiguration(file);
			//Saving stage
			for(LuckyItems item : this.getItems()) {
				if(item == null)
					continue;

				config.set(item.getRegistryName() + ".percent", item.getChance());
				config.set(item.getRegistryName() + ".createdDate", item.getCreatedDate());
				config.set(item.getRegistryName() + ".creator", item.getCreator().toString());
				config.set(item.getRegistryName() + ".itemSerial", item.getItemSerial());
				config.set(item.getRegistryName() + ".item", item.getItem());
			}
			config.save(file);
			Bukkit.getLogger().info("[LuckyBlocks] Saved " + this.getItems().size() + " items to local database!");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void openLuckyBlockEvent(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
		Block block = e.getClickedBlock();
		if(block == null || item == null)
			return;
		if(e.getAction().toString().contains("RIGHT_CLICK_")) {
			NBTItem nbti = new NBTItem(item);
			if(!nbti.hasKey("lucky-block"))
				return;

			if(RequestManager.instance().hasRequest(player.getUniqueId())) {
				e.setCancelled(true);
				StringUtils.messages(player, this.language.get(ConfigSection.LUCKY_BLOCK_PLACE_ALREADY_ROLLING));
				return;
			}

			if(this.getItems().size() < 1) {
				e.setCancelled(true);
				StringUtils.messages(player, this.language.get(ConfigSection.LUCKY_BLOCK_NO_REWARD));
				return;
			}else if(this.getItems().size() == 1) {
				String nameColor = language.get(ConfigSection.ITEM_PREVIEW_NAME_COLOR);
				LuckyItems reward = this.getItems().iterator().next();
				ItemStack rewardItem = reward.getItem().clone();
				ItemMeta rewardMeta = rewardItem.getItemMeta();
				String rewardName = StringUtils.color(rewardMeta.hasDisplayName() 
						? rewardMeta.getDisplayName()
								: nameColor + StringUtils.formatItemName(rewardItem));
				
				if(player.isOnline()) {
					StringUtils.messages(player, this.language.get(ConfigSection.LUCKY_BLOCK_ANNOUNCE_PLAYER)
							.replace("%reward-item%", StringUtils.formatItemName(rewardItem))
							.replace("%amount%", "" + rewardItem.getAmount()));
					if(player.getInventory().addItem(rewardItem).size() > 1) {
						player.getWorld().dropItem(player.getLocation(), rewardItem);
					}
				}else {
					player.getWorld().dropItem(player.getLocation(), reward.getItem());
				}
				
				boolean hasGlobalBroadcast = config.getBoolean("broadcastGlobal", true);
				if(hasGlobalBroadcast) {
					StringUtils.broadcastGlobal(player, language.get(ConfigSection.LUCKY_BLOCK_ANNOUNCE_GLOBAL)
							.replace("%player-name%", player.getName())
							.replace("%reward-item%", rewardName)
							.replace("%amount%", "" + rewardItem.getAmount()));
				}
				return;
			}
			
			Location upperBlock = block.getLocation().clone().add(0,1,0);
			
			long animationTime = config.getLong("animationTime", 10);
			long animationSpeed = (long) (10*config.getDouble("animationSpeed", 0.02D));
			if(animationSpeed < 1)
				animationSpeed = 1;
			long waitingTime = config.getLong("waitingTime",2L);
			if(waitingTime < 1)
				waitingTime = 1;
			LuckyRequest request = LuckyRequest.invoke(player.getUniqueId(), upperBlock, 
					animationTime, animationSpeed, waitingTime);


			RequestManager.instance().invokeRequest(request);

			upperBlock.getBlock().setMetadata("lucky-block", new FixedMetadataValue(plugin, true));
			StringUtils.messages(player, this.language.get(ConfigSection.LUCKY_BLOCK_PLACE_ACTIVATE));
			
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		if(block == null)
			return;
		if(!block.hasMetadata("lucky-block"))
			return;
		e.setCancelled(true);
		
		StringUtils.messages(player, this.language.get(ConfigSection.LUCKY_BLOCK_BREAK));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent e) {
		Block block = e.getBlockPlaced();
		if(block == null)
			return;
		Location blockLocation = block.getLocation();
		
		blockLocation.getWorld().getNearbyEntities(blockLocation, 1, 1, 1).stream()
		.filter(entity -> entity instanceof Item)
		.map(entity -> ((Item) entity))
		.forEach(item -> {
			if(item == null || item.isDead() || !item.hasMetadata("lucky-item-display"))
				return;
			e.setCancelled(true);
		});
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent e) {
		Iterator<Block> iterator = e.getBlocks().iterator();
		while(iterator.hasNext()) {
			Block block = iterator.next();
			if(block == null)
				continue;
			
			Optional<LuckyRequest> optRequest = RequestManager.instance()
					.getRequest(RequestOperation.BLOCK, block.getLocation());
			if(!optRequest.isPresent())
				continue;
			
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPistonRetract(BlockPistonRetractEvent e) {
		Iterator<Block> iterator = e.getBlocks().iterator();
		while(iterator.hasNext()) {
			Block block = iterator.next();
			if(block == null)
				continue;
			
			Optional<LuckyRequest> optRequest = RequestManager.instance()
					.getRequest(RequestOperation.BLOCK, block.getLocation());
			if(!optRequest.isPresent())
				continue;
			
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockExplode(EntityExplodeEvent e) {
		Iterator<Block> iterator = e.blockList().iterator();
		while(iterator.hasNext()) {
			Block block = iterator.next();
			if(block == null)
				continue;
			
			Optional<LuckyRequest> optRequest = RequestManager.instance()
					.getRequest(RequestOperation.BLOCK, block.getLocation());
			if(!optRequest.isPresent())
				continue;
			
			iterator.remove();
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent e) {
		Block block = e.getBlock();
		if(block == null)
			return;
		Optional<LuckyRequest> optRequest = RequestManager.instance()
				.getRequest(RequestOperation.BLOCK, block.getLocation());
		if(!optRequest.isPresent())
			return;
		
		e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onItemDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		if(entity instanceof Item) {
			Item item = (Item) entity;
			if(!item.hasMetadata("lucky-item-display"))
				return;
			e.setCancelled(true);
		}
	}

	public void clearItems() {
		this.items.clear();
	}
}