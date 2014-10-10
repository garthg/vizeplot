package db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;


/**
 * A two-dimensional ArrayList.
 * 
 * @author gwg
 *
 * @param <Type>
 * 	The type of object that will be stored in the two-dimensional ArrayList.
 */
public class ArrayList2d<Type> implements List2d<Type> {
	
	// backing ArrayList (one row per bucket)
	private ArrayList<ArrayList<Type>> arr;
	
	// stores the size of the 2d array along each dimension
	private int[] sizes = {0,0};
	
	/**
	 * Constructor.
	 */
	public ArrayList2d() {
		arr = new ArrayList<ArrayList<Type>>();
		sizes[ROW] = sizes[COL] = 0;
	}
	
	/**
	 * Checks if the 2d array is empty.
	 * @return true if there are any elements in the 2d array, otherwise false
	 */
	public boolean isEmpty() {
		return (sizes[ROW] == 0 && sizes[COL] == 0);
	}
	
	/**
	 * Size of the whole array.
	 * @return the total number of elements in the 2d array
	 */
	public int size() {
		return sizes[0]*sizes[1];
	}
	
	/**
	 * Size of one dimension of the array.
	 * @return the total number of vectors in the 2d array along the specified dimension
	 */
	public int size(int dimension) {
		return sizes[dimension];
	}
	
	/**
	 * Number of rows.
	 * @return number of rows
	 */
	public int numRows() {
		return sizes[ROW];
	}
	
	/**
	 * Number of columns.
	 * @return number of columns
	 */
	public int numCols() {
		return sizes[COL];
	}
	
	/**
	 * Empty the entire 2d array.
	 */
	public void clear() {
		// calling clear on the backing array should empty everything out
		arr.clear();
		sizes[0] = sizes[1] = 0;
	}
	
	/**
	 * Set the value of a particular cell.
	 * @param row row index to set
	 * @param col column index to set
	 * @param value new value for the cell
	 */
	public void set(int row, int col, Type value) {
		arr.get(row).set(col, value);
	}
	
	/**
	 * Get the value of a particular cell.
	 * @param row row index to get
	 * @param col column index to retrieve
	 * @return object at index (row,col) in the 2d array
	 */
	public Type get(int row, int col) throws IndexOutOfBoundsException {
		return arr.get(row).get(col);
	}
	
	/**
	 * Get a row or column.
	 * @param dimension the dimension to retrieve the vector from (row or column)
	 * @param index the index to retrieve
	 * @return an array of all the values from the specified index along the specified dimension
	 */
	public List<Type> getVector(int dimension, int index) throws IndexOutOfBoundsException {
		
		// make sure dimension is either ROW or COL
		if (dimension == ROW || dimension == COL) {
			
			// make sure the target index is less than the dimension length
			if (index < sizes[dimension]) {
				
				// make a new List of Type with the appropriate length
				int resultLen = sizes[(dimension+1)%2];
				List<Type> result = new Vector<Type>(resultLen);
				
				// retrieve the values and store in the array
				if (dimension == ROW) {
					for (int i=0; i<resultLen; i++) {
						result.add(get(index,i));
					}
				} else {
					for (int i=0; i<resultLen; i++) {
						result.add(get(i,index));
					}
				}
				
				// return the populated array
				return result;
				
			} else throw new IndexOutOfBoundsException("Index param.");
		} else throw new IndexOutOfBoundsException("Dimension param.");
	}
	
	public void insVector(int dimension, int index, List<Type> v) 
	throws IllegalArgumentException,IndexOutOfBoundsException 
	{
		if (dimension < 2 && dimension >= 0) {
			if (index <= sizes[dimension]) {
				int len = v.size();
				if (isEmpty() || len == sizes[(dimension+1)%2]) {
					if (dimension == ROW) {
						ArrayList<Type> r = new ArrayList<Type>();
						for (int i=0; i<len; i++) {
							r.add(v.get(i));
						}
						arr.add(index, r);
					}
					if (dimension == COL) {
						for (int i=0; i<len; i++) {
							arr.get(i).add(index,v.get(i));
						}
					}
				} else throw new IllegalArgumentException("Vector length must match.");
			} else throw new IndexOutOfBoundsException("Index param.");
		} else throw new IndexOutOfBoundsException("Dimension param.");
		sizes[dimension]++;
		if (sizes[dimension] == 1) sizes[(dimension+1)%2] = v.size();
	}
	
	public void insVector(int dimension, List<Type> v) 
	throws IllegalArgumentException,IndexOutOfBoundsException 
	{
		insVector(dimension, sizes[dimension], v);
	}
	
	public void remVector(int dimension, int index) throws IndexOutOfBoundsException {
		if (index < sizes[dimension]) {
			if (dimension == ROW) {
				arr.remove(index);
			} else if (dimension == COL) {
				for (int i=0; i<sizes[ROW]; i++) {
					arr.get(i).remove(index);
				}
			} else throw new IndexOutOfBoundsException("Dimension param.");
		} else throw new IndexOutOfBoundsException("Index param.");
		sizes[dimension]--;
		if (sizes[dimension] == 0) sizes[(dimension+1)%2] = 0;
	}
	
	public void printCell(int row, int col) {
		Type v = get(row,col);
		if (v != null) System.out.print(v.toString());
		else System.out.print("(null)");
	}
	
	public void printFull() {
		String colsep = "\t";
		System.out.println();
		System.out.println("ArrayList2d: "+sizes[ROW]+" rows, "+sizes[COL]+" cols.");
		boolean first;
		for (int i=0; i<sizes[ROW]; i++) {
			first = true;
			for (int j=0; j<sizes[COL]; j++) {
				if (first) first = false;
				else System.out.print(colsep);
				printCell(i,j);
			}
			System.out.print('\n');
		}
		System.out.println();
	}
	
	public void printVector(int dim, int ind) {
		System.out.println();
		int otherDim = (dim+1)%2;
		for (int i=0; i<size(otherDim); i++) {
			if (dim == ROW) printCell(ind,i);
			else printCell(i,ind);
			System.out.print('\n');
		}
		System.out.println();
	}

	public Iterator<Type> iterator() {
		return new Iterator<Type>() {
			private int row = 0, col = 0;
			
			public boolean hasNext() {
				return (row+1 < size(ROW) || (row+1 == size(ROW) && col+1 < size(COL)));
			}
			
			public Type next() {
				if (!hasNext()) throw new NoSuchElementException();
				else {
					if (col+1 < size(COL)) col++;
					else {
						row++;
						col = 0;
					}
					return get(row,col);
				}
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
}
