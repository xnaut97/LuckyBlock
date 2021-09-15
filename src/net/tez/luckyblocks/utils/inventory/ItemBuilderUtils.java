package net.tez.luckyblocks.utils.inventory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class ItemBuilderUtils extends ItemStack {

	private Material mat;
	private String name;
	private String[] lore;

	public ItemBuilderUtils(Material mat, String name, String... lore) {
		super(mat);
		this.mat = mat;
		this.name = name;
		this.lore = lore;
		ItemMeta mt = getItemMeta();
		mt.setDisplayName(name);
		mt.setLore(Arrays.asList(lore));
		setItemMeta(mt);
	}
	
	public ItemBuilderUtils(ItemStack item, String name) {
		super(item);
		ItemMeta mt = getItemMeta();
		mt.setDisplayName(name);
		setItemMeta(mt);
	}
	
	public ItemStack setTexture(String texture) {
		String base64 =  Base64.getEncoder().encodeToString(
				("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + texture + "\"}}}")
				.getBytes(StandardCharsets.UTF_8));
		ItemStack item = CSkull.itemFromBase64(base64);
		ItemMeta mt = item.getItemMeta();
		mt.setDisplayName(name);
		mt.setLore(Arrays.asList(lore));
		item.setItemMeta(mt);
		return item;
	}
	
	public ItemStack setPotionColor(Color color) {
		ItemStack item = new ItemStack(mat);
		PotionMeta mt = (PotionMeta) item.getItemMeta();
		mt.setDisplayName(name);
		mt.setLore(Arrays.asList(lore));
		item.setItemMeta(mt);
		return item;
	}

}
