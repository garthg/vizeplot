package csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;


import db.CategoryColumn;
import db.ColumnHead;
import db.FloatColumn;
import db.List2d;
import db.StringColumn;
import db.ColumnHead.DataType;

import au.com.bytecode.opencsv.CSVReader;

public class CSVHandler {
	
	private int maxCategories;
	
	public CSVHandler() {
		this(12);
	}
	
	public CSVHandler(int maxNumCategories) {
		maxCategories = maxNumCategories;
	}
	
	public boolean isFloatType(String field) {
		try {
			Float.parseFloat(field);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public ColumnHead[] detectColumns(List<String[]> lines) {
		return detectColumns(lines, 0.8f);
	}
	
	public ColumnHead[] detectColumns(List<String[]> lines, float minDetectAgreement) {
		
		// figure out how many columns
		int numCols = 0;
		for (String[] l : lines) {
			numCols = Math.max(numCols, l.length);
		}
		
		// make empty array of translators to return
		ColumnHead[] result = new ColumnHead[numCols];
		
		// make empty array of int counters to keep track of column types
		int[][] colTypeCounts = new int[numCols][2]; // 0 = number, 1 = string or category
		
		// make empty array of ArrayLists of strings to store categories
		ArrayList<ArrayList<String>> categories = new ArrayList<ArrayList<String>>(numCols);
		
		// initialize tracking variables
		for (int i=0; i<numCols; i++) {
			result[i] = null;
			categories.add(new ArrayList<String>());
			for (int j=0; j<colTypeCounts[0].length; j++) 
				colTypeCounts[i][j] = 0;
		}
		
		// set up how often to guess
		int checkEvery = 5;
		int checkFirst = 20;
		if (checkFirst >= lines.size()) {
			checkFirst = lines.size()-1;
			checkEvery = 1;
		}
		
		// iterate over all lines
		int currLine = 0;
		for (String[] line : lines) {
			currLine++;
			boolean done = true;
			for (int i=0; i<line.length; i++) {
				if (result[i] == null) {
					done = false;
					String field = line[i].trim();
					if (field != null && !field.equals("") && !ColumnHead.isMissingStr(field)) {
						ArrayList<String> catArr = categories.get(i);
						boolean found = false;
						for (String c : catArr) {
							if (c.equals(field)) {
								found = true;
								break;
							}
						}
						if (!found) catArr.add(field);
						if (isFloatType(field)) colTypeCounts[i][0]++;
						else colTypeCounts[i][1]++;
					}
					if ((currLine%checkEvery == 0 && currLine >= checkFirst) || currLine+1 == lines.size()) {
						float totalLines = (float)lines.size();
						float ratioFloat = (float)colTypeCounts[i][0] / totalLines;
						if (ratioFloat >= minDetectAgreement) result[i] = new FloatColumn();
						float ratioString = (float)colTypeCounts[i][1] / totalLines;
						if (ratioString >= minDetectAgreement) {
							if (categories.get(i).size() > maxCategories) result[i] = new StringColumn();
							else {
								if (currLine+1 == lines.size()) {
									ArrayList<String> catArr = categories.get(i);
									String[] catArrArg = new String[catArr.size()];
									result[i] = new CategoryColumn(catArr.toArray(catArrArg));
								}
							}
						}
					}
				}
			}
			if (done) break;
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public ColumnHead[] readFile(String infile, List2d target) throws FileNotFoundException, IOException {
		System.out.println("Reading file: '"+infile+"'.");
		
		CSVReader reader = new CSVReader(new FileReader(infile));
		List<String[]> allLines = reader.readAll();
		
		ColumnHead[] cols = detectColumns(allLines.subList(1, allLines.size())); 
		System.out.println("Detected "+cols.length+" columns:");
		for (ColumnHead t : cols) {
			System.out.print("  ");
			if (t == null) System.out.println("Unknown column!");
			else System.out.println(t.toString());
		}
		
		boolean nonStringDataNotFirstLine = false;
		for (ColumnHead t : cols) {
			if (t.getDataType() != DataType.DATA_STRING) {
				nonStringDataNotFirstLine = true;
				break;
			}
		}
		String[] firstLine = allLines.get(0);
		boolean hasHeaderLine = false;
		if (nonStringDataNotFirstLine) {
			boolean nonStringInFirstLine = false;
			for (String field : firstLine) {
				if (isFloatType(field)) nonStringInFirstLine = true;
			}
			if (!nonStringInFirstLine) hasHeaderLine = true;
		}
		
		if (hasHeaderLine) {
			for (int i=0; i<firstLine.length; i++) {
				cols[i].columnName = firstLine[i];
			}
			System.out.println("Column headers:");
			for (String s : firstLine) {
				System.out.println("  "+s);
			}
		}
		
		if (!hasHeaderLine) {
			for (int i=0; i<cols.length; i++) {
				ColumnHead t = cols[i];
				if (t instanceof CategoryColumn) {
					CategoryColumn ct = (CategoryColumn)t;
					String field = firstLine[i].trim();
					boolean found = false;
					for (String c : ct.categories) {
						if (c.equals(field)) {
							found = true;
							break;
						}
					}
					if (!found) {
						int newLen = ct.categories.length+1;
						String[] newCategories = Arrays.copyOf(ct.categories, newLen);
						newCategories[newLen-1] = field;
						ct.categories = newCategories;
					}
				}
			}
		}
		
		int numCols = cols.length;
		for (int i=(hasHeaderLine?1:0); i<allLines.size(); i++) {
			String[] line = allLines.get(i);
			boolean hasData = false;
			for (String s : line) {
				if (!s.equals("")) {
					hasData = true;
					break;
				}
			}
			if (hasData) {
				List v = new Vector(numCols);
				for (int j=0; j<numCols; j++) {
					String field;
					if (j < line.length) field = line[j];
					else field = null;
					Object val;
					ColumnHead t = cols[j];
					try {
						val = t.toValue(field);
					} catch (IllegalArgumentException e) {
						val = null;
					}
					v.add(val);
				}
				target.insVector(List2d.ROW, v);
			}
		}
		
		return cols;
	}
	
}
