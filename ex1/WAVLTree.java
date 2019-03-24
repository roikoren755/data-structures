/**
 * Nadav Gasner
 * username: nadavgasner, id:204057566
 * <p>
 * and
 * <p>
 * Roi Koren
 * username: roikoren, id:305428369
 * <p>
 * WAVLTree
 * <p>
 * An implementation of a WAVL Tree with
 * distinct integer keys and info
 */

public class WAVLTree {

	private IWAVLNode root;
	private IWAVLNode EXT = new WAVLNode();
	private IWAVLNode MIN;
	private IWAVLNode MAX;

	public WAVLTree() {
		this.root = EXT;
		this.MIN = EXT;
		this.MAX = EXT;
	}

	public WAVLTree(IWAVLNode root) {
		this.root = root;
		this.root.setParent(EXT);
		this.MIN = root;
		this.MAX = root;
	}

	/**
	 * public boolean empty()
	 * <p>
	 * returns true if and only if the tree is empty
	 */
	public boolean empty() {
		return this.root == EXT;
	}

	/**
	 * private IWAVLNode recSearch(IWAVLNode node, int k)
	 * <p>
	 * recursively returns the IWAVLNode with key k if it exists in the tree
	 * otherwise, returns the last real node encountered
	 */
	public IWAVLNode recSearch(IWAVLNode node, int k) {
		if (node == EXT) // should only get here if tree is empty
			return null;
		if (node.getKey() == k) // if this is the key we want, return node
			return node;
		if (node.getKey() < k) { // if key we are at is smaller, go left
			if (node.getRight() == EXT) // check we aren't reaching external leaf
				return node;
			return recSearch(node.getRight(), k);
		}
		if (node.getLeft() == EXT) // only here if node.getKey() > k, then symmetric to above
			return node;
		return recSearch(node.getLeft(), k);
	}

	/**
	 * public String search(int k)
	 * <p>
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k) {
		IWAVLNode loc = recSearch(root, k); // recursive search starting at root
		if (loc == null || loc.getKey() != k) // meaning k wasn't found
			return null;
		return loc.getValue();
	}

	/**
	 * private void rightRotate(IWAVLNode node)
	 * <p>
	 * performs a right rotation around node, by reassigning parents and children
	 * does not promote() or demote() nodes
	 */
	private void rightRotate(IWAVLNode node) {
		IWAVLNode parent = node.getParent();
		IWAVLNode grandParent = parent.getParent();
		IWAVLNode right = node.getRight();
		if (right != EXT)
			right.setParent(parent);
		node.setParent(grandParent);
		if (parent == grandParent.getRight())
			grandParent.setRight(node);
		if (parent == grandParent.getLeft())
			grandParent.setLeft(node);
		node.setRight(parent);
		parent.setParent(node);
		parent.setLeft(right);
		node.setSize(parent.getSubtreeSize());
		parent.setSize(parent.getLeft().getSubtreeSize() + parent.getRight().getSubtreeSize() + 1);
	}


	/**
	 * private void leftRotate(IWAVLNode node)
	 * <p>
	 * same as above, only left rotate. couldn't think of a better way than copy paste...
	 */
	private void leftRotate(IWAVLNode node) {
		IWAVLNode parent = node.getParent();
		IWAVLNode grandParent = parent.getParent();
		IWAVLNode left = node.getLeft();
		if (left != EXT)
			left.setParent(parent);
		node.setParent(grandParent);
		if (parent == grandParent.getRight())
			grandParent.setRight(node);
		if (parent == grandParent.getLeft())
			grandParent.setLeft(node);
		node.setLeft(parent);
		parent.setParent(node);
		parent.setRight(left);
		node.setSize(parent.getSubtreeSize());
		parent.setSize(parent.getLeft().getSubtreeSize() + parent.getRight().getSubtreeSize() + 1);
	}

