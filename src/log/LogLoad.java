package log;

import java.awt.Window;

import editing.DataLoadedEvent;

public class LogLoad extends LogEntry {
	private final DataLoadedEvent _event;
	
	public LogLoad(Window source, DataLoadedEvent event) {
		super(source);
		_event = event;
	}

	public String toLogString() {
		return super.toLogString() + ": loaded file '"+ _event.getFilename()+"'.";
	}
}
