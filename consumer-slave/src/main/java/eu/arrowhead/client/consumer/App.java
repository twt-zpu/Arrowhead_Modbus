package eu.arrowhead.client.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.arrowhead.client.Modbus_GUI.ModbusDataDisplay;
import eu.arrowhead.client.Modbus_GUI.ModbusGUI;

public class App {
	private static SlaveTCP slave;
	private static ModbusGUI frame = new ModbusDataDisplay();
	
	public static void main(String[] args) {
		frame.init("Arrowhead Modbus Communication Data - Consumer");
		frame.setVisible(true);
		
		slave = new SlaveTCP(args);
		Thread thread = new Thread(new Runnable(){
			public void run(){
				slave.startSlave();
			}
		});
		thread.start();
		
		System.out.println("Type \"stop\" to shutdown Consumer...");
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
