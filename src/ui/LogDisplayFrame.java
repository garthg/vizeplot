package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.Main;


public class LogDisplayFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1634381432973802334L;
	
	private JFileChooser fileChooser = new JFileChooser(".");
	private JButton fullLogButton;
	private JButton logCSVButton;
	private JButton selectCSVButton;
	
	public LogDisplayFrame() {
		super("Vize Logging");
		
		setPreferredSize(new Dimension(500,100));
		Point parentLoc = Main.view.containerWindow.getLocationOnScreen();
		setLocation(parentLoc.x+20, parentLoc.y+20);
		
		add(new ButtonPanel());
		
		pack();
		setVisible(true);
	}
	
	private class ButtonPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3451658024409966932L;
		
		public ButtonPanel() {
			
			setLayout(new FlowLayout(FlowLayout.CENTER));
			
			ButtonListener l = new ButtonListener();
			
			fullLogButton = new JButton("Write full log");
			fullLogButton.addActionListener(l);
			add(fullLogButton);
			
			logCSVButton = new JButton("Write full log as CSV");
			logCSVButton.addActionListener(l);
			add(logCSVButton);
			
			selectCSVButton = new JButton("Write selection log as CSV");
			selectCSVButton.addActionListener(l);
			add(selectCSVButton);
		}
		
		class ButtonListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				int val = fileChooser.showSaveDialog(LogDisplayFrame.this);
				if (val == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (e.getSource() == fullLogButton) Main.log.writeLogToFile(file);
					if (e.getSource() == logCSVButton) Main.log.writeLogToFileAsCSV(file);
					if (e.getSource() == selectCSVButton) Main.log.writeSelectionHistoryToFile(file);
				}
			}
			
		}
	}
}
