package eu.arrowhead.client.Modbus_GUI;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ModbusDataDisplay extends JFrame implements ModbusGUI {
	private final int windowWidth = 480;
	private final int windowHeight = 470;
	private final int sensorStartAddress = 0;
	private final int actuatorStartAddress = 512;
	private final JSplitPane vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private final JSplitPane hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private final static Object[][] sensorData = new Object[9][2];
	private final static Object[][] actutorData = new Object[10][2];
	private final static Object[][] communicationData = new Object[3][2];
	private final static String[] columnNames = {"Name", "Status"};
	private final static DefaultTableModel sensorModel = new DefaultTableModel(sensorData, columnNames);
	private final static DefaultTableModel actuatorModel = new DefaultTableModel(actutorData, columnNames);
	private final static DefaultTableModel communicationModel = new DefaultTableModel(communicationData, columnNames);
	private final static TableWithLamp sensorTable = new TableWithLamp(sensorModel);
	private final static TableWithLamp actuatorTable = new TableWithLamp(actuatorModel);
	private final static TableWithLamp communicationTable = new TableWithLamp(communicationModel);
	private final static JPanel sensorPanel = new JPanel();
	private final static JPanel actuatorPanel = new JPanel();
	private final static JPanel communicationPanel = new JPanel();
	
	public void init(String title){
		setTitle(title);
		setBounds(0, 0, windowWidth, windowHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initSensorData();
		initAcutaorData();
		initCommunicationData();
		setPanels();
		setPanes();
	}
	
	public void initSensorData(){
		sensorModel.setValueAt("DI01_F4", 0, 0);
		sensorModel.setValueAt("DI02_F3", 1, 0);
		sensorModel.setValueAt("DI03_F11", 2, 0);
		sensorModel.setValueAt("DI04_F2", 3, 0);
		sensorModel.setValueAt("DI05_F12", 4, 0);
		sensorModel.setValueAt("DI06_PD22", 5, 0);
		sensorModel.setValueAt("DI07_PD21", 6, 0);
		sensorModel.setValueAt("DI08_PD12", 7, 0);
		sensorModel.setValueAt("DI09_PD11", 8, 0);
		
		for (int i = 0; i < 9; i++)
			sensorModel.setValueAt(false, i, 1);
		
		sensorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sensorTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		sensorTable.getColumnModel().getColumn(1).setPreferredWidth(35);
		sensorTable.setRowHeight(25);
	}
	
	public void initAcutaorData(){
		actuatorModel.setValueAt("DO01_C4", 0, 0);
		actuatorModel.setValueAt("DO02_M2", 1, 0);
		actuatorModel.setValueAt("DO03_C3", 2, 0);
		actuatorModel.setValueAt("DO04_M1", 3, 0);
		actuatorModel.setValueAt("DO05_C2", 4, 0);
		actuatorModel.setValueAt("DO06_C1", 5, 0);
		actuatorModel.setValueAt("DO07_PD22", 6, 0);
		actuatorModel.setValueAt("DO08_PD21", 7, 0);
		actuatorModel.setValueAt("DO09_PD12", 8, 0);
		actuatorModel.setValueAt("DO10_PD11", 9, 0);
		
		for (int i = 0; i < 10; i++)
			actuatorModel.setValueAt(false, i, 1);
		
		actuatorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		actuatorTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		actuatorTable.getColumnModel().getColumn(1).setPreferredWidth(35);
		actuatorTable.setRowHeight(25);
	}
	
	public void initCommunicationData(){
		communicationModel.setValueAt("Arrowhead Server", 0, 0);
		communicationModel.setValueAt("Arrowhead Client", 1, 0);
		communicationModel.setValueAt("Modbus TCP", 2, 0);
		
		for (int i = 0; i < 3; i++)
			communicationModel.setValueAt(false, i, 1);
		
		communicationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		communicationTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		communicationTable.getColumnModel().getColumn(1).setPreferredWidth(35);
		communicationTable.setRowHeight(25);
	}
	
 	private void setPanels(){
		sensorPanel.setBorder(BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder(),
				"Sensors", TitledBorder.CENTER, TitledBorder.TOP));
		actuatorPanel.setBorder(BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder(),
				"Actuators", TitledBorder.CENTER, TitledBorder.TOP));
		communicationPanel.setBorder(BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder(),
				"Communication Chanels", TitledBorder.CENTER, TitledBorder.TOP));
		sensorPanel.add(sensorTable);
		actuatorPanel.add(actuatorTable);
		communicationPanel.add(communicationTable);
	}
	
	private void setPanes(){
		vSplitPane.setDividerLocation(120);
		vSplitPane.setOneTouchExpandable(true);
		vSplitPane.setContinuousLayout(true);
		
		hSplitPane.setDividerLocation(windowWidth/2);
		hSplitPane.setOneTouchExpandable(true);
		hSplitPane.setContinuousLayout(true);
		
		getContentPane().add(vSplitPane, BorderLayout.CENTER);
		vSplitPane.setLeftComponent(communicationPanel);
		vSplitPane.setRightComponent(hSplitPane);
		hSplitPane.setLeftComponent(sensorPanel);
		hSplitPane.setRightComponent(actuatorPanel);
	}
	
	@SuppressWarnings("rawtypes")
	public void setSensorData(HashMap<Integer, Boolean> data){
		for (Map.Entry entry: data.entrySet()){
			int row = (int)entry.getKey() - sensorStartAddress;
			sensorModel.setValueAt(entry.getValue(), row, 1);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void setAcutuatorData(HashMap<Integer, Boolean> data){
		for (Map.Entry entry: data.entrySet()){
			int row = (int)entry.getKey() - actuatorStartAddress;
			actuatorModel.setValueAt(entry.getValue(), row, 1);
		}
	}
	
	public void setCommunicationData(String key, Boolean value){
		switch (key){
		case "server": communicationModel.setValueAt(value, 0, 1); break;
		case "client": communicationModel.setValueAt(value, 1, 1); break;
		case "modbus": communicationModel.setValueAt(value, 2, 1); break;
		default: break;
		}
	}
}
