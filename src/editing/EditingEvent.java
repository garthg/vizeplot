package editing;

import java.util.EventObject;

public class EditingEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5680893136347125983L;
	
	private EditingAction _action;
	
	public EditingEvent(Object source, EditingAction a) {
		super(source);
		_action = a;
	}
	
	public EditingAction getAction() {
		return _action;
	}
	
	public Object getTarget() {
		return super.source;
	}
}