	/**
	 * private int insertRebalance(IWAVLNode toRebalance)
	 * <p>
	 * performs the different rebalancing operations on the tree,
	 * following an insertion, until it is a balanced WAVL tree again.
	 * returns the number of rebalancing operations performed
	 */
	private int insertRebalance(IWAVLNode toRebalance) {
		int rebalances = 0;
		IWAVLNode parent = toRebalance.getParent();
		if (parent.getLeft() != EXT && parent.getRight() != EXT)
			return rebalances; // case B, no rebalancing needed
		while (parent != EXT && parent.getRank() == toRebalance.getRank()) {
			int[] type = parent.getType();
			if ((type[0] == 0 && type[1] == 1) || (type[0] == 1 && type[1] == 0)) { // case 1
				parent.promote();
				rebalances++;
				toRebalance = parent;
				parent = toRebalance.getParent();
				continue;
			}
			if (type[0] == 0 && type[1] == 2) {
				int[] type1 = parent.getLeft().getType();
				if (type1[0] == 1) { // case 2
					rightRotate(toRebalance);
					parent.demote();
					rebalances += 2;
					return rebalances;
				}
				if (type1[0] == 2) { // case 3
					IWAVLNode newParent = toRebalance.getRight();
					leftRotate(newParent); // double rotate around node's right child
					rightRotate(newParent);
					toRebalance.demote();
					parent.demote();
					newParent.promote();
					rebalances += 5;
					return rebalances;
				}
			}
			if (type[0] == 2 && type[1] == 0) {
				int[] type1 = parent.getRight().getType();
				if (type1[1] == 1) { // symmetric case 2
					leftRotate(toRebalance);
					parent.demote();
					rebalances += 2;
					return rebalances;
				}
				if (type1[1] == 2) { // symmetric case 3
					IWAVLNode newParent = toRebalance.getLeft();
					rightRotate(newParent); // double rotate around node's right child
					leftRotate(newParent);
					toRebalance.demote();
					parent.demote();
					newParent.promote();
					rebalances += 5;
					return rebalances;
				}
			}
		}
		return rebalances; // get here if promotions were enough
	}

	/**
	 * public int insert(int k, String i)
	 * <p>
	 * inserts an item with key k and info i to the WAVL tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	 * returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {
		IWAVLNode toInsert = new WAVLNode(k, i); // create new node
		IWAVLNode insertPoint = recSearch(this.root, k); // search for what will be node's parent
		if (MIN == EXT || k < MIN.getKey())
			MIN = toInsert;
		if (MAX == EXT || k > MAX.getKey())
			MAX = toInsert;
		toInsert.setLeft(EXT);
		toInsert.setRight(EXT);
		if (insertPoint == null) { // means tree is empty
			this.root = toInsert;
			this.root.setParent(EXT);
			return 0; // no rebalancing needed
		}
		if (insertPoint.getKey() == k) // key already in tree
			return -1;
		toInsert.setParent(insertPoint);
		if (insertPoint.getKey() > k) // decide if left or right child
			insertPoint.setLeft(toInsert);
		else
			insertPoint.setRight(toInsert);
		while (insertPoint != EXT) { // increase all needed subtree sizes
			insertPoint.setSize(insertPoint.getSubtreeSize() + 1);
			insertPoint = insertPoint.getParent();
		}
		int rebalances = insertRebalance(toInsert); // rebalance
		while (this.root.getParent() != EXT)//checks if we did'nt changed the root in the process, and update if needed
			this.root = this.root.getParent();
		return rebalances;
	}

	private void switchRight(IWAVLNode node1, IWAVLNode node2) {//switch node when node1 is node2's right son
		IWAVLNode temp = node2.getParent();
		if (temp != EXT && temp.getLeft() == node2)
			temp.setLeft(node1);
		if (temp != EXT && temp.getRight() == node2)
			temp.setRight(node1);
		node1.setParent(temp);
		node2.setParent(node1);
		temp = node1.getLeft();
		if (temp != EXT)
			temp.setParent(node2);
		IWAVLNode temp2 = node2.getLeft();
		if (temp2 != EXT)
			temp2.setParent(node1);
		node1.setLeft(temp2);
		node2.setLeft(temp);
		temp = node1.getRight();
		if (temp != EXT)
			temp.setParent(node2);
		node1.setRight(node2);
		node2.setRight(temp);
		int tempInt = node1.getRank();
		node1.setRank(node2.getRank());
		node2.setRank(tempInt);
		tempInt = node1.getSubtreeSize();
		node1.setSize(node2.getSubtreeSize());
		node2.setSize(tempInt);
		if (node2 == this.root)
			this.root = node1;
	}

	private void switchLeft(IWAVLNode node1, IWAVLNode node2) {//switch node when one is the other's left son
		IWAVLNode temp = node2.getParent();
		if (temp != EXT && temp.getLeft() == node2)
			temp.setLeft(node1);
		if (temp != EXT && temp.getRight() == node2)
			temp.setRight(node1);
		node1.setParent(temp);
		node2.setParent(node1);
		temp = node1.getLeft();
		if (temp != EXT)
			temp.setParent(node2);
		node1.setLeft(node2);
		node2.setLeft(temp);
		temp = node1.getRight();
		if (temp != EXT)
			temp.setParent(node2);
		IWAVLNode temp2 = node2.getRight();
		if (temp2 != EXT)
			temp2.setParent(node1);
		node2.setRight(temp);
		node1.setRight(temp2);
		int tempInt = node1.getRank();
		node1.setRank(node2.getRank());
		node2.setRank(tempInt);
		tempInt = node1.getSubtreeSize();
		node1.setSize(node2.getSubtreeSize());
		node2.setSize(tempInt);
		if (node2 == this.root)
			this.root = node1;
	}

	/**
	 * private void switchNode(IWAVLNode node1, IWAVLNode node2)
	 * <p>
	 * switch the given nodes, including parent, children,
	 * rank, subtreesize, leaving key, value nad reference to node intact
	 */
	private void switchNode(IWAVLNode node1, IWAVLNode node2) {
		if (node1 == node2.getLeft()) {
			switchLeft(node1, node2);
			return;
		}
		if (node1 == node2.getRight()) {
			switchRight(node1, node2);
			return;
		}
		if (node2 == node1.getLeft()) {
			switchLeft(node2, node1);
			return;
		}
		if (node2 == node1.getRight()) {
			switchRight(node2, node1);
			return;
		}
		IWAVLNode temp = node1.getLeft();
		if (temp != EXT)
			temp.setParent(node2);
		temp = node1.getRight();
		if (temp != EXT)
			temp.setParent(node2);
		temp = node2.getLeft();
		if (temp != EXT)
			temp.setParent(node1);
		temp = node2.getRight();
		if (temp != EXT)
			temp.setParent(node1);
		temp = node1.getParent();
		if (temp.getRight() == node1)
			temp.setRight(node2);
		if (temp.getLeft() == node1)
			temp.setLeft(node2);
		temp = node2.getParent();
		if (temp.getRight() == node2)
			temp.setRight(node1);
		if (temp.getLeft() == node2)
			temp.setLeft(node1);
		temp = node1.getParent();
		node1.setParent(node2.getParent());
		node2.setParent(temp);
		temp = node1.getRight();
		node1.setRight(node2.getRight());
		node2.setRight(temp);
		temp = node1.getLeft();
		node1.setLeft(node2.getLeft());
		node2.setLeft(temp);
		int tempInt = node1.getRank();
		node1.setRank(node2.getRank());
		node2.setRank(tempInt);
		tempInt = node1.getSubtreeSize();
		node1.setSize(node2.getSubtreeSize());
		node2.setSize(tempInt);
		if (node1 == this.root)
			this.root = node2;
		else {
			if (node2 == this.root)
				this.root = node1;
		}
	}

