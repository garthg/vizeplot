package log;

import java.awt.Window;

import editing.EditingAction;
import editing.UndoableEditingAction;

public class LogEdit extends LogEntry {
	private final EditingAction _editingAction;
	private final Boolean _wasUndo;
	private final Boolean _wasRedo;
	
	public LogEdit(Window source, EditingAction e) {
		super(source);
		_editingAction = e;
		_wasUndo = _wasRedo = null;
	}
	
	public LogEdit(Window source, UndoableEditingAction e) {
		super(source);
		_editingAction = e;
		_wasUndo = !e.actionDone();
		if (!_wasUndo) _wasRedo = (e.numTimesDone() > 1);
		else _wasRedo = null;
	}
	
	public String toLogString() {
		String s = super.toLogString() + ": ";
		if (_wasUndo != null && _wasUndo) s += "UNDO ";
		else if (_wasRedo != null && _wasRedo) s += "REDO ";
		s += _editingAction.toString();
		return s;
	}

}
