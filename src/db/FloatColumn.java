package db;


public class FloatColumn extends ColumnHead {
	
	@Override
	public Float toValue(String s) {
		if (isMissingStr(s)) return null;
		else try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString(Object v) throws IllegalArgumentException {
		if (isMissingVal(v)) return missingValueString;
		if (! (v instanceof Float)) throw new IllegalArgumentException();
		else return Float.toString((Float)v); 
	}
	
	public String toString() {
		return "CSVFloatTranslator instance";
	}

	@Override
	public DataType getDataType() {
		return DataType.DATA_NUMBER;
	}
}
