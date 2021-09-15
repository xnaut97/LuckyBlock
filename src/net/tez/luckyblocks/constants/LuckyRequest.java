package net.tez.luckyblocks.constants;

import java.util.UUID;

import org.bukkit.Location;

import net.tez.luckyblocks.manager.RequestManager;
import net.tez.luckyblocks.threads.LuckyTask;

public class LuckyRequest {
	
	private UUID activator;
	
	private Location location;
	
	private long animationTime;
	
	private long animationSpeed;
	
	private long waitingTime;
	
	private LuckyTask task;
	
	private LuckyRequest(UUID activator, Location location, long animationTime, long animationSpeed, long waitingTime) {
		this.activator = activator;
		this.location = location;
		this.animationTime = animationTime;
		this.animationSpeed = animationSpeed;
		this.waitingTime = waitingTime;
		this.task = new LuckyTask(this);
		RequestManager.instance().invokeRequest(this);
	}
	
	public static LuckyRequest invoke(UUID activator, Location location, long animationTime, long animationSpeed, long waitingTime) {
		return new LuckyRequest(activator, location, animationTime, animationSpeed, waitingTime);
	}
	
	public UUID getActivator() {
		return activator;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public long getAnimationTime() {
		return animationTime;
	}
	
	public long getAnimationSpeed() {
		return animationSpeed;
	}
	
	public long getWaitingTime() {
		return waitingTime;
	}
	
	public LuckyTask getTask() {
		return task;
	}
}
