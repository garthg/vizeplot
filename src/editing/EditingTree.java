package editing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class EditingTree {
	public final TreeNode<UndoableEditingAction> root;
	public TreeNode<UndoableEditingAction> curr;
	private int numNodes;
	
	public EditingTree() {
		root = new TreeNode<UndoableEditingAction>(null);
		root.depth = 0;
		curr = root;
		numNodes = 1;
	}
	
	public void doAction(UndoableEditingAction a) {
		TreeNode<UndoableEditingAction> childNode = new TreeNode<UndoableEditingAction>(a,curr);
		curr.addChild(childNode);
		childNode.data.doAction();
		curr = childNode;
		numNodes++;
	}
	
	public boolean canUndo() {
		return (curr != root);
	}
	
	public void undoAction() {
		curr.data.undoAction();
		curr = curr.getParent();
	}
	
	public boolean canRedo() {
		return (curr.hasChildren());
	}
	
	public void redoAction() throws NoSuchElementException {
		if (!canRedo()) throw new NoSuchElementException("No action to redo.");
		else {
			TreeNode<UndoableEditingAction> childNode = curr.getLastChild();
			childNode.data.doAction();
			curr = childNode;
		}
	}
	
	public void redoAction(int i) throws NoSuchElementException {
		if (i<0 || i >= curr.numChildren()) throw new NoSuchElementException("Index out of bounds.");
		else {
			TreeNode<UndoableEditingAction> childNode = curr.getChildAtIndex(i);
			childNode.data.doAction();
			curr = childNode;
		}
	}
	
	public Iterator<TreeNode<UndoableEditingAction>> breadthFirstIterator() {
		return new Iterator<TreeNode<UndoableEditingAction>>() {
			private ArrayList<TreeNode<UndoableEditingAction>> visited = new ArrayList<TreeNode<UndoableEditingAction>>();
			private int childIndex = -1, parentIndex = -1;
			private TreeNode<UndoableEditingAction> parentNode = null; 
			
			public boolean hasNext() {
				return (visited.size() < numNodes);
			}
			
			public TreeNode<UndoableEditingAction> next() {
				TreeNode<UndoableEditingAction> result;
				if (parentIndex == -1) {
					parentNode = root;
					result = root;
					parentIndex = 0;
				} else {
					if (childIndex+1 < parentNode.numChildren()) {
						childIndex++;
					} else { 
						parentIndex++;
						while (!visited.get(parentIndex).hasChildren()) {
							parentIndex++;
						}
						parentNode = visited.get(parentIndex);
						childIndex = 0;
					}
					result = parentNode.getChildAtIndex(childIndex);
				}
				visited.add(result);
				return result;
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public Iterator<TreeNode<UndoableEditingAction>> depthFirstIterator() {
		return new Iterator<TreeNode<UndoableEditingAction>>() {
			private ArrayList<TreeNode<UndoableEditingAction>> stack = new ArrayList<TreeNode<UndoableEditingAction>>();
			private boolean first = true;
			
			private void stackPush(TreeNode<UndoableEditingAction> p) {
				stack.add(p);
			}
			
			private TreeNode<UndoableEditingAction> stackPop() {
				return stack.remove(stack.size()-1);
			}
			
			public boolean hasNext() {
				return ((first && numNodes > 0) || !stack.isEmpty());
			}
			
			public TreeNode<UndoableEditingAction> next() {
				TreeNode<UndoableEditingAction> result;
				if (first) {
					stackPush(root);
					first = false;
				}
				result = stackPop();
				for (int i=result.numChildren()-1; i>=0; i--) stackPush(result.getChildAtIndex(i));
				return result;
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public void print() {
		System.out.println("Printing tree with "+numNodes+" nodes.");
		Iterator<TreeNode<UndoableEditingAction>> itr = depthFirstIterator();
		TreeNode<UndoableEditingAction> currP;
		String indent = "   ";
		System.out.println();
		while (itr.hasNext()) {
			currP = itr.next();
			if (curr.equals(currP)) System.out.print(" > ");
			else System.out.print("   ");
			for (int i=0; i<currP.depth; i++) System.out.print(indent);
			System.out.println(currP.toString());
		}
		System.out.println();
	}
}
