package eu.arrowhead.client.provider;

import eu.arrowhead.client.Modbus_GUI.ModbusDataDisplay;
import eu.arrowhead.client.Modbus_GUI.ModbusGUI;

public class App {
	private static Provider provider;
	private static SlaveTCP slave;
	private static ModbusGUI frame = new ModbusDataDisplay();
	
	public static void main(String[] args){
		provider = new Provider(args);
		slave = new SlaveTCP();
		
		frame.init("Arrowhead Modbus Communication Data - Provider");
		frame.setVisible(true);
		new ProviderThread().start();
		new SlaveThread().start();
	}
	
	
	public static class ProviderThread extends Thread{
		@Override public void run(){
			provider.startProvider();
		}
	}
	
	public static class SlaveThread extends Thread{
		@Override public void run(){
			slave.startSlave();
		}
	}
	
}
