package net.tez.luckyblocks.utils.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LocationUtils {

	public static String toString(Location loc) {
		String world = loc.getWorld().getName();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		return world + "," + x + "," + y + "," + z;
	}

	public static String toStringInDouble(Location loc) {
		String world = loc.getWorld().getName();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		return world + "," + x + "," + y + "," + z;
	}

	public static Location parseLoc(String string) {
		String[] args = string.split(",");
		String world = args[0];
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int z = Integer.parseInt(args[3]);
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

//	public static BlockFace getBlockFace(Player player) {
//		List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 36);
//		if (lastTwoTargetBlocks.size() != 2) 
//			return null;
//		Block targetBlock = lastTwoTargetBlocks.get(1);
//		Block adjacentBlock = lastTwoTargetBlocks.get(0);
//		return  targetBlock.getFace(adjacentBlock);
//	}

	@Deprecated
	public static boolean compare(Location loc1, Location loc2) {
		String w1 = loc1.getWorld().getName();
		String w2 = loc2.getWorld().getName();

		int x1 = loc1.getBlockX();
		int y1 = loc1.getBlockY();
		int z1 = loc1.getBlockZ();

		int x2 = loc2.getBlockX();
		int y2 = loc2.getBlockY();
		int z2 = loc2.getBlockZ();

		if (w1.equals(w2) && x1 == x2 && y1 == y2 && z1 == z2)
			return true;
		return false;
	}

	public static boolean compareExact(Location loc1, Location loc2) {
		String w1 = loc1.getWorld().getName();
		String w2 = loc2.getWorld().getName();

		int x1 = loc1.getBlockX();
		int y1 = loc1.getBlockY();
		int z1 = loc1.getBlockZ();

		int x2 = loc2.getBlockX();
		int y2 = loc2.getBlockY();
		int z2 = loc2.getBlockZ();

		if (w1.equals(w2) && x1 == x2 && y1 == y2 && z1 == z2)
			return true;
		return false;
	}
	
	public static boolean compareIgnoreY(Location loc1, Location loc2) {
		String w1 = loc1.getWorld().getName();
		String w2 = loc2.getWorld().getName();

		int x1 = loc1.getBlockX();
		int z1 = loc1.getBlockZ();

		int x2 = loc2.getBlockX();
		int z2 = loc2.getBlockZ();

		if (w1.equals(w2) && x1 == x2 && z1 == z2)
			return true;
		return false;
	}

	public static boolean compareHeightEquals(Location loc1, Location loc2, int height) {
		String w1 = loc1.getWorld().getName();
		String w2 = loc2.getWorld().getName();

		int x1 = loc1.getBlockX();
		int y1 = loc1.getBlockY();
		int z1 = loc1.getBlockZ();

		int x2 = loc2.getBlockX();
		int y2 = loc2.getBlockY();
		int z2 = loc2.getBlockZ();

		if (w1.equals(w2) && x1 == x2 && z1 == z2 && (y2 - y1) == height)
			return true;
		return false;
	}

//	public static void playCircleEffect(Location loc, double radius, double y) {
//		for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 36) {
//			final double x = radius * Math.cos(angle);
//			final double z = radius * Math.sin(angle);
//
//			loc.add(x, y, z);
//			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 1,
//					new DustOptions(Color.GREEN, 1));
//			loc.subtract(x, y, z);
//		}
//	}

	public static Location getSurface(Location loc) {
		for (int i = loc.getBlockY(); i >= 0; i -= 1) {
			Location location = new Location(loc.getWorld(), loc.getX(), i, loc.getZ());
			Block block = location.getBlock();
			if (block == null || block.getType() == Material.AIR || !block.getType().isSolid()
					|| block.getType().isEdible())
				continue;
			return new Location(loc.getWorld(), loc.getX(), i + 1, loc.getZ());
		}
		return loc;
	}

//	public static void createHelix(Location loc, double r, double h) {
//		//		loc.add(loc.getX() > 0 ? 0.5 : -0.5, 0, loc.getZ() > 0 ? 0.5 : -0.5);
//		for(double y = 0; y <= h; y+=h/1000) {
//			r-=r/1000;
//			double x = loc.getX() + (r * Math.cos(y));
//			double z = loc.getZ() + (r * Math.sin(y));
//			net.minecraft.server.v1_14_R1.ParticleParam param = CraftParticle.toNMS(Particle.REDSTONE, new DustOptions(Color.GREEN, 0.5f));
//			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(param,true, (float) (loc.getX() + x), (float) (loc.getY() + y), (float) (loc.getZ() + z), 0, 0, 0, 0,1);
//			for(Player online : Bukkit.getOnlinePlayers()) {
//				((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);
//			}
//		}
//
//	}	

//	public static void animatedHelix(Location loc, double radius) {
//		new BukkitRunnable() {
//			double phi = 0;
//			double time = 0;
//			public void run() {
//				phi = phi + Math.PI / 8;
//				double x, y, z;
//				Location location1 = loc.clone().add(0.5,0,0.5);
//				time+=0.1;
//				if(time > 1.5) {
//					this.cancel();
//					return;
//				}
//				for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
//					for (double i = 0; i <= 1; i = i + 1) {
//						x = 0.4 * (2 * Math.PI - t) * 0.5 * Math.cos(t + phi + i * Math.PI);
//						y = 0.5 * t;
//						z = 0.4 * (2 * Math.PI - t) * 0.5 * Math.sin(t + phi + i * Math.PI);
//						location1.add(x, y, z);
//						send(location1);
//						location1.subtract(x, y, z);
//
//						//						if (phi > 10 * Math.PI) {
//						//							this.cancel();
//						//						}
//
//					}
//				}
//			}
//		}.runTaskTimerAsynchronously(EmpireX.getInstance(), 1, 1);
//	}

//	public static void createGreenHelix(Location loc)
//	{
//		loc.add(0.5,0,0.5);
//		new BukkitRunnable() {
//			double phi = 0;
//			@Override
//			public void run() {
//				phi += Math.PI/16;
//				double x; double y; double z;
//				for(double t = 0; t<= 1.75*Math.PI; t += Math.PI/16)
//				{
//					for(double i = 0; i< 1; i+=1)
//					{
//						x = Math.cos(t + phi + i*Math.PI);
//						y = 0.5*t;
//						z = Math.sin(t + phi + i*Math.PI);
//						loc.add(x,y,z);
//						send(loc);
//						loc.subtract(x,y,z);
//					}
//				}
//				if(phi > 100) {
//					this.cancel();
//				}
//			}
//		}.runTaskTimerAsynchronously(EmpireX.getInstance(), 0, 1);
	// new BukkitRunnable() {
	// double phi = 0;
	// @Override
	// public void run() {
	// phi += Math.PI/16;
	// double x; double y; double z;
	// for(double t = 0; t<= 1.75*Math.PI; t += Math.PI/16)
	// {
	// for(double i = 0; i< 1; i+=1)
	// {
	// x = Math.cos(t + phi + Math.PI);
	// y = 0.5*t;
	// z = Math.sin(t + phi + Math.PI);
	// loc.add(x,y,z);
	// send(loc);
	// loc.subtract(x,y,z);
	// }
	// }
	// }
	// }.runTaskTimerAsynchronously(EmpireX.getInstance(), 0, 1);
//	}

//	private static void send(Location loc) {
//		ParticleParam param = CraftParticle.toNMS(Particle.REDSTONE, new DustOptions(Color.GREEN, 0.5f));
//		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(param,true, (float) (loc.getX()), (float) (loc.getY()), (float) (loc.getZ()), 0, 0, 0, 0,1);
//		for(Player online : Bukkit.getOnlinePlayers()) {
//			((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);
//		}
//	}

	public static EmpireDirection getCardinalDirection(Player player) {
		double rotation = (player.getLocation().getYaw() - 180) % 360.0F;
		if (rotation < 0.0D) {
			rotation += 360.0D;
		}
		if ((0.0D <= rotation) && (rotation < 22.5D)) {
			return EmpireDirection.NORTH;
		}
		if ((22.5D <= rotation) && (rotation < 67.5D)) {
			return EmpireDirection.NORTH_EAST;
		}
		if ((67.5D <= rotation) && (rotation < 112.5D)) {
			return EmpireDirection.EAST;
		}
		if ((112.5D <= rotation) && (rotation < 157.5D)) {
			return EmpireDirection.SOUTH_EAST;
		}
		if ((157.5D <= rotation) && (rotation < 202.5D)) {
			return EmpireDirection.SOUTH;
		}
		if ((202.5D <= rotation) && (rotation < 247.5D)) {
			return EmpireDirection.SOUTH_WEST;
		}
		if ((247.5D <= rotation) && (rotation < 292.5D)) {
			return EmpireDirection.WEST;
		}
		if ((292.5D <= rotation) && (rotation < 337.5D)) {
			return EmpireDirection.NORTH_WEST;
		}
		if ((337.5D <= rotation) && (rotation < 360.0D)) {
			return EmpireDirection.NORTH;
		}
		return null;
	}

	public enum EmpireDirection {
		NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;
	}
}
