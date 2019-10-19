
import java.util.Vector;
public class LocalHeap 
{
	public int clusterID;
	public MaxHeap<Goodness> tHeap;
	public int numberOfTerms;

	//To add the node into Heap
	public void add(Goodness node) 
	{
		tHeap.add(node);
	}
	
	//To get the node with Maximum Goodness
	public Goodness getMaxGoodness() 
	{
		if (tHeap.isEmpty()) return null;
		return this.tHeap.getMaxElement();
	}

	//To remove the Node with clusterid Clusterid
	public int removeNodewithClusId(int Clusterid) 
	{
		MaxHeap<Goodness> tempHeap = new MaxHeap<Goodness>();
		int flag = 0;
		int size = this.tHeap.size();
		for (int i = 0; i < size; i++)
		{
			Goodness node = tHeap.removeTop();
			if (node.neighborClusterID != Clusterid)
			{
				tempHeap.add(node);
			}
			else
			{
				flag = 1; 
			}    
		}
		tHeap = tempHeap;
		if(flag == 0)
			return 0;
		else
			return 1;
	}
}

