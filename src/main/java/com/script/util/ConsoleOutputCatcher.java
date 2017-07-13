package com.script.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class ConsoleOutputCatcher {
	
	private ByteArrayOutputStream baos;
	
	private PrintStream previous;
	
	private boolean capturing;
	
	public void start() {
		
		if (capturing)
			return;
		
		capturing = true;
		previous = System.out;
		baos = new ByteArrayOutputStream();
		
		System.setOut(new PrintStream(
				 new OutputStreamCombiner(Arrays.asList(previous, baos))));
		
	}
	
	public String stop() {
		
		if (!capturing)
			return "";
		
		System.setOut(previous);
		
		String str = baos + "";
		
		baos = null;
		previous = null;
		capturing = false;
		
		return str;
	}
}
