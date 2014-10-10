package db;


public class CategoryColumn extends ColumnHead {
	public String[] categories;
	
	public CategoryColumn(String[] arg) {
		super();
		categories = arg;
	}
	
	@Override
	public String toString(Object v) {
		if (isMissingVal(v)) return missingValueString;
		if (!(v instanceof Float)) throw new IllegalArgumentException();
		else return categories[Math.round((Float)v)];
	}

	@Override
	public Float toValue(String s) {
		if (isMissingStr(s)) return null;
		else {
			for (int i=0; i<categories.length; i++) {
				if (s.equals(categories[i])) return i*1.0f;
			}
			throw new IllegalArgumentException("Argument matches no categories.");
		}
	}
	
	public String toString() {
		String str = "CSVCategoryTranslator instance with categories: ";
		boolean first = true;
		for (String s : categories) {
			if (first) first = false;
			else str += ", ";
			str += s;
		}
		return str;
	}

	@Override
	public DataType getDataType() {
		return DataType.DATA_CATEGORY;
	}

	@Override
	public String getDataTypeString() {
		String result = "Categories {";
		boolean first = true;
		for (String s : categories) {
			if (first) first = false;
			else result += ",";
			result += s;
		}
		result += "}";
		return result;
	}

}
