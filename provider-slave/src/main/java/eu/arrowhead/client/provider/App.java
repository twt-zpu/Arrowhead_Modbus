package eu.arrowhead.client.provider;

public class App {
	private static Provider provider;
	private static SlaveTCP slave;
	
	public static void main(String[] args){
		provider = new Provider(args);
		slave = new SlaveTCP();
		// start provider
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
