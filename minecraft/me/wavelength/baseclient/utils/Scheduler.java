package me.wavelength.baseclient.utils;

import java.util.Calendar;

public class Scheduler {

	private Scheduler scheduler;
	private boolean running = false;
	private long endTime;

	private int hours;
	private int minutes;
	private int seconds;
	private long milliseconds;

	private Runnable finishAction;
	private Runnable stopAction;
	private Runnable runningAction;

	private boolean threaded;

	/** Start of Milliseconds-Only layout */
	public Scheduler(long milliseconds, Runnable finishAction, Runnable stopAction, Runnable runningAction) {
		initTimer(0, 0, 0, milliseconds, true, new Runnable[] { finishAction, stopAction, runningAction });
	}

	public Scheduler(long milliseconds, boolean threaded, Runnable finishAction, Runnable stopAction, Runnable runningAction) {
		initTimer(0, 0, 0, milliseconds, threaded, new Runnable[] { finishAction, stopAction, runningAction });
	}

	public Scheduler(long milliseconds, Runnable... actions) {
		initTimer(0, 0, 0, milliseconds, true, actions);
	}

	public Scheduler(long milliseconds, boolean threaded, Runnable... actions) {
		initTimer(0, 0, 0, milliseconds, threaded, actions);
	}

	/** End of Milliseconds-Only layout */

	/** Start of Milliseconds-Less layout */
	public Scheduler(int hours, int minutes, int seconds, Runnable finishAction, Runnable stopAction, Runnable runningAction) {
		initTimer(hours, minutes, seconds, 0, true, new Runnable[] { finishAction, stopAction, runningAction });
	}

	public Scheduler(int hours, int minutes, int seconds, boolean threaded, Runnable finishAction, Runnable stopAction, Runnable runningAction) {
		initTimer(hours, minutes, seconds, 0, threaded, new Runnable[] { finishAction, stopAction, runningAction });
	}

	public Scheduler(int hours, int minutes, int seconds, Runnable... actions) {
		initTimer(hours, minutes, seconds, 0, true, actions);
	}

	public Scheduler(int hours, int minutes, int seconds, boolean threaded, Runnable... actions) {
		initTimer(hours, minutes, seconds, 0, threaded, actions);
	}

	/** End of Milliseconds-Less layout */

	/** Start of Complete layout */
	public Scheduler(int hours, int minutes, int seconds, long milliseconds, Runnable finishAction, Runnable stopAction, Runnable runningAction) {
		initTimer(hours, minutes, seconds, milliseconds, true, new Runnable[] { finishAction, stopAction, runningAction });
	}

	public Scheduler(int hours, int minutes, int seconds, long milliseconds, boolean threaded, Runnable finishAction, Runnable stopAction, Runnable runningAction) {
		initTimer(hours, minutes, seconds, milliseconds, threaded, new Runnable[] { finishAction, stopAction, runningAction });
	}

	public Scheduler(int hours, int minutes, int seconds, long milliseconds, Runnable... actions) {
		initTimer(hours, minutes, seconds, milliseconds, true, actions);
	}

	public Scheduler(int hours, int minutes, int seconds, long milliseconds, boolean threaded, Runnable... actions) {
		initTimer(hours, minutes, seconds, milliseconds, threaded, actions);
	}

	private void initTimer(int hours, int minutes, int seconds, long milliseconds, boolean threaded, Runnable... actions) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliseconds = milliseconds;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, hours);
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);
		calendar.add(Calendar.MILLISECOND, (int) milliseconds);
		this.endTime = calendar.getTimeInMillis();

		this.scheduler = this;

		this.threaded = threaded;
		this.finishAction = actions[0];
		if (actions.length > 1) {
			this.stopAction = actions[1];
			if (actions.length > 2)
				this.runningAction = actions[2];
		}
	}

	/** End of Complete layout */

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getMissingTime() {
		return endTime - (Time.getTimestamp(new Time.Add(Calendar.HOUR, 1)));
	}

	public String getMissingTimeFormatted() {
		return getMissingTimeFormatted("HH 'hours', mm 'minutes', ss 'seconds', SSS 'milliseconds'");
	}

	public String getMissingTimeFormatted(String format) {
		return Time.getTime(scheduler.getMissingTime(), format);
	}

	public boolean isRunning() {
		return running;
	}

	public Runnable getFinishAction() {
		return finishAction;
	}

	public Runnable getStopAction() {
		return stopAction;
	}

	public Runnable getRunningAction() {
		return runningAction;
	}

	public Scheduler startTimer(Runnable finishAction, Runnable stopAction, Runnable runningAction) {
		if (running)
			return this;
		return startTimer(finishAction, stopAction, runningAction, threaded);
	}

	public Scheduler startTimer(Runnable finishAction, Runnable stopAction, Runnable runningAction, boolean threaded) {
		if (running)
			return this;
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					running = true;
					while (running) {
						synchronized (scheduler) {
							// Corrects the synchronization between the Thread and the main thread, this way
							// if the running boolean gets updated it will be updated here too (more
							// informations about this can be found here:
							// https://meta.stackoverflow.com/questions/269174/questions-about-threadloop-not-working-without-print-statement
						}

						if (runningAction != null)
							runningAction.run();

						if (Time.getTimestamp() < endTime)
							continue;

						if (finishAction != null)
							finishAction.run();

						stopTimer(stopAction);
					}
				}
			}).start();
			return this;
		}
		running = true;
		while (running) {
			synchronized (scheduler) {
				// Corrects the synchronization between the Thread and the main thread, this way
				// if the running boolean gets updated it will be updated here too (more
				// informations about this can be found here:
				// https://meta.stackoverflow.com/questions/269174/questions-about-threadloop-not-working-without-print-statement
			}

			if (runningAction != null)
				runningAction.run();

			if (Time.getTimestamp() < endTime)
				continue;

			if (finishAction != null)
				finishAction.run();

			stopTimer(stopAction);
		}
		return this;
	}

	public Runnable[] getActions() {
		return new Runnable[] { finishAction, stopAction, runningAction };
	}

	public Scheduler startTimer() {
		return startTimer(hours, minutes, seconds);
	}

	public Scheduler startTimer(int hours, int minutes, int seconds) {
		return startTimer(hours, minutes, seconds, threaded);
	}

	public Scheduler startTimer(int hours, int minutes, int seconds, boolean threaded) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, hours);
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);
		calendar.add(Calendar.MILLISECOND, (int) milliseconds);
		this.endTime = calendar.getTimeInMillis();
		return startTimer(finishAction, stopAction, runningAction, threaded);
	}

	public Scheduler stopTimer() {
		return stopTimer(null);
	}

	public Scheduler stopTimer(Runnable action) {
		if (!running)
			return this;

		if (action != null)
			action.run();
		running = false;
		return this;
	}

}