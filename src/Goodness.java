
public class Goodness implements Comparable<Goodness>
{
	public int neighborClusterID;
	public double goodnessWithClusterIdJ;

	public int compareTo(Goodness negh)
	{
		int s = 0; 
		if (this.goodnessWithClusterIdJ > negh.goodnessWithClusterIdJ) s = 1;
		else if (this.goodnessWithClusterIdJ < negh.goodnessWithClusterIdJ) s = -1;
		return s;
	}
}
