package de.mimuc.pem_music_graph.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import de.mimuc.pem_music_graph.R;

public class Utils {
	
	/**
	 * Get a formated time string for the header of a list entry
	 * 
	 * @param startTime
	 * @return
	 */
	public static String getHeaderTime(long startTime){
		DateTime eventTime = new DateTime(startTime);
		DateTime now = new DateTime();
		LocalDate today = now.toLocalDate();
		LocalDate tomorrow = today.plusDays(1);
		LocalDate afterTomorrow = today.plusDays(2);

		DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
		DateTime startOfTomorrow = tomorrow.toDateTimeAtStartOfDay(now.getZone()).plusHours(8);
		DateTime startOfDayAfterTomorrow = afterTomorrow.toDateTimeAtStartOfDay(now.getZone()).plusHours(8);
		
		String timeString;
		if(startOfToday.getMillis() <= eventTime.getMillis() &&
			eventTime.getMillis() < startOfTomorrow.getMillis()){
			timeString = ApplicationController.getInstance().getString(R.string.list_header_date_today) + " - "+
					ApplicationController.getInstance().getString(R.string.list_header_date_from) + " "+
					((eventTime.getHourOfDay() < 10) ? "0"+eventTime.getHourOfDay() : eventTime.getHourOfDay()) + ":"+
					((eventTime.getMinuteOfHour() < 10) ? "0"+eventTime.getMinuteOfHour() : eventTime.getMinuteOfHour()) + " "+
					ApplicationController.getInstance().getString(R.string.list_detail_clock);
		}
		else if(startOfTomorrow.getMillis() <= eventTime.getMillis() &&
				eventTime.getMillis() < startOfDayAfterTomorrow.getMillis()){
			timeString = ApplicationController.getInstance().getString(R.string.list_header_date_tomorrow) + " - "+
					ApplicationController.getInstance().getString(R.string.list_header_date_from) + " "+
					((eventTime.getHourOfDay() < 10) ? "0"+eventTime.getHourOfDay() : eventTime.getHourOfDay()) + ":"+
					((eventTime.getMinuteOfHour() < 10) ? "0"+eventTime.getMinuteOfHour() : eventTime.getMinuteOfHour()) + " "+
					ApplicationController.getInstance().getString(R.string.list_detail_clock);
			
		}
		else {
			timeString = Utils.formatTime(eventTime.getMillis());
		}
		
		return timeString;
	}

	/**
	 * formats the date
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		String[] weekdays = ApplicationController.getInstance().getResources().getStringArray(
				R.array.weekdays);
		String[] months = ApplicationController.getInstance().getResources().getStringArray(R.array.months);

		DateTime date = new DateTime(time);

		String dayWeek = weekdays[date.getDayOfWeek() - 1];
		String dayMonth = date.getDayOfMonth() + "";
		String month = months[date.getMonthOfYear() - 1];
		String hours = (date.getHourOfDay() < 10) ? "0" + date.getHourOfDay()
				: date.getHourOfDay() + "";
		String minutes = (date.getMinuteOfHour() < 10) ? "0"
				+ date.getMinuteOfHour() : date.getMinuteOfHour() + "";

				return dayWeek + ", " + dayMonth + ". " + month + ". " + hours + ":"
				+ minutes;

	}
	
	/**
	 * if distance >=1000m, information in km, else in m
	 * 
	 * @param distance
	 * @return string
	 */
	public static String roundDistance(float distance) {
		String distanceUnity = "m";
		if (distance >= 1000) {
			float dist = distance;
			dist = distance / 1000;
			dist = Math.round(dist * 10);
			dist = dist / 10;
			distance = dist;
			distanceUnity = "km";
		} else
			distance = Math.round(distance);
		return "ca. " + (int) distance + " " + distanceUnity;
	}
	
	/**
	 * Tests if a string is neither "", "null" nor null
	 * @param string
	 * @return true if string is not empty
	 */
	public static boolean stringNotEmpty(String string) {
		if (string.equals("") || string.equals("null") || string == null){
			return false;
		} else {
			return true;
		}
	}
}
