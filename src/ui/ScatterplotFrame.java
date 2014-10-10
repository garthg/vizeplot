package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import selection.SelectionEvent;
import selection.SelectionListener;

import db.CategoryColumn;
import db.ColumnHead;
import db.List2d;

import editing.DataLoadedEvent;
import editing.EditingEvent;
import editing.EditingListener;

import main.Main;

public class ScatterplotFrame extends JFrame implements EditingListener, SelectionListener {
	
	private static final long serialVersionUID = -3500073714844967313L;
	private static final int plotDimensions = 3;
	private JComboBox[] axisComboBoxes = new JComboBox[plotDimensions];
	private Scatterplot plot;
	private Integer _plotNumber;
	
	public ScatterplotFrame(Integer plotNumber) {
		super();
		
		Main.model.addEditingListener(this);
		Main.model.addSelectionListener(this);
		
		addWindowListener(VisView.windowFocusListener);
		
		setPreferredSize(new Dimension(500,500));
		Point parentLoc = Main.view.containerWindow.getLocationOnScreen();
		setLocation(parentLoc.x+20, parentLoc.y+20);
		
		_plotNumber = plotNumber;
		if (plotNumber != null) setTitle("Scatterplot "+plotNumber+" - Vize");
		else setTitle("Scatterplot - Vize");
		
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2,3));
		topPanel.add(new JLabel("X Axis"));
		topPanel.add(new JLabel("Y Axis"));
		topPanel.add(new JLabel("Color"));
		for (int i=0; i<axisComboBoxes.length; i++) {
			axisComboBoxes[i] = new JComboBox();
			axisComboBoxes[i].addActionListener(new ComboBoxListener());
			topPanel.add(axisComboBoxes[i]);
		}
		add(topPanel,BorderLayout.NORTH);
		
		plot = new Scatterplot();
		add(plot,BorderLayout.CENTER);
		
		_refreshData();
		
		pack();
		setVisible(true);
	}
	
	public ScatterplotFrame() {
		this(null);
	}
	
	public String toString() {
		String str = "Vize Scatterplot";
		if (_plotNumber != null) str += " " + Integer.toString(_plotNumber);
		return str;
	}

	private void _refreshData() {
		String[] colNames = Main.model.getColumnNames();
		for (int i=0; i<axisComboBoxes.length; i++) {
			Object prevSelected = axisComboBoxes[i].getSelectedItem();
			DefaultComboBoxModel cbm = new DefaultComboBoxModel(colNames);
			int prevInd;
			if (prevSelected == null) prevInd = -1;
			else prevInd = cbm.getIndexOf(prevSelected);
			axisComboBoxes[i].setModel(cbm);
			axisComboBoxes[i].setSelectedIndex(prevInd);
		}
		plot.refreshData();
	}
	
	@Override
	public void editingPerformed(EditingEvent e) {
		if (e instanceof DataLoadedEvent || e.getTarget() instanceof List2d<?>) {
			_refreshData();
		}
	}
	
	@Override
	public void selectionChanged(SelectionEvent e) {
		plot.repaint();
	}
	
	public List<Integer> getCurrentAxes() {
		Vector<Integer> result = new Vector<Integer>(axisComboBoxes.length);
		for (int i=0; i<axisComboBoxes.length; i++) {
			result.add(axisComboBoxes[i].getSelectedIndex());
		}
		return result;
	}
	
	private class Scatterplot extends JPanel {
		
		private JLabel[] axisLabels = null;
		private PlotDataPanel plotDataPanel = new PlotDataPanel();
		private static final long serialVersionUID = -4370160539715131944L;
		
		public Scatterplot() {
			setPreferredSize(new Dimension(400,400));
			setLayout(new BorderLayout());
			
			axisLabels = new JLabel[plotDimensions*2];
			for (int i=0; i<axisLabels.length; i++) {
				axisLabels[i] = new JLabel("label"+i);
			}
			
			/*
			JPanel pTop = new JPanel();
			add(p1top,BorderLayout.NORTH);
			
			JPanel pCenter = new JPanel();
			add(p1center,BorderLayout.CENTER);
			p1center.setLayout(new BorderLayout());
			
			JPanel pCenterLeft = new JPanel();
			
			JPanel p1bottom = new JPanel();
			add(p1bottom,BorderLayout.SOUTH);
			*/
			
			
			JPanel bottomPanel = new JPanel();
			//bottomPanel.setLayout(new GridLayout(2,1));
			bottomPanel.setLayout(new GridLayout(1,1));
			JPanel bottomXAxisPanel = new JPanel();
			bottomXAxisPanel.setLayout(new BorderLayout());
			bottomXAxisPanel.add(axisLabels[0],BorderLayout.WEST);
			bottomXAxisPanel.add(axisLabels[1],BorderLayout.EAST);
			bottomXAxisPanel.add(Box.createVerticalStrut(50));
			bottomPanel.add(bottomXAxisPanel);
			
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new BorderLayout());
			leftPanel.add(axisLabels[2],BorderLayout.SOUTH);
			leftPanel.add(axisLabels[3],BorderLayout.NORTH);
			leftPanel.add(Box.createHorizontalStrut(50));
			add(leftPanel,BorderLayout.WEST);
			
			/*
			JPanel colorPanel = new JPanel();
			colorPanel.setLayout(new BorderLayout());
			colorPanel.add(axisLabels[4],BorderLayout.WEST);
			colorPanel.add(axisLabels[5],BorderLayout.EAST);
			colorPanel.setBackground(new Color(1f,0f,0f));
			bottomPanel.add(colorPanel);
			*/
			
			add(bottomPanel,BorderLayout.SOUTH);
			
			add(plotDataPanel,BorderLayout.CENTER);
			
			
			refreshData();
		}
		
		public void refreshData() {
			if (axisLabels != null) {
				for (int i=0; i<axisComboBoxes.length; i++) {
					int currCol = axisComboBoxes[i].getSelectedIndex();
					if (currCol >= 0 && currCol < Main.model.numCols()) {
						axisLabels[i*2].setText(Main.model.getMin(currCol).toString());
						axisLabels[i*2+1].setText(Main.model.getMax(currCol).toString());
					} else {
						axisLabels[i*2].setText("");
						axisLabels[i*2+1].setText("");
					}
				}
				int[] cols = new int[plotDimensions];
				for (int i=0; i<axisComboBoxes.length; i++) cols[i] = axisComboBoxes[i].getSelectedIndex();
				plotDataPanel.setColumns(cols);
			}
		}
		
		private class PlotDataPanel extends JPanel {
			private static final long serialVersionUID = -8903295235252282925L;
			
			// final variables
			private final Color[] _categoryColors = {
					Color.RED,
					Color.GREEN,
					Color.BLUE,
					Color.ORANGE,
					Color.PINK,
					Color.CYAN,
					Color.MAGENTA,
					Color.YELLOW
					};
			private final float _pointAlpha = 0.5f;
			private final int _padding = 10;
			private final int _pointDiameter = 10;
			private final int _pointRadius = _pointDiameter/2;
			private final int _selectedDiameter = _pointDiameter+10;
			private final int _selectedRadius = _selectedDiameter/2;
			private final Color _selectionRectColor = new Color(.6f,.6f,.9f,.5f);
			private final Color _selectedPointColor = new Color(.5f,.5f,.5f,.5f);
			
			// changes when setColumns
			private final int[] _colInds = new int[plotDimensions];
			
			// changes when updatePaintData
			private boolean _colorAsCategory;
			private final Vector<List<Float>> _vals = new Vector<List<Float>>(_colInds.length);
			private final float[] _mins = new float[_colInds.length];
			private final float[] _ranges = new float[_colInds.length];
			private final float[] _coeffs = new float[_colInds.length];
			
			// selection data
			private final int[] _selectionRect = new int[4];
			private boolean _selectionActive = false;
			private final int _mouseClickJitter = 6;
			private Vector<Boolean> _prevSelectionCol;
			
			public PlotDataPanel() {
				setBackground(Color.WHITE);
				for (int i=0; i<_colInds.length; i++) _colInds[i] = -1;
				
				MouseSelectionListener l = new MouseSelectionListener();
				addMouseListener(l);
				addMouseMotionListener(l);
				
				setFocusable(true);
				requestFocusInWindow();
				addKeyListener(new KeySelectionListener());
				
				for (int i=0; i<_categoryColors.length; i++) {
					Color c = _categoryColors[i];
					_categoryColors[i] = new Color(c.getRed(),c.getGreen(),c.getBlue(),Math.round(_pointAlpha*256));
				}
			}
			
			public void setColumns(int[] cols) {
				for (int i=0; i<cols.length; i++) _colInds[i] = cols[i];
				for (int i=0; i<4; i++) {
					_selectionRect[i] = -1;
				}
				updatePaintData();
				repaint();
			}
			
			private void updatePaintData() {
				_vals.clear();
				for (int i=0; i<_colInds.length; i++) {
					int colInd = _colInds[i];
					if (colInd >= 0 && colInd < Main.model.numCols()) {
						_vals.add(Main.model.getVector(List2d.COL, colInd));
					} else {
						int numRows = Main.model.numRows();
						Vector<Float> v = new Vector<Float>(numRows);
						for (int j=0; j<numRows; j++) v.add(0f);
						_vals.add(v);
					}
				}
				
				_colorAsCategory = false;
				if (_colInds[2] >= 0 && _colInds[2] < Main.model.numCols()) {
					ColumnHead colorHead = Main.model.getHeader(_colInds[2]);
					if (colorHead instanceof CategoryColumn) {
						CategoryColumn colorHeadC = (CategoryColumn)colorHead;
						if (colorHeadC.categories.length <= _categoryColors.length) _colorAsCategory = true;
					}
				}
				
				int colMaxInt = _colorAsCategory?_colInds.length-1:_colInds.length;
				for (int i=0; i<colMaxInt; i++) {
					int colInd = _colInds[i];
					if (colInd >= 0 && colInd < Main.model.numCols()) {
						_mins[i] = Main.model.getMin(colInd);
						_ranges[i] = Main.model.getMax(colInd)-_mins[i];
						_coeffs[i] = 1/_ranges[i];
					} else {
						_mins[i] = _ranges[i] = _coeffs[i] = 0;
					}
				}
				
			}
			
			private Integer _valX(int index) {
				Float val = _vals.get(0).get(index);
				if (val == null) return null;
				else {
					int width = getWidth() - 2*_padding;
					float coeff = _coeffs[0]*width;
					float fval = (val-_mins[0])*coeff;
					return Math.round(fval) + _padding;
				}
			}
			
			private Integer _valY(int index) {
				Float val = _vals.get(1).get(index);
				if (val == null) return null;
				else {
					int height = getHeight() - 2*_padding;
					float coeff = _coeffs[1]*height;
					float fval = (val-_mins[1])*coeff;
					return Math.round(fval) + _padding;
				}
			}
			
			private Color _valColor(int index) {
				Float val = _vals.get(2).get(index);
				if (val == null) return null;
				else {
					if (_colorAsCategory) {
						return _categoryColors[Math.round(val)];
					} else {
						float fval = (val-_mins[2])*_coeffs[2];
						return new Color(fval,0f,1-fval,_pointAlpha);
					}
				}
			}
			
			private void paintPoints(Graphics g) {
				boolean renderPoints = false;
				for (int i : _colInds) if (i >= 0) renderPoints = true;
				
				if (renderPoints) {

					Integer x,y;
					Color c;
					
					int numRows = Main.model.numRows();
					
					if (Main.model.anyPointsSelected()) {
						for (int i=0; i<numRows; i++) {
							x = _valX(i);
							y = _valY(i);
							
							if (x != null && y != null) {
								
								if (Main.model.isSelected(i)) {
									g.setColor(_selectedPointColor);
									g.fillOval(
											x-_selectedRadius, 
											y-_selectedRadius,
											_selectedDiameter, 
											_selectedDiameter);
								}
							}
						}
					}
					
					for (int i=0; i<numRows; i++) {
						
						x = _valX(i);
						y = _valY(i);
						c = _valColor(i);
						
						if (x != null && y != null && c != null) {		
							g.setColor(c);
							g.fillOval(
									x-_pointRadius, 
									y-_pointRadius, 
									_pointDiameter, 
									_pointDiameter);
						}
					}
				}
			}
			
			private void paintSelectionRect(Graphics g) {
				if (_selectionActive) {
					g.setColor(_selectionRectColor);
					g.fillRect(
							_selectionRect[0],
							_selectionRect[1],
							_selectionRect[2],
							_selectionRect[3]);
				}
			}
			
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintSelectionRect(g);
				paintPoints(g);
			}
			
			private boolean coordsInSelectionRect(int x, int y) {
				return (
						x >= _selectionRect[0] && 
						y >= _selectionRect[1] && 
						x-_selectionRect[0] <= _selectionRect[2] && 
						y-_selectionRect[1] <= _selectionRect[3]);
			}
			
			private void _setSelectedPoints(List<Integer> indices) {
				switch (VisView.selectionMode) {
				case 0: // none
					Main.model.setSelectedRows(indices);
					break;
				case 1: // ctrl
					Main.model.setRowsSelected(indices, false);
					break;
				case 2: // shift
					Main.model.setRowsSelected(indices, true);
					break;
				case 3: // ctrl+shift
					ArrayList<Integer> deselectIndices = new ArrayList<Integer>();
					for (int i=0; i<indices.size(); i++) {
						int ind = indices.get(i);
						if (_prevSelectionCol.get(ind)) {
							deselectIndices.add(ind);
						}
					}
					indices.removeAll(deselectIndices);
					Main.model.setRowsSelected(deselectIndices, false);
					Main.model.setRowsSelected(indices, true);
					break;
				default:
					break;
				}
			}
			
			private void _setSelectedPointsFromRect() {				
				ArrayList<Integer> indices = new ArrayList<Integer>();
				Integer x,y;
				for (int i=0; i<Main.model.numRows(); i++) {
					x = _valX(i);
					y = _valY(i);
					if (x != null && y != null) {
						if (coordsInSelectionRect(x, y)) {
							indices.add(i);
						}
					}
					
				}
				_setSelectedPoints(indices);
			}
			
			private void _setSelectedPointsFromPoint(int x, int y) {
				ArrayList<Integer> indices = new ArrayList<Integer>();
				Integer valX, valY;
				for (int i=0; i<Main.model.numRows(); i++) {
					valX = _valX(i);
					valY = _valY(i);
					if (valX != null && valY != null) {
						if (Math.abs(valX-x) < _mouseClickJitter && Math.abs(valY-y) < _mouseClickJitter) {
							indices.add(i);
						}
					}
				}
				_setSelectedPoints(indices);
			}
			
			private class MouseSelectionListener extends MouseInputAdapter {
				
				private int[] _mouseDown = new int[2];
				private int[] _mouseCurr = new int[2];
				
				private void _setSelectionRectFromMouse() {
					int x0 = _mouseDown[0];
					int y0 = _mouseDown[1];
					int x1 = _mouseCurr[0];
					int y1 = _mouseCurr[1];
					_selectionRect[0] = (x0 < x1)?x0:x1;
					_selectionRect[1] = (y0 < y1)?y0:y1;
					_selectionRect[2] = Math.abs(x0-x1);
					_selectionRect[3] = Math.abs(y0-y1);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					requestFocusInWindow();
					if (e.getButton() == MouseEvent.BUTTON1) {
						_prevSelectionCol = Main.model.getSelectionColumnCopy();
						_selectionActive = true;
						if (VisView.selectionMode == 0) Main.model.setSelectedRows(null);
						_mouseDown[0] = e.getX();
						_mouseDown[1] = e.getY();
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					_mouseDown[0] = _mouseDown[1] = _mouseCurr[0] = _mouseCurr[1] = -1;
					if (_selectionActive) {
						_selectionActive = false;
						_setSelectionRectFromMouse();
						Main.model.doneSelectingRows();
						repaint();
					}
				}
				
				@Override 
				public void mouseDragged(MouseEvent e) {
					if (_selectionActive) {
						_mouseCurr[0] = e.getX();
						_mouseCurr[1] = e.getY();
						_setSelectionRectFromMouse();
						_setSelectedPointsFromRect();
						repaint();
					}
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					_setSelectedPointsFromPoint(e.getX(), e.getY());
					Main.model.doneSelectingRows();
				}
				
			} // end inner class MouseSelectionListener
			
			private class KeySelectionListener implements KeyListener {
				
				private void _setSelectionMode() {
					int mode = 0;
					if (VisView.shift) mode += 2;
					if (VisView.ctrl) mode += 1;
					VisView.selectionMode = mode;
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					int keyCode = e.getKeyCode();
					if (keyCode == KeyEvent.VK_CONTROL) VisView.ctrl = true;
					if (keyCode == KeyEvent.VK_SHIFT) VisView.shift = true;
					_setSelectionMode();
				}

				@Override
				public void keyReleased(KeyEvent e) {
					int keyCode = e.getKeyCode();
					if (keyCode == KeyEvent.VK_CONTROL) VisView.ctrl = false;
					if (keyCode == KeyEvent.VK_SHIFT) VisView.shift = false;
					_setSelectionMode();
				}

				@Override
				public void keyTyped(KeyEvent e) { }
				
			} // end inner class KeySelectionListener
			
		} // end inner class PlotDataPanel
		
	} // end inner class Scatterplot
	
	private class ComboBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			plot.refreshData();
		}
		
	}
}
