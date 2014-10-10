package selection;

import java.util.EventObject;

public abstract class SelectionEvent extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7671005784467547899L;

	public SelectionEvent(Object source) {
		super(source); 
	}

	public Object getTarget() {
		return super.source;
	}

}
