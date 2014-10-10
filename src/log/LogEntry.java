package log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEntry {
	protected final Date _timestamp;
	protected final Object _source;
	public static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public LogEntry(Object source) {
		_timestamp = new Date();
		_source = source;
	}
	
	public Date getTimestamp() {
		return _timestamp;
	}
	
	public Object getSource() {
		return _source;
	}
	
	public String getSourceAsString() {
		if (_source == null) return "(no active window)";
		else return _source.toString();
	}
	
	public String toLogString() {
		String dateStr = dateFormat.format(_timestamp);
		String sourceStr = getSourceAsString();
		return dateStr + " | " + sourceStr;
	}
	
}
