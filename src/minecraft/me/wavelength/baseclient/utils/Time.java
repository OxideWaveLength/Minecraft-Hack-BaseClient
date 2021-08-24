package me.wavelength.baseclient.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

public class Time {

	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		return getTime(cal.getTimeInMillis());
	}

	public static String getTime(long timestamp) {
		SimpleDateFormat ctf = new SimpleDateFormat("HH:mm:ss (dd/MM/yyyy)");
		String ct = ctf.format(timestamp);
		return ct;
	}

	public static String getTime(long timestamp, String format) {
		SimpleDateFormat ctf = new SimpleDateFormat(format);
		String ct = ctf.format(timestamp);
		return ct;
	}

	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	public static long getTimestamp(Add... adds) {
		Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < adds.length; i++) {
			calendar.add(adds[i].getField(), adds[i].getAmount());
		}
		return calendar.getTimeInMillis();
	}

	public static Timer delayExecute(Runnable runnable, int milliseconds) {
		Timer t = new java.util.Timer();
		t.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runnable.run();
				t.cancel();
			}
		}, milliseconds);
		return t;
	}

	public static class Add {

		private int field;
		private int amount;

		public Add(int field, int amount) {
			this.field = field;
			this.amount = amount;
		}

		public int getField() {
			return field;
		}

		public int getAmount() {
			return amount;
		}

	}

}