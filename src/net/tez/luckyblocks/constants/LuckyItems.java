package net.tez.luckyblocks.constants;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import net.tez.luckyblocks.utils.ItemWrapper;
import net.tez.luckyblocks.utils.MathUtils;

public class LuckyItems {
	
	private String registryName;
	
	private double chance;
	
	private String itemSerial;

	private long createdDate;
	
	private UUID creator;
	
	private LuckyItems(String registryName, double chance, ItemStack item, long createdDate, UUID creator) {
		this.registryName = registryName;
		this.chance = chance;
		this.itemSerial = ItemWrapper.toBase64(item);
		this.createdDate = createdDate;
		this.creator = creator;
	}
	
	public static LuckyItems create(String registryName, double chance, ItemStack item, long createdDate, UUID creator) {
		return new LuckyItems(registryName, chance, item, createdDate, creator);
	}

	public static LuckyItems copy(LuckyItems item) {
		return new LuckyItems(item.getRegistryName(), item.getChance(), item.getItem(), item.getCreatedDate(), item.getCreator());
	}
	
	public String getRegistryName() {
		return registryName;
	}

	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}
	
	public void modifyChance(ModifyAction action, double amount) {
		double value = this.chance;
		switch(action) {
		case ADD:
			value += amount;
			break;
		case DIVIDE:
			value -= amount;
			break;
		case MULTIPLY:
			value *= amount;
			break;
		case SUBTRACT:
			value /= amount;
			break;
		}
		if(value > 100)
			value = 100D;
		else if(value < 0)
			value = 0;
		this.setChance(MathUtils.roundDouble(value, 1));
	}
	
	public String getItemSerial() {
		return itemSerial;
	}

	public ItemStack getItem() {
		return ItemWrapper.fromBase64(this.itemSerial);
	}
	
//	public String getName() {
//		String name = "";
//		ItemMeta meta = this.getItem().getItemMeta();
//		if(meta.hasDisplayName())
//			name = meta.getDisplayName();
//		else
//			name = this.getItem().getType().toString();
//		return name;
//			
//	}
	
	public void setItemSerial(String itemSerial) {
		this.itemSerial = itemSerial;
	}
	
	public void setItem(ItemStack item) {
		this.itemSerial = ItemWrapper.toBase64(item);
	}
	
	public long getCreatedDate() {
		return createdDate;
	}
	
	public UUID getCreator() {
		return creator;
	}
	
	public enum ModifyAction {
		ADD,SUBTRACT,MULTIPLY,DIVIDE;
	}
}
