package net.tez.luckyblocks.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.constants.LuckyItems;
import net.tez.luckyblocks.constants.LuckyRequest;
import net.tez.luckyblocks.manager.RequestManager;
import net.tez.luckyblocks.utils.LootMap;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.language.Language;

public class LuckyTask extends BukkitRunnable{

	private Player activator;

	private Location location;

	private long animationTime;

	private long animationSpeed;

	private Location itemLocation;

	private Item item;

	private List<LuckyItems> displayItems;

	private int count = 0;

	private Random rand = ThreadLocalRandom.current();

	private LuckyItems reward;

	private Language language = LuckyBlocks.instance().getCurrentLanguage();

	private long waitingTime;

	private FileConfiguration config = LuckyBlocks.instance().getConfig();

	public LuckyTask(LuckyRequest request) {
		this.activator = Bukkit.getPlayer(request.getActivator());
		this.location = request.getLocation();
		this.itemLocation = request.getLocation().clone().add(0.5,1.2,0.5);
		this.animationTime = 20*request.getAnimationTime();
		this.animationSpeed = request.getAnimationSpeed();
		this.waitingTime = request.getWaitingTime();

		//Get the reward
		Optional<LuckyItems> optReward = this.getReward();
		if(optReward.isPresent()) {
			this.reward = optReward.get();
		}
		//Filter item that is not reward
		this.displayItems = this.getDisplayItems();
		this.item = this.generateItem(this.itemLocation, this.displayItems.get(0).getItem());
		this.runTaskTimerAsynchronously(LuckyBlocks.instance(), request.getAnimationSpeed(), request.getAnimationSpeed());
	}

	@Override
	public void run() {
		String nameColor = language.get(ConfigSection.ITEM_PREVIEW_NAME_COLOR);

		LuckyItems toDisplay = displayItems.get(rand.nextInt(displayItems.size()));

		ItemStack itemDisplay = toDisplay.getItem().clone();
		ItemMeta displayMeta = itemDisplay.getItemMeta();

		String displayName = StringUtils.color(displayMeta.hasDisplayName() 
				? displayMeta.getDisplayName()
						: nameColor + StringUtils.formatItemName(itemDisplay));
		item.setItemStack(toDisplay.getItem());
		item.setCustomName(displayName);

		if(count >= animationTime) {
			this.cancel();
			RequestManager.instance().revokeRequest(activator.getUniqueId());
			if(reward == null) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if(activator.isOnline()) {
							StringUtils.messages(activator, language.get(ConfigSection.LUCKY_BLOCK_ANNOUNCE_UNLUCKY));
						}
						item.remove();
						location.getBlock().setType(Material.AIR);
						this.cancel();
						RequestManager.instance().revokeRequest(activator.getUniqueId());
					}
				}.runTask(LuckyBlocks.instance());
				return;
			}

			ItemStack rewardItem = reward.getItem().clone();
			ItemMeta rewardMeta = rewardItem.getItemMeta();
			String rewardName = StringUtils.color(rewardMeta.hasDisplayName() 
					? rewardMeta.getDisplayName()
							: nameColor + StringUtils.formatItemName(rewardItem));
			item.setItemStack(rewardItem);
			item.setCustomName(rewardName);

			new BukkitRunnable() {
				@Override
				public void run() {
					//Give item to player
					if(activator.isOnline()) {
						StringUtils.messages(activator, language.get(ConfigSection.LUCKY_BLOCK_ANNOUNCE_PLAYER)
								.replace("%reward-item%", StringUtils.formatItemName(rewardItem))
								.replace("%amount%", "" + rewardItem.getAmount()));
						if(activator.getInventory().addItem(rewardItem).size() > 1) {
							activator.getWorld().dropItem(activator.getLocation(), rewardItem);
						}
					}else {
						location.getWorld().dropItem(location, reward.getItem());
					}

					item.remove();
					location.getBlock().setType(Material.AIR);

					boolean hasGlobalBroadcast = config.getBoolean("broadcastGlobal", true);
					if(hasGlobalBroadcast) {
						StringUtils.broadcastGlobal(activator, language.get(ConfigSection.LUCKY_BLOCK_ANNOUNCE_GLOBAL)
								.replace("%player-name%", activator.getName())
								.replace("%reward-item%", rewardName)
								.replace("%amount%", "" + rewardItem.getAmount()));
					}
				}
			}.runTaskLater(LuckyBlocks.instance(), 20*waitingTime);
			return;
		}
		count+=animationSpeed;
	}

	public Location getItemLocation() {
		return itemLocation;
	}

	private Optional<LuckyItems> getReward() {
		LootMap<Double, LuckyItems> lootMap = new LootMap<>();
		for(LuckyItems i : LuckyBlocks.instance().getItemManager().getItems()) {
			lootMap.put(i.getChance(), i);
		}
		double random = rand.nextDouble()*100;  // 0 -> 1
		LuckyItems item = lootMap.get(lootMap.ceilingKey(random));
		if(item == null)
			return Optional.empty();
		return Optional.ofNullable(item);
	}

	private Item generateItem(Location location, ItemStack dropItem) {
		Item item = location.getWorld().dropItem(location, dropItem);
		item.setCustomNameVisible(true);
		item.setPickupDelay(Integer.MAX_VALUE);
		item.setMetadata("lucky-item-display", new FixedMetadataValue(LuckyBlocks.instance(), true));
		item.setVelocity(new Vector(0,0,0));
		return item;
	}

	private List<LuckyItems> getDisplayItems(){
		return new ArrayList<>(LuckyBlocks.instance().getItemManager().getItems());
	}

	public Location getLocation() {
		return location;
	}

	public Item getItem() {
		return item;
	}

	public Player getActivator() {
		return activator;
	}

	public LuckyItems getItems() {
		return this.reward;
	}
}
