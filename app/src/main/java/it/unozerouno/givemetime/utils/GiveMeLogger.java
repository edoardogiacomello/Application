package it.unozerouno.givemetime.utils;

import it.unozerouno.givemetime.view.main.fragments.DebugFragment;

public class GiveMeLogger {
	public static synchronized void log(String msg){
		DebugFragment.log(msg);
		System.out.println("Logger: "+ msg);
	}
}
