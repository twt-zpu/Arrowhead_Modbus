package eu.arrowhead.client.provider;

import java.net.InetAddress;
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
	
	
  	public HashMap<Integer, Boolean> readMasterCoils(int offset, int quantity){
		HashMap<Integer, Boolean> coilsMap = new HashMap<Integer, Boolean>();
		try{
			if (!master.isConnected()){
				master.connect();
			}
			boolean[] coilsArray = master.readCoils(slaveId, offset, quantity);
			for (int index = 0; index < quantity; index++){
				int offsetIndex = offset + index;
				coilsMap.put(offsetIndex, coilsArray[index]);
			}
		} catch (ModbusProtocolException e) {
            e.printStackTrace();
        } catch (ModbusNumberException e) {
            e.printStackTrace();
        } catch (ModbusIOException e) {
            e.printStackTrace();
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e) {
                e.printStackTrace();
            }
        }
		return coilsMap;
	}
  	
  	public HashMap<Integer, Integer> readMasterRegisters(int offset, int quantity){
		HashMap<Integer, Integer> registersMap = new HashMap<Integer, Integer>();
		try{
			if (!master.isConnected()){
				master.connect();
			}
			int[] registersArray = master.readInputRegisters(slaveId, offset, quantity);
			for (int index = 0; index < quantity; index++){
				int offsetIndex = offset + index;
				registersMap.put(offsetIndex, registersArray[index]);
			}
		} catch (ModbusProtocolException e) {
            e.printStackTrace();
        } catch (ModbusNumberException e) {
            e.printStackTrace();
        } catch (ModbusIOException e) {
            e.printStackTrace();
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e) {
                e.printStackTrace();
            }
        }
		return registersMap;
	}
	
	public void writeMasterCoils(HashMap<Integer, Boolean> coilsMap){
		try {
			if (!master.isConnected()){
				master.connect();
			}
		} catch (ModbusIOException e) {
				e.printStackTrace();
		}
		for (Map.Entry<Integer, Boolean> entry : coilsMap.entrySet()){
			int address = entry.getKey();
			boolean value = entry.getValue();
			try {
				master.writeSingleCoil(slaveId, address, value);
			} catch (ModbusProtocolException | ModbusNumberException
					| ModbusIOException e) {
				e.printStackTrace();
			}
				
		}
	}
	
	public void writeMasterCoilsAtID(int address, boolean[] coils){
		try {
			if (!master.isConnected()){
				master.connect();
			}
			master.writeMultipleCoils(slaveId, address, coils);
		} catch (ModbusProtocolException | ModbusNumberException
				| ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeMasterRegisters(HashMap<Integer, Integer> registersMap){
		try {
			if (!master.isConnected()){
				master.connect();
			}
		} catch (ModbusIOException e) {
				e.printStackTrace();
		}
		for (Map.Entry<Integer, Integer> entry : registersMap.entrySet()){
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
	}
	
	public void writeMasterRegistersAtID(int address, int[] registers){	
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
		} catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
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
