/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap {
	public HeapNode min;
	private int c;
	private int size;
	private int trees;
	private int totalCuts;
	private int totalLinks;

	/**
	 *
	 * Constructor to initialize an empty heap.
	 * pre: c >= 2.
	 *
	 */
	public FibonacciHeap(int c) {
		this.c = c;
		this.size = 0;
		this.trees = 0;
		this.min = null;
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info) {
		HeapNode node = new HeapNode(key, info, this.min);
		this.size++;
		this.trees++;
		if (key < this.min.key) {
			this.min = node;
		}
		return node;
	}

	/**
	 * 
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin() {
		return min;
	}

	/**
	 * 
	 * Delete the minimal item.
	 * Return the number of links.
	 *
	 */
	public int deleteMin() {
		FibonacciHeap children = new FibonacciHeap(c);

		this.trees--;
		this.size -= this.min.rank;

		HeapNode current = this.min.child;
		current.next.prev = null;
		while(current != null) {
			children.insert(current.key, current.info);
			current = current.next;
		}

		//finding new min
		
		if(this.trees == 1) {
			this.min = null;
		}
		else if(this.trees == 2) {
			this.min 
		}
		else {
			HeapNode newMin = this.min.next;
		}



		this.meld(children);
		current = this.min.next.next;
		

		return 46; // should be replaced by student code
	}

	/**
	 * 
	 * pre: 0<diff<x.key
	 * 
	 * Decrease the key of x by diff and fix the heap.
	 * Return the number of cuts.
	 * 
	 */
	public int decreaseKey(HeapNode x, int diff) {
		x.key -= diff;

		if (x.parent == null) { // decreasing root
			if (x.key < this.min.key) {
				this.min = x;
			}
			return 0;
		}

		if (x.key >= x.parent.key) {
			return 0;
		} // no cutting needed

		int numberOfCuts = this.cascadingCuts(x);

		this.totalCuts += numberOfCuts;

		return numberOfCuts;
	}

	private int cascadingCuts(HeapNode x) { // cutting, not changing key
		if (x.parent == null) {
			if (x.key < this.min.key) {
				this.min = x;
			}
			return 0;
		}

		// fixing pointers
		if (x.next == x) { // no siblings
			x.parent.child = null;
		} else {
			x.parent.child = x.next;
			x.next.prev = x.prev;
			x.prev.next = x.next;
		}
		x.parent.rank -= 1;
		x.parent.cuttedChildren += 1;

		// Adding to tree list
		x.next = this.min.next;
		x.prev = this.min;
		this.min.next = x;
		this.min.next.prev = x;

		// Checking if min has changed
		if (x.key < this.min.key) {
			this.min = x;
		}

		// cascading
		if (x.parent.cuttedChildren == this.c) {
			return 1 + this.cascadingCuts(x.parent);
		}

		return 1;
	}

	/**
	 * 
	 * Delete the x from the heap.
	 * Return the number of links.
	 *
	 */
	public int delete(HeapNode x) {
		int minimum = this.min.key - 1;

		this.decreaseKey(x, x.key - minimum);
		return this.deleteMin();
	}

	/**
	 * 
	 * Return the total number of links.
	 * 
	 */
	public int totalLinks() {
		return 46; // should be replaced by student code
	}

	/**
	 * 
	 * Return the total number of cuts.
	 * 
	 */
	public int totalCuts() {
		return this.totalCuts; // should be replaced by student code
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2) {
		this.min.next.prev = heap2.min.prev;
		heap2.min.prev.next = this.min.next;
		this.min.next = heap2.min;
		heap2.min.prev = this.min;
		
		if(heap2.min.key < this.min.key) {
			this.min = this.min.next;
		}


		this.totalLinks += heap2.totalLinks;
		this.totalCuts += heap2.totalCuts;
		return;
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {
		return this.size; // should be replaced by student code
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees() {
		return trees;
	}

	/**
	 * Class implementing a node in a Fibonacci Heap.
	 * 
	 */
	public static class HeapNode {
		public int key;
		public String info;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public int rank;
		public int cuttedChildren;

		public HeapNode(int key, String info, HeapNode prev) {
			this.key = key;
			this.info = info;
			this.child = null;
			this.parent = null;
			this.rank = 0;
			this.cuttedChildren = 0;

			if (prev == null) {
				this.prev = this;
				this.next = this;
			} else {
				this.prev = prev;
				this.next = prev.next;
				prev.next = this;

				if (prev.prev == prev) {
					prev.prev = this;
				}
			}
		}
	}
}