	/**
	 * private int deleteRebalance(IWAVLNode toRebalance)
	 * <p>
	 * rebalances tree, starting with node toRebalance,
	 * and returns the number of rebalancing operations performed
	 */
	private int deleteRebalance(IWAVLNode toRebalance) {
		int rebalances = 0;
		int[] type = toRebalance.getType();
		if (type[0] == 2 && type[1] == 2) {//node is a leaf, since we deleted his child, he became a (2,2)
			toRebalance.demote();
			rebalances++;
			toRebalance = toRebalance.getParent();
			if (toRebalance != EXT) {
				type = toRebalance.getType();
			}
		}
		while (toRebalance != EXT && (type[0] == 3 || type[1] == 3)) {//stops when we are at the root or if the current node does not have 3 as rank differential with his sons
			type = toRebalance.getType();
			if ((type[0] == 3 && type[1] == 2) || (type[0] == 2 && type[1] == 3)) {//case 1
				toRebalance.demote();
				rebalances++;
				toRebalance = toRebalance.getParent();
				if (toRebalance != EXT) {
					type = toRebalance.getType();
				}
				continue;
			}
			if ((type[0] == 3 && type[1] == 1)) {
				IWAVLNode right = toRebalance.getRight();
				int[] rightType = right.getType();//checks the right node type
				if (rightType[0] == 2 && rightType[1] == 2) {//case 2
					toRebalance.demote();
					right.demote();
					rebalances += 2;
					toRebalance = toRebalance.getParent();
					if (toRebalance != EXT) {
						type = toRebalance.getType();
					}
					continue;
				}
				if (rightType[1] == 1) {//case 3
					leftRotate(right);
					right.promote();
					toRebalance.demote();
					rebalances += 3;
					type = toRebalance.getType();
					if (type[0] == 2 && type[1] == 2 && toRebalance.getLeft() == EXT && toRebalance.getRight() == EXT) {
						toRebalance.demote();
						rebalances++;
					}
					return rebalances;
				}
				if (rightType[1] == 2) {//case 4
					IWAVLNode toRotate = right.getLeft();
					rightRotate(toRotate);
					leftRotate(toRotate);
					right.demote();
					toRebalance.setRank(toRebalance.getRank() - 2);
					toRotate.setRank(toRotate.getRank() + 2);
					rebalances += 7;
					return rebalances;
				}
			}
			if ((type[0] == 1 && type[1] == 3)) {//Symmetric cases 2-4
				IWAVLNode left = toRebalance.getLeft();
				int[] leftType = left.getType();
				if (leftType[0] == 2 && leftType[1] == 2) {//Sym case 2
					toRebalance.demote();
					left.demote();
					rebalances += 2;
					toRebalance = toRebalance.getParent();
					if (toRebalance != EXT) {
						type = toRebalance.getType();
					}
					continue;
				}
				if (leftType[0] == 1) {//Sym case 3
					rightRotate(left);
					left.promote();
					toRebalance.demote();
					rebalances += 3;
					type = toRebalance.getType();
					if (type[0] == 2 && type[1] == 2 && toRebalance.getLeft() == EXT && toRebalance.getRight() == EXT) {
						toRebalance.demote();
						rebalances++;
					}
					return rebalances;
				}
				if (leftType[0] == 2) {//Sym case 4
					IWAVLNode toRotate = left.getRight();
					leftRotate(toRotate);
					rightRotate(toRotate);
					left.demote();
					toRebalance.setRank(toRebalance.getRank() - 2);
					toRotate.setRank(toRotate.getRank() + 2);
					rebalances += 7;
					return rebalances;
				}
			}
		}

		return rebalances; //get here if demotions were enough
	}

