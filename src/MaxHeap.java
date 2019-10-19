
import java.util.Vector;

public class MaxHeap<T> {
	public Vector<T> data;
	public MaxHeap() {
		data = new Vector<T>();
	}

	//constructs a new priority queue according to GoodnessMeasure from an unordered vector
	public void calculatepriority(Vector<T> v) 
	{
		data = new Vector<T>();
		for (int i = 0; i < v.size(); i++)
		{
			add(v.get(i));
		}
	}

	//Add the node into MaxHeap
	public void add(T value) 
	{
		data.add(value);
		goesUp(data.size() - 1);		
	}

	//return true if data is not empty
	public boolean isEmpty() 
	{
		return data.isEmpty();
	}

	//To return the size of Heap
	public int size() 
	{
		return data.size();
	}

	//It returns the first element of Heap or the node with Highest Goodness
	public T getMaxElement()
	{
		if (isEmpty()) return null;
		return data.firstElement();
	}

	//To return and remove maximum value from Heap
	public T removeTop()
	{
		if (this.isEmpty()) return null;
		T maxValue = data.get(0);
		data.set(0, data.get(data.size() - 1));
		data.setSize(data.size() - 1);
		if (data.size() > 1)
			pushDown(0);
		return maxValue;
	}

	//To return index of parent of node at location i
	public int parent(int i)
	{
		return (i - 1) / 2;
	}

	//To return index of left child of node at location i
	public int leftChild(int i) 
	{
		return 2 * i + 1;
	}

	//To return index of right child of node at location i
	public int rightChild(int i)
	{
		return 2 * (i + 1);
	}

	//It moves node at index of leaf up to an appropriate position
	@SuppressWarnings("unchecked")
	protected void goesUp(int leaf) 
	{
		int prnt = parent(leaf);
		T value = data.get(leaf);
		while (leaf > 0
				&& ((Comparable<T>) value).compareTo((T) data.get(prnt)) > 0) 
		{
			data.set(leaf, data.get(prnt));
			leaf = prnt;
			prnt = parent(leaf);
		}
		data.set(leaf, value);
	}

	//It moves node at index of root down to appropriate position
	@SuppressWarnings("unchecked")
	protected void pushDown(int root) 
	{
		int hpSize = data.size();
		T value = data.get(root);
		while (root < hpSize)
		{
			int child = leftChild(root);

			//It ensures that there exists left and right child and child indexes the bigger of the two children
			if (child < hpSize) 
			{
				if (rightChild(root) < hpSize
						&& ((Comparable<T>)data.get(child + 1)).compareTo(data.get(child)) > 0) 
				{
					child += 1;
				}

				//If the value of child is bigger than value of Parent it keeps moving down
				if (((Comparable<T>)value).compareTo(data.get(child)) < 0) 
				{
					data.set(root, data.get(child));
					root = child;
				} 
				else 
				{
					data.set(root, value);
					return;
				}
			} 
			else
			{ 
				data.set(root, value);
				return;
			}
		}
	}
}


