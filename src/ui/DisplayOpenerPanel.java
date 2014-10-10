package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import main.Main;

public class DisplayOpenerPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8469502540056239579L;
	
	private int counter = 1;
	
	JButton openScatterButton;
	JButton openEditHistoryButton;
	JButton openLogDisplayButton;
	
	public DisplayOpenerPanel() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
		
		ButtonListener l = new ButtonListener();
		
		openScatterButton = new JButton("Scatterplot");
		openScatterButton.addActionListener(l);
		add(openScatterButton);
		
		openEditHistoryButton = new JButton("Editing history");
		openEditHistoryButton.addActionListener(l);
		add(openEditHistoryButton);
		
		openLogDisplayButton = new JButton("Logging");
		openLogDisplayButton.addActionListener(l);
		add(openLogDisplayButton);
	}
	
	class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Main.model.hasData()) {	
				if (e.getSource() == openScatterButton) new ScatterplotFrame(counter++);
				if (e.getSource() == openEditHistoryButton) Main.model.printEditingTree();
			}
			if (e.getSource() == openLogDisplayButton) new LogDisplayFrame();
		}
		
	}
}
