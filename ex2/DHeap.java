/**
 * Roi Koren
 * username: roikoren, id: 305428369
 * <p>
 * and
 * <p>
 * Nadav Gasner
 * username: nadavgasner, id: 204057566
 * <p>
 * D-Heap
 * <p>
 * An implementation of a D-ary heap.
 */

public class DHeap {

	private int size, max_size, d;
	private DHeap_Item[] array;

	// Constructor
	// m_d >= 2, m_size > 0
	DHeap(int m_d, int m_size) {
		this.max_size = m_size;
		this.d = m_d;
		this.array = new DHeap_Item[this.max_size];
		this.size = 0;
	}

	/**
	 * public static int parent(i,d), child(i,k,d)
	 * (2 methods)
	 * <p>
	 * precondition: i >= 0, d >= 2, 1 <= k <= d
	 * <p>
	 * The methods compute the index of the parent and the k-th child of
	 * vertex i in a complete D-ary tree stored in an array.
	 * Note that indices of arrays in Java start from 0.
	 */
	public static int parent(int i, int d) {
		if (i <= d) // children of root
			return 0;
		if (i % d == 0) // if the item is the d child of the parent
			return i / d - 1;
		else
			return i / d; // the floor of i/d
	}

	public static int child(int i, int k, int d) {
		return i * d + k; // k-th child of i
	}

	/**
	 * Sort the input array using heap-sort (build a heap, and
	 * perform n times: get-min, del-min).
	 * Sorting should be done using the DHeap, name of the items is irrelevant.
	 * <p>
	 * Returns the number of comparisons performed.
	 * <p>
	 * postcondition: array1 is sorted
	 */
	public static int DHeapSort(int[] array1, int d) {
		int comparisons = 0;
		DHeap dHeap = new DHeap(d, array1.length); // build a new heap with array1 as its array
		for (int i = 0; i < array1.length; i++) // for each item in array
			comparisons += dHeap.Insert(new DHeap_Item(String.valueOf(i), array1[i])); // insert to heap (keep count of comparisons)
		for (int i = 0; i < array1.length; i++) { // for each slot in array
			array1[i] = dHeap.Get_Min().getKey(); // get key of min element and put in correct place in array
			comparisons += dHeap.Delete_Min(); // delete min to prepare for next iteration
		}
		return comparisons;
	}

	/**
	 * public int getSize()
	 * <p>
	 * Returns the number of elements in the heap.
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * public int arrayToHeap()
	 * <p>
	 * The function builds a new heap from the given array.
	 * Previous data of the heap should be erased.
	 * <p>
	 * preconidtion: array1.length() <= max_size
	 * postcondition: isHeap()
	 * size = array1.length()
	 * <p>
	 * Returns number of comparisons along the function run.
	 */
	public int arrayToHeap(DHeap_Item[] array1) {
		this.array = array1;
		this.size = array1.length;
		this.max_size = this.getSize();
		for (int i = 0; i < this.getSize(); i++)
			this.array[i].setPos(i); // set item's interim positions
		int comparisons = 0;
		for (int i = this.getSize() / this.d + 1; i >= 0; i--) { // for all non-leaves in heap, from last to root
			if (this.d * i + 1 < this.getSize() && this.array[this.d * i + 1] != null) // make sure we're in the array, and have an item
				comparisons += Heapify_Down(this.array[i]); // heapify-down and count comparisons
		}
		return comparisons;
	}

	/**
	 * public boolean isHeap()
	 * <p>
	 * The function returns true if and only if the D-ary tree rooted at array[0]
	 * satisfies the heap property or has size == 0.
	 */
	public boolean isHeap() {
		for (int i = 0; i < this.getSize(); i++) { // for all nodes in tree
			for (int j = 0; j < this.d; j++) { // for all children of node
				int child = child(i, j + 1, this.d);
				if (child >= this.getSize()) // means we already checked everything
					return true;
				if (this.array[child] == null) { // case where heap isn't full, but shouldn't ever get here...
					for (int k = child + 1; k < this.getSize(); k++) {
						if (this.array[k] != null) // check we don't have any items after last before null
							return false;
					}
				} else {
					if (this.array[child].getKey() < this.array[i].getKey())
						return false; // if a child's key is smaller than his parent's
				}
			}
		}
		return true; // get here if there wasn't a problem or size == 0
	}

	/**
	 * public int Insert(DHeap_Item item)
	 * <p>
	 * Inserts the given item to the heap.
	 * Returns number of comparisons during the insertion.
	 * <p>
	 * precondition: item != null
	 * isHeap()
	 * size < max_size
	 * postcondition: isHeap()
	 */
	public int Insert(DHeap_Item item) {
		if (this.size >= this.max_size) // to be safe
			return 0;
		this.size++; // increase size
		this.array[this.getSize() - 1] = item; // place item at end of array
		item.setPos(this.getSize() - 1); // set item's pos
		return Heapify_Up(item); // heapify-up and return num of comparisons
	}

