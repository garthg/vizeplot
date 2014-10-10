package selection;

import java.util.List;

public class SelectionFinalizedEvent extends SelectionEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5000904468201804631L;
	
	protected List<Integer> _selectedRows;
	
	public SelectionFinalizedEvent(Object source, List<Integer> selectedRows) {
		super(source);
		_selectedRows = selectedRows;
	}
	
	public List<Integer> getSelectedRows() {
		return _selectedRows;
	}
	
}
