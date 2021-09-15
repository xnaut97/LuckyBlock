package net.tez.luckyblocks.utils.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

import net.tez.luckyblocks.LuckyBlocks;

public class UI implements InventoryHolder {
	private Inventory inv;
	
	private UIItemStack[] actions;

	private int[] slots = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
			25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44 };
	
	public UI(int size, String title) {
		this.inv = Bukkit.createInventory(this, size, title);
		this.setActions(new UIItemStack[size]);
	}

	public Inventory getInventory() {
		return this.inv;
	}

	public void setItem(int slot, UIItemStack item) {
		if (slot >= this.inv.getSize())
			return; 
		this.inv.setItem(slot, item.getItem());
		this.getActions()[slot] = item;
	}

	public String getTitle()
	{
		InventoryView invView = (InventoryView) inv.getViewers();

		return invView.getTitle();
	}


	public Inventory getPlayerInv()
	{
		InventoryView view = (InventoryView) inv.getViewers();
		return view.getBottomInventory();

	}

	public void onClick(InventoryClickEvent e) {
		UIItemStack i = this.getActions()[e.getSlot()];
		if (i != null)
			i.onClick(e); 
	}

	public void onOpen(InventoryOpenEvent e) {

	}

	public void onClose(InventoryCloseEvent e) {
		for (UIItemStack i : getActions()) {
			if(i == null) continue;
			i.onClose(e);
		}
	}

	public UIItemStack[] getActions() {
		return actions;
	}

	public void setActions(UIItemStack[] actions) {
		this.actions = actions;
	}
	
	public static void cancelUI(LuckyBlocks plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onClick(InventoryClickEvent e) {
				if (e.getCurrentItem() == null)
					return;
				Inventory inv = e.getClickedInventory();
				try {
					if (inv.getHolder() != null && inv.getHolder() instanceof UI)
						((UI) inv.getHolder()).onClick(e);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, plugin);

		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onClick(InventoryCloseEvent e) {
				Inventory inv = e.getInventory();
				try {
					if (inv.getHolder() != null && inv.getHolder() instanceof UI)
						((UI) inv.getHolder()).onClose(e);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, plugin);

		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onClick(InventoryOpenEvent e) {
				Inventory inv = e.getInventory();
				try {
					if (inv.getHolder() != null && inv.getHolder() instanceof UI)
						((UI) inv.getHolder()).onOpen(e);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, plugin);
	}

	public void openInvSync(Player p, UI ui) {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(ui.getInventory());
			}
		}.runTask(LuckyBlocks.instance());
	}
	
	public void closeInvSync(Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.closeInventory();
			}
		}.runTask(LuckyBlocks.instance());
	}
	
	public static void forcePlayerClose() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player == null)
				continue;
			if(player.getOpenInventory().getTopInventory().getHolder() instanceof UI) {
				player.closeInventory();
			}
		}
	}
	
	public boolean updateItems(int page, int size, int slots) {
		return page == 0 ? false : size <= page*slots;
	}
	
	public boolean canNextPage(int page, int size, int slots) {
		return (page+1)*slots >= size;
	}
	
	
	public int[] getSlots() {
		return slots;
	}
	
}
