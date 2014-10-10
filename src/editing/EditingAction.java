package editing;

public abstract class EditingAction {
	protected Object _target;
	
	EditingAction(Object actionTarget) {
		_target = actionTarget;
	}
	
	protected abstract void doActionBody();
	
	public void doAction() {
		if (_target == null) throw new NullPointerException("doAction target is null.");
		doActionBody();
	}
	
	public abstract String toShortString();
	
	public Object getTarget() {
		return _target;
	}
}
