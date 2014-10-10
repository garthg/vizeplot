package editing;

import java.util.List;
import java.util.Vector;

import db.List2d;


public class UndoableInsVect<Type> extends UndoableEditingAction {

	protected final List2d<Type> _target;
	protected final int _index, _dimension;
	protected List<Type> _v;
	
	public UndoableInsVect(List2d<Type> target, int dimension, List<Type> v) {
		this(target, target.size(dimension), dimension, v);
	}
	
	public UndoableInsVect(List2d<Type> actionTarget, int dimension, int index, List<Type> v) {
		super(actionTarget);
		_index = index;
		_dimension = dimension;
		_v = new Vector<Type>(v.size());
		for (Type x : v) _v.add(x);
		_target = actionTarget;
	}

	protected void doActionBody() {
		_target.insVector(_dimension, _index, _v);
	}
	
	protected void undoActionBody() {
		_target.remVector(_dimension, _index);
	}
	
	public String toString() {
		return "Insert vector at index "+_index+" along dimension "+_dimension+".";
	}

	@Override
	public String toShortString() {
		if (_dimension == List2d.ROW) return "insert row";
		else return "insert column";
	}
	
	public List2d<Type> getTarget() {
		return _target;
	}
	
	public List<Type> getVector() {
		return _v;
	}
	
	public int getDimension() {
		return _dimension;
	}
	
	public int getIndex() {
		return _index;
	}
	
}
