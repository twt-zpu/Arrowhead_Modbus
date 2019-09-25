package eu.arrowhead.client.provider;

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


@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class Resource {
	static final String SERVICE_GET_URI = "modbus/GetCoils";
	static final String SERVICE_SET_URI = "modbus/SetCoils";
	private static MasterTCP master = new MasterTCP();

	@GET
	@Path("modbus/SetSlaveAddress/{slave_address}")
	public Response setServerAddress(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("slave_address") String address) {
		master.setModbusMaster(address);
	    return Response.status(Status.OK).build();
	}
	
	@GET
	@Path("modbus/GetCoils/{offset}/{quantity}")
	public Response getCoils(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    String providerName = "FeldbusCouplerL";
	    ModbusMeasurement coils = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    HashMap<Integer, Boolean> coilsInput = master.readMasterCoils(offset, quantity);
	    coils.getE().setCoilsInput(coilsInput);
	    return Response.status(Status.OK).entity(coils).build();
	}
	
	@GET
	@Path("modbus/GetRegisters/{offset}/{quantity}")
	public Response getRegisters(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("quantity") int quantity) {
	    String providerName = "FeldbusCouplerL";
	    ModbusMeasurement coils = new ModbusMeasurement(providerName, System.currentTimeMillis(), " ", 1);
	    HashMap<Integer, Integer> registersInput = master.readMasterRegisters(offset, quantity);
	    coils.getE().setRegistersInput(registersInput);
	    return Response.status(Status.OK).entity(coils).build();
	}
	
	@GET
	@Path("modbus/SetCoils")
	public Response setCoils(@QueryParam("coil") List<String> coilsList) {
		HashMap<Integer, Boolean> coilsMap = new HashMap<Integer, Boolean>();
		for (String coil : coilsList){
			String[] coil_key_value = coil.split("-");
			int key = Integer.valueOf(coil_key_value[0]);
			boolean value = Boolean.valueOf(coil_key_value[1]);
			coilsMap.put(key, value);
		}
		master.writeMasterCoils(coilsMap);
	    return Response.status(Status.ACCEPTED).build();
	}
	
	@GET
	@Path("modbus/SetCoils/{offset}/{values}")
	public Response setCoilsAtID(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("values") String values) {
		String[] valuesString = values.split("-");
		boolean[] valuesBoolean = new boolean[valuesString.length];
		for (int idx = 0; idx < valuesString.length; idx++){
			valuesBoolean[idx] = Boolean.valueOf(valuesString[idx]);
		}
		master.writeMasterCoilsAtID(offset, valuesBoolean);
	    return Response.status(Status.ACCEPTED).build();
	}
	
	@GET
	@Path("modbus/SetRegisters")
	public Response SetRegisters(@QueryParam("coil") List<String> coilsList) {
		HashMap<Integer, Boolean> coilsMap = new HashMap<Integer, Boolean>();
		for (String coil : coilsList){
			String[] coil_key_value = coil.split("-");
			int key = Integer.valueOf(coil_key_value[0]);
			boolean value = Boolean.valueOf(coil_key_value[1]);
			coilsMap.put(key, value);
		}
		master.writeMasterCoils(coilsMap);
	    return Response.status(Status.ACCEPTED).build();
	}
	
	@GET
	@Path("modbus/SetRegisters/{offset}/{values}")
	public Response setRegistersAtID(@Context SecurityContext context, @QueryParam("token") String token, 
			@QueryParam("signature") String signature, @PathParam("offset") int offset, @PathParam("values") String values) {
		String[] valuesString = values.split("-");
		int[] valuesInt = new int[valuesString.length];
		for (int idx = 0; idx < valuesString.length; idx++){
			valuesInt[idx] = Integer.valueOf(valuesString[idx]);
		}
		master.writeMasterRegistersAtID(offset, valuesInt);
	    return Response.status(Status.ACCEPTED).build();
	}
}
