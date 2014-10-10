package ui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JPanel;


public class DisplayRowColumnPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4061470770231134909L;

	public DisplayRowColumnPanel() {
		add(new ColumnPanel());
		add(Box.createRigidArea(new Dimension(20,20)));
		add(new RowPanel());
	}

}
