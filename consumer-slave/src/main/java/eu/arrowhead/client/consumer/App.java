package eu.arrowhead.client.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
	private static SlaveTCP Salve;
	
	public static void main(String[] args) {
		Salve = new SlaveTCP(args);
		Thread thread = new Thread(new Runnable(){
			public void run(){
				Salve.startSlave();
			}
		});
		thread.start();
		
		System.out.println("Type \"stop\" to shutdown Server...");
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		try {
	        while (!input.equalsIgnoreCase("stop")) {
	          input = br.readLine();
	        }
	        br.close();
	        thread.interrupt();
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	}
}
