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
		this.totalCuts = 0;
		this.totalLinks = 0;
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info) {
		// inserting new node after the current min
		HeapNode node = new HeapNode(key, info, this.min);
		this.size++;
		this.trees++;
		// updating min
		if (size == 1) {
			this.min = node;
		} else if (key < this.min.key) {
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
		return this.min;
	}

	/**
	 * 
	 * Delete the minimal item.
	 * Return the number of links.
	 *
	 */
	public int deleteMin() {
		// updating size and number of trees
		if (this.size == 0)
			return 0;

		this.trees += this.min.rank - 1;
		this.size--;

		if (this.size == 0) {
			this.min = null;
			return 0;
		}
		// adding the children of min as tress
		HeapNode current = this.min.child;
		if (current != null)
			current.prev.next = null;
		while (current != null) {
			// insert children as tree after current min
			current.parent = null;
			HeapNode next = current.next;
			insertTree(current);
			current = next;
		}
		// deleteing the old minimum
		this.min.prev.next = this.min.next;
		this.min.next.prev = this.min.prev;

		// finding new min

		HeapNode start = this.min.prev;
		HeapNode curr = this.min.prev;
		HeapNode Nmin = curr;
		int min = curr.key;
		curr = curr.next;
		while (curr != start) {
			if (curr.key < min) {
				min = curr.key;
				Nmin = curr;
			}
			curr = curr.next;
		}
		this.min = Nmin;
		//UP TO HERE GOOD
		return this.SuccessiveLinking();

	}

	private int SuccessiveLinking() {
		java.util.ArrayList<Object> buckets = new java.util.ArrayList<>();
		int links = 0;

		//ordering the nodes, so we could run on them in an organized manner
		HeapNode start = this.min;
		HeapNode current = this.min;
		do {
			current.nextInLine = current.next;
			current = current.next;
		}
		while(current != start);



		start = this.min;
		current = this.min;
		do {
			int currentRank = current.rank;
			HeapNode next = current.nextInLine;
			// Changing array length
			if (currentRank + 1 >= buckets.size()) {
				for (int i = buckets.size(); i < currentRank + 2; i++) {
					buckets.add(null);
				}
			}

			if (buckets.get(currentRank) == null) {
				buckets.set(currentRank, current);
			} else { // linking
				links += this.Linking(buckets, current); // returns number of links
			}

			current = next;
		}
		while (current != start);



		HeapNode newMin = null;
		for(int i=0; i<buckets.size(); i++) {
			HeapNode node = (HeapNode) buckets.get(i);
			if(node != null && (newMin==null || node.key < newMin.key)) {
				newMin = node;
			}
		}
		this.min = newMin;

		// all the trees are in the buckets. now we want to insert them all back.
		this.min.next = this.min;
		this.min.prev = this.min;

		for (int i = 0; i < buckets.size(); i++) {
			current = (HeapNode) buckets.get(i);
			if (current != null && current != this.min) {
				current.parent = null;
				current.prev = this.min;
				current.next = this.min.next;
				this.min.next = current;
				current.next.prev = current;
			}
		}
		this.totalLinks += links;
		return links;
	}

	private int Linking(java.util.ArrayList<Object> buckets, HeapNode current) {
		int currentRank = current.rank;

		if (currentRank + 1 >= buckets.size()) {
			buckets.add(null);
		}

		if (buckets.get(currentRank) == null) {
			buckets.set(currentRank, current);
			return 0;
		}

		this.trees -= 1;

		HeapNode otherNode = (HeapNode) buckets.get(currentRank);
		if (current.key < otherNode.key) {
			if (current.child == null) { // adding first child
				current.child = otherNode;
				otherNode.next.prev = otherNode.prev;
				otherNode.prev.next = otherNode.next;
				otherNode.next = otherNode;
				otherNode.prev = otherNode;
			} else {
				current.child.prev.next = otherNode;
				otherNode.prev.next = otherNode.next;
				otherNode.next.prev = otherNode.prev;
				otherNode.prev = current.child.prev;
				otherNode.next = current.child;
				current.child.prev = otherNode;
			}
			otherNode.parent = current;
			current.rank += 1;

			buckets.set(currentRank, null);

			if (buckets.get(currentRank + 1) == null) {
				buckets.set(currentRank + 1, current);
				return 1;
			}

			return 1 + Linking(buckets, current);
		} else {
			if (otherNode.child == null) { // adding first child
				otherNode.child = current;
				current.next.prev = current.prev;
				current.prev.next = current.next;
				current.next = current;
				current.prev = current;
			} else {
				otherNode.child.prev.next = current;
				current.prev.next = current.next;
				current.next.prev = current.prev;
				current.prev = otherNode.child.prev;
				current.next = otherNode.child;
				otherNode.child.prev = current;
			}
			current.parent = otherNode;
			otherNode.rank += 1;

			buckets.set(currentRank, null);

			if (buckets.get(currentRank + 1) == null) {
				buckets.set(currentRank + 1, otherNode);
				return 1;
			}

			return 1 + Linking(buckets, otherNode);
		}
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
		HeapNode parent = x.parent;

		// fixing pointers
		if (x.next == x) { // no siblings
			parent.child = null;
		} else {
			parent.child = x.next;
			x.next.prev = x.prev;
			x.prev.next = x.next;
		}
		parent.rank -= 1;
		parent.cuttedChildren += 1;

		// Adding to tree list and checking new minimum
		insertTree(x);
		this.trees += 1;

		// cascading
		if (parent != null && parent.cuttedChildren == this.c) {
			return 1 + this.cascadingCuts(parent);
		}

		return 1;
	}

	public void insertTree(HeapNode x) {
		// adding a node with possible children after min
		x.parent = null;
		if (this.min == null) {
			this.min = x;
			x.next = x;
			x.prev = x;
			return;
		}
		x.next = this.min.next;
		x.prev = this.min;
		this.min.next.prev = x;
		this.min.next = x;

		// Checking if min has changed
		if (x.key < this.min.key) {
			this.min = x;
		}
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
		return this.totalLinks; // should be replaced by student code
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
		if (this.min != null && heap2.min != null) {
			this.min.next.prev = heap2.min.prev;
			heap2.min.prev.next = this.min.next;
			this.min.next = heap2.min;
			heap2.min.prev = this.min;

			if (heap2.min.key < this.min.key) {
				this.min = this.min.next;
			}
		} else if (this.min == null && heap2.min != null) {
			this.min = heap2.min;
		}

		this.totalLinks += heap2.totalLinks;
		this.totalCuts += heap2.totalCuts;
		this.trees += heap2.trees;
		this.size += heap2.size;
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
		public HeapNode nextInLine;

		public HeapNode(int key, String info, HeapNode prev) {
			this.key = key;
			this.info = info;
			this.child = null;
			this.parent = null;
			this.rank = 0;
			this.cuttedChildren = 0;
			this.nextInLine = null;

			if (prev == null) {
				this.prev = this;
				this.next = this;
			} else {
				this.prev = prev;
				this.next = prev.next;
				prev.next.prev = this;
				prev.next = this;

				if (prev.prev == prev) {
					prev.prev = this;
				}
			}
		}
	}
}
