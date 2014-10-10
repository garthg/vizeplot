package ui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import editing.EditingEvent;
import editing.EditingListener;
import editing.UndoableEditingAction;

import main.Main;

public class MainMenuBar extends MenuBar implements EditingListener {
	
	private MenuItem editUndoItem;
	private Menu editRedoMenu;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8648195999870453299L;

	public MainMenuBar() {
		super();
		
		Main.model.addEditingListener(this);
		
		NonRedoMenuListener l = new NonRedoMenuListener();
		
		Menu fileMenu = new Menu("File");
		
		Menu editMenu = new Menu("Edit");
		
		MenuItem fileExitItem = new MenuItem("Exit");
		fileExitItem.addActionListener(l);
		
		editUndoItem = new MenuItem("Undo");
		editUndoItem.addActionListener(l);
		
		editRedoMenu = new Menu("Redo");
		
		fileMenu.add(fileExitItem);
		editMenu.add(editUndoItem);
		editMenu.add(editRedoMenu);
		add(fileMenu);
		add(editMenu);
		
		
		
	}
	
	class NonRedoMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String actionStr = e.getActionCommand();
			if (actionStr.equals("Exit")) System.exit(0);
			if (actionStr.startsWith("Undo")) Main.model.undo();
		}
	}
	
	
	class MenuEditRedoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Main.model.redo(Integer.parseInt(e.getActionCommand()));
		}
		
	}

	@Override
	public void editingPerformed(EditingEvent e) {
		boolean canUndo = Main.model.canUndo(), canRedo = Main.model.canRedo();
		
		editUndoItem.setEnabled(canUndo);
		editRedoMenu.setEnabled(canRedo);
		
		if (canUndo) editUndoItem.setLabel("Undo "+Main.model.actionWouldUndo().toShortString());
		else editUndoItem.setLabel("Can't undo");
		
		if (canRedo) {
			editRedoMenu.setLabel("Redo");
			editRedoMenu.removeAll();
			MenuEditRedoListener l = new MenuEditRedoListener();
			List<UndoableEditingAction> redoActions = Main.model.actionsCanRedo();
			for (int i=redoActions.size()-1; i>=0; i--) {
				MenuItem item = new MenuItem("Redo "+redoActions.get(i).toShortString());
				item.setActionCommand(Integer.toString(i));
				item.addActionListener(l);
				editRedoMenu.add(item);
			}
		} else {
			editRedoMenu.setLabel("Can't redo");
			editRedoMenu.removeAll();
		}
	}
	
}
