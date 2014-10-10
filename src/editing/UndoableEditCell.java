package editing;

import db.List2d;


public class UndoableEditCell<Type> extends UndoableEditingAction {
	protected final List2d<Type> _target;
	protected final int _row, _col;
	private Type oldValue, newValue;

	public UndoableEditCell(List2d<Type> actionTarget, int row, int col, Type val) {
		super(actionTarget);
		_target = actionTarget;
		_row = row;
		_col = col;
		newValue = val;
	}
	
	protected void doActionBody() {
		oldValue = _target.get(_row, _col);
		_target.set(_row, _col, newValue);
	}
	
	protected void undoActionBody() {
		_target.set(_row, _col, oldValue);
	}
	
	@Override
	public String toShortString() {
		return "edit cell";
	}

	@Override
	public String toString() {
		return "Edit cell ("+_row+", "+_col+") to "+newValue+".";
	}

	public List2d<Type> getTarget() {
		return _target;
	}
	
	public int getRow() {
		return _row;
	}
	
	public int getCol() {
		return _col;
	}
	
	public Type getValue() {
		return newValue;
	}
	
}