	/**
	 * public int delete(int k)
	 * <p>
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		IWAVLNode toDelete = recSearch(this.root, k); // get node to delete
		if (toDelete == null || toDelete.getKey() != k) // if key not in tree
			return -1;
		if (toDelete == this.root && toDelete.getRank() == 0) {//if key is the root and a leaf
			this.root = EXT;
			MIN = EXT;
			MAX = EXT;
			toDelete.setParent(null);
			toDelete.setRight(null);
			toDelete.setLeft(null);
			return 0;
		}
		boolean fixedExtremes = false; //flag if already changed MIN & MAX
		if (toDelete == this.root && this.size() == 2) { //if key is the root and has one child, switch with his son
			if (toDelete.getRight() != EXT)
				switchRight(toDelete.getRight(), toDelete);
			if (toDelete.getLeft() != EXT)
				switchLeft(toDelete.getLeft(), toDelete);
			MIN = this.root;
			MAX = this.root;
			fixedExtremes = true;
		}
		if (!fixedExtremes && toDelete == MIN)
			MIN = successor(toDelete);
		if (!fixedExtremes && toDelete == MAX)
			MAX = predecessor(toDelete);
		if (toDelete.getRight() != EXT && toDelete.getLeft() != EXT) // if node to delete has two children
			switchNode(toDelete, successor(toDelete)); // switch node with its successor
		IWAVLNode parent = toDelete.getParent();
		IWAVLNode temp = toDelete.getParent();
		toDelete.setParent(null);
		boolean isLeft = (parent.getLeft() == toDelete); // node to delete is left child
		if (toDelete.getRight() != EXT) { // node has only right child
			IWAVLNode right = toDelete.getRight(); // cut node out
			right.setParent(parent);
			if (isLeft)
				parent.setLeft(right);
			else
				parent.setRight(right);
		} else { // node might have left child
			IWAVLNode left = toDelete.getLeft(); // cut node out
			if (left != EXT)
				left.setParent(parent);
			if (isLeft)
				parent.setLeft(left);
			else
				parent.setRight(left);
		}
		toDelete.setRight(null);
		toDelete.setLeft(null);
		while (temp != EXT && temp != null) {// increase all needed subtree sizes
			temp.setSize(temp.getSubtreeSize() - 1);
			temp = temp.getParent();
		}
		int rebalances = deleteRebalance(parent);
		while (this.root.getParent() != EXT)//checks if we did'nt changed the root in the process, and update if needed
			this.root = this.root.getParent();
		return rebalances;
	}


	/**
	 * public String min()
	 * <p>
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 */
	public String min() {
		if (this.root == EXT) // if tree is empty
			return null;
		return MIN.getValue(); // returns value of minimal node
	}


