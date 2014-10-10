package db;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import log.VisLog;
import main.Main;

public class VisController {
	
	public void start(String filename) {
		Main.view.addStartListener(new ViewStartListener(filename));
		Main.view.start();	
	}
	
	public void start() {
		start(null);
	}
	
	private void loadFile(String filename) {
		try {
			Main.log = new VisLog();
			Main.model.loadFromFile(filename);
		} catch (FileNotFoundException err) {
			String alertStr = "File not found: '"+filename+"'.";
			System.err.println(alertStr);
			err.printStackTrace();
			Main.view.alert(alertStr);
		} catch (IOException err) {
			String alertStr = "Error reading file: '"+filename+"'.";
			System.err.println(alertStr);
			err.printStackTrace();
			Main.view.alert(alertStr);
		}
	}
	
	class ViewStartListener implements ActionListener {
		private String _filename;
		public ViewStartListener(String filename) {
			_filename = filename;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Main.view.containerWindow.fileLoadPanel.addLoadFileListener(new LoadFileListener());
			if (_filename != null) loadFile(_filename);
		}
	}
	
	class LoadFileListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String filename = e.getActionCommand();
			loadFile(filename);
		}	
	}
	
	class DeleteRowListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int row = Integer.parseInt(e.getActionCommand());
			Main.model.uRemVector(List2d.ROW, row);
		}
		
	}
}
