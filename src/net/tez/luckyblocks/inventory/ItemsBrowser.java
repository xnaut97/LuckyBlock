package net.tez.luckyblocks.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.constants.ConfigSection;
import net.tez.luckyblocks.constants.LuckyItems;
import net.tez.luckyblocks.manager.ItemManager;
import net.tez.luckyblocks.utils.StringUtils;
import net.tez.luckyblocks.utils.inventory.ItemBuilderUtils;
import net.tez.luckyblocks.utils.inventory.UI;
import net.tez.luckyblocks.utils.inventory.UIItemStack;
import net.tez.luckyblocks.utils.language.Language;
import net.tez.luckyblocks.utils.time.TimeUtils;
import net.tez.luckyblocks.utils.xseries.XMaterial;

public class ItemsBrowser extends UI {

	private int[] border = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 53, 52, 51, 50, 49, 48, 47, 46, 45, 36, 27, 18,
			9 },
			slots = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39,
					40, 41, 42, 43 };
	private ItemManager itemManager = LuckyBlocks.instance().getItemManager();

	private Language language = LuckyBlocks.instance().getCurrentLanguage();

	private int previous=48,info=49,next=50;
	
	public ItemsBrowser(Player player, int page) {
		super(54, "§lBROWSING ITEMS...");

		background();
		
		showItems(player, page);
	}

	private void showItems(Player player, int page) {
		new BukkitRunnable() {
			@Override
			public void run() {
				List<LuckyItems> items = sortItems();
				
				boolean toPreviousPage = updateItems(page, items.size(), slots.length);
				if(toPreviousPage) {
					openInvSync(player, new ItemsBrowser(player, page-1));
					return;
				}
				
				for (int i = page * slots.length; i < (page + 1) * slots.length; i++) {
					int index = page > 0 ? i - (45 * page) : i;
					if (i >= items.size()) {
						setItem(slots[index], new UIItemStack(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial(), " ") {
							@Override
							public void onClick(InventoryClickEvent e) {
								e.setCancelled(true);
							}
						});
						continue;
					}

					LuckyItems luckyItems = items.get(i);

					ItemStack toDisplay = luckyItems.getItem().clone();
					ItemMeta meta = toDisplay.getItemMeta();
					if (!meta.hasDisplayName()) {
						String nameColor = language.get(ConfigSection.ITEM_PREVIEW_NAME_COLOR);
						meta.setDisplayName(StringUtils.color(nameColor + StringUtils.formatItemName(toDisplay)));
					}
					List<String> lore = new ArrayList<>();
					lore.add(" ");
					if (player.hasPermission("luckyblock.item.modify")) {
						for (String str : language.getList(ConfigSection.ITEM_PREVIEW_LORE_ADMIN)) {
							str = str.replace("&", "§").replace("%registry-name%", luckyItems.getRegistryName())
									.replace("%roll-chance%", "" + luckyItems.getChance())
									.replace("%date-created%",
											TimeUtils.formatDate(luckyItems.getCreatedDate()))
									.replace("%item-creator%",
											Bukkit.getOfflinePlayer(luckyItems.getCreator()).getName());

							lore.add(str);
						}
					} else {
						for (String str : language.getList(ConfigSection.ITEM_PREVIEW_LORE_MEMBER)) {
							str = str.replace("&", "§").replace("%roll-chance%", "" + luckyItems.getChance());

							lore.add(str);
						}
					}
					meta.setLore(lore);
					toDisplay.setItemMeta(meta);

					setItem(slots[index], new UIItemStack(toDisplay) {
						@Override
						public void onClick(InventoryClickEvent e) {
							e.setCancelled(true);
						}
					});
				}
				
				previous(player, page);
				info(page);
				next(player, page);
			}
		}.runTaskTimerAsynchronously(LuckyBlocks.instance(), 1, 1);
	}

	private void previous(Player player, int page) {
		if(page > 0) {
			setItem(previous, new UIItemStack(new ItemBuilderUtils(XMaterial.PLAYER_HEAD.parseMaterial(),
					"§e<< Trang trước", "§7Lùi về trang " + page)
					.setTexture("9945491898496b136ffaf82ed398a54568289a331015a64c843a39c0cbf357f7")) {
				@Override
				public void onClick(InventoryClickEvent e) {
					e.setCancelled(true);
					openInvSync(player, new ItemsBrowser(player, page-1));
				}
			});
		}
	}

	private void info(int page) {
		setItem(info, new UIItemStack(new ItemBuilderUtils(Material.MAP, "§eTrang " + (page+1))) {
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
			}	
		});
	}

	private void next(Player player, int page) {
		boolean isMax = canNextPage(page, itemManager.getItems().size(), slots.length);
		if(!isMax) {
			setItem(next, new UIItemStack(new ItemBuilderUtils(XMaterial.PLAYER_HEAD.parseMaterial(), 
					"§eTrang kế >>", "§7Qua trang " + (page + 2))
					.setTexture("4ae29422db4047efdb9bac2cdae5a0719eb772fccc88a66d912320b343c341")) {
				@Override
				public void onClick(InventoryClickEvent e) {
					e.setCancelled(true);
					openInvSync(player, new ItemsBrowser(player, page+1));
				}
			});
		}
	}
	
	private void background() {
		for (int i = 0; i < 54; i++) {
			setItem(i, new UIItemStack(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()) {
				@Override
				public void onClick(InventoryClickEvent e) {
					e.setCancelled(true);
				}
			});
		}
		for (int i : border) {
			setItem(i, new UIItemStack(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) {

				@Override
				public void onClick(InventoryClickEvent e) {
					e.setCancelled(true);
				}
			});
		}
	}
	
	private List<LuckyItems> sortItems(){
		return this.itemManager.getItems().stream()
				.sorted(new Comparator<LuckyItems>() {
					@Override
					public int compare(LuckyItems o1, LuckyItems o2) {
						if(o2.getCreatedDate() > o1.getCreatedDate())
							return -1;
						else if(o1.getCreatedDate() > o2.getCreatedDate())
							return 1;
						return 0;
					}
				})
				.collect(Collectors.toList());
	}
}
