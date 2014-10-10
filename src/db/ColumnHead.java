package db;

public abstract class ColumnHead {
	public static String missingValueString = "?";
	
	public static enum DataType { DATA_NUMBER, DATA_CATEGORY, DATA_STRING };
	
	public abstract String toString(Object v) throws IllegalArgumentException;
	public abstract Object toValue(String s) throws IllegalArgumentException;
	public abstract DataType getDataType();
	
	public String getDataTypeString() {
		DataType type = getDataType();
		if (type == DataType.DATA_NUMBER) return "Numeric";
		if (type == DataType.DATA_CATEGORY) return "Categories";
		if (type == DataType.DATA_STRING) return "String";
		return "Unknown";
	}
	
	public boolean isMissingVal(Object v) {
		if (v != null) return false;
		else return true;
	}
	
	public static boolean isMissingStr(String s) {
		if (s == null) return true;
		if (s.equals(missingValueString)) return true;
		return false;
	}
	
	public String columnName = null;
}
