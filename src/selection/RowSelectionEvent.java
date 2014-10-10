package selection;

import db.VisModel;

public class RowSelectionEvent extends SelectionEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 188127774619050401L;
	private VisModel _source;
	
	public RowSelectionEvent(VisModel source) {
		super(source);
		_source = source;
	}
	
	public VisModel getSource() {
		return _source;
	}
	
}
