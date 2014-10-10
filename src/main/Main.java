package main;

import log.VisLog;
import db.VisController;
import db.VisModel;
import ui.VisView;


/**
 * This class contains the 'main' entry point for the program, which creates
 * a new instance of the 'Main' object.
 * 
 * Instantiating Main creates the data object, populates it from the 
 * file passed as the first argument, and then starts the user interface.
 * 
 * @author gwg
 */
public class Main {
	
	/**
	 * The data structure. This will always be in its most recent state as regards
	 * undoing/redoing.
	 */
	public static VisModel model = new VisModel();
	
	/**
	 * UI
	 */
	public static VisView view = new VisView();
	
	/**
	 * Controller (probably unnecessary but it's here anyway)
	 */
	public static VisController controller  = new VisController();
	
	/**
	 * Logging module
	 */
	public static VisLog log = null;
	
	/**
	 * Program entry point.
	 * Parses command-line arguments and instantiates Main class.
	 * 
	 * @param args
	 * 	system passes command-line arguments
	 */
	public static void main(String[] args) {
		
		// make new Main
		new Main();
		
		// if passed a file on the command line, load it up
		String passedFilename = null;
		if (args.length == 1) {
			String fname = args[0];
			System.out.println("Using file: '"+fname+"'.");
			passedFilename = fname;
		}
		
		// start the UI
		controller.start(passedFilename);
	}
}
