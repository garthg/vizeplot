package editing;

public abstract class UndoableEditingAction extends EditingAction {
	
	private int _doCount = 0;
	private int _undoCount = 0;
	
	public UndoableEditingAction(Object actionTarget) {
		super(actionTarget);
	}
	
	protected abstract void undoActionBody();
	
	public void doAction() {
		if (actionDone()) throw new IllegalStateException("Action already done.");
		super.doAction();
		_doCount++;
	}
	
	public void undoAction() {
		if (!actionDone()) throw new IllegalStateException("Action not done.");
		if (_target == null) throw new NullPointerException("undoAction target is null.");
		undoActionBody();
		_undoCount++;
	}
	
	public boolean actionDone() {
		return (_doCount > _undoCount);
	}
	
	public int numTimesDone() {
		return _doCount;
	}
	
	public int numTimesUndone() {
		return _undoCount;
	}
	
}
