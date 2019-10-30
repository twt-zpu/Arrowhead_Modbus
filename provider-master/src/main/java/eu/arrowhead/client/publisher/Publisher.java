/*
 *	Copyright (c) 2018 AITIA International Inc.
 *
 *	This work is part of the Productive 4.0 innovation project, which receives grants from the
 *	European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *	(project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *	national funding authorities from involved countries.
 */

package eu.arrowhead.client.publisher;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.arrowhead.client.common.ArrowheadClientMain;
import eu.arrowhead.client.common.Utility;
import eu.arrowhead.client.common.misc.ClientType;
import eu.arrowhead.client.common.model.ArrowheadSystem;
import eu.arrowhead.client.common.model.Event;
import eu.arrowhead.client.common.model.PublishEvent;

//This class extends ArrowheadClientMain, which is responsible for starting and stopping the web server
//The publisher only uses a web server in order to provide an interface for the Event Handler to signal back the result of the event publishing
public class Publisher extends ArrowheadClientMain {
	private String ehUri;
	private ArrowheadSystem source;
	private HashMap<String, String> events = new HashMap<String, String>();
	private HashMap<String, String> eventPoses = new HashMap<String, String>();
	private ArrayList<String> modbusMemoryTypes = new ArrayList<String>();
	
	public Publisher() {
		//Start the web server, read in the command line arguments
		Set<Class<?>> classes = new HashSet<>(Collections.singleton(PublisherResource.class));
		String[] packages = {"eu.arrowhead.client.common"};
		String[] args = {};
		init(ClientType.PUBLISHER, args, classes, packages);
		initEhUri();
		initSource();
		publishInitEvents();
	}

	public static void main(String[] args) {
		Publisher publisher = new Publisher();
		publisher.publishEvent("modbus/product_processing_finished", "true");
	}

	private void initEhUri(){
		//Read in the Event Handler address related properties, create the full URL with the getUri() utility method
		String ehAddress = props.getProperty("eh_address", "0.0.0.0");
		int ehPort = isSecure ? props.getIntProperty("eh_secure_port", 8455) : props.getIntProperty("eh_insecure_port", 8454);
		ehUri = Utility.getUri(ehAddress, ehPort, "eventhandler/publish", isSecure, false);
	}
	
	private void initSource(){
	//Read in the fields needed to create the event
		String systemName = isSecure ? props.getProperty("secure_system_name") : props.getProperty("insecure_system_name");
		String address = props.getProperty("address", "0.0.0.0");
		int insecurePort = props.getIntProperty("publisher_insecure_port", ClientType.PUBLISHER.getInsecurePort());
		int securePort = props.getIntProperty("publisher_secure_port", ClientType.PUBLISHER.getSecurePort());
		int usedPort = isSecure ? securePort : insecurePort; 
		source = new ArrowheadSystem(systemName, address, usedPort, base64PublicKey);
	}
	
	private void initEvents(){
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
			
			events.put(eventTypes[idx].trim(), "");
			eventPoses.put(eventTypes[idx].trim(), eventPositions[idx]);
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
	
	private void publishInitEvents(){
		initEvents();
		for (@SuppressWarnings("rawtypes") Map.Entry entry: events.entrySet()){
			Event event = new Event(entry.getKey().toString(), entry.getValue().toString(), ZonedDateTime.now(), null);
			PublishEvent eventPublishing = new PublishEvent(source, event, "publisher/feedback");
			Utility.sendRequest(ehUri, "POST", eventPublishing);
			System.out.println("Event published to EH.");
		}
	}
	
	public void publishEvent(String type, String playload) {
		if (!events.containsKey(type) || (events.get(type) == playload))
			return;
		events.put(type, playload);
		Event event = new Event(type, playload, ZonedDateTime.now(), null);
		PublishEvent eventPublishing = new PublishEvent(source, event, "publisher/feedback");
		Utility.sendRequest(ehUri, "POST", eventPublishing);
		System.out.println(String.format("Event %s published to EH.", type));
	}
	
	@SuppressWarnings("rawtypes")
	public void publishEvents(String modbusMemoryType, HashMap values) {
		if (!modbusMemoryTypes.contains(modbusMemoryType))
			return;
		
		for(Map.Entry entryPose: eventPoses.entrySet()){
			String[] eventPos = entryPose.getValue().toString().split("-");
			if (eventPos[0].trim() != modbusMemoryType)
				continue;
			int address = Integer.parseInt(eventPos[1]);
			if (!values.containsKey(address))
				continue;
			
			publishEvent(entryPose.getKey().toString(), values.get(address).toString());
		}
		
	}
}
