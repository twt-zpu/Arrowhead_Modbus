package eu.arrowhead.client.Modbus_GUI;

import java.util.HashMap;

public interface ModbusGUI {
	public void init(String title);
	public void setSensorData(HashMap<Integer, Boolean> data);
	public void setAcutuatorData(HashMap<Integer, Boolean> data);
	public void setCommunicationData(String key, Boolean value);
	public void setVisible(boolean b);
}
