package editing;

public class DataLoadedEvent extends EditingEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3527431745772209661L;

	private String _filename;
	
	public DataLoadedEvent(Object source, String filename) {
		super(source, null);
		_filename = filename;
	}
	
	public String getFilename() {
		return _filename;
	}
}
