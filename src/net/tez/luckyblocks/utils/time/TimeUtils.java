package net.tez.luckyblocks.utils.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

	public static String formatDate(long time) {
		if(time < 1)
			return "none";
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(time);
	}
	
	public static long calcTime(long time) {
		Date date = new Date(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, 1);
		return calendar.getTime().getTime();
	}
	
	public static long oneWeek() {
		Date date = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		return calendar.getTime().getTime();
	}
}
