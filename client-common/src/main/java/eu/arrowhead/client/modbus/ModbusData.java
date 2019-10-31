package eu.arrowhead.client.modbus;

import java.util.HashMap;

import eu.arrowhead.client.common.model.ModbusMeasurementEntry;

public class ModbusData {
	private static final ModbusMeasurementEntry entry = new ModbusMeasurementEntry();
	private static final HashMap<String, String> topics = new HashMap<String, String>();
	private static final HashMap<String, String> topicPoses = new HashMap<String, String>();
	private static HashMap<Integer, Integer> coilsRead = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> coilsWrite = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> discreteInputs = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> holdingRegistersRead = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> holdingRegistersWrite = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> inputRegisters = new HashMap<Integer, Integer>();
	private static boolean isWriteOccupied = false;
	private static int readOccupiedNumber = 0;
	
	public static synchronized ModbusMeasurementEntry getEntryToWrite(){
		while (isWriteOccupied && readOccupiedNumber != 0);
		isWriteOccupied = true;
		return entry;
	}
	
	public static synchronized ModbusMeasurementEntry getEntryToRead(){
		while (isWriteOccupied);
		readOccupiedNumber++;
		return entry;
	}
	
	public static synchronized void releaseEntryToWrite(){
		isWriteOccupied = false;
	}
	
	public static synchronized void releaseEntryToRead(){
		readOccupiedNumber--;
	}

	public static synchronized HashMap<Integer, Integer> getCoilsRead() {
		return coilsRead;
	}

	public static synchronized void setCoilsRead(HashMap<Integer, Integer> coilsRead) {
		ModbusData.coilsRead = coilsRead;
	}

	public static synchronized HashMap<Integer, Integer> getCoilsWrite() {
		return coilsWrite;
	}

	public static synchronized void setCoilsWrite(HashMap<Integer, Integer> coilsWrite) {
		ModbusData.coilsWrite = coilsWrite;
	}

	public static synchronized HashMap<Integer, Integer> getDiscreteInputs() {
		return discreteInputs;
	}

	public static synchronized void setDiscreteInputs(HashMap<Integer, Integer> discreteInputs) {
		ModbusData.discreteInputs = discreteInputs;
	}

	public static synchronized HashMap<Integer, Integer> getHoldingRegistersRead() {
		return holdingRegistersRead;
	}

	public static synchronized void setHoldingRegistersRead(
			HashMap<Integer, Integer> holdingRegistersRead) {
		ModbusData.holdingRegistersRead = holdingRegistersRead;
	}

	public static synchronized HashMap<Integer, Integer> getHoldingRegistersWrite() {
		return holdingRegistersWrite;
	}

	public static synchronized void setHoldingRegistersWrite(
			HashMap<Integer, Integer> holdingRegistersWrite) {
		ModbusData.holdingRegistersWrite = holdingRegistersWrite;
	}

	public static synchronized HashMap<Integer, Integer> getInputRegisters() {
		return inputRegisters;
	}

	public static synchronized void setInputRegisters(HashMap<Integer, Integer> inputRegisters) {
		ModbusData.inputRegisters = inputRegisters;
	}
	
	public static synchronized void setTopic(String type, String playload){
		topics.put(type, playload);
		setTypeDataInEntry(type);
	}
	
	public static synchronized void setTopicPose(String type, String pos){
		topicPoses.put(type, pos);
	}
	
	public static synchronized String getTopic(String type){
		return topics.get(type);
	}
	
	public static synchronized HashMap<String, String> getTopic(){
		return topics;
	}
	
	private static void setTypeDataInEntry(String type){
		// TODO 
	}
}
