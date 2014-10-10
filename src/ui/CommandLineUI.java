package ui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import main.Main;

import db.ArrayList2d;

/**
 * This class is a simple command-line user interface.
 * It makes changes to the Main.data object.
 * 
 * @author gwg
 *
 */
public class CommandLineUI {
	
	// reader to get input from user
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	/**
	 * Starts the UI.
	 */
	public void start() {
		runMainMenu();
	}
	
	/*
	 * Prints a menu of choices to the user, optionally adding a "back" choice.
	 */
	private void printChoices(String[] choices, boolean withBack) {
		int i;
		for (i=0; i<choices.length; i++) {
			System.out.println((i+1)+". "+choices[i]);
		}
		if (withBack) System.out.println((i+1)+". Back");
	}
	
	/*
	 * Prompts the user to enter an int in the specified range, optionally allowing
	 * the input of -1 as well. Continues prompting until the user provides correct
	 * input, and then returns the entered value.
	 */
	private int runIntPrompt(int min, int max, boolean allowNegOne) {
		String promptStr = "  > ";
		String formatStr = "Please enter an integer between "+min+" and "+max+". ";
		String input = null;
		int choice = -1;
		boolean goodInput = false;
		while (!goodInput) {
			System.out.println(formatStr);
			System.out.print(promptStr);
			try {
				input = reader.readLine();
				try {
					choice = Integer.parseInt(input.trim());
					if ((choice >= min && choice <= max) || (allowNegOne && choice == -1)) {
						goodInput = true;
					} else {
						System.out.println("Number out of range.");
					}
				} catch (NumberFormatException e) {
					System.out.println("Incorrectly formatted input.");
				}
			} catch (IOException e) {
				System.out.println("Error reading from System.in.");
			}
		}
		return choice;
	}
	
	/*
	 * Default choices for runIntPrompt
	 */
	private int runIntPrompt(int max) {
		return runIntPrompt(1, max);
	}
	private int runIntPrompt(int min, int max) {
		return runIntPrompt(min, max, false);
	}
	
	/*
	 * Runs a menu based on the array of strings.
	 * Combines printMenu with runIntPrompt.
	 */
	private int runMenu(String[] choices, boolean withBack) {
		printChoices(choices, withBack);
		int max = choices.length;
		if (withBack) max++;
		return runIntPrompt(max);
	}
	
	private void waitForEnter() {
		System.out.println("Hit <enter> to continue.");
		try {
			reader.readLine();
		} catch (IOException e) {
			System.out.println("Error reading from system.in.");
		}
	}
	
	private void runMainMenu() {
		System.out.println("Main Menu");
		String[] arr = {"Print","Edit","Exit"};
		int item;
		while((item = runMenu(arr,false)) != arr.length) {
			switch(item) {
			case 1:
				runPrintMenu();
				break;
			case 2:
				runEditMenu();
				break;
			}
		}
		System.out.println("Goodbye.");
	}
	
	private void runPrintMenu() {
		String dimStr = "("+Main.model.numRows()+"x"+Main.model.numCols()+")";
		System.out.println("Main Menu -> Print:");
		String[] arr = {"Print all data "+dimStr,"Print one row or column", "Print one cell"};
		int item;
		while ((item = runMenu(arr,true)) != arr.length+1) {
			switch(item) {
			case 1:
				System.out.println();
				System.out.println("Printing all data "+dimStr+":");
				Main.model.printFull();
				waitForEnter();
				break;
			case 2:
				runPrintVectorMenu();
				break;
			case 3:
				runPrintCellMenu();
				break;
			}
		}
	}
	
	private void runPrintVectorMenu() {
		System.out.println("Main Menu -> Print -> Print vector");
		String[] arr1 = {"Print row (length "+Main.model.numCols()+")","Print column (length "+Main.model.numRows()+")"};
		int dim = runMenu(arr1,true)-1;
		if (dim == 2) runPrintMenu();
		else {
			String str1 = (dim==0)?"row":"column";
			System.out.println("Main Menu -> Print data -> Print vector -> Print "+str1+":");
			System.out.println("Enter "+str1+" index or -1 to go back");
			int ind = runIntPrompt(0,Main.model.size(dim)-1,true);
			if (ind == -1) runPrintVectorMenu();
			else {
				System.out.println();
				System.out.println("Printing "+str1+" "+ind+" (length "+Main.model.size((dim+1)%2)+")");
				Main.model.printVector(dim, ind);
				waitForEnter();
			}
		}
	}
	
	private void runPrintCellMenu() {
		System.out.println("Main Menu -> Print -> Print cell");
		int rowSize = Main.model.numRows();
		int colSize = Main.model.numCols();
		System.out.println("Dimensions are "+rowSize+" rows by "+colSize+" columns.");
		System.out.println("Choose row or -1 to go back.");
		int rowInd = runIntPrompt(0, rowSize-1, true);
		if (rowInd != -1) {
			System.out.println("Choose column or -1 to go back.");
			int colInd = runIntPrompt(0, colSize-1, true);
			if (colInd != -1) {
				System.out.println("Value for cell ("+rowInd+","+colInd+"):");
				Main.model.printCell(rowInd, colInd);
				System.out.println();
				waitForEnter();
			}
		}
	}
	