	/**
	 * public int Delete_Min()
	 * <p>
	 * Deletes the minimum item in the heap.
	 * Returns the number of comparisons made during the deletion.
	 * <p>
	 * precondition: size > 0
	 * isHeap()
	 * postcondition: isHeap()
	 */
	public int Delete_Min() {
		if (this.size <= 0) // just to be safe
			return 0;
		this.size--; // decrease size
		DHeap_Item last = this.array[this.getSize()];
		this.array[0] = last; // replace root with last item in array, update it's pos
		last.setPos(0);
		return Heapify_Down(last); // heapify-down and return num of comparisons
	}

	/**
	 * public DHeap_Item Get_Min()
	 * <p>
	 * Returns the minimum item in the heap.
	 * <p>
	 * precondition: heapsize > 0
	 * isHeap()
	 * size > 0
	 * postcondition: isHeap()
	 */
	public DHeap_Item Get_Min() {
		if (this.getSize() == 0) // to be safe
			return null;
		return this.array[0]; // return root
	}

	/**
	 * public int Decrease_Key(DHeap_Item item, int delta)
	 * <p>
	 * Decerases the key of the given item by delta.
	 * Returns number of comparisons made as a result of the decrease.
	 * <p>
	 * precondition: item.pos < size;
	 * item != null
	 * isHeap()
	 * postcondition: isHeap()
	 */
	public int Decrease_Key(DHeap_Item item, int delta) {
		item.setKey(item.getKey() - delta);  // change key
		return Heapify_Up(item); // fix heap and return num of comparisons
	}

	/**
	 * public int Delete(DHeap_Item item)
	 * <p>
	 * Deletes the given item from the heap.
	 * Returns number of comparisons during the deletion.
	 * <p>
	 * precondition: item.pos < size;
	 * item != null
	 * isHeap()
	 * postcondition: isHeap()
	 */
	public int Delete(DHeap_Item item) {
		this.size--; // decrease size
		if (this.getSize() == 0) // if deleted last item
			return 0;
		DHeap_Item last = this.array[this.getSize()];
		int delPos = item.getPos();
		this.array[delPos] = last;
		last.setPos(delPos); // put last element in the place of deleted item
		if (delPos != 0 && last.getKey() < this.array[parent(delPos, this.d)].getKey()) //decides the direction of the return Heapify
			return Heapify_Up(last) + 1; // parent is smaller than last, heapify-up
		else                              // +1 on each return since we compared with parent
			return Heapify_Down(last) + 1; // parent is larger, try heapify-down
	}

	/**
	 * private int Heapify_Up(DHeap_Item item)
	 * <p>
	 * Fixes heap by comparing item with parent, switching if needed,
	 * and keeps going until item isn't switched.
	 * <p>
	 * Returns number of comparisons made
	 * <p>
	 * precondition: item != null
	 * -1 < item.pos < size
	 * postcondition: isHeap()
	 */
	private int Heapify_Up(DHeap_Item item) {
		int pos = item.getPos();
		if (pos == 0) // can't heapify-up from root
			return 0;
		int comparisons = 0;
		DHeap_Item parent = this.array[parent(pos, this.d)];
		while (pos > 0 && item.getKey() < parent.getKey()) { // while there is a parent, and its key is larger
			comparisons++;
			Swap(item, parent); // swap item and parent
			pos = item.getPos();
			parent = this.array[parent(pos, this.d)]; // get new parent
		}
		if (pos != 0) // means we did another comparison
			comparisons++;
		return comparisons;
	}

	/**
	 * private int Heapify_Down(DHeap_Item item)
	 * <p>
	 * Fixes heap by comparing item with children,
	 * switching with minimum of them if needed,
	 * and keeps going until reaching a leaf,
	 * or item isn't switched.
	 * <p>
	 * precondition: item != null
	 * -1 < item.pos < size
	 */
	private int Heapify_Down(DHeap_Item item) {
		int comparisons = 0;
		int pos = item.getPos();
		DHeap_Item smallest = item;
		while (true) {
			for (int i = pos * this.d + 1; i < this.getSize() && i <= (pos + 1) * this.d; i++) {  // for each child of item
				if (this.array[i] == null) // no children left
					break;
				comparisons++;   // if i >= size we don't get here 
				if (this.array[i].getKey() < smallest.getKey())
					smallest = this.array[i]; // get minimum of item and its children
			}
			if (smallest == item)  // means item is smaller then all his children
				break;
			if (smallest != item) { // minimum of children is smaller than item
				Swap(item, smallest); // swap item and minimum of children
				pos = item.getPos();
				smallest = item; // update item, pos and smallest and keep going
			}
		}
		return comparisons;
	}

	/**
	 * private void Swap(DHeap_Item item1, DHeap_Item item2)
	 * <p>
	 * swaps item1 and item2's pos and location in array,
	 * while keeping references to items
	 * <p>
	 * precondition: item1 != null
	 * item2 != null
	 */
	private void Swap(DHeap_Item item1, DHeap_Item item2) {
		int pos1 = item1.getPos();
		int pos2 = item2.getPos();
		array[pos1] = item2;
		array[pos2] = item1;
		item1.setPos(pos2);
		item2.setPos(pos1);
	}
}