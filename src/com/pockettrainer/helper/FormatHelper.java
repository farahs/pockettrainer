package com.pockettrainer.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatHelper {
	
	public static String	SYSTEM_DATE_FORMAT	= "yyyy-MM-dd HH:mm:ss";

	public static String	SHOW_DATE_FORMAT	= "d MMMM yyyy";

	public static String getFormattedStringDate(Date date, String dateFormat)
	{

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String dateStr = sdf.format(date);

		return dateStr;
	}

	public static Date getFormattedDate(String dateStr, String dateFormat)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		try
		{
			date = sdf.parse(dateStr);
		}
		catch (ParseException e)
		{
		
		}

		return date;
	}
	
}
