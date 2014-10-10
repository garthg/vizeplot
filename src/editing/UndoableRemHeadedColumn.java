package editing;


import db.ColumnHead;
import db.HeadedList2d;
import db.List2d;

public class UndoableRemHeadedColumn<Type> extends UndoableRemVect<Type> {
	private ColumnHead header;
	private HeadedList2d<Type> _headedTarget;
	
	public UndoableRemHeadedColumn(HeadedList2d<Type> actionTarget, int index) {
		super(actionTarget, List2d.COL, index);
		_headedTarget = actionTarget;
	}
	
	protected void doActionBody() {
		header = _headedTarget.getHeader(_index);
		super.doActionBody();
	}
	
	protected void undoActionBody() {
		_headedTarget.insHeadedColumn(_index, removedVector, header);
	}
	
	public String toString() {
		return "Remove headed column at index "+_index;
	}
}
