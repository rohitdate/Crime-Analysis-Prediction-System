
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

class Cluster
{
	private static final int MAXIMUM=500;
	int clusId;
	int numterms;
	int arrcluster[]=new int[MAXIMUM];
}

public class Rock extends Applet implements ActionListener
{
	private static final long serialVersionUID = 1L;
	int col = 3, w;

	int MAX = 500;				//Maximum data
	double theta = 0.5;			//value of theta
	int kvalue = 12;				//Number of cluster

	String[][] temp = new String[MAX][20];
	int[][] arrneighbr = new int[MAX][MAX];
	int[][] link = new int[MAX][MAX];

	double goodnessmeasure;
	double goodness;
	int n = 0;					//n is the number of touples in data

	int NumberOfCluster;
	Cluster clus[] = new Cluster[MAX];
	int result[] = new int[MAX];
	LocalHeap lheap[] = new LocalHeap[MAX];
	LocalHeap GlobalHeap = new LocalHeap();

	public void init()
	{
		setLayout(null);
	}
	@SuppressWarnings({ "null", "resource" })

	public void paint(Graphics g) 
	{
		String fileName;

		fileName = "finish.csv";		//data file
		File file = new File(fileName);
		Scanner inputStream = null;
		try
		{
			inputStream = new Scanner(file).useDelimiter("\\n");

		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		String data=inputStream.next();		//ignore first line


		//scanning the data and putting in string matrix
		while(inputStream.hasNext())
		{
			data=inputStream.next();
			String[] values = data.split(",");
			temp[n][0] = values[4]; //Time Category
			temp[n][1] = values[5]; //Day 
			temp[n][2] = values[7]; //Age Category
			temp[n][3] = values[8]; //Gender
			temp[n][4] = values[9]; //Vehicle used
			temp[n][5] = values[12]; //Place Category
			temp[n][8] = values[18]; //Number Of suspects
			n++;
		}
		
		initialize();
		NumberOfCluster=n;
		CalculateNeighbors();	
		CalculateLink();
		BuildLocalHeap();
		GlobalHeap.numberOfTerms=n;
		BuildGlobalHeap();
		rockAlgo();
		Print();
		WriteIntoFile();
	}

	//To initialize
	public void initialize()
	{
		for(int i= 0; i < n; i++)
		{
			clus[i] = new Cluster();
			clus[i].clusId = i;
			clus[i].numterms = 1;
			clus[i].arrcluster[0] = i;
		}
		for(int i = 0;i < n; i++)
		{
			lheap[i] = new LocalHeap();
		}
	}


	public void rockAlgo()
	{
		Goodness tempneighbor = new Goodness();
		LocalHeap templocheap = new LocalHeap();

		//Algorithm iterates until only required number of  clusters are remaining in the global heap Q.
		//it also stops clustering if any two clusters can't be merged further as the number of links between
		//every pair of the remaining clusters becomes zero.
		while(NumberOfCluster > kvalue && GlobalHeap.numberOfTerms > 0)
		{

			//Remove all those clusters which has only one element when the number of remaining cluster is n/3
			if(NumberOfCluster == (n/3))
			{
				RemoveOutlier();
				BuildGlobalHeap();
			}

			//Max cluster U is extracted from GlobalHeap Q
			Goodness tempneighbor1 = new Goodness(); 
			int u = 0;
			if(GlobalHeap.tHeap.isEmpty() != true)
			{
				tempneighbor=GlobalHeap.getMaxGoodness();
				u=tempneighbor.neighborClusterID;
			}
			else
			{
				break;
			}

			//node V is extracted from lHeap[u].It is the first element of lHeap[u]
			tempneighbor1 = lheap[u].tHeap.data.get(0);
			int v=tempneighbor1.neighborClusterID;

			//cluster V is removed from GlobalHeap Q
			GlobalHeap.removeNodewithClusId(v);
			GlobalHeap.numberOfTerms--;
			clus[v].clusId=-1;

			//Merging of two Clusters U and V into U
			for(int i = 0; i < clus[v].numterms; i++)
			{
				clus[u].arrcluster[clus[u].numterms++] = clus[v].arrcluster[i];

			}
			NumberOfCluster--;
			int[] arrx = new int[n];
			int k;
			k=CalXQuUnionQv(arrx,u,v);	

			//link[x,w]=link[x,u]+link[x,v]
			for(int i = 0; i < k; i++)
			{
				link[arrx[i]][w] = link[arrx[i]][u]+link[arrx[i]][v];
			}

			//deleting node u and node v from q[x]
			for(int i = 0; i < k; i++)
			{
				int check;
				check = lheap[arrx[i]].removeNodewithClusId(u);
				if(check == 1)
					lheap[arrx[i]].numberOfTerms--;
				check = lheap[arrx[i]].removeNodewithClusId(v);
				if(check == 1)
					lheap[arrx[i]].numberOfTerms--;
			}
			w=u;

			//inserting w,g(x,w) in q[x]
			for(int i = 0; i < k; i++)
			{
				if(arrx[i] != u && arrx[i] != v)
				{
					Vector<Goodness> vdata1 = (Vector<Goodness>) new Vector();
					MaxHeap<Goodness> Maxvector1 = (MaxHeap<Goodness>) new  MaxHeap();
					for(int j = 0; j < lheap[arrx[i]].numberOfTerms; j++)
					{
						vdata1.add(lheap[arrx[i]].tHeap.data.get(j));
					}

					//To calculate the Goodness measure of Clus[w] and clus[arrx[i]]
					double goodness = GoodnessMeasure(clus[w], clus[arrx[i]]);
					Goodness tempneighbor2 = new Goodness();
					tempneighbor2.goodnessWithClusterIdJ = goodness;
					tempneighbor2.neighborClusterID=w;
					vdata1.add(tempneighbor2);
					Maxvector1.calculatepriority(vdata1);
					lheap[arrx[i]].tHeap=Maxvector1;
					lheap[arrx[i]].numberOfTerms++;
				}
			}

			//insert(q[w],x,g(x,w)
			lheap[w] = null;
			lheap[w] = new LocalHeap();
			int num = 0;
			Vector<Goodness> vdata1 = (Vector<Goodness>) new Vector();
			@SuppressWarnings("unchecked")
			MaxHeap<Goodness> Maxvector1 = (MaxHeap<Goodness>) new MaxHeap();
			int flagcheck = 0;
			for(int i = 0; i < k; i++)
			{
				if(arrx[i] != w && arrx[i] != v)
				{
					double goodness = GoodnessMeasure(clus[w], clus[arrx[i]]);
					Goodness tempneighbor2 = new Goodness();
					tempneighbor2.goodnessWithClusterIdJ = goodness;
					tempneighbor2.neighborClusterID = arrx[i];
					vdata1.add(tempneighbor2);
					Maxvector1.calculatepriority(vdata1);
					lheap[w].tHeap = Maxvector1;
					num++;
					flagcheck = 1;
				}
			}
			if(flagcheck == 0)
			{
				GlobalHeap.numberOfTerms--;
			}
			lheap[w].numberOfTerms = num;
			lheap[v].clusterID = -1;
			BuildGlobalHeap();		    	
		}	
	}

	//Remove all those clusters which has only one element when 
	//the number of remaining cluster in GlobalHeap is n/3
	public void RemoveOutlier()
	{
		for(int i = 0; i < n; i++)
		{
			if(clus[i].numterms == 1 && clus[i].clusId != -1)
			{
				clus[i].clusId = -1;
				GlobalHeap.numberOfTerms--;
				NumberOfCluster--;
				int[] arrxtemp = new int[MAX];
				int k = 0;
				for(int j = 0; j < lheap[i].numberOfTerms; j++)
				{
					Goodness tempneighbor2 = new Goodness();
					tempneighbor2 = lheap[i].tHeap.data.get(j);
					arrxtemp[k] = tempneighbor2.neighborClusterID;
					k++;
				}
				for(int l = 0; l < k; l++)
				{
					int check;
					check = lheap[arrxtemp[l]].removeNodewithClusId(i);
					if(check == 1)
						lheap[arrxtemp[l]].numberOfTerms--;
				} 
				lheap[i].clusterID = -1;
			}
		}
	}
	
	//calculate the similarity function between Pi and pj if sim(pi,pj)>theta
	//then pi and pj are neighbors
	public void CalculateNeighbors()
	{
		int tunion,tinter;
		int[] noterms = new int[n];
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				tunion = tinter = 0;

				for(int k = 0; k < col; k++)
				{
					if(temp[i][k].equals(temp[j][k]) == true)
					{
						tunion = tunion + 1;
						tinter = tinter + 1;
					}
					else
					{
						tunion = tunion + 2;
					}
				}
				double sim = (double)tinter / (double)tunion;
				if(sim >= theta == true)
				{
					noterms[i]++;
					arrneighbr[i][j] = 1;
				}
				else
				{
					arrneighbr[i][j] = 0;
				}
			}
		}
	}

	//calculation of link (common neighbors between pi and pj)
	//computing the number of links for all pairs of points is simply that of multiplying the adjacency
	//matrix arrneghbr with itself.
	public void CalculateLink()
	{
		for (int i = 0; i < n; i++) 
		{
			for (int j = 0; j < n; j++) 
			{
				if(i != j)
				{
					for (int k = 0; k < n; k++) 
					{
						link[i][j] = link[i][j] + arrneighbr[i][k] * arrneighbr[k][j];
					}
				}
				else
				{
					link[i][j] = 0;
				}
			}
		}
	}

	//For each cluster i, we build a local heap q[i] and maintain the heap
	//q[i] contains every cluster j such that link[i; j] is non-zero.
	//ordered in the decreasing order of the goodness measure
	public void BuildLocalHeap()
	{
		@SuppressWarnings("unchecked")
		Vector<Goodness>[] vdata = (Vector<Goodness>[]) new Vector[n];
		@SuppressWarnings("unchecked")
		MaxHeap<Goodness>[] Maxv = (MaxHeap<Goodness>[]) new  MaxHeap[n];
		for(int i = 0; i < n; i++)
		{
			vdata[i] = new Vector<Goodness>();
			Maxv[i] = new MaxHeap<Goodness>();
		}
		int numberterms = 0;
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				Goodness neighbor = new Goodness();
				if(link[i][j] != 0)
				{	
					neighbor.neighborClusterID = j;
					goodnessmeasure = GoodnessMeasure(clus[i],clus[j]);
					neighbor.goodnessWithClusterIdJ = goodnessmeasure;
					vdata[i].add(neighbor);
					numberterms++;
				}
			}

			//constructs a new priority queue acoording to GoodnessMeasure from an unordered vector
			Maxv[i].calculatepriority(vdata[i]);
			lheap[i].clusterID = i;
			lheap[i].tHeap = Maxv[i];
			lheap[i].numberOfTerms = numberterms;
			numberterms = 0;
		}
	}

	//Algorithm maintains an additional global heap Q that contains all the clusters.clusters in Q are ordered in the
	//decreasing order of their best goodness measures.
	//At each step, the max cluster j in Q and the max cluster in q[j] are the best pair of clusters to be merged.
	public void BuildGlobalHeap()
	{
		Vector<Goodness> TempGlobal = (Vector<Goodness>) new Vector();
		for(int i = 0; i < n; i++)
		{
			Goodness tempneighbor1 = new Goodness();
			Goodness tempneighbor2 = new Goodness();
			if(lheap[i].clusterID != -1 && lheap[i].numberOfTerms > 0)
			{
				//Getting the first node of every heap that is the node with highest Goodness measure 
				tempneighbor2 = lheap[i].tHeap.data.get(0);
				tempneighbor1.neighborClusterID = i;
				tempneighbor1.goodnessWithClusterIdJ = tempneighbor2.goodnessWithClusterIdJ;
				//adding node to the Heap
				TempGlobal.add(tempneighbor1);
				tempneighbor1 = lheap[i].tHeap.getMaxElement();
			}
		}
		//constructs a new priority queue acoording to GoodnessMeasure from an unordered vector
		MaxHeap<Goodness> MaxvGlobal = (MaxHeap<Goodness>) new MaxHeap();
		MaxvGlobal.calculatepriority(TempGlobal);
		GlobalHeap.clusterID = -1;
		GlobalHeap.tHeap = MaxvGlobal;
	}

	//To get all the nodes in U or V
	public int CalXQuUnionQv(int[] arrx, int u, int v)
	{
		//finding all the nodes in Local heap(u) and putting it in arrx[]
		int CountUV = 0;
		for(int j = 0; j < lheap[u].numberOfTerms; j++)
		{
			Goodness tempneighbor2 = new Goodness();
			tempneighbor2 = lheap[u].tHeap.data.get(j);
			arrx[CountUV] = tempneighbor2.neighborClusterID;
			CountUV++;
		}
		//finding all the nodes in Local heap(v) and putting it in arrx[]
		for(int j = 0; j < lheap[v].numberOfTerms; j++)
		{
			int flagCheckRedundancy = 0;
			Goodness tempneighbor2 = new Goodness();
			tempneighbor2=lheap[v].tHeap.data.get(j);
			//To check if the node is already present in arrx
			for(int l = 0; l < CountUV; l++)
			{
				if(arrx[l] == tempneighbor2.neighborClusterID)
				{
					flagCheckRedundancy = 1;
					break;
				}
			}
			if(flagCheckRedundancy == 0)
			{
				arrx[CountUV] = tempneighbor2.neighborClusterID;
				CountUV++;
			}
		}
		return CountUV;
	}

	//To calculate the goodness measure for a pair of clusters Ci,Cj
	/*g(Ci,Cj) = link[Ci,Cj]/(ni + nj)1+2f(theta)-ni(1+2f(theta)-nj(1+2f(theta))*/
	public double GoodnessMeasure(Cluster c1, Cluster c2)
	{	
		double ftheta = (1-theta) / (1+theta);
		int n1 = c1.numterms;
		int n2 = c2.numterms;
		double first = Math.pow(n1 + n2, (1+2 * ftheta));
		double second=Math.pow(n1, (1 + 2 * ftheta));
		double third=Math.pow(n2, 1 +2 * ftheta);
		goodness = (link[c1.clusId][c2.clusId]) / (first-second-third);
		return goodness;
	}

	
	public void Print()
	{
		System.out.println("-------------------------------------------------------------------");
		System.out.println();
		if(NumberOfCluster != kvalue)
		{
			System.out.println("sorry the dataset can't be divided into  " + kvalue + " clusters");
			System.out.println("Minimum number of cluster="+NumberOfCluster);
		}
		else
		{
			System.out.println("number of cluster=" + NumberOfCluster);

		}
		System.out.println();
		int count = 0;
		for(int i = 0; i < n; i++)
		{
			if(clus[i].clusId != -1)
			{
				count++;
				System.out.println();
				System.out.println("CLUSTER NUMBER  " + count);	 
				System.out.println("cluster id=" + clus[i].clusId + "  Number of terms in the cluster " + clus[i].numterms + "   " );
				for(int j = 0; j < clus[i].numterms; j++)
				{
					System.out.print(clus[i].arrcluster[j] + "\t");
					if(j==19 || j==39 || j==59 || j==79|| j==99)
					{
						System.out.println(); 
					}
				}
				System.out.println();
			}
		} 
	}

	//This is to write The toupleId and its corresponiding Cluster Number into CSV file.
	//This is the main output of this Algorithm.
	public void WriteIntoFile()
	{
		int val = 1;
		for(int i = 0; i < n; i++) 
		{
			if(clus[i].clusId != -1)
			{
				clus[i].clusId = val;
				val++;
			}
		}
		for(int i = 0; i < n; i++) 
		{
			if(clus[i].clusId != -1)
			{
				for(int j = 0; j < clus[i].numterms; j++)
				{
					result[clus[i].arrcluster[j]] = clus[i].clusId;
				}
			}
		}
		OutputStream output;
		try {
			String str;
			String str1;
			output = new FileOutputStream("test.csv");
			str = "Cluster Number";
			str1 = " \n";
			output.write(str.getBytes("UTF-8"));
			output.write(str1.getBytes("UTF-8"));
			for (int i = 0; i < n; i++) 
			{
				if(result[i] != 0)
					str = Integer.toString(result[i]);

				//Tuple is not in any of the cluster
				else
					str = "outlier";
				str.concat("\n");
				str1 = " \n";
				output.write(str.getBytes("UTF-8"));
				output.write(str1.getBytes("UTF-8"));
			}
			output.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) 
	{
		repaint();
	}
}
