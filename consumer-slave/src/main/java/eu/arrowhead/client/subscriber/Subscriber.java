/*
 *	Copyright (c) 2018 AITIA International Inc.
 *
 *	This work is part of the Productive 4.0 innovation project, which receives grants from the
 *	European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *	(project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *	national funding authorities from involved countries.
 */

package eu.arrowhead.client.subscriber;

import eu.arrowhead.client.common.ArrowheadClientMain;
import eu.arrowhead.client.common.Utility;
import eu.arrowhead.client.common.misc.ClientType;
import eu.arrowhead.client.common.model.ArrowheadSystem;
import eu.arrowhead.client.common.model.EventFilter;
import eu.arrowhead.client.modbus.ModbusData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.PropertyConfigurator;

//This class extends ArrowheadClientMain, which is responsible for starting and stopping the web server
//The subscriber uses a web server in order to provide an interface for the Event Handler to provide events with the type the subscriber asked for
public class Subscriber extends ArrowheadClientMain {

	private static Set<String> EVENT_TYPES = new HashSet<>();
	private static String CONSUMER_NAME;
	private static String EH_URI;
	private ArrayList<String> modbusMemoryTypes = new ArrayList<String>();

	private Subscriber() {
		//Start the web server, read in the command line arguments
		Set<Class<?>> classes = new HashSet<>(Collections.singleton(SubscriberResource.class));
		String[] packages = {"eu.arrowhead.client.common"};
		String[] args = {};
		init(ClientType.SUBSCRIBER, args, classes, packages);
		initEventsInModBusData();
		
		//Log4j configuration
		PropertyConfigurator.configure(props);

		//Read in the Event Handler address related properties, create the full URL with the getUri() utility method
		String ehAddress = props.getProperty("eh_address", "0.0.0.0");
		int ehPort = isSecure ? props.getIntProperty("eh_secure_port", 8455) : props.getIntProperty("eh_insecure_port", 8454);
		EH_URI = Utility.getUri(ehAddress, ehPort, "eventhandler/subscription", isSecure, false);

		//Read in the fields needed to create the EventFilter POJO payload for subscribing/unsubscribing
		String typeList = props.getProperty("event_types");
		if (typeList != null && !typeList.isEmpty()) {
			EVENT_TYPES.addAll(Arrays.asList(typeList.replaceAll("\\s+", "").split(",")));
		}
		CONSUMER_NAME = isSecure ? props.getProperty("secure_system_name") : props.getProperty("insecure_system_name");
		//Subscribe to all the event types in the EVENT_TYPES Set
		subscribe();
	}

	public static void main(String[] args) {
		new Subscriber();
	}

	private void initEventsInModBusData(){
		String[] eventTypes = props.getProperty("event_type").split(",");
		String[] eventPositions = props.getProperty("event_position").split(",");
		modbusMemoryTypes.add("discreteInput");
		modbusMemoryTypes.add("coil");
		modbusMemoryTypes.add("holdingRegister");
		modbusMemoryTypes.add("inputRegister");
		
		if (eventTypes.length != eventPositions.length)
			return;
		
		for (int idx = 0; idx < eventTypes.length; idx++){
			if(eventTypes[idx].trim().isEmpty())
				continue;
			if (!checkEventPosition(eventPositions[idx]))
				continue;
			
			// ModbusData.setTopic(eventTypes[idx].trim(), "");
			ModbusData.setTopicPose(eventTypes[idx].trim(), eventPositions[idx]);
		}
		
	}
	
	private boolean checkEventPosition(String eventPosition){
		String[] eventPos = eventPosition.split("-");
		if(eventPos.length != 2)
			return false;
		
		if (!modbusMemoryTypes.contains(eventPos[0]))
			return false;
		
		try {  
		    Integer.parseInt(eventPos[1]);
		} catch(NumberFormatException e){  
			return false;  
		}
		
		return true;
	}
	
	//Shutdown the web server, overridden from ArrowheadClientMain. Unsubscribes from all the event types before stopping the JVM process.
	@Override
	protected void shutdown() {
		unsubscribe();
		if (server != null) {
			server.shutdownNow();
		}
		System.out.println("Arrowhead Subscriber Server stopped.");
		System.exit(0);
	}

	private void subscribe() {
		//Create the EventFilter request payload, send the subscribe request to the Event Handler
		URI uri;
		try {
			uri = new URI(baseUri);
		} catch (URISyntaxException e) {
			throw new AssertionError("Parsing the BASE_URI resulted in an error.", e);
		}

		ArrowheadSystem consumer = new ArrowheadSystem(CONSUMER_NAME, uri.getHost(), uri.getPort(), base64PublicKey);
		String notifyPath = props.getProperty("notify_uri");
		for (String eventType : EVENT_TYPES) {
			EventFilter filter = new EventFilter(eventType, consumer, notifyPath);
			Utility.sendRequest(EH_URI, "POST", filter);
			System.out.println("Subscribed to " + eventType + " event types.");
		}
	}

	//Unsubscribe from all the event types we subscribed to at the start
	private static void unsubscribe() {
		for (String eventType : EVENT_TYPES) {
			String url = UriBuilder.fromPath(EH_URI).path("type").path(eventType).path("consumer").path(CONSUMER_NAME).toString();
			Utility.sendRequest(url, "DELETE", null);
			System.out.println("Unsubscribed from " + eventType + " event types.");
		}
	}

}
