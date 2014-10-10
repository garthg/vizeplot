package log;

import java.util.List;

import selection.SelectionFinalizedEvent;
import ui.ScatterplotFrame;

public class LogSelection extends LogEntry {
	private final SelectionFinalizedEvent _event;
	private final List<Integer> _viewingAxes;
	
	public LogSelection(ScatterplotFrame source, SelectionFinalizedEvent e) {
		super(source);
		_event = e;
		_viewingAxes = source.getCurrentAxes();
	}
	
	public String toLogString() {
		return 
			super.toLogString() + 
			": Viewing axes " + 
			_viewingAxes.toString() + 
			", selected rows " + 
			_event.getSelectedRows().toString()
		;
	}
	
	public List<Integer> getViewedAxes() {
		return _viewingAxes;
	}
	
	public List<Integer> getSelectedRows() {
		return _event.getSelectedRows();
	}
}
