package eu.arrowhead.client.provider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.data.DataHolder;
import com.intelligt.modbus.jlibmodbus.data.ModbusCoils;
import com.intelligt.modbus.jlibmodbus.data.ModbusHoldingRegisters;
import com.intelligt.modbus.jlibmodbus.exception.IllegalDataAddressException;
import com.intelligt.modbus.jlibmodbus.exception.IllegalDataValueException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.slave.ModbusSlave;
import com.intelligt.modbus.jlibmodbus.slave.ModbusSlaveFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import com.intelligt.modbus.jlibmodbus.utils.FrameEvent;
import com.intelligt.modbus.jlibmodbus.utils.FrameEventListener;
import com.intelligt.modbus.jlibmodbus.utils.ModbusSlaveTcpObserver;
import com.intelligt.modbus.jlibmodbus.utils.TcpClientInfo;

import eu.arrowhead.client.common.Utility;
import eu.arrowhead.client.common.misc.TypeSafeProperties;

public class SlaveTCP {
	private TypeSafeProperties props = Utility.getProp();
	private ModbusSlave slave;
	private TcpParameters tcpParameters = new TcpParameters();
	private int range = Integer.valueOf(props.getProperty("coil_register_memory_range", "100"));;
	private ModbusCoils hc = new ModbusCoils(600);
	private ModbusCoils hcd = new ModbusCoils(range);
	private ModbusHoldingRegisters hr = new ModbusHoldingRegisters(range); 
	private ModbusHoldingRegisters hri = new ModbusHoldingRegisters(range);
	private MyOwnDataHolder dh = new MyOwnDataHolder();
	
