package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.Logger;

public class Util {
	public static Util.EnumOS getOSType() {
		String s = System.getProperty("os.name").toLowerCase();
		return s.contains("win") ? Util.EnumOS.WINDOWS : (s.contains("mac") ? Util.EnumOS.OSX : (s.contains("solaris") ? Util.EnumOS.SOLARIS : (s.contains("sunos") ? Util.EnumOS.SOLARIS : (s.contains("linux") ? Util.EnumOS.LINUX : (s.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN)))));
	}

	public static <V> V func_181617_a(FutureTask<V> p_181617_0_, Logger p_181617_1_) {
		try {
			p_181617_0_.run();
			return p_181617_0_.get();
		} catch (ExecutionException executionexception) {
			p_181617_1_.fatal((String) "Error executing task", (Throwable) executionexception);
		} catch (InterruptedException interruptedexception) {
			p_181617_1_.fatal((String) "Error executing task", (Throwable) interruptedexception);
		}

		return (V) null;
	}

	public static enum EnumOS {
		LINUX, SOLARIS, WINDOWS, OSX, UNKNOWN;
	}
}
