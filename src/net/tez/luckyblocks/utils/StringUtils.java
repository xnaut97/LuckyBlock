package net.tez.luckyblocks.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;

import net.tez.luckyblocks.utils.ReflectionUtils.PackageName;

public class StringUtils {
	
	private static Method copy, getName, toString;
	static {
		try {
			copy = ReflectionUtils.getClass(PackageName.OBC, "inventory.CraftItemStack").getMethod("asNMSCopy",
					ItemStack.class);
			getName = ReflectionUtils.getMethod(ReflectionUtils.getClass(PackageName.NMS, "ItemStack"), "getName");

			if (ReflectionUtils.isVersion("v1_13_", "v1_14_", "v1_15_", "v1_16_")) {
				toString = ReflectionUtils.getMethod(ReflectionUtils.getClass(PackageName.NMS, "IChatBaseComponent"),
						"getString");
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public static String formatItemName(ItemStack item) {
		if (item == null)
			return null;
		String name = item.getType().toString();
		try {
			Object instance = copy.invoke(null, item);

			if (toString == null) {
				name = (String) getName.invoke(instance);
			} else {
				name = (String) toString.invoke(getName.invoke(instance));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		return name;
	}

	public static String format(String string) {
		string = string.toLowerCase();
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (String s : string.split("_")) {
			if (i == 0)
				builder.append(Character.toUpperCase(s.charAt(0)) + s.substring(1));
			else
				builder.append(" " + Character.toUpperCase(s.charAt(0)) + s.substring(1));
			i++;
		}
		return builder.toString();
	}

	public static String parseConfig(String string) {
		String[] split = string.toLowerCase().split("_");
		StringBuilder builder = new StringBuilder();
		builder.append(split[0]);
		for (int i = 1; i < split.length; i++) {
			String s = split[i];
			builder.append(Character.toUpperCase(s.charAt(0)) + s.substring(1));
		}
		return builder.toString();
	}

	public static boolean contains(String string, String... contain) {
		for (String s : contain) {
			if (string.contains(s))
				return true;
		}
		return false;
	}

	public static boolean equals(String string, String... equal) {
		for (String s : equal) {
			if (string.equals(s))
				return true;
		}
		return false;
	}

	public static boolean endsWith(String string, String... end) {
		for (String s : end) {
			if (string.endsWith(s))
				return true;
		}
		return false;
	}

	public static void message(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		return;
	}

	public static void messages(Player p, String... msg) {
		Arrays.asList(msg).forEach(
				s -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
	}
	
	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static boolean hasWhiteSpace(String s) {
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(s);
		boolean found = matcher.find();
		if (found)
			return true;
		return false;
	}

	public static boolean hasSpecialChars(String s) {
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		boolean b = m.find();

		if(b) 
			return true;
		return false;
	}
	
	public static boolean hasNumber(String s) {
		Pattern p = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		boolean b = m.find();

		if(b) 
			return true;
		return false;
	}

	public List<String> formatLength(int numberOfChar, String s){
		return Arrays.asList(ChatPaginator.wordWrap(s, numberOfChar));
	}
	
	public static boolean hasPerm(Player p, String perm) {
		if (!p.hasPermission(perm)) {
			return false;
		}
		return true;
	}

	public static String alignCenter(int width, String s) {
		return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
	}

	public static String listFormat(List<String> list, String colorCode) {
		return list.stream().map(key -> key.toString()).collect(Collectors.joining(colorCode + ", "));
	}
	
	public static String reset(String name) {
		return ChatColor.stripColor(name);
	}

	public static Double roundDouble(double amount) {
		return new BigDecimal(amount).setScale(1, RoundingMode.HALF_UP).doubleValue();
	}

	public static double round(double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}

	public static void console(String string) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', string));
	}

	public static String formatNumberUnit(long number) {
		String finalFormat = "" + number;
		for (NumberUnit unit : NumberUnit.values()) {
			if (number / unit.getDevideUnit() >= 1) {
				String raw = "" + new BigDecimal(number / unit.getDevideUnit()).doubleValue();
				String[] args = raw.split(Pattern.quote("."));
				finalFormat = args[0] + "." + args[1].substring(0,2) + unit.getFormat();
				break;
			}else
			{
				finalFormat = formatNumber(number);
			}
			
		}
		return finalFormat;
	}

	public static String formatNumber(long number) {
		return new DecimalFormat("###,###,###").format(number);
	}
	
	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}

	public static void broadcastGlobal(Player p, String... msg) {
		Bukkit.getOnlinePlayers().stream()
		.filter(player -> !player.getUniqueId().equals(p.getUniqueId()))
		.forEach(player -> StringUtils.messages(player, msg));
	}
	
	public static void broadcastGlobalExclude(Collection<? extends UUID> excludePlayers, String... msg) {
		Bukkit.getOnlinePlayers().stream()
		.filter(player -> !excludePlayers.contains(player.getUniqueId()))
		.forEach(player -> StringUtils.messages(player, msg));
	}
	
	public static void broadcastGlobalExcludes(Collection<? extends Player> excludePlayers, String... msg) {
		Bukkit.getOnlinePlayers().stream()
		.filter(player -> !excludePlayers.contains(player))
		.forEach(player -> StringUtils.messages(player, msg));
	}
	
	public static void broadcastGlobalInclude(Collection<? extends UUID> includePlayers, String... msg) {
		Bukkit.getOnlinePlayers().stream()
		.filter(player -> includePlayers.contains(player.getUniqueId()))
		.forEach(player -> StringUtils.messages(player, msg));
	}
	
	public static void broadcastGlobalIncludes(Collection<? extends Player> includePlayers, String... msg) {
		Bukkit.getOnlinePlayers().stream()
		.filter(player -> includePlayers.contains(player))
		.forEach(player -> StringUtils.messages(player, msg));
	}
	
	enum NumberUnit {
		BILLION(1000000000, "B"), MILLION(1000000, "M");

		private double devideUnit;

		private String format;

		private NumberUnit(double devideUnit, String format) {
			this.devideUnit = devideUnit;
			this.format = format;
		}

		public double getDevideUnit() {
			return devideUnit;
		}

		public String getFormat() {
			return format;
		}
	}

	public static boolean compareUUID(UUID uuid1, UUID uuid2) {
		return uuid1.equals(uuid2);
	}
	
	public static UUID generateEmptyUUID() {
		return UUID.fromString("00000000-0000-0000-0000-000000000000");
	}

}
