package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Main;

import editing.DataLoadedEvent;
import editing.EditingEvent;
import editing.EditingListener;


public class FileLoadPanel extends JPanel implements EditingListener,ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2011692803517790774L;
	
	private String loadBtnStr = "Load";
	private JLabel fileLabel;
	
	private ArrayList<ActionListener> _loadFileListeners = new ArrayList<ActionListener>();
	
	public synchronized void addLoadFileListener(ActionListener l) {
		_loadFileListeners.add(l);
	}
	
	public synchronized void removeLoadFileListener(ActionListener l) {
		_loadFileListeners.remove(l);
	}
	
	private synchronized void _dispatchLoadFileEvent(ActionEvent e) {
		for (ActionListener l : _loadFileListeners) l.actionPerformed(e);
	}
	
	public FileLoadPanel() {
		Main.model.addEditingListener(this);
		
		JButton loadButton = new JButton(loadBtnStr);
		loadButton.addActionListener(this);
		
		fileLabel = new JLabel();
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(loadButton);
		add(fileLabel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(loadBtnStr)) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("CSV file","csv","CSV"));
			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				String fname = fileChooser.getSelectedFile().getPath();
				_dispatchLoadFileEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, fname));
			}
		}
	}

	@Override
	public void editingPerformed(EditingEvent e) {
		if (e instanceof DataLoadedEvent) {
			String name = Main.model.getFilename();
			if (name != null) fileLabel.setText(name);
			else fileLabel.setText("Error loading file.");
		}
	}
}
