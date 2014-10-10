package db;


public class StringColumn extends ColumnHead {

	@Override
	public String toString(Object v) {
		if (isMissingVal(v)) return missingValueString;
		if (!(v instanceof String)) throw new IllegalArgumentException();
		else return (String)v;
	}

	@Override
	public String toValue(String s) {
		if (isMissingStr(s)) return null;
		else return s;
	}

	public String toString() {
		return "CSVStringTranslator instance";
	}

	@Override
	public DataType getDataType() {
		return DataType.DATA_STRING;
	}
}
