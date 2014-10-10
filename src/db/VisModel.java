package db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import selection.RowSelectionEvent;
import selection.SelectionEvent;
import selection.SelectionFinalizedEvent;
import selection.SelectionListener;

import csv.CSVHandler;
import editing.*;

public class VisModel extends HeadedArrayList2d<Float> {
	
	private String _filename;
	private EditingTree _editingTree;
	private ArrayList<EditingListener> _editingListeners = new ArrayList<EditingListener>();
	private ArrayList<SelectionListener> _selectionListeners = new ArrayList<SelectionListener>();
	private ArrayList<Boolean> _selectionColumn;
	private boolean _hasData;
	
	private void init() {
		_editingTree = new EditingTree();
		_selectionColumn = new ArrayList<Boolean>();
		_hasData = false;
	}
	
	public String getFilename() {
		return _filename;
	}
	
	public boolean hasData() {
		return _hasData;
	}
	
	@Override
	public void clear() {
		super.clear();
		init();
	}
	
	@Override
	public void insVector(int dimension, int index, List<Float> v) 
	throws IllegalArgumentException,IndexOutOfBoundsException 
	{
		super.insVector(dimension, index, v);
		if (dimension == ROW) _selectionColumn.add(index, false);
	}
	
	@Override
	public void remVector(int dimension, int index) 
	throws IndexOutOfBoundsException 
	{
		super.remVector(dimension, index);
		if (dimension == ROW) _selectionColumn.remove(index);
	}
	
	public boolean isSelected(int row) {
		return _selectionColumn.get(row);
	}
	
	private void doUndoableEditingAction(UndoableEditingAction a) {
		_editingTree.doAction(a);
		_dispatchEditingEvent();
	}
	
	public void uInsVector(int dimension, int index, List<Float> v) {
		doUndoableEditingAction(new UndoableInsVect<Float>(this,dimension,index,v));
	}
	
	public void uInsVector(int dimension, List<Float> v) {
		doUndoableEditingAction(new UndoableInsVect<Float>(this,dimension,v));
	}

	public void uRemVector(int dimension, int index) {
		UndoableEditingAction a;
		if (dimension == List2d.COL) a = new UndoableRemHeadedColumn<Float>(this,index); 
		else a = new UndoableRemVect<Float>(this,dimension,index);
		doUndoableEditingAction(a);
	}

	public void uSet(int row, int col, Float val) {
		doUndoableEditingAction(new UndoableEditCell<Float>(this,row,col,val));
	}
	
	public void uSetFromString(int row, int col, String str) {
		Float val = (Float)this.getHeader(col).toValue(str);
		uSet(row, col, val);
	}
	
	public boolean canUndo() {
		return _editingTree.canUndo();
	}

	public boolean canRedo() {
		return _editingTree.canRedo();
	}
	
	public void undo() {
		UndoableEditingAction a = actionWouldUndo();
		_editingTree.undoAction();
		_dispatchEditingEvent(a);
	}
	
	public void redo() throws NoSuchElementException {
		_editingTree.redoAction();
		_dispatchEditingEvent();
	}
	
	public void redo(int i) throws NoSuchElementException {
		_editingTree.redoAction(i);
		_dispatchEditingEvent();
	}
	
	public void printEditingTree() {
		_editingTree.print();
	}
	
	public UndoableEditingAction actionWouldUndo() {
		return _editingTree.curr.data;
	}
	
	public UndoableEditingAction actionWouldRedo() throws NoSuchElementException {
		return _editingTree.curr.getLastChild().data;
	}
	
	public List<UndoableEditingAction> actionsCanRedo() throws NoSuchElementException {
		List<TreeNode<UndoableEditingAction>> nodes = _editingTree.curr.getChildren();
		Vector<UndoableEditingAction> result = new Vector<UndoableEditingAction>(nodes.size());
		for (int i=0; i<nodes.size(); i++) result.add(nodes.get(i).data);
		return result;
	}
	
	public synchronized void addEditingListener(EditingListener l) {
		_editingListeners.add(l);
	}
	
	public synchronized void removeEditingListener(EditingListener l) {
		_editingListeners.remove(l);
	}
	
