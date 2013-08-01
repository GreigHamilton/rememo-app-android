package com.greighamilton.rememo.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.text.format.Time;

/**
 * Class for collection of helper methods used throughout all classes.
 * 
 * @author Greig Hamilton
 *
 */
public class Util {
	
	public static boolean ASCENDING = true;
	public static boolean DESCENDING = false;
	
	/**
	 * Methods for creating data string objects: day, month and year
	 * 
	 */
	public static String makeDayString(int day) { return ((day < 10) ? "0" + day : "" + day); }
	
	public static String makeDayString(String day) { return ((day.length() < 2) ? "0" + day : "" + day); }
	
	public static String makeMonthString(int month) { return ((month < 10) ? "0" + month : "" + month); }
	
	public static String makeMonthString(String month) { return ((month.length() < 2) ? "0" + month : "" + month); }
	
	public static String makeYearString(int year) { return "" + year; }

	/**
	 * Method for making a complete data string object.
	 * 
	 * @param day		the day
	 * @param month		the month
	 * @param year		the year
	 * 
	 * @return			string representation of the date
	 */
	public static String makeDateString(int day, int month, int year) {
		
		return makeYearString(year) + "-" +
		makeMonthString(month) + "-" +
		makeDayString(day);
		
	}
	
	/**
	 * Method for making a complete data string object of type String.
	 * 
	 * @param day		the day
	 * @param month		the month
	 * @param year		the year
	 * 
	 * @return			string representation of the date
	 */
	public static String makeDateString(String day, String month, String year) {
		
		return makeYearString(Integer.parseInt(year)) + "-" +
		makeMonthString(Integer.parseInt(month)) + "-" +
		makeDayString(Integer.parseInt(day));
		
	}
	
	/**
	 * Method that gets the current date.
	 * 
	 * @return			todays date as a string object
	 */
	public static String getTodaysDate() {	
		
		return makeDateString(getTodaysDay(), getTodaysMonth(), getTodaysYear());	
		
	}
	
