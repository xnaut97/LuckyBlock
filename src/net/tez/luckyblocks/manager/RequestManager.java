package net.tez.luckyblocks.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.minebuilders.clearlag.events.EntityRemoveEvent;
import net.tez.luckyblocks.LuckyBlocks;
import net.tez.luckyblocks.constants.LuckyItems;
import net.tez.luckyblocks.constants.LuckyRequest;
import net.tez.luckyblocks.threads.LuckyTask;
import net.tez.luckyblocks.utils.location.LocationUtils;

public class RequestManager implements Listener {

	private Set<LuckyRequest> requests;

	private static RequestManager instance;

	public RequestManager(LuckyBlocks plugin) {
		instance = this;
		this.requests = new HashSet<>();

		if(Bukkit.getPluginManager().getPlugin("ClearLag") != null) {
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
	}

	public void invokeRequest(LuckyRequest request) {
		Optional<LuckyRequest> optRequest = this.getRequest(request.getActivator());
		if(optRequest.isPresent())
			return;
		this.requests.add(request);
	}

	public void revokeRequest(UUID uuid) {
		Optional<LuckyRequest> optRequest = this.getRequest(uuid);
		if(!optRequest.isPresent())
			return;
		this.requests.remove(optRequest.get());
	}

	public boolean hasRequest(UUID uuid){
		return this.requests.stream()
				.filter(request -> request.getActivator().equals(uuid))
				.findAny()
				.isPresent();
	}

	public Optional<LuckyRequest> getRequest(UUID uuid){
		return this.requests.stream()
				.filter(request -> request.getActivator().equals(uuid))
				.findAny();
	}

	public Optional<LuckyRequest> getRequest(RequestOperation operation, Location location){
		Optional<LuckyRequest> request = null;
		switch(operation) {
		case BLOCK:
			request = this.requests.stream()
			.filter(r -> LocationUtils.compareExact(location, r.getLocation().clone()))
			.findAny();
			break;
		case ITEM:
			this.requests.stream()
			.filter(r -> LocationUtils.compareExact(location, r.getTask().getLocation().clone().add(0,1,0)))
			.findAny();
			break;
		}
		return request == null ? Optional.empty() : request;
	}

	@EventHandler
	public void clearItemEvent(EntityRemoveEvent e) {
		List<Entity> filterEntities = e.getEntityList().stream()
				.filter(entity -> entity != null && entity.hasMetadata("lucky-item-display"))
				.collect(Collectors.toList());
		if(filterEntities.size() < 1)
			return;
		for(Entity entity : filterEntities) {
			if(entity == null || entity.isDead())	
				continue;
			e.removeEntity(entity);
		}
	}

	public void revokeOnDisable() {
		if(this.requests.size() < 1) {
			for(LuckyRequest request : this.requests) {
				if(request == null)
					continue;
				LuckyTask task = request.getTask();
				Player player = task.getActivator();
				LuckyItems reward = task.getItems();
				task.cancel();
				task.getItem().remove();
				task.getLocation().getBlock().setType(Material.AIR);
				
				if(reward == null)
					continue;
				ItemStack item = reward.getItem().clone();
				if(player.isOnline()) {
					if(player.getInventory().addItem(item).size() > 1) {
						player.getWorld().dropItem(player.getLocation(), item);
					}
				}else {
					task.getItemLocation().getWorld().dropItem(task.getItemLocation(), item);
				}
			}
			this.requests.clear();
		}
	}

	public Set<LuckyRequest> getRequests() {
		return requests;
	}

	public static RequestManager instance() {
		return instance;
	}

	public enum RequestOperation {
		BLOCK,ITEM;
	}
}