	public synchronized void addSelectionListener(SelectionListener l) {
		_selectionListeners.add(l);
	}
	
	public synchronized void removeSelectionListener(SelectionListener l) {
		_selectionListeners.remove(l);
	}
	
	private synchronized void _dispatchEditingEvent(EditingEvent e) {
		for (EditingListener l : _editingListeners) {
			l.editingPerformed(e);
		}
	}
	
	private synchronized void _dispatchSelectionEvent(SelectionEvent e) {
		for (SelectionListener l : _selectionListeners) {
			l.selectionChanged(e);
		}
	}
	
	private synchronized void _dispatchEditingEvent() {
		_dispatchEditingEvent(_editingTree.curr.data);
	}
	
	private synchronized void _dispatchEditingEvent(EditingAction a) {
		_dispatchEditingEvent(new EditingEvent(this, a));
	}
	
	public void loadFromFile(String filename) throws FileNotFoundException, IOException {
		_filename = null;
		clear();
		CSVHandler csvh = new CSVHandler();
		setHeaders(csvh.readFile(filename, this));
		_filename = filename;
		_hasData = true;
		_dispatchEditingEvent(new DataLoadedEvent(this,filename));
	}
	
	public String[] getColumnNames() {
		String[] colNames = new String[headers.size()];
		for (int i=0; i<headers.size(); i++) {
			if (headers.get(i).columnName == null) colNames[i] = "<column "+Integer.toString(i)+">";
			else colNames[i] = headers.get(i).columnName;
		}
		return colNames;
	}
	
	public Float getMax(int column) {
		List<Float> vect = getVector(COL,column);
		Float max = Float.MIN_VALUE;
		for (Float n : vect) if (n != null) max = Math.max(max, n);
			return max;
	}
	
	public Float getMin(int column) {
		List<Float> vect = getVector(COL,column);
		Float min = Float.MAX_VALUE;
		for (Float n : vect) if (n != null) min = Math.min(min, n);
		return min;
	}
	
	public void setRowSelected(int index, boolean selected) 
	throws IndexOutOfBoundsException
	{
		if (index < numRows()) {
			_selectionColumn.set(index, selected);
			_dispatchSelectionEvent(new RowSelectionEvent(this));
		} else throw new IndexOutOfBoundsException();
		
	}
	
	/*
	public void uSetSelectedRows(Collection<Integer> indices) {
		doUndoableEditingAction(new UndoableSetSelectedRows(this,indices));
	}
	
	public void uSetRowsSelected(Collection<Integer> indices, boolean selected) {
		doUndoableEditingAction(new UndoableSetRowsSelected(this,indices,selected));
	}
	*/
	
	public void setSelectedRows(Collection<Integer> indices) 
	throws IndexOutOfBoundsException
	{
		for (int i=0; i<numRows(); i++) {
			_selectionColumn.set(i, false);
		}
		setRowsSelected(indices, true);
	}
	
	public void setRowsSelected(Collection<Integer> indices, boolean selected) 
	throws IndexOutOfBoundsException
	{
		int numRows = numRows();
		if (indices != null) {
			for (int i : indices) {
				if (i >= 0 && i < numRows) {
					_selectionColumn.set(i,selected);
				} else throw new IndexOutOfBoundsException("index out of bounds: "+i);
			}
		}
		_dispatchSelectionEvent(new RowSelectionEvent(this));
	}
	
	public Vector<Boolean> getSelectionColumnCopy() {
		Vector<Boolean> v = new Vector<Boolean>(numRows());
		for (int i=0; i<numRows(); i++) {
			if (_selectionColumn.get(i)) v.add(true);
			else v.add(false);
		}
		return v;
	}
	
	public List<Integer> getSelectedRows() {
		Vector<Integer> v = new Vector<Integer>();
		for (int i=0; i<numRows(); i++) {
			if (_selectionColumn.get(i)) v.add(i);
		}
		return v;
	}
	
	public boolean anyPointsSelected() {
		for (boolean b : _selectionColumn) {
			if (b) return true;
		}
		return false;
	}
	
	public void doneSelectingRows() {
		// TODO put in undo
		_dispatchSelectionEvent(new SelectionFinalizedEvent(this,getSelectedRows()));
	}

}
