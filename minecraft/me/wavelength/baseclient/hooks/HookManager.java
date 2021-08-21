package me.wavelength.baseclient.hooks;

public class HookManager {
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