	/**
	 * public String max()
	 * <p>
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 */
	public String max() { // same as min(), only other direction
		if (this.root == EXT) // tree is empty
			return null;
		return MAX.getValue(); // returns value of maximal node
	}

	private void recIOWalkKeys(IWAVLNode node, IntStack keysStack) {//performs inorder recursive walk on tree, using stack
		if (node == EXT)
			return;
		recIOWalkKeys(node.getLeft(), keysStack);
		keysStack.push(node.getKey());
		recIOWalkKeys(node.getRight(), keysStack);
	}

	/**
	 * public int[] keysToArray()
	 * <p>
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	public int[] keysToArray() {
		IntStack keysStack = new IntStack(this.root.getSubtreeSize());
		recIOWalkKeys(this.root, keysStack);
		return keysStack.arr;
	}

	private void recIOWalkString(IWAVLNode node, StringStack infoStack) {//performs inorder recursive walk on tree, using stack
		if (node == EXT)
			return;
		recIOWalkString(node.getLeft(), infoStack);
		infoStack.push(node.getValue());
		recIOWalkString(node.getRight(), infoStack);
	}

	/**
	 * public String[] infoToArray()
	 * <p>
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		StringStack infoStack = new StringStack(this.root.getSubtreeSize());
		recIOWalkString(this.root, infoStack);
		return infoStack.getArray();
	}


	/**
	 * public int size()
	 * <p>
	 * Returns the number of nodes in the tree.
	 * <p>
	 * precondition: none
	 * postcondition: none
	 */
	public int size() {
		return this.root.getSubtreeSize(); // size should be explicitly kept
	}


	/**
	 * public IWAVLNode selectNode(int i){
	 * <p>
	 * Returns the value of the i'th smallest key of a given node's Subtree
	 * precondition: size() >= i > 0
	 * postcondition: none
	 */
	public IWAVLNode selectNode(IWAVLNode x, int i) {//works like algorithm shown in class
		int r = x.getLeft().getSubtreeSize() + 1;
		if (i == r)
			return x;
		if (i < r)
			return selectNode(x.getLeft(), i);
		if (i > r)
			return selectNode(x.getRight(), i - r);
		return null;
	}

	/**
	 * public String select(int i)
	 * <p>
	 * Returns the value of the i'th smallest key (return -1 if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key
	 * Example 2: select(size()) returns the value of the node with maximal key
	 * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor
	 * <p>
	 * precondition: size() >= i > 0
	 * postcondition: none
	 */
	public String select(int i) {
		if (this.root == EXT || this.size() < i)
			return null;
		IWAVLNode x = MIN;
		while (x.getSubtreeSize() < i)
			x = x.getParent();     //find the LCA of the minimal key and i'th smallest key
		IWAVLNode toReturn = selectNode(x, i); //perform the algo shown in class starting from the LCA, to achieve O(logi)
		if (toReturn == null)
			return null;
		return toReturn.getValue();
	}

	/**
	 * public IWAVLNode getRoot()
	 * <p>
	 * Returns the root WAVL node, or null if the tree is empty
	 * <p>
	 * precondition: none
	 * postcondition: none
	 */
	public IWAVLNode getRoot() {
		return this.root == EXT ? null : this.root; // check if tree is empty
	}

	/**
	 * private IWAVLNode minNode(IWAVLNode)
	 * <p>
	 * Returns the node with the smallest key of given node's subtree
	 */
	private IWAVLNode minNode(IWAVLNode node) {
		while (node.getLeft() != EXT) // keep going left, stop before external leaf
			node = node.getLeft();
		return node; // found min()
	}

	/**
	 * private IWAVLNode successor(IWAVLNode x)
	 * <p>
	 * Returns the node with key following x according to the sorted order of keys.
	 * <p>
	 * precondition: x is not the maximal node of the tree
	 * postcondition: none
	 */
	private IWAVLNode successor(IWAVLNode x) {//works like algorithm shown in class
		if (x.getRight() != EXT) {
			return minNode(x.getRight());
		}
		IWAVLNode y = x.getParent();
		while (y != EXT && x == y.getRight()) {
			x = y;
			y = x.getParent();
		}
		return y;
	}

