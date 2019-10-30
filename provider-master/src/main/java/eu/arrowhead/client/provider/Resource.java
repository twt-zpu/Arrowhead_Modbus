package eu.arrowhead.client.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import eu.arrowhead.client.common.model.ModbusMeasurement;
import eu.arrowhead.client.common.model.ModbusMeasurementEntry;
import eu.arrowhead.client.modbus.MasterTCP;
import eu.arrowhead.client.publisher.Publisher;


@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class Resource {
	private String providerName = "master";
	private static MasterTCP master = new MasterTCP();
	private static Publisher publisher = new Publisher();

	@GET
	@Path("modbus/SetSlaveAddress/{slave_address}")
	public Response setServerAddress(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("slave_address") String address) {
		master.setModbusMaster(address);
		System.out.println("set slave adress: " + address);
	    return Response.status(Status.OK).build();
	}
	
	@GET
	@Path("modbus/GetCoils/{offset}/{quantity}")
	public Response getCoils(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    
	    ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	    List<ModbusMeasurementEntry> entryList = new ArrayList<>();
	    HashMap<Integer, Boolean> coils = master.readCoils(offset, quantity);
	    publisher.publishEvents("coil", coils);
	    entry.setCoils(coils);
	    entryList.add(entry);
	    measurement.setE(entryList);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/GetDiscreteInputs/{offset}/{quantity}")
	public Response getDiscreteInputs(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    
	    ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	    List<ModbusMeasurementEntry> entryList = new ArrayList<>();
	    HashMap<Integer, Boolean> discreteInputs = master.readDiscreteInputs(offset, quantity);
	    publisher.publishEvents("discreteInput", discreteInputs);
	    entry.setDiscreteInputs(discreteInputs);
	    entryList.add(entry);
	    measurement.setE(entryList);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/GetHoldingRegisters/{offset}/{quantity}")
	public Response getHoldingRegisters(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	    List<ModbusMeasurementEntry> entryList = new ArrayList<>();
	    HashMap<Integer, Integer> holdingRegisters = master.readHoldingRegisters(offset, quantity);
	    publisher.publishEvents("holdingRegister", holdingRegisters);
	    entry.setHoldingRegisters(holdingRegisters);
	    entryList.add(entry);
	    measurement.setE(entryList);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/GetInputRegisters/{offset}/{quantity}")
	public Response getInputRegisters(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	    List<ModbusMeasurementEntry> entryList = new ArrayList<>();
	    HashMap<Integer, Integer> inputRegisters = master.readInputRegisters(offset, quantity);
	    publisher.publishEvents("inputRegister", inputRegisters);
	    entry.setHoldingRegisters(inputRegisters);
	    entryList.add(entry);
	    measurement.setE(entryList);
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
		publisher.publishEvents("coil", coilsMap);
		master.writeCoils(coilsMap);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/SetCoils/{offset}/{values}")
	public Response setCoilsAtID(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("values") String values) {
		ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
		String[] valuesString = values.split("-");
		boolean[] valuesBoolean = new boolean[valuesString.length];
		for (int idx = 0; idx < valuesString.length; idx++){
			valuesBoolean[idx] = Boolean.valueOf(valuesString[idx]);
		}
		master.writeCoilsAtID(offset, valuesBoolean);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/SetHoldingRegisters")
	public Response SetHoldingRegisters(@QueryParam("coil") List<String> coilsList) {
		ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
		HashMap<Integer, Integer> registersMap = new HashMap<Integer, Integer>();
		for (String coil : coilsList){
			String[] coil_key_value = coil.split("-");
			int key = Integer.valueOf(coil_key_value[0]);
			int value = Integer.valueOf(coil_key_value[1]);
			registersMap.put(key, value);
		}
		publisher.publishEvents("holdingRegister", registersMap);
		master.writeHoldingRegisters(registersMap);
	    return Response.status(Status.OK).entity(measurement).build();
	}
	
	@GET
	@Path("modbus/SetHoldingRegisters/{offset}/{values}")
	public Response setHoldingRegistersAtID(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("values") String values) {
		ModbusMeasurement measurement = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
		String[] valuesString = values.split("-");
		int[] valuesInt = new int[valuesString.length];
		for (int idx = 0; idx < valuesString.length; idx++){
			valuesInt[idx] = Integer.valueOf(valuesString[idx]);
		}
		master.writeHoldingRegistersAtID(offset, valuesInt);
	    return Response.status(Status.OK).entity(measurement).build();
	}
}
