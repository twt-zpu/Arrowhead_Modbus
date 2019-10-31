/*
 *    Copyright (c) 2018 AITIA International Inc.
 *
 *    This work is part of the Productive 4.0 innovation project, which receives grants from the
 *    European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *    (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *    national funding authorities from involved countries.
 */

package eu.arrowhead.client.consumer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLContextConfigurator.GenericStoreException;

import eu.arrowhead.client.common.CertificateBootstrapper;
import eu.arrowhead.client.common.Utility;
import eu.arrowhead.client.common.exception.ArrowheadException;
import eu.arrowhead.client.common.misc.ClientType;
import eu.arrowhead.client.common.misc.TypeSafeProperties;
import eu.arrowhead.client.common.model.ArrowheadService;
import eu.arrowhead.client.common.model.ArrowheadSystem;
import eu.arrowhead.client.common.model.ModbusMeasurement;
import eu.arrowhead.client.common.model.ModbusMeasurementEntry;
import eu.arrowhead.client.common.model.OrchestrationResponse;
import eu.arrowhead.client.common.model.ServiceRequestForm;
import eu.arrowhead.client.modbus.ModbusData;

public class Consumer {
	private String slaveAddress;
	private String coilOutputs;
	private String registerOutputs;
	private HashMap<Integer, Boolean> coilsMap;
	private HashMap<Integer, Integer> registersMap;
    private boolean isSecure;
    private String orchestratorUrl;
    private TypeSafeProperties props = Utility.getProp();
    private final String consumerSystemName = props.getProperty("consumer_system_name");
    private final String consumerSystemAddress = props.getProperty("consumer_system_address", "0.0.0.0");
    private final Integer consumerSystemPort = Integer.valueOf(props.getProperty("consumer_system_port", "8080"));
    
    
    public Consumer(String[] args) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        getOrchestratorUrl(args);
        setServerAddress();
    }
    
    public void start(){
    	readData();
    	writeData();
    }
    
    private void readData(){
    	HashMap<Integer, Integer> dataQuantity;
    	
    	dataQuantity = ModbusData.getCoilsRead();
		for(Map.Entry<Integer, Integer> entry: dataQuantity.entrySet()){
			HashMap<Integer, Boolean> coils;
			coils = getCoils(entry.getKey(), entry.getValue());
			ModbusData.getEntryToWrite().setCoils(coils);
			ModbusData.releaseEntryToWrite();
		}
		
		dataQuantity = ModbusData.getDiscreteInputs();
		for(Map.Entry<Integer, Integer> entry: dataQuantity.entrySet()){
			HashMap<Integer, Boolean> discreteInputs;
			discreteInputs = getDiscreteInputs(entry.getKey(), entry.getValue());
			ModbusData.getEntryToWrite().setDiscreteInputs(discreteInputs);;
			ModbusData.releaseEntryToWrite();
		}
		
		dataQuantity = ModbusData.getDiscreteInputs();
		for(Map.Entry<Integer, Integer> entry: dataQuantity.entrySet()){
			HashMap<Integer, Boolean> discreteInputs;
			discreteInputs = getDiscreteInputs(entry.getKey(), entry.getValue());
			ModbusData.getEntryToWrite().setDiscreteInputs(discreteInputs);
			ModbusData.releaseEntryToWrite();
		}
		
		dataQuantity = ModbusData.getHoldingRegistersRead();
		for(Map.Entry<Integer, Integer> entry: dataQuantity.entrySet()){
			HashMap<Integer, Integer> holdingRegisters;
			holdingRegisters = getHoldingRegisters(entry.getKey(), entry.getValue());
			ModbusData.getEntryToWrite().setHoldingRegisters(holdingRegisters);
			ModbusData.releaseEntryToWrite();
		}
		
		dataQuantity = ModbusData.getInputRegisters();
		for(Map.Entry<Integer, Integer> entry: dataQuantity.entrySet()){
			HashMap<Integer, Integer> inputRegisters;
			inputRegisters = getHoldingRegisters(entry.getKey(), entry.getValue());
			ModbusData.getEntryToWrite().setInputRegisters(inputRegisters);
			ModbusData.releaseEntryToWrite();
		}
    }
    
    private void writeData(){
    	HashMap<Integer, Integer> dataQuantity;
    	
    	dataQuantity = ModbusData.getCoilsWrite();
    	HashMap<Integer, Boolean> coils = ModbusData.getEntryToRead().getCoils();
		ModbusData.releaseEntryToRead();
		for(Map.Entry<Integer, Integer> entry: dataQuantity.entrySet()){
			int address = entry.getKey();
			int quantity = entry.getValue();
			boolean[] dataToWrite = new boolean[quantity];
			for (int idx = 0; idx < quantity; idx++){
				int offset = address + idx;
				dataToWrite[idx] = coils.get(offset);
			}
			setCoilsAtID(address, dataToWrite);
		}
		
		dataQuantity = ModbusData.getCoilsWrite();
    	HashMap<Integer, Integer> registers = ModbusData.getEntryToRead().getHoldingRegisters();
		ModbusData.releaseEntryToRead();
		for(Map.Entry<Integer, Integer> entry: dataQuantity.entrySet()){
			int address = entry.getKey();
			int quantity = entry.getValue();
			int[] dataToWrite = new int[quantity];
			for (int idx = 0; idx < quantity; idx++){
				int offset = address + idx;
				dataToWrite[idx] = registers.get(offset);
			}
			setHoldingRegistersAtID(address, dataToWrite);
		}
    }
    
    public void setServerAddress(){
    	String method = "SetSlaveAddress";
    	slaveAddress = props.getProperty("remote_io_address", "10.12.90.14");
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method);
    	if (Utility.sendRequest(providerUrl, "GET", null).getStatus() == 0){
    		System.out.println("Consumer/setServerAddress: provider can not connect with " + slaveAddress);
    		System.exit(0);
    	}
    }
    
    public HashMap<Integer, Boolean> getCoils(int offset, int quantity){
    	String method = "GetCoils";
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method, offset, quantity);
    	return consumeService(providerUrl).getCoils();
    }
    
    public HashMap<Integer, Boolean> getDiscreteInputs(int offset, int quantity){
    	String method = "GetDiscreteInputs";
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method, offset, quantity);
    	return consumeService(providerUrl).getDiscreteInputs();
    }
    
    public HashMap<Integer, Integer> getHoldingRegisters(int offset, int quantity){
    	String method = "GetHoldingRegisters";
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method, offset, quantity);
    	return consumeService(providerUrl).getHoldingRegisters();
    }
    
    public HashMap<Integer, Integer> getInputRegisters(int offset, int quantity){
    	String method = "GetInputRegisters";
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method, offset, quantity);
    	return consumeService(providerUrl).getInputRegisters();
    }
    
    public void setCoils(HashMap<Integer, Boolean> coilsMap){
    	String method = "SetCoils";
    	this.coilsMap = coilsMap;
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method);
    	consumeService(providerUrl);
    }
    
    public void setCoilsAtID(int offset, boolean[] values){
    	String method = "SetCoilsAtCertainAddress";
    	if (values.length > 0)
    		this.coilOutputs = String.valueOf(values[0]);
    	for (int idx = 1; idx < values.length; idx++)
    		this.coilOutputs += "-" + String.valueOf(values[idx]);
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method, offset);
    	consumeService(providerUrl);
    }
    
    public void setHoldingRegisters(HashMap<Integer, Integer> registersMap){
    	String method = "SetHoldingRegisters";
    	this.registersMap = registersMap;
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method);
    	consumeService(providerUrl);
    }
    
    public void setHoldingRegistersAtID(int offset, int[] values){
    	String method = "SetHoldingRegistersAtCertainAddress";
    	if (values.length > 0)
    		this.registerOutputs = String.valueOf(values[0]);
    	for (int idx = 1; idx < values.length; idx++)
    		this.registerOutputs += "-" + String.valueOf(values[idx]);
    	ServiceRequestForm srf = compileSRF(method);
    	String providerUrl = sendOrchestrationRequest(srf, method, offset);
    	consumeService(providerUrl);
    }

    private void getOrchestratorUrl(String[] args) {
        String orchAddress = props.getProperty("orch_address", "0.0.0.0");
        int orchInsecurePort = props.getIntProperty("orch_insecure_port", 8440);
        int orchSecurePort = props.getIntProperty("orch_secure_port", 8441);

        for (String arg : args) {
          if (arg.equals("-tls")) {
            isSecure = true;
            SSLContextConfigurator sslCon = new SSLContextConfigurator();
            sslCon.setKeyStoreFile(props.getProperty("keystore"));
            sslCon.setKeyStorePass(props.getProperty("keystorepass"));
            sslCon.setKeyPass(props.getProperty("keypass"));
            sslCon.setTrustStoreFile(props.getProperty("truststore"));
            sslCon.setTrustStorePass(props.getProperty("truststorepass"));

            try {
              SSLContext sslContext = sslCon.createSSLContext(true);
              Utility.setSSLContext(sslContext);
            } catch (GenericStoreException e) {
              System.out.println("Provided SSLContext is not valid, moving to certificate bootstrapping.");
              e.printStackTrace();
              sslCon = CertificateBootstrapper.bootstrap(ClientType.CONSUMER, consumerSystemName);
              props = Utility.getProp();
              Utility.setSSLContext(sslCon.createSSLContext(true));
            }
            break;
          }
        }

        if (isSecure) {
          Utility.checkProperties(props.stringPropertyNames(), ClientType.CONSUMER.getSecureMandatoryFields());
          orchestratorUrl = Utility.getUri(orchAddress, orchSecurePort, "orchestrator/orchestration", true, false);
        } else {
          orchestratorUrl = Utility.getUri(orchAddress, orchInsecurePort, "orchestrator/orchestration", false, false);
        }
      }
    
    private ServiceRequestForm compileSRF(String serviceName) {
        /*
            ArrowheadSystem: systemName, (address, port, authenticationInfo)
            Since this Consumer skeleton will not receive HTTP requests (does not provide any services on its own),
            the address, port and authenticationInfo fields can be set to anything.
            SystemName can be an arbitrarily chosen name, which makes sense for the use case.
         */
        ArrowheadSystem consumer = new ArrowheadSystem(consumerSystemName, consumerSystemAddress, consumerSystemPort, "null");
        Map<String, String> metadata = new HashMap<>();
        if (isSecure) {
            metadata.put("security", "token");
        }
        ArrowheadService service = new ArrowheadService(serviceName, Collections.singleton("JSON"), metadata);
        Map<String, Boolean> orchestrationFlags = new HashMap<>();
        orchestrationFlags.put("overrideStore", true);
        orchestrationFlags.put("pingProviders", false);
        orchestrationFlags.put("metadataSearch", true);
        orchestrationFlags.put("enableInterCloud", true);

        ServiceRequestForm srf = new ServiceRequestForm.Builder(consumer).requestedService(service).orchestrationFlags(orchestrationFlags).build();
        // System.out.println("Service Request payload: " + Utility.toPrettyJson(null, srf));
        return srf;
    }

    private ModbusMeasurementEntry consumeService(String providerUrl){
    	Response getResponse = Utility.sendRequest(providerUrl, "GET", null);
        ModbusMeasurement readout = getResponse.readEntity(ModbusMeasurement.class);
        // System.out.println("Provider Response payload (get): " + Utility.toPrettyJson(null, readout));
        if (!readout.getE().isEmpty())
        	return readout.getE().get(0);
        else
        	return new ModbusMeasurementEntry();
    }
    
    private String sendOrchestrationRequest(ServiceRequestForm srf, String method) {
    	return sendOrchestrationRequest(srf, method, 0, 0);
    }
    
    private String sendOrchestrationRequest(ServiceRequestForm srf, String method, int offset) {
    	return sendOrchestrationRequest(srf, method, offset, 0);
    }
    
    private String sendOrchestrationRequest(ServiceRequestForm srf, String method, int offset, int quantity) {
        Response postResponse = Utility.sendRequest(orchestratorUrl, "POST", srf);
        OrchestrationResponse orchResponse = postResponse.readEntity(OrchestrationResponse.class);
        // System.out.println("Orchestration Response payload: " + Utility.toPrettyJson(null, orchResponse));
        if (orchResponse.getResponse().isEmpty()) {
            throw new ArrowheadException("Orchestrator returned with 0 Orchestration Forms!");
        }
        
        ArrowheadSystem provider = orchResponse.getResponse().get(0).getProvider();
        String serviceURI = orchResponse.getResponse().get(0).getServiceURI();
        UriBuilder ub = UriBuilder.fromPath("").host(provider.getAddress()).scheme("http");
        if (provider.getPort() != null && provider.getPort() > 0)
            ub.port(provider.getPort());
        switch(method) {
        case "SetSlaveAddress": setUri_SetSlaveAddress(ub, serviceURI); break;
        case "GetCoils": setUri_GetCoils(ub, serviceURI, offset, quantity); break;
        case "GetDiscreteInputs": setUri_GetDiscreteInputs(ub, serviceURI, offset, quantity); break;
        case "GetHoldingRegisters": setUri_GetHoldingRegisters(ub, serviceURI, offset, quantity); break;
        case "GetInputRegisters": setUri_GetInputRegisters(ub, serviceURI, offset, quantity); break;
        case "SetCoils": setUri_SetCoils(ub, serviceURI); break;
        case "SetCoilsAtCertainAddress": setUri_SetCoilsAtCertainAddress(ub, serviceURI, offset); break;
        case "SetHoldingRegisters": setUri_SetHoldingRegisters(ub, serviceURI); break;
        case "SetHoldingRegistersAtCertainAddress": setUri_SetHoldingRegistersAtCertainAddress(ub, serviceURI, offset); break;
        default: setUri_rest(ub, serviceURI); break;
        }
        
        if (orchResponse.getResponse().get(0).getService().getServiceMetadata().containsKey("security")) {
            ub.scheme("https");
            ub.queryParam("token", orchResponse.getResponse().get(0).getAuthorizationToken());
            ub.queryParam("signature", orchResponse.getResponse().get(0).getSignature());
        }
        // System.out.println("Received provider system URL: " + ub.toString());
        return ub.toString();
    }
    
    private void setUri_SetSlaveAddress(UriBuilder ub, String serviceURI){
    	if (slaveAddress != null)
    		serviceURI = serviceURI.replace("{slave_address}", slaveAddress);
    	if (serviceURI != null)
            ub.path(serviceURI);
    }
    
    private void setUri_GetCoils(UriBuilder ub, String serviceURI, int offset, int quantity){
    	if (offset >= 0)
    		serviceURI = serviceURI.replace("{offset}", String.valueOf(offset));
    	if (quantity > 0)
    		serviceURI = serviceURI.replace("{quantity}", String.valueOf(quantity));
    	if (serviceURI != null)
            ub.path(serviceURI);
    }
    
    private void setUri_GetDiscreteInputs(UriBuilder ub, String serviceURI, int offset, int quantity){
    	if (offset >= 0)
    		serviceURI = serviceURI.replace("{offset}", String.valueOf(offset));
    	if (quantity > 0)
    		serviceURI = serviceURI.replace("{quantity}", String.valueOf(quantity));
    	if (serviceURI != null)
            ub.path(serviceURI);
    }
    
    private void setUri_GetHoldingRegisters(UriBuilder ub, String serviceURI, int offset, int quantity){
    	if (offset >= 0)
    		serviceURI = serviceURI.replace("{offset}", String.valueOf(offset));
    	if (quantity > 0)
    		serviceURI = serviceURI.replace("{quantity}", String.valueOf(quantity));
    	if (serviceURI != null)
            ub.path(serviceURI);
    }
    
    private void setUri_GetInputRegisters(UriBuilder ub, String serviceURI, int offset, int quantity){
    	if (offset >= 0)
    		serviceURI = serviceURI.replace("{offset}", String.valueOf(offset));
    	if (quantity > 0)
    		serviceURI = serviceURI.replace("{quantity}", String.valueOf(quantity));
    	if (serviceURI != null)
            ub.path(serviceURI);
    }
    
    private void setUri_SetCoils(UriBuilder ub, String serviceURI){
    	if (serviceURI != null)
            ub.path(serviceURI);
    	for (Map.Entry<Integer, Boolean> entry : coilsMap.entrySet()){
    		int address = entry.getKey();
    		boolean value = entry.getValue();
    		String coilSet = address + "-" + value;
    		ub.queryParam("coil", coilSet);
    	}
    }
    
    private void setUri_SetCoilsAtCertainAddress(UriBuilder ub, String serviceURI, int offset){
    	if (offset >= 0)
    		serviceURI = serviceURI.replace("{offset}", String.valueOf(offset));
    	if (coilOutputs != null)
    		serviceURI = serviceURI.replace("{values}", coilOutputs);
    	if (serviceURI != null)
            ub.path(serviceURI);
    }
    
    private void setUri_SetHoldingRegisters(UriBuilder ub, String serviceURI){
    	if (serviceURI != null)
            ub.path(serviceURI);
    	for (Map.Entry<Integer, Integer> entry : registersMap.entrySet()){
    		int address = entry.getKey();
    		int value = entry.getValue();
    		String registerSet = address + "-" + value;
    		ub.queryParam("register", registerSet);
    	}
    }
    
    private void setUri_SetHoldingRegistersAtCertainAddress(UriBuilder ub, String serviceURI, int offset){
    	if (offset >= 0)
    		serviceURI = serviceURI.replace("{offset}", String.valueOf(offset));
    	if (registerOutputs != null)
    		serviceURI = serviceURI.replace("{values}", registerOutputs);
    	if (serviceURI != null)
            ub.path(serviceURI);
    }
    
    private void setUri_rest(UriBuilder ub, String serviceURI){
        if (serviceURI != null) {
            ub.path(serviceURI);
        }
    }
}
