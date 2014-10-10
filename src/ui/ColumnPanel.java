package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import db.ColumnHead;
import db.List2d;

import editing.DataLoadedEvent;
import editing.EditingEvent;
import editing.EditingListener;

import main.Main;

public class ColumnPanel extends JPanel implements EditingListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8779078698968124670L;
	
	private JList columnList = new JList();
	private JLabel columnLabel = new JLabel();
	private JPanel rightPanel = new JPanel();
	
	public ColumnPanel() {
		Main.model.addEditingListener(this);
		columnList.addListSelectionListener(new ColumnSelectionListener());
		columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayout(new GridLayout(1,2));
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(new JLabel("Dataset columns:"),BorderLayout.NORTH);
		leftPanel.add(columnList,BorderLayout.CENTER);
		add(leftPanel);
		leftPanel.setAlignmentX(0f);
		rightPanel.setLayout(new GridLayout(2,1));
		rightPanel.add(columnLabel);
		JButton delButton = new JButton("Delete column");
		delButton.addActionListener(new DeleteButtonListener());
		rightPanel.add(delButton);
		rightPanel.setVisible(false);
		add(rightPanel);
	}

	private void refreshColumnList() {
		columnList.setListData(Main.model.getColumnNames());
		columnList.setSelectedIndex(-1);
	}
	
	@Override
	public void editingPerformed(EditingEvent e) {
		if (e instanceof DataLoadedEvent || e.getTarget() instanceof List2d<?>) {
			refreshColumnList();
		}
	}
	
	class ColumnSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			int index = columnList.getSelectedIndex();
			if (index < 0) rightPanel.setVisible(false);
			else {
				ColumnHead h = Main.model.getHeader(columnList.getSelectedIndex());
				columnLabel.setText(h.getDataTypeString());
				rightPanel.setVisible(true);
			}
		}
	}
	
	class DeleteButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Main.model.uRemVector(List2d.COL, columnList.getSelectedIndex());
		}	
	}
}
