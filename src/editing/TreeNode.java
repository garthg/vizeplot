package editing;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<Type> {
	public Type data;
	private TreeNode<Type> _parent;
	private ArrayList<TreeNode<Type>> _children;
	public int depth;
	
	public TreeNode() {
		data = null;
		_parent = null;
		depth = -1;
		_children = new ArrayList<TreeNode<Type>>();
	}
	
	public TreeNode(Type initData) {
		this();
		data = initData;
	}
	
	public TreeNode(Type initData, TreeNode<Type> parent) {
		this(initData);
		_parent = parent;
		if (parent.depth >= 0) {
			depth = parent.depth+1;
		}
	}
	
	public TreeNode(Type initData, TreeNode<Type> parent, ArrayList<TreeNode<Type>> children) {
		this(initData, parent);
		_children = children;
	}
	
	public int numChildren() {
		return _children.size();
	}
	
	public boolean hasChildren() {
		return (numChildren() > 0);
	}
	
	public TreeNode<Type> getParent() {
		return _parent;
	}
	
	public List<TreeNode<Type>> getChildren() {
		return _children;
	}
	
	public TreeNode<Type> getChildAtIndex(int index) throws IndexOutOfBoundsException {
		if (index >= numChildren() || index < 0) throw new IndexOutOfBoundsException();
		return _children.get(index);
	}
	
	public TreeNode<Type> getLastChild() throws IndexOutOfBoundsException {
		return getChildAtIndex(numChildren()-1);
	}
	
	public void addChild(int index, TreeNode<Type> child) {
		_children.add(index, child);
	}
	
	public void addChild(TreeNode<Type> child) {
		_children.add(child);
	}
	
	public String toString() {
		if (data != null) return "TreeNode: "+data.toString();
		else return "TreeNode: null.";
	}
}
