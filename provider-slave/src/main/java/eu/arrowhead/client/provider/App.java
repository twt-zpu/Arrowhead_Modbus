package eu.arrowhead.client.provider;

public class App {
	private static Provider provider;
	
	public static void main(String[] args){
		provider = new Provider(args);
		
		// start provider
		new ProviderThread().start();
		
	}
	
	
	public static class ProviderThread extends Thread{
		@Override public void run(){
			provider.startProvider();
		}
	}
	
}
