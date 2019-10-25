package eu.arrowhead.client.Modbus_GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class TableWithLamp extends JTable{
	public TableWithLamp(DefaultTableModel model){
		super(model);
	}
	
	@Override public TableCellRenderer getCellRenderer( int row, int column ) {
        return new PlusMinusCellRenderer();
    }
	
	class PlusMinusCellRenderer extends JPanel implements TableCellRenderer {
        public Component getTableCellRendererComponent(
                            final JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        	String valueString  = value.toString();
        	if (valueString.equalsIgnoreCase("true"))
        		this.add(new CircleComponent(Color.GREEN));
        	else if (valueString.equalsIgnoreCase("false"))
        		this.add(new CircleComponent(Color.RED));
        	else
        		this.add(new JLabel(valueString));
        	return this;
        }
	}
	
	class CircleComponent extends JPanel{
		private Color color;
		
		public CircleComponent(Color color){
			this.color = color;
		}
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setPaint(color);
			Shape circle = new Ellipse2D.Double(0, 0, 10, 10);
			g2.fill(circle);
		}
	}
}
