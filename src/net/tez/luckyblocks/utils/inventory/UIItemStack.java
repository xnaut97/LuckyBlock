package net.tez.luckyblocks.utils.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UIItemStack {
  private ItemStack item;
  private ClickType click;
  
  public UIItemStack(Material material, int amount, short data) {
    this.item = new ItemStack(material, amount, data);
  }
  
  public UIItemStack(Material material, int amount) {
    this.item = new ItemStack(material, amount);
  }
  
  public UIItemStack(Material material) {
    this.item = new ItemStack(material);
  }
  
  public UIItemStack(Material material, String name) {
    this.item = new ItemStack(material);
    ItemMeta im = this.item.getItemMeta();
    im.setDisplayName(name);
    this.item.setItemMeta(im);
  }
  
  public UIItemStack(ItemStack item) {
    this.item = item;
  }
  
  public void onClick(InventoryClickEvent e) {}
  
  public void onClose(InventoryCloseEvent e) {}
  
//  public void onDrag(InventoryDragEvent e) {}
  
  public ItemStack getItem() {
    return this.item;
  }
  public ClickType getClick()
  {
	  return this.click;
  }

}
