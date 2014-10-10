package db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeadedArrayList2d<Type> extends ArrayList2d<Type> implements HeadedList2d<Type> {
	protected ArrayList<ColumnHead> headers = null;
	
	@Override
	public void clear() {
		super.clear();
		headers = null;
	}
	
	@Override
	public String cellToString(int row, int col) {
		Type value = get(row,col);
		ColumnHead t;
		if (headers != null) {
			if (col < headers.size()) {
				if ((t = headers.get(col)) != null) {
					return t.toString(value);
				}
			}
		}
		if (value == null) return "(null)";
		else return value.toString();
	}
	
	@Override
	public void printCell(int row, int col) {
		System.out.print(cellToString(row, col));
	}

	@Override
	public ColumnHead[] getHeaders() {
		return headers.toArray(new ColumnHead[headers.size()]);
	}

	@Override
	public void setHeaders(ColumnHead[] columnHeaders) {
		headers = new ArrayList<ColumnHead>(Arrays.asList(columnHeaders));
	}
	
	@Override
	public void remVector(int dimension, int index) throws IndexOutOfBoundsException {
		super.remVector(dimension, index);
		if (dimension == COL) headers.remove(index);
	}
	
	@Override
	public void insVector(int dimension, int index, List<Type> v) 
	throws IllegalArgumentException,IndexOutOfBoundsException 
	{
		super.insVector(dimension, index, v);
		if (dimension == COL) headers.add(index, null);
	}
	
	public void insHeadedColumn(int index, List<Type> v, ColumnHead h) 
	throws IllegalArgumentException,IndexOutOfBoundsException
	{
		super.insVector(COL, index, v);
		headers.add(index, h);
	}

	@Override
	public ColumnHead getHeader(int index) {
		return headers.get(index);
	}

}
