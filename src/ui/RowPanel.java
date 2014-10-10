package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import selection.SelectionEvent;
import selection.SelectionFinalizedEvent;
import selection.SelectionListener;

import db.CategoryColumn;
import db.ColumnHead;
import db.List2d;

import editing.DataLoadedEvent;
import editing.EditingEvent;
import editing.EditingListener;

import main.Main;

public class RowPanel extends JPanel implements EditingListener, SelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -887707164918846537L;

	private JSlider slider = new JSlider(0,0);
	private SingleRowModel tableModel = new SingleRowModel();
	private JTable table = new JTable(tableModel);
	private JLabel selectedLabel = new JLabel();
	private JTextField editingField = new JTextField();
	private JLabel editingLabel = new JLabel();
	private JList editingList = new JList();
	private JPanel rightBottomPanel = new JPanel();
	private JButton updateButton = new JButton("Update");
	
	public RowPanel() {
		Main.model.addEditingListener(this);
		Main.model.addSelectionListener(this);
		
		slider.setOrientation(JSlider.VERTICAL);
		slider.setSnapToTicks(true);
		slider.addChangeListener(new SliderListener());
		
		//setLayout(new GridLayout(1,2));
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		//leftPanel.add(new JLabel("Row:"),BorderLayout.NORTH);
		leftPanel.add(slider,BorderLayout.CENTER);
		leftPanel.setAlignmentX(0f);
		leftPanel.add(Box.createVerticalStrut(50),BorderLayout.WEST);
		leftPanel.add(Box.createHorizontalStrut(20),BorderLayout.EAST);
		add(leftPanel);
		
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getSelectionModel().addListSelectionListener(new SelectionListener());
		JButton delButton = new JButton("Delete row");
		delButton.addActionListener(new ButtonListener());
		
		JPanel rightPanel = new JPanel();
		JPanel rightTopPanel = new JPanel();
		
		rightTopPanel.setLayout(new BorderLayout());
		rightTopPanel.add(selectedLabel,BorderLayout.NORTH);
		rightTopPanel.add(table,BorderLayout.CENTER);
		rightTopPanel.setAlignmentX(1f);
		rightTopPanel.add(delButton,BorderLayout.SOUTH);
		editingField.addActionListener(new EditingFieldListener());
		updateButton.addActionListener(new ButtonListener());
		rightBottomPanel.setVisible(false);
		rightBottomPanel.add(editingField);
		rightBottomPanel.add(editingList);
		rightBottomPanel.add(editingLabel);
		rightBottomPanel.add(updateButton);
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(rightTopPanel,BorderLayout.NORTH);
		rightPanel.add(rightBottomPanel,BorderLayout.SOUTH);
		add(rightPanel);
		
		
	}

	class SingleRowModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2286049020703931445L;

		@Override
		public int getColumnCount() {
			return Main.model.numCols();
		}

		@Override
		public int getRowCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int row, int col) {
			return Main.model.cellToString(_getSelectedRow(), col);
		}
		
	}
	
	class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Delete row"))
				Main.model.uRemVector(List2d.ROW, slider.getValue());	
			if (e.getActionCommand().equals("Update")) {
				Main.model.uSet(_getSelectedRow(), table.getSelectedColumn(), editingList.getSelectedIndex()*1f);
			}
		}
		
	}
	
	class SelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int selectedRow = _getSelectedRow();
			int selectedCol = table.getSelectedColumn();
			if (selectedCol < 0 || selectedRow < 0) rightBottomPanel.setVisible(false);
			else {
				ColumnHead h = Main.model.getHeader(selectedCol);
				if (h instanceof CategoryColumn) {
					CategoryColumn hc = (CategoryColumn)h;
					editingList.setListData(hc.categories);
					editingList.setSelectedIndex(Math.round(Main.model.get(selectedRow, selectedCol)));
					editingList.setVisible(true);
					updateButton.setVisible(true);
					editingField.setVisible(false);
				} else {
					editingField.setText(Main.model.cellToString(selectedRow, selectedCol));
					editingField.setVisible(true);
					editingList.setVisible(false);
					updateButton.setVisible(false);
				}
				
				
				rightBottomPanel.setVisible(true);
			}
		}
	}
	
	class EditingFieldListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ColumnHead h = Main.model.getHeader(table.getSelectedColumn());
			try {
				Float value = (Float)h.toValue(editingField.getText());
				editingLabel.setVisible(false);
				Main.model.uSet(_getSelectedRow(), table.getSelectedColumn(), value);
			} catch (IllegalArgumentException err) {
				editingLabel.setText("bad input");
				editingLabel.setVisible(true);
			}
		}
		
	}
	
	private void _refreshRowDisplay() {
		int selectedCol = table.getSelectedColumn();
		tableModel.fireTableDataChanged();
		tableModel.fireTableStructureChanged();
		if (selectedCol >= 0 && selectedCol < tableModel.getColumnCount())
			table.getSelectionModel().setSelectionInterval(selectedCol, selectedCol);
	}
	
	@Override
	public void editingPerformed(EditingEvent e) {
		if (e instanceof DataLoadedEvent || e.getTarget() instanceof List2d<?>) {
			int nextMax = Main.model.numRows()-1;
			boolean setToMax = (slider.getValue() > nextMax || slider.getMaximum() == 0);
			slider.setMaximum(nextMax);
			if (setToMax) slider.setValue(nextMax);
			_refreshRowDisplay();
		}
	}
	
	class SliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			selectedLabel.setText("Row "+Integer.toString(_getSelectedRow()));
			tableModel.fireTableDataChanged();
		}
		
	}
	
	private int _getSelectedRow() {
		return slider.getMaximum() - slider.getValue();
	}
	
	private void _setSelectedRow(int row) {
		slider.setValue(slider.getMaximum() - row);
	}

	@Override
	public void selectionChanged(SelectionEvent e) {
		if (e instanceof SelectionFinalizedEvent) {
			SelectionFinalizedEvent se = (SelectionFinalizedEvent)e;
			List<Integer> selection = se.getSelectedRows();
			if (selection.size() > 0) _setSelectedRow(selection.get(0));
		}
	}
}
