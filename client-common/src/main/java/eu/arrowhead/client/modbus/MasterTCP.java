package eu.arrowhead.client.modbus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

public class MasterTCP {
	private TcpParameters tcpParameters = new TcpParameters();
	private ModbusMaster master;
	private int slaveId = 1;
	
	
  	public HashMap<Integer, Boolean> readCoils(int offset, int quantity){
		HashMap<Integer, Boolean> coils = new HashMap<Integer, Boolean>();
		try{
			if (!master.isConnected()){
				master.connect();
			}
			boolean[] coilsArray = master.readCoils(slaveId, offset, quantity);
			for (int index = 0; index < quantity; index++){
				int offsetIndex = offset + index;
				coils.put(offsetIndex, coilsArray[index]);
			}
			ModbusData.getEntryToWrite().setCoils(coils);
			ModbusData.releaseEntryToWrite();
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
            e.printStackTrace();
        }
		return coils;
	}
  	
	public HashMap<Integer, Boolean> readDiscreteInputs(int offset, int quantity){
		HashMap<Integer, Boolean> discreteInputs = new HashMap<Integer, Boolean>();
		try{
			if (!master.isConnected()){
				master.connect();
			}
			boolean[] coilsArray = master.readDiscreteInputs(slaveId, offset, quantity);
			for (int index = 0; index < quantity; index++){
				int offsetIndex = offset + index;
				discreteInputs.put(offsetIndex, coilsArray[index]);
			}
			ModbusData.getEntryToWrite().setDiscreteInputs(discreteInputs);
			ModbusData.releaseEntryToWrite();
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
            e.printStackTrace();
        }
		return discreteInputs;
	}
  	
  	public HashMap<Integer, Integer> readHoldingRegisters(int offset, int quantity){
		HashMap<Integer, Integer> registers = new HashMap<Integer, Integer>();
		try{
			if (!master.isConnected()){
				master.connect();
			}
			int[] registersArray = master.readHoldingRegisters(slaveId, offset, quantity);
			for (int index = 0; index < quantity; index++){
				int offsetIndex = offset + index;
				registers.put(offsetIndex, registersArray[index]);
			}
			ModbusData.getEntryToWrite().setHoldingRegisters(registers);
			ModbusData.releaseEntryToWrite();
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
            e.printStackTrace();
        }
		return registers;
	}
  	
  	public HashMap<Integer, Integer> readInputRegisters(int offset, int quantity){
		HashMap<Integer, Integer> registers = new HashMap<Integer, Integer>();
		try{
			if (!master.isConnected()){
				master.connect();
			}
			int[] registersArray = master.readInputRegisters(slaveId, offset, quantity);
			for (int index = 0; index < quantity; index++){
				int offsetIndex = offset + index;
				registers.put(offsetIndex, registersArray[index]);
			}
			ModbusData.getEntryToWrite().setHoldingRegisters(registers);
			ModbusData.releaseEntryToWrite();
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
            e.printStackTrace();
        }
		return registers;
	}
	
	public void writeCoils(HashMap<Integer, Boolean> coils){
		try {
			if (!master.isConnected()){
				master.connect();
			}
		} catch (ModbusIOException e) {
				e.printStackTrace();
		}
		for (Map.Entry<Integer, Boolean> entry : coils.entrySet()){
			int address = entry.getKey();
			boolean value = entry.getValue();
			try {
				master.writeSingleCoil(slaveId, address, value);
			} catch (ModbusProtocolException | ModbusNumberException
					| ModbusIOException e) {
				e.printStackTrace();
			}	
		}
		ModbusData.getEntryToWrite().setCoils(coils);
		ModbusData.releaseEntryToWrite();
	}
	
	public void writeCoilsAtID(int address, boolean[] coils){
		try {
			if (!master.isConnected()){
				master.connect();
			}
			master.writeMultipleCoils(slaveId, address, coils);
			ModbusData.getEntryToWrite().setCoils(address, coils);
			ModbusData.releaseEntryToWrite();
		} catch (ModbusProtocolException | ModbusNumberException
				| ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeHoldingRegisters(HashMap<Integer, Integer> registers){
		try {
			if (!master.isConnected()){
				master.connect();
			}
		} catch (ModbusIOException e) {
				e.printStackTrace();
		}
		for (Map.Entry<Integer, Integer> entry : registers.entrySet()){
			int address = entry.getKey();
			int value = entry.getValue();
			new Thread(new Runnable(){
				public void run(){
					try {
						master.writeSingleRegister(slaveId, address, value);
					} catch (ModbusProtocolException | ModbusNumberException
							| ModbusIOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		ModbusData.getEntryToWrite().setHoldingRegisters(registers);
		ModbusData.releaseEntryToWrite();
	}
	
	public void writeHoldingRegistersAtID(int address, int[] registers){	
		try {
			if (!master.isConnected()){
				master.connect();
			}
			master.writeMultipleRegisters(slaveId, address, registers);
		} catch (ModbusProtocolException | ModbusNumberException
				| ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ModbusData.getEntryToWrite().setHoldingRegisters(address, registers);
		ModbusData.releaseEntryToWrite();
	}
	
	private void setTCPParameters(String address){
		try{
			String[] nums = address.split("\\.");
			byte[] ip = {0, 0, 0, 0};
			if (nums.length == 4)
				for (int idx = 0; idx < nums.length ; idx++)
					ip[idx] = Byte.valueOf(nums[idx]);
			tcpParameters.setHost(InetAddress.getByAddress(ip));
	        tcpParameters.setKeepAlive(true);
	        tcpParameters.setPort(502);
		} catch (RuntimeException | UnknownHostException e) {
            e.printStackTrace();
        }
	}
	
	public void setModbusMaster(String address){
		setTCPParameters(address);
		master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
        Modbus.setAutoIncrementTransactionId(true);
        if (!master.isConnected())
			try {
				master.connect();
			} catch (ModbusIOException e) {
				// TODO Auto-generated catch block
				System.out.println("cannot connected with " + address);
				e.printStackTrace();
			}
        if (master.isConnected())
        	System.out.println("connected with " + address);
	}
	
	
}
