package ui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class VisView {
	public ContainerWindow containerWindow;
	
	private static Window _activeWindow;
	public static boolean ctrl = false;
	public static boolean shift = false;
	public static int selectionMode = 0;
	public static FocusListener windowFocusListener;
	
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	
	public VisView() {
		windowFocusListener = new FocusListener();
	}
	
	public void addStartListener(ActionListener l) {
		listeners.add(l);
	}
	
	public void remStartListener(ActionListener l) {
		listeners.remove(l);
	}
	
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				containerWindow = new ContainerWindow();
				for (ActionListener l : listeners) {
					l.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"started"));
				}
			}
		});
	}
	
	public void alert(String alertStr) {
		JOptionPane.showMessageDialog(containerWindow, alertStr, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	private class FocusListener extends WindowAdapter {
		public void windowActivated(WindowEvent e) {
			_activeWindow = e.getWindow();
		}
	}
	
	public Window getActiveWindow() {
		return _activeWindow;
	}

}
