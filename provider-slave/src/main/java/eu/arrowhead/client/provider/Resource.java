package eu.arrowhead.client.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import eu.arrowhead.client.Modbus_GUI.ModbusDataDisplay;
import eu.arrowhead.client.Modbus_GUI.ModbusGUI;
import eu.arrowhead.client.common.model.ModbusMeasurement;
import eu.arrowhead.client.common.model.ModbusMeasurementEntry;


@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class Resource {
	private String providerName = "master";
	private static MasterTCP master = new MasterTCP();
	private ModbusGUI frame = new ModbusDataDisplay();
	
	@GET
	@Path("modbus/SetSlaveAddress/{slave_address}")
	public Response setServerAddress(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("slave_address") String address) {
		frame.setCommunicationData("client", true);
		frame.setCommunicationData("modbus", true);
		master.setModbusMaster(address);
		System.out.println("set slave adress: " + address);
	    return Response.status(Status.OK).build();
	}
	
	@SuppressWarnings({ "rawtypes" })
	@GET
	@Path("modbus/GetCoils/{offset}/{quantity}")
	public Response getCoils(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    
	    ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	    List<ModbusMeasurementEntry> entryList = new ArrayList<>();
	    HashMap<Integer, Boolean> coilsInput = master.readCoils(offset, quantity);
	    entry.setCoils(coilsInput);
	    entryList.add(entry);
	    measurement.setE(entryList);
	    frame.setSensorData(coilsInput);
	    HashMap<Integer, Boolean> coilsInputSlave = new HashMap<Integer, Boolean>();
	    for (Map.Entry coilEntry : coilsInput.entrySet())
	    	coilsInputSlave.put((Integer)((int)coilEntry.getKey() + 522), (Boolean) coilEntry.getValue());	
	    ModbusData.entry.setCoils(coilsInputSlave);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@SuppressWarnings("rawtypes")
	@GET
	@Path("modbus/GetDiscreteInputs/{offset}/{quantity}")
	public Response getDiscreteInputs(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    
	    ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	    List<ModbusMeasurementEntry> entryList = new ArrayList<>();
	    HashMap<Integer, Boolean> discreteInputs = master.readDiscreteInputs(offset, quantity);
	    entry.setDiscreteInputs(discreteInputs);
	    entryList.add(entry);
	    measurement.setE(entryList);
	    HashMap<Integer, Boolean> DiscreteInputsSlave = new HashMap<Integer, Boolean>();
	    for (Map.Entry coilEntry : discreteInputs.entrySet())
	    	DiscreteInputsSlave.put((Integer)((int)coilEntry.getKey() + 522), (Boolean) coilEntry.getValue());
	    ModbusData.entry.setCoils(DiscreteInputsSlave);
	    ModbusData.entry.setDiscreteInputs(discreteInputs);
	    frame.setSensorData(discreteInputs);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/GetHoldingRegisters/{offset}/{quantity}")
	public Response getHoldingRegisters(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	    List<ModbusMeasurementEntry> entryList = new ArrayList<>();
	    HashMap<Integer, Integer> registersInput = master.readHoldingRegisters(offset, quantity);
	    entry.setHoldingRegisters(registersInput);
	    entryList.add(entry);
	    measurement.setE(entryList);
	    ModbusData.entry.setHoldingRegisters(registersInput);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/GetInputRegisters/{offset}/{quantity}")
	public Response getRegisters(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	    List<ModbusMeasurementEntry> entryList = new ArrayList<>();
	    HashMap<Integer, Integer> registersInput = master.readInputRegisters(offset, quantity);
	    entry.setInputRegisters(registersInput);
	    entryList.add(entry);
	    measurement.setE(entryList);
	    ModbusData.entry.setInputRegisters(registersInput);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/SetCoils")
	public Response setCoils(@QueryParam("coil") List<String> coilsList) {
		ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
		HashMap<Integer, Boolean> coilsMap = new HashMap<Integer, Boolean>();
		for (String coil : coilsList){
			String[] coil_key_value = coil.split("-");
			int key = Integer.valueOf(coil_key_value[0]);
			boolean value = Boolean.valueOf(coil_key_value[1]);
			coilsMap.put(key, value);
		}
		frame.setAcutuatorData(coilsMap);
		master.writeCoils(coilsMap);
	    ModbusData.entry.setCoils(coilsMap);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/SetCoils/{offset}/{values}")
	public Response setCoilsAtID(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("values") String values) {
		ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
		String[] valuesString = values.split("-");
		boolean[] valuesBoolean = new boolean[valuesString.length];
		HashMap<Integer, Boolean> coilsMap = new HashMap<Integer, Boolean>();
		for (int idx = 0; idx < valuesString.length; idx++){
			valuesBoolean[idx] = Boolean.valueOf(valuesString[idx]);
			coilsMap.put(offset + idx, valuesBoolean[idx]);
		}
		frame.setAcutuatorData(coilsMap);
		master.writeCoilsAtID(offset, valuesBoolean);
		ModbusData.entry.setCoils(offset, valuesBoolean);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/SetHoldingRegisters")
	public Response SetRegisters(@QueryParam("coil") List<String> coilsList) {
		ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
		HashMap<Integer, Integer> RegistersMap = new HashMap<Integer, Integer>();
		for (String coil : coilsList){
			String[] coil_key_value = coil.split("-");
			int key = Integer.valueOf(coil_key_value[0]);
			Integer value = Integer.valueOf(coil_key_value[1]);
			RegistersMap.put(key, value);
		}
		master.writeHoldingRegisters(RegistersMap);
		ModbusData.entry.setHoldingRegisters(RegistersMap);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/SetHoldingRegisters/{offset}/{values}")
	public Response setRegistersAtID(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("values") String values) {
		ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
		String[] valuesString = values.split("-");
		int[] valuesInt = new int[valuesString.length];
		for (int idx = 0; idx < valuesString.length; idx++){
			valuesInt[idx] = Integer.valueOf(valuesString[idx]);
		}
		master.writeHoldingRegistersAtID(offset, valuesInt);
		ModbusData.entry.setHoldingRegisters(offset, valuesInt);
	    return Response.status(Status.OK).entity(measurement).build();
	}
}
