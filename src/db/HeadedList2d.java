package db;

import java.util.List;

public interface HeadedList2d<Type> extends List2d<Type> {
	ColumnHead[] getHeaders();
	ColumnHead getHeader(int index);
	void setHeaders(ColumnHead[] headers);
	String cellToString(int row, int col);
	void insHeadedColumn(int index, List<Type> v, ColumnHead h) 
		throws IllegalArgumentException,IndexOutOfBoundsException;
	
}
