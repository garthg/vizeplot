package log;

import java.awt.Window;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import selection.SelectionEvent;
import selection.SelectionFinalizedEvent;
import selection.SelectionListener;
import ui.ScatterplotFrame;

import editing.DataLoadedEvent;
import editing.EditingAction;
import editing.EditingEvent;
import editing.EditingListener;
import editing.UndoableEditingAction;

import main.Main;

// log popularity of axes
// log most commonly selected rows/cols

// log:
// open window
// close window
// select points (mouse up)
// select points
// edit/insert/remove/undo/redo

public class VisLog implements EditingListener,SelectionListener {
	private Vector<LogEntry> _log;
	
	public VisLog() {
		_log = new Vector<LogEntry>();
		Main.model.addSelectionListener(this);
		Main.model.addEditingListener(this);
	}
	
	public void writeLogToFile(File file) {
		writeLogToFile(_log, file);
	}
	
	public void writeLogToFileAsCSV(File file) {
		writeLogToFileAsCSV(_log, file);
	}
	
	public void writeSelectionHistoryToFile(File file) {
		writeSelectionHistoryToFile(_log, file);
	}
	
	private void writeLogToFile(List<LogEntry> log, File file) {
		try {
			BufferedWriter b = new BufferedWriter(new FileWriter(file));
			for (LogEntry e : log) b.write(e.toLogString() + '\n');
			b.close();
		} catch (IOException e) {
			System.err.println("Error writing log file.");
			e.printStackTrace();
		}
	}
	
	private void writeLogToFileAsCSV(List<LogEntry> log, File file) {
		try {
			BufferedWriter b = new BufferedWriter(new FileWriter(file));
			char sep = ',';
			
			final String[] outCols = {
					"Elapsed ms",
					"Source window",
					"Event type"
			};
			
			boolean first = true;
			for (String s : outCols) {
				if (first) first = false;
				else b.write(sep);
				b.write(s);
			}
			
			if (log.size() > 0) {
				long t0 = log.get(0).getTimestamp().getTime();
				for (LogEntry e : log) {
					b.write('\n');
					long t = e.getTimestamp().getTime() - t0;
					b.write(Long.toString(t) + sep);
					b.write(e.getSourceAsString() + sep);
					Class<? extends LogEntry> c = e.getClass();
					b.write(c.toString().substring(10));		
				}
			}
			b.close();
		} catch (IOException e) {
			System.out.println("Error writing history csv");
			e.printStackTrace();
		}
	}
	
	private void writeSelectionHistoryToFile(List<LogEntry> log, File file) {
		try {
			BufferedWriter b = new BufferedWriter(new FileWriter(file));
			char sep = ',';
			
			final String[] outCols = {
					"Elapsed ms",
					"Number of rows selected",
					"x axis column",
					"y axis column",
					"color axis column"
			};
			
			String[] colNames = Main.model.getColumnNames();
			
			boolean first = true;
			for (String s : outCols) {
				if (first) first = false;
				else b.write(sep);
				b.write(s);
			}
			
			if (log.size() > 0) {
				long t0 = log.get(0).getTimestamp().getTime();
				for (LogEntry e : log) {
					if (e instanceof LogSelection) {
						b.write('\n');
						LogSelection sel = (LogSelection)e;
						long t = sel.getTimestamp().getTime() - t0;
						b.write(Long.toString(t));
						b.write(sep);
						b.write(Integer.toString(sel.getSelectedRows().size()));
						b.write(sep);
						List<Integer> axes = sel.getViewedAxes();
						for (int i=0; i<3; i++) {
							if (i>0) b.write(sep);
							int axis = axes.get(i);
							if (axis >= 0) b.write(colNames[axis]);
							else b.write("[none]");
						}
					}
				}
			}
			b.close();
		} catch (IOException e) {
			System.err.println("Error writing history file.");
			e.printStackTrace();
		}
	}

	@Override
	public void editingPerformed(EditingEvent e) {
		Window w = Main.view.getActiveWindow();
		LogEntry l;
		if (e instanceof DataLoadedEvent) {
			DataLoadedEvent de = (DataLoadedEvent)e;
			l = new LogLoad(w, de);
		} else {
			EditingAction a = e.getAction();
			if (a instanceof UndoableEditingAction) l = new LogEdit(w,(UndoableEditingAction)a);
			else l = new LogEdit(w, a);
		}
		_log.add(l);
		//System.out.println(log.get(log.size()-1).toLogString());
	}

	@Override
	public void selectionChanged(SelectionEvent e) {
		if (e instanceof SelectionFinalizedEvent) {
			SelectionFinalizedEvent se = (SelectionFinalizedEvent)e;
			Window w = Main.view.getActiveWindow();
			if (w instanceof ScatterplotFrame) {
				ScatterplotFrame sw = (ScatterplotFrame)w;
				_log.add(new LogSelection(sw, se));
				System.out.println(_log.get(_log.size()-1).toLogString());
			} else throw new IllegalStateException("expected ScatterplotFrame");
		}
	}
}