	private void runEditMenu() {
		while(true) {
			System.out.println("Main Menu -> Edit");
			String[] arr = {
					"Add row or column",
					"Remove row or column",
					"Change existing cell value",
					"Undo change",
					"Redo change",
					"Print editing history"
					};
			boolean abort = false;
			boolean canUndo = Main.model.canUndo(), canRedo = Main.model.canRedo(), isHistory = canUndo || canRedo;
			if (!canUndo) arr[3] = "(No change to undo)";
			if (!canRedo) arr[4] = "(No change to redo)";
			if (!isHistory) arr[5] = "(No editing history exists)";
			int item = runMenu(arr,true);
			switch(item) {
			case 1:
				runEditAddMenu();
				break;
			case 2:
				runEditRemoveMenu();
				break;
			case 3:
				runEditExistingMenu();
				break;
			case 4:
				if (canUndo) {
					System.out.println("Undo: "+Main.model.actionWouldUndo().toString());
					Main.model.undo();
					waitForEnter();
				} else System.out.println("No change to undo.");
				break;
			case 5:
				if (canRedo) {
					System.out.println("Redo: "+Main.model.actionWouldRedo().toString());
					Main.model.redo();
					waitForEnter();
				} else System.out.println("No change to redo.");
				break;
			case 6:
				if (isHistory) {
					System.out.println("Printing editing history.");
					Main.model.printEditingTree();
					waitForEnter();
				} else System.out.println("No editing history available.");
				break;
			case 7:
				abort = true;
				break;
			}
			if (abort) return;
		}
	}
	
	private void runEditAddMenu() {
		System.out.println("Main Menu -> Edit -> Add row or column");
		String[] arr = {"Add row","Add column"};
		int dim = runMenu(arr,true)-1;
		if (dim != 2) {
			String dimStr = (dim==ArrayList2d.ROW)?"row":"column";
			int dimLen = Main.model.size(dim);
			System.out.println("There are currently "+dimLen+" "+dimStr+"s.");
			System.out.println("Enter "+dimStr+" insertion index, or enter -1 to append, or enter -2 to go back.");
			int index = runIntPrompt(-2, dimLen, false);
			if (index != -2) {
				int insLen = Main.model.size((dim+1)%2);
				List<Float> newVect = new Vector<Float>(insLen);
				System.out.println("Enter "+insLen+" float values for the new "+dimStr+".");
				System.out.println("To abort, enter 'stop'.");
				boolean aborted = false;
				for (int i=0; i<insLen; i++) {
					boolean goodInput = false;
					aborted = false;
					float value = 0;
					while (!goodInput) {
						System.out.print((i+1)+"/"+insLen+'\t'+": ");
						try {
							String inputStr = reader.readLine();
							String inputTrim = inputStr.trim();
							if (
									inputTrim.equals("stop") || 
									inputTrim.equals("quit") || 
									inputTrim.equals("abort") ||
									inputTrim.equals("exit")
									) {
								aborted = true;
								goodInput = true;
							} else {
								try {
									value = Float.parseFloat(inputStr);
									goodInput = true;
								} catch (NumberFormatException e) {
									System.out.println("Error parsing '"+inputStr+"' as float.");
								}
							}
						} catch (IOException e) {
							System.out.println("Error reading from System.in.");
						}
					}
					if (aborted) break;
					newVect.set(i,value);
				}
				if (!aborted) {
					if (index == -1) index = dimLen;
					Main.model.uInsVector(dim, index, newVect);
					System.out.println("Added the following new "+dimStr+" at index "+index+":");
					Main.model.printVector(dim, index);
					waitForEnter();
				}
			}
		}
	}
	
	private void runEditRemoveMenu() {
		System.out.println("Main Menu -> Edit -> Remove row or column");
		String[] arr = {"Remove row","Remove column"};
		int dim = runMenu(arr,true)-1;
		if (dim != 2) {
			String dimStr = (dim==ArrayList2d.ROW)?"row":"column";
			int dimLen = Main.model.size(dim);
			System.out.println("There are currently "+dimLen+" "+dimStr+"s.");
			System.out.println("Choose an index to remove, or -1 to go back.");
			int ind = runIntPrompt(0, dimLen-1, true);
			Main.model.uRemVector(dim, ind);
			System.out.println("Removed the "+dimStr+" from index "+ind+".");
		}
	}
	
	private void runEditExistingMenu() {
		System.out.println("Main Menu -> Edit -> Change existing cell value");
		int rowSize = Main.model.numRows();
		int colSize = Main.model.numCols();
		System.out.println("Dimensions are "+rowSize+" rows by "+colSize+" columns.");
		System.out.println("Choose row or -1 to go back.");
		int rowInd = runIntPrompt(0, rowSize-1, true);
		if (rowInd != -1) {
			System.out.println("Choose column or -1 to go back.");
			int colInd = runIntPrompt(0, colSize-1, true);
			if (colInd != -1) {
				System.out.println("Editing cell ("+rowInd+","+colInd+").");
				System.out.print("Enter new float value or 'stop' to abort: ");
				boolean goodInput = false;
				boolean aborted = false;
				float value = 0;
				while (!goodInput) {
					try {
						String inputStr = reader.readLine();
						String inputTrim = inputStr.trim();
						if (
								inputTrim.equals("stop") || 
								inputTrim.equals("quit") || 
								inputTrim.equals("abort") ||
								inputTrim.equals("exit")
								) {
							aborted = true;
							goodInput = true;
						} else {
							try {
								value = Float.parseFloat(inputStr);
								goodInput = true;
							} catch (NumberFormatException e) {
								System.out.println("Error parsing '"+inputStr+"' as float.");
							}
						}
					} catch (IOException e) {
						System.out.println("Error reading from System.in.");
					}
				}
				if (!aborted) {
					Main.model.uSet(rowInd, colInd, value);
					System.out.println("Set cell ("+rowInd+","+colInd+") to "+value+".");
					waitForEnter();
				}
			}
		}
	}
}