	/**
	 * Method that gets the current week start date.
	 * 
	 * @return			start of week date as a string object
	 */
	public static String getCurrentWeekDates() {
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		String[] days = new String[7];
		for (int i = 0; i < 7; i++)
		{
		    days[i] = format.format(calendar.getTime());
		    calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		String startWeek = days[0].substring(0, 4) + "-" + days[0].substring(5, 7) + "-" + days[0].substring(8);
		
		String endWeek = Util.getTomorrowsDate(days[6]);

		return startWeek + "#" + endWeek;
		
	}
	
	/**
	 * Method that gets the current date day.
	 * 
	 * @return		the current day
	 */
	public static int getTodaysDay() {	
		
		Time time = new Time();
		time.setToNow();
		return time.monthDay;	
		
	}
	
	/**
	 * Method that gets the current date month
	 * 
	 * @return		the current month
	 */
	public static int getTodaysMonth() {	
		
		Time time = new Time();
		time.setToNow();
		return time.month + 1;	
		
	}
	
	/**
	 * Method that gets the current date year
	 * 
	 * @return		the current year
	 */
	public static int getTodaysYear() {	
		
		Time time = new Time();
		time.setToNow();
		return time.year;	
		
	}
	
	/**
	 * Method that gets the current date day.
	 * 
	 * @return		the current second
	 */
	public static int getCurrentSecond() {	
		
		Time time = new Time();
		time.setToNow();
		return time.second;	
		
	}
	
	/**
	 * Method that gets the current time minute
	 * 
	 * @return		the current minute
	 */
	public static int getCurrentMinute() {	
		
		Time time = new Time();
		time.setToNow();
		return time.minute;	
		
	}
	
	/**
	 * Method that gets the current time hour
	 * 
	 * @return		the current hour
	 */
	public static int getCurrentHour() {	
		
		Time time = new Time();
		time.setToNow();
		return time.hour;	
		
	}
	
	/**
	 * Method for making a complete time string object.
	 * 
	 * @param hour		the hour
	 * @param minute		the minute
	 * @param second		the second
	 * 
	 * @return			string representation of the date
	 */
	public static String makeTimeString(int hour, int minute) {
		
		String hourTemp = "";
		String minuteTemp = "";
		
		if (hour < 10) hourTemp = "0"+hour; else hourTemp = ""+hour;
		if (minute < 10) minuteTemp = "0"+minute; else minuteTemp = ""+minute;
		
		return hourTemp + ":" + minuteTemp;
		
	}
	
	/**
	 * Method that parses a date and extracts the day.
	 * 
	 * @param date		date to be parsed
	 * 
	 * @return			the day from date
	 */
	public static int getDayFromString(String date) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			return dt.getDayOfMonth();
		} catch (Exception e) {
			return -1;
		}	
		
	}
	
	/**
	 * Method that parses a date and extracts the month.
	 * 
	 * @param date		date to be parsed
	 * 
	 * @return			the month from date
	 */
	public static int getMonthFromString(String date) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			return dt.getMonthOfYear();
		} catch (Exception e) {
			return -1;
		}	
		
	}
	
	/**
	 * Method that parses a date and extracts the year.
	 * 
	 * @param date		date to be parsed
	 * 
	 * @return			the year from date
	 */
	public static int getYearFromString(String date) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			return dt.getYear();
		} catch (Exception e) {
			return -1;
		}	
		
	}
	
	/**
	 * Method that creates a list of months (first three letters).
	 * 
	 * @return		list of months
	 */
	public static List<String> getListOfMonthsShort(){
		
		List<String> months = new ArrayList<String>();
		months.add("Jan"); months.add("Feb"); months.add("Mar"); months.add("Apr");
		months.add("May"); months.add("Jun"); months.add("Jul"); months.add("Aug");
		months.add("Sep"); months.add("Oct"); months.add("Nov"); months.add("Dec");
		return months;
		
	}
	
	/**
	 * Method that creates a list of months
	 * 
	 * @return		list of months
	 */
	public static List<String> getListOfMonthsLong(){
		
		List<String> months = new ArrayList<String>();
		months.add("January"); months.add("February"); months.add("March"); months.add("April");
		months.add("May"); months.add("June"); months.add("July"); months.add("August");
		months.add("September"); months.add("October"); months.add("November"); months.add("December");
		return months;
		
	}
	
	/**
	 * Method that creates a list of years.
	 * 
	 * @return		list of years
	 */
	public static List<String> getListOfYears(){
		
		List<String> years = new ArrayList<String>();
		years.add("2013"); years.add("2014"); years.add("2015"); years.add("2016");
		years.add("2017"); years.add("2018"); years.add("2019"); years.add("2020");
		return years;
		
	}
	
	/**
	 * Method that calculates the number of days until a given date.
	 * 
	 * @param dateString		the date to be calculated
	 * 
	 * @return					the number of days
	 * 
	 * @throws java.text.ParseException
	 */
	public static int daysUntil(String dateString) throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);			
		Date itemDate = (Date) sdf.parse(dateString);
		Date nowDate = (Date) sdf.parse(getTodaysDate());
		return Days.daysBetween(new DateTime(nowDate), new DateTime(itemDate)).getDays();
		
	}
	
	/**
	 * Method to add a number of weeks to a date.
	 * 
	 * @param date				the start date
	 * @param numberOfWeeks		number of weeks to be added
	 * 
	 * @return					date in number of weeks time
	 */
	public static String addWeeksToDate(String date, int numberOfWeeks) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			DateTime dt2 = dt.plusWeeks(numberOfWeeks);
			return dt2.toString("yyyy-MM-dd");
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}
	}
	
	/**
	 * Method to add a number of months to a date.
	 * 
	 * @param date				the start date
	 * @param numberOfMonths	number of months to be added
	 * 
	 * @return					date in number of months time
	 */
	public static String addMonthsToDate(String date, int numberOfMonths) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			DateTime dt2 = dt.plusMonths(numberOfMonths);
			return dt2.toString("yyyy-MM-dd");
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}
	}
	
	/**
	 * Method to add a number of years to a date.
	 * 
	 * @param date				the start date
	 * @param numberOfYears		the number of years to be added
	 * 
	 * @return					date in number of years time
	 */
	public static String addYearsToDate(String date, int numberOfYears) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			DateTime dt2 = dt.plusYears(numberOfYears);
			return dt2.toString("yyyy-MM-dd");
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}
	}
	
	/**
	 * Method to convert a float to a string rounded to 2 decimal places
	 * 
	 * @param number		number to be converted
	 * 
	 * @return				string representation to 2 decimal places
	 */
	public static String floatFormat(float number) {
		BigDecimal bd = new BigDecimal(number);
		BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return ""+rounded;
	}

	/**
	 * Method to convert a integer to a string rounded to 2 decimal places
	 * 
	 * @param number		number to be converted
	 * 
	 * @return				string representation to 2 decimal places
	 */
	public static String floatFormat(int number) {
		BigDecimal bd = new BigDecimal(number);
		BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return ""+rounded;
	}
	
	/**
	 * Method to convert a string to a string rounded to 2 decimal places
	 * 
	 * @param number		number to be converted
	 * 
	 * @return				string representation to 2 decimal places
	 */
	public static String floatFormat(String number) {
		BigDecimal bd = new BigDecimal(number);
		BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return ""+rounded;
	}

	/**
	 * Method that takes a date string, increments the date and returns the new date string
	 * 
	 * @param currentCursorDate		date to be incremented
	 * 
	 * @return						string date object
	 */
	public static String incrementDate(String currentCursorDate) {
		
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar c = Calendar.getInstance();
	    try {
	        c.setTime(sdf.parse(currentCursorDate));
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	    String output = sdf1.format(c.getTime());
		
		return output;
	}

	/**
	 * Method that takes a date string object and returns the month in text format
	 * 
	 * @param date		input date string
	 * 
	 * @return			the returned month as string object
	 */
	public static String getMonthText(String date) {
		
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		SimpleDateFormat month_date = new SimpleDateFormat("MMM");
		String month_name = month_date.format(cal.getTime());
		
		return month_name;
	}

	/**
	 * Method that takes a date string object and returns the day of the week in string format
	 * 
	 * @param date		input date string
	 * 
	 * @return			the returned day as string object
	 */
	public static String getDayOfWeek(String date) {

		TimeZone MyTimezone = TimeZone.getDefault();
		Calendar calendar = new GregorianCalendar(MyTimezone);
		calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7))-1, Integer.parseInt(date.substring(8, 10)));

		String day_name = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);// TODO set Locale);
		return day_name;
	}

	/**
	 * Method that returns the following weeks dates, given a date string
	 * 
	 * @param date		the current week date string
	 * 
	 * @return			next weeks date in a string object
	 */
	public static String getNextWeekDates(String date) {
		
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(year, month, day);
		calendar.add(Calendar.DAY_OF_YEAR, 7);

		String[] days = new String[7];
		for (int i = 0; i < 7; i++)
		{
		    days[i] = format.format(calendar.getTime());
		    calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		String startWeek = days[0].substring(0, 4) + "-" + days[0].substring(5, 7) + "-" + days[0].substring(8);
		
		String endWeek = days[6].substring(0, 4) + "-" + days[6].substring(5, 7) + "-" + days[6].substring(8);

		return startWeek + "#" + endWeek;
	}
	
	/**
	 * Method that returns last weeks dates, given a date string
	 * 
	 * @param date		the current week date string
	 * 
	 * @return			last weeks date in a string object
	 */
	public static String getLastWeekDates(String date) {
		
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(year, month, day);
		calendar.add(Calendar.DAY_OF_YEAR, -7);

		String[] days = new String[7];
		for (int i = 0; i < 7; i++)
		{
		    days[i] = format.format(calendar.getTime());
		    calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		String startWeek = days[0].substring(0, 4) + "-" + days[0].substring(5, 7) + "-" + days[0].substring(8);
		
		String endWeek = days[6].substring(0, 4) + "-" + days[6].substring(5, 7) + "-" + days[6].substring(8);

		return startWeek + "#" + endWeek;
	}

	/**
	 * Method that takes a date and returns tomorrows date
	 * 
	 * @param date
	 * @return
	 */
	public static String getTomorrowsDate(String date) {
		
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.add(Calendar.DAY_OF_MONTH, 1);   
        return format.format(cal.getTime());
	}
	
	/**
	 * Method that returns next weeks date, given a date string
	 * 
	 * @param date		the current week date string
	 * 
	 * @return			last week date in a string object
	 */
	public static String getNextWeeksDate(String date) {
		
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.add(Calendar.DAY_OF_MONTH, 7);   
        return format.format(cal.getTime());
	}

	/**
	 * Method that takes a date and returns yesterdays date
	 * 
	 * @param date
	 * @return
	 */
	public static String getYesterdaysDate(String date) {
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.add(Calendar.DAY_OF_MONTH, -1);   
        return format.format(cal.getTime());
	}

	/**
	 * Method that returns date a week from input date
	 * 
	 * @param date		the initial date string
	 * 
	 * @return			next week date in a string object
	 */
	public static String getWeekFromDate(String date) {
		
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.add(Calendar.DAY_OF_MONTH, 7);   
        return format.format(cal.getTime());
	}
	
	/**
	 * Method that takes a date and gets the date three days before it
	 * 
	 * @param date
	 * @return
	 */
	public static String getThreeDaysBeforeDate(String date) {
		
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.add(Calendar.DAY_OF_MONTH, -3);   
        return format.format(cal.getTime());
	}

	/**
	 * Method that takes a date and gets the date three days after it
	 * 
	 * @param date
	 * @return
	 */
	public static String getThreeDaysAfterDate(String date) {
		
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int year = Integer.parseInt(date.substring(0, 4));
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.add(Calendar.DAY_OF_MONTH, 4);   
        return format.format(cal.getTime());
	}
}
