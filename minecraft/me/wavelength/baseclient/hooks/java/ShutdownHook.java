package me.wavelength.baseclient.hooks.java;

public class ShutdownHook {
	public static boolean installShutdownHook(Thread shutdownThread) {
		try {
        	Runtime.getRuntime().addShutdownHook(shutdownThread);
        	return true;
		}
		catch (Exception e) {
			return false;
		}
    }

    public static boolean uninstallShutdownHook(Thread shutdownThread) {
    	try {
    		Runtime.getRuntime().removeShutdownHook(shutdownThread);
    		return true;
    	}
    	catch (Exception e) {
    		return false;
    	}
    }
}
