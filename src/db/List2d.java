package db;

import java.util.List;

/**
 * Defines the operations a two-dimensional list must support.
 * 
 * @author gwg
 *
 * @param <Type>
 * 	The data type stored in the list.
 */
public interface List2d<Type> {
	void insVector(int dimension, int index, List<Type> v);
	void insVector(int dimension, List<Type> v);
	void remVector(int dimension, int index);
	List<Type> getVector(int dimension, int index);
	Type get(int row, int col);
	void set(int row, int col, Type val);
	int size(int dimension);
	
	/**
	 * static ints ROW and COL are used to index along a particular dimension (0 or 1). 
	 */
	int ROW = 0;
	int COL = 1;
}
