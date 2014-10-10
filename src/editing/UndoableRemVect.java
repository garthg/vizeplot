package editing;

import java.util.List;

import db.List2d;


public class UndoableRemVect<Type> extends UndoableEditingAction {
	
	protected final List2d<Type> _target;
	protected final int _dimension, _index;
	protected List<Type> removedVector;

	public UndoableRemVect(List2d<Type> actionTarget, int dimension, int index) {
		super(actionTarget);
		_target = actionTarget;
		_dimension = dimension;
		_index = index;
	}
	
	protected void doActionBody() {
		removedVector = _target.getVector(_dimension, _index);
		_target.remVector(_dimension, _index);
	}
	
	protected void undoActionBody() {
		_target.insVector(_dimension, _index, removedVector);
	}
	
	public String toString() {
		return "Remove vector at index "+_index+" along dimension "+_dimension+".";
	}

	@Override
	public String toShortString() {
		if (_dimension == List2d.ROW) return "remove row";
		else return "remove column";
	}
	
	public List2d<Type> getTarget() {
		return _target;
	}
	
	public int getDimension() {
		return _dimension;
	}
	
	public int getIndex() {
		return _index;
	}

}