	public SlaveTCP(){
		try {
			setSlave();
			System.out.println("range : " +range);
		} catch (IllegalDataAddressException | IllegalDataValueException
				| UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
 	public void startSlave(){
		try {
			slave.listen();
		} catch (ModbusIOException e) {
			e.printStackTrace();
		}
	}
	
	private void setSlave() throws IllegalDataAddressException, IllegalDataValueException, UnknownHostException{
		setTCPConnection();
		slave = ModbusSlaveFactory.createModbusSlaveTCP(tcpParameters);
		slave.setServerAddress(Modbus.TCP_DEFAULT_ID);
        slave.setBroadcastEnabled(true);
        slave.setReadTimeout(1000);
        Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);
        setDataHolder();
        setFrameEventListener();
        setObserver();
        slave.setServerAddress(1);
	}
	
	private void setTCPConnection() throws UnknownHostException{
	    tcpParameters.setHost(InetAddress.getLocalHost());
	    tcpParameters.setKeepAlive(true);
	    tcpParameters.setPort(503);
	}

	private void setDataHolder() throws IllegalDataAddressException, IllegalDataValueException{
		dh.addEventListener(new ModbusEventListener() {
			@Override
			public void onReadMultipleCoils(int address, int quantity) {
				// System.out.print("onReadMultipleCoils: address " + address + ", quantity " + quantity + "\n");
				HashMap<Integer, Boolean> valuesMap = new HashMap<Integer, Boolean>();
            	valuesMap = ModbusData.entry.getCoils();
				for(int index = 0; index < quantity; index++){
					int offsetIndex = address + index;
					try {
						// System.out.print("value: (" + offsetIndex + ", " + valuesMap.get(offsetIndex) + "); ");
						if (valuesMap.get(offsetIndex) == null)
							hc.set(offsetIndex, Boolean.FALSE);
						else
							hc.set(offsetIndex, valuesMap.get(offsetIndex));
							
					} catch (IllegalDataAddressException
							| IllegalDataValueException e) {
						e.printStackTrace();
					}
				}
			}
            
            @Override
            public void onReadMultipeDiscreteInputs(int address, int quantity){
            	// System.out.print("onReadMultipeDiscreteInputs: address " + address + ", quantity " + quantity + "\n");
            	HashMap<Integer, Boolean> valuesMap = new HashMap<Integer, Boolean>();
            	valuesMap = ModbusData.entry.getDiscreteInputs();
				for(int index = 0; index < quantity; index++){
					int offsetIndex = address + index;
					try {
						if (valuesMap.get(offsetIndex) == null)
							hcd.set(offsetIndex, Boolean.FALSE);
						else
							hcd.set(offsetIndex, valuesMap.get(offsetIndex));
					} catch (IllegalDataAddressException
							| IllegalDataValueException e) {
						e.printStackTrace();
					}
				}
            }
            
            @Override
            public void onReadSingleHoldingResgister(int address) {
            	onReadMultipleHoldingRegisters(address, 1);
            }
            
            @Override
            public void onReadMultipleHoldingRegisters(int address, int quantity) {
            	// System.out.print("onReadMultipleHoldingRegisters: address " + address + ", quantity " + quantity + "\n");
            	HashMap<Integer, Integer> valuesMap = new HashMap<Integer, Integer>();
            	valuesMap = ModbusData.entry.getHoldingRegisters();
            	for(int index = 0; index < quantity; index++){
					int offsetIndex = address + index;
					try {
						if (valuesMap.get(offsetIndex) == null)
							hr.set(offsetIndex, 0);
						else
							hr.set(offsetIndex, valuesMap.get(offsetIndex));
					} catch (IllegalDataAddressException
							| IllegalDataValueException e) {
						e.printStackTrace();
					}
				}
            }
            
            @Override
            public void onReadMultipleInputRegisters(int address, int quantity){
            	// System.out.print("onReadMultipleInputRegisters: address " + address + ", quantity " + quantity + "\n");
            	HashMap<Integer, Integer> valuesMap = new HashMap<Integer, Integer>();
            	valuesMap = ModbusData.entry.getInputRegisters();
				for(int index = 0; index < quantity; index++){
					int offsetIndex = address + index;
					try {
						if (valuesMap.get(offsetIndex) == null)
							hri.set(offsetIndex, 0);
						else
							hri.set(offsetIndex, valuesMap.get(offsetIndex));
					} catch (IllegalDataAddressException
							| IllegalDataValueException e) {
						e.printStackTrace();
					}
				}
            }
            
            @Override
            public void onWriteToSingleCoil(int address, boolean value) {
				// System.out.print("onWriteToSingleCoil: address " + address + ", value " + value + "\n");
				boolean[] values = new boolean[]{value};
				ModbusData.entry.setCoils(address, values);
            }
            
            @Override
            public void onWriteToMultipleCoils(int address, int quantity, boolean[] values) {
                // System.out.print("onWriteToMultipleCoils: address " + address + ", quantity " + quantity + "\n");
            	ModbusData.entry.setCoils(address, values);
            }

            @Override
            public void onWriteToSingleHoldingRegister(int address, int value) {
                // System.out.print("onWriteToSingleHoldingRegister: address " + address + ", value " + value + "\n");
            	int[] values = new int[]{value};
            	ModbusData.entry.setHoldingRegisters(address, values);
            }

            @Override
            public void onWriteToMultipleHoldingRegisters(int address, int quantity, int[] values) {
                // System.out.print("onWriteToMultipleHoldingRegisters: address " + address + ", quantity " + quantity + "\n");
            	ModbusData.entry.setHoldingRegisters(address, values);
            }
        });
        slave.setDataHolder(dh);
        slave.getDataHolder().setCoils(hc);
        slave.getDataHolder().setDiscreteInputs(hcd);
        slave.getDataHolder().setHoldingRegisters(hr);
        slave.getDataHolder().setInputRegisters(hri);
	}
	
	private void setFrameEventListener(){
		FrameEventListener listener = new FrameEventListener() {
            @Override
            public void frameSentEvent(FrameEvent event) {
                // System.out.println("frame sent " + DataUtils.toAscii(event.getBytes()));
            }

            @Override
            public void frameReceivedEvent(FrameEvent event) {
                // System.out.println("frame recv " + DataUtils.toAscii(event.getBytes()));
            }
        };
        slave.addListener(listener);
	}
	
	private void setObserver(){
		Observer o = new ModbusSlaveTcpObserver() {
            @Override
            public void clientAccepted(TcpClientInfo info) {
                System.out.println("Client connected " + info.getTcpParameters().getHost());
            }

            @Override
            public void clientDisconnected(TcpClientInfo info) {
                System.out.println("Client disconnected " + info.getTcpParameters().getHost());
            }
        };
        slave.addObserver(o);
	}
	
	public interface ModbusEventListener {
        void onReadMultipleCoils(int address, int quantity);
        
        void onReadMultipeDiscreteInputs(int address, int quantity);
        
        void onReadSingleHoldingResgister(int address);
        
        void onReadMultipleHoldingRegisters(int address, int quantity);
        
        void onReadMultipleInputRegisters(int address, int quantity);

        void onWriteToSingleCoil(int address, boolean value);
        
        void onWriteToMultipleCoils(int address, int quantity, boolean[] values);

        void onWriteToSingleHoldingRegister(int address, int value);

        void onWriteToMultipleHoldingRegisters(int address, int quantity, int[] values);
    }

    public static class MyOwnDataHolder extends DataHolder {

        final List<ModbusEventListener> modbusEventListenerList = new ArrayList<ModbusEventListener>();

        public MyOwnDataHolder() {
        }

        public void addEventListener(ModbusEventListener listener) {
            modbusEventListenerList.add(listener);
        }

        public boolean removeEventListener(ModbusEventListener listener) {
            return modbusEventListenerList.remove(listener);
        }

        @Override
        public void writeHoldingRegister(int offset, int value) throws IllegalDataAddressException, IllegalDataValueException {
            for (ModbusEventListener l : modbusEventListenerList) {
                l.onWriteToSingleHoldingRegister(offset, value);
            }
            super.writeHoldingRegister(offset, value);
        }

        @Override
        public void writeHoldingRegisterRange(int offset, int[] range) throws IllegalDataAddressException, IllegalDataValueException {
            for (ModbusEventListener l : modbusEventListenerList) {
                l.onWriteToMultipleHoldingRegisters(offset, range.length, range);
            }
            super.writeHoldingRegisterRange(offset, range);
        }

        @Override
        public void writeCoil(int offset, boolean value) throws IllegalDataAddressException, IllegalDataValueException {
            for (ModbusEventListener l : modbusEventListenerList) {
                l.onWriteToSingleCoil(offset, value);
            }
            super.writeCoil(offset, value);
        }

        @Override
        public void writeCoilRange(int offset, boolean[] range) throws IllegalDataAddressException, IllegalDataValueException {
            for (ModbusEventListener l : modbusEventListenerList) {
                l.onWriteToMultipleCoils(offset, range.length, range);
            }
            super.writeCoilRange(offset, range);
        }
        
        @Override
        public boolean[] readCoilRange(int offset, int quantity) throws IllegalDataAddressException, IllegalDataValueException{
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadMultipleCoils(offset, quantity);
            }
        	boolean[] values = super.readCoilRange(offset, quantity);
            return values;
        }
        
        @Override
        public boolean[] readDiscreteInputRange(int offset, int quantity) throws IllegalDataAddressException, IllegalDataValueException {
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadMultipeDiscreteInputs(offset, quantity);
            }
        	boolean[] values = super.readDiscreteInputRange(offset, quantity);
        	return values;
        }
        
        @Override
        public int readHoldingRegister(int offset) throws IllegalDataAddressException {
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadSingleHoldingResgister(offset);
            }
        	int value = super.readHoldingRegister(offset);
        	return value;
        }
        
        @Override
        public int[] readHoldingRegisterRange(int offset, int quantity) throws IllegalDataAddressException {
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadMultipleHoldingRegisters(offset, quantity);
            }
        	int[] values = super.readHoldingRegisterRange(offset, quantity);
        	return values;
        }
        
        @Override
        public int[] readInputRegisterRange(int offset, int quantity) throws IllegalDataAddressException {
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadMultipleInputRegisters(offset, quantity);
            }
        	int[] values = super.readInputRegisterRange(offset, quantity);
        	return values;
        }
    }
	
}