	/**
	 * private IWAVLNode maxNode(IWAVLNode)
	 * <p>
	 * Returns the node with the biggest key of given node's subtree
	 */
	private IWAVLNode maxNode(IWAVLNode node) {// same as minNode(), only other direction
		while (node.getRight() != EXT) // keep going right, stop before external leaf
			node = node.getRight();
		return node; // found max()
	}

	/**
	 * private IWAVLNode predecessor(IWAVLNode x)
	 * <p>
	 * Returns the node with key preceding x according to the sorted order of keys.
	 * <p>
	 * precondition: x is not the minimal node of the tree
	 * postcondition: none
	 */
	private IWAVLNode predecessor(IWAVLNode x) { //same as Successor, only symmetric
		if (x.getLeft() != EXT) {
			return maxNode(x.getLeft());
		}
		IWAVLNode y = x.getParent();
		while (y != EXT && x == y.getLeft()) {
			x = y;
			y = x.getParent();
		}
		return y;
	}


	/**
	 * public interface IWAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IWAVLNode {
		int getKey(); // returns node's key (for virtual node return -1)

		String getValue(); // returns node's value [info] (for virtual node return null)

		IWAVLNode getLeft(); // returns left child (if there is no left child return null)

		void setLeft(IWAVLNode left); // sets the left child

		IWAVLNode getRight(); // returns right child (if there is no right child return null)

		void setRight(IWAVLNode right); // sets the right child

		boolean isRealNode(); // Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)

		int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))

		IWAVLNode getParent(); // Returns the node's parent

		void setParent(IWAVLNode parent); // sets the node's parent

		int getRank(); // returns the node's rank

		void setRank(int rank); //sets the node's rank

		void setSize(int size); // sets the node's subtree size

		void promote(); // increases the node's rank by 1

		void demote(); // decreases the node's rank by 1

		int[] getType(); // returns the node's type
	}

	/**
	 * public class WAVLNode
	 * <p>
	 * If you wish to implement classes other than WAVLTree
	 * (for example WAVLNode), do it in this file, not in
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IWAVLNode)
	 */
	public class WAVLNode implements IWAVLNode {

		private int key;
		private String value;
		private IWAVLNode left;
		private IWAVLNode right;
		private IWAVLNode parent;
		private int rank;
		private int size;

		public WAVLNode() {
			this.key = -1;
			this.value = null;
			this.left = null;
			this.right = null;
			this.parent = null;
			this.rank = -1;
			this.size = 0;
		}

		public WAVLNode(int key, String value) {
			this.key = key;
			this.value = value;
			this.rank = 0;
			this.size = 1;
			this.left = null;
			this.right = null;
			this.parent = null;
		}

		public int getKey() {
			return this.key;
		}

		public String getValue() {
			return this.value;
		}

		public IWAVLNode getLeft() {
			return this.left;
		}

		public void setLeft(IWAVLNode left) {
			this.left = left;
		}

		public IWAVLNode getRight() {
			return this.right;
		}

		public void setRight(IWAVLNode right) {
			this.right = right;
		}

		public IWAVLNode getParent() {
			return this.parent;
		}

		public void setParent(IWAVLNode parent) {
			this.parent = parent;
		}

		// Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
		public boolean isRealNode() {
			return this.parent != null;
		}

		public int getSubtreeSize() {
			return this.size;
		}

		public int getRank() {
			return this.rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public void promote() {
			this.rank++;
		}

		public void demote() {
			this.rank--;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public int[] getType() {//returns array with the rank differential between node and his sons
			return new int[]{this.getRank() - this.getLeft().getRank(), this.getRank() - this.getRight().getRank()};
		}
	}

	private class StringStack {
		public String[] arr;
		private int end = 0;

		public StringStack(int n) {
			this.arr = new String[n];
		}

		public void push(String val) {
			this.arr[end] = val;
			end++;
		}

		public int getEnd() {
			return this.end;
		}

		public String[] getArray() {
			return this.arr;
		}
	}

	private class IntStack {
		public int[] arr;
		private int end = 0;
		private int start = 0;

		public IntStack() {
			this.arr = new int[1000];
		}

		public IntStack(int n) {
			this.arr = new int[n];
		}

		public void push(int val) {
			this.arr[end] = val;
			end++;
		}

		public int getEnd() {
			return this.end;
		}

		public int[] getArray() {
			return this.arr;
		}
	}


}
