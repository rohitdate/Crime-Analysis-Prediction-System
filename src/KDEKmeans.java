

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//Common variables' declarations
class Global
{
	public static int totalPoints = 3000;
}

// Structure used for KDE Algorithm
class dataList
{
	double latitude;
	double longitude;
	double xtstar;
	double ytstar;
	double st;
	boolean visited;
}

public class KDEKmeans extends Applet implements ActionListener,MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	private Image map = null;
	dataList n1[] = new dataList[Global.totalPoints];
	int n2[][] = new int[2000][Global.totalPoints];
	int notrms[] = new int[Global.totalPoints];
	int noData = 1, count = 0;
	int[] amed = new int[Global.totalPoints];
	double[] maximaX = new double[Global.totalPoints];
	double[] maximaY = new double[Global.totalPoints];
	boolean flagb1 = false, flagb2 = false, flagb3 = true, flagb4 = false,flagb5 = false;
	int[][] arrSquare = new int[20][15];
	int back = -1,front = 0;
	int count1,count2,count3,count4,count5;
	boolean isButtonPressed = false;
	int mx,my,thresholdval;
	double changeval = 0.01;	//change in probability density
	int noclust = 0;
	double h = 0.0167;
	
	TextField textbox_kvalue, textbox_xcord, textbox_ycord, textbox_kde;
	Button button_kmeans, button_kde, button_chicago, button_pune,button_sqcount;
	Checkbox[] crimeTypes = new Checkbox[4];
	CheckboxGroup ops = new CheckboxGroup();

	Checkbox checkbox_db,checkbox_select;

	// Initial method
	public void init()
	{
		setLayout(null);

		// K VALUE
		textbox_kvalue = new TextField(10);
		textbox_kvalue.setBounds(1235, 123, 50, 25);
		add(textbox_kvalue);

		// X COORDINATE
		textbox_xcord = new TextField(10);
		textbox_xcord.setBounds(1000, 460, 70, 25);
		add(textbox_xcord);

		// Y COORDINATE
		textbox_ycord = new TextField(10);
		textbox_ycord.setBounds(1170, 460, 70, 25);
		add(textbox_ycord);

		// KDE RESULT
		textbox_kde = new TextField(10);
		textbox_kde.setBounds(970, 540, 160, 25);
		add(textbox_kde);

		

		// SELECTIVE CRIMES FOR FILTERING OF DATABASE
		crimeTypes[0] = new Checkbox("THEFT");
		crimeTypes[1] = new Checkbox("DOMESTIC CRIME");
		crimeTypes[2] = new Checkbox("NARCOTICS");
		crimeTypes[3] = new Checkbox("CRIMINAL DAMAGE");

		crimeTypes[0].setBounds(970, 200, 90, 30);
		crimeTypes[1].setBounds(1120, 200, 130, 30);
		crimeTypes[2].setBounds(970, 240, 90, 30);
		crimeTypes[3].setBounds(1120, 240, 130, 30);

		for (count1 = 0; count1 < 4; count1++)
		{
			add(crimeTypes[count1]);
		}

		checkbox_db = new Checkbox("WHOLE DATABASE", ops, true);
		checkbox_select = new Checkbox("SELECTED CRIMES", ops, false);

		checkbox_db.setBounds(970, 295, 140, 30);
		checkbox_select.setBounds(1120, 295, 140, 30);

		add(checkbox_db);
		add(checkbox_select);

		addMouseListener( this );
		addMouseMotionListener( this );

		mx = getWidth()/2;
		my = getHeight()/2;

		// K MEANS ALGORITHM
		button_kmeans = new Button("Run");
		button_kmeans.setBounds(1165, 85, 100, 25);
		add(button_kmeans);
		button_kmeans.addActionListener(this);

		// KERNEL DENSITY ESTIMATION
		button_kde = new Button("Run");
		button_kde.setBounds(1165, 385, 110, 25);
		add(button_kde);
		button_kde.addActionListener(this);

		// TO SELECT MAP OF CHICAGO OR PUNE
		button_chicago = new Button("CHICAGO");
		button_chicago.setBounds(970, 40, 80, 25);
		add(button_chicago);
		button_chicago.addActionListener(this);

		button_pune = new Button("PUNE");
		button_pune.setBounds(1080, 40, 80, 25);
		add(button_pune);
		button_pune.addActionListener(this);

		button_sqcount = new Button("Sqcount");
		button_sqcount.setBounds(1190, 40, 80, 25);
		add(button_sqcount);
		button_sqcount.addActionListener(this);
	}

	public void mouseEntered( MouseEvent e ){
		//It is called when the pointer enters the applet's rectangular area
	}
	public void mouseExited( MouseEvent e ) {
		// It is called when the pointer leaves the applet's rectangular area
	}
	public void mouseClicked( MouseEvent e ) {
	}
	public void mousePressed( MouseEvent e ) {  // called after a button is pressed down
		isButtonPressed = true;
		if(flagb3 == true)
		{
			textbox_xcord.setText(String.valueOf(41.674936+(mx*(42.01002-41.674936)/950)));
			textbox_ycord.setText(String.valueOf(-87.940857+(my*(-87.525036-(-87.940857))/650)));
		}
		else
		{
			textbox_xcord.setText(String.valueOf(18.322589+(mx*(18.745758-18.322589)/950)));
			textbox_ycord.setText(String.valueOf(73.54866+(my*(74.045684-(73.54866))/650)));  
		}
		repaint();
		e.consume();
	}

	//It is called after a button is released
	public void mouseReleased( MouseEvent e ) {  
		isButtonPressed = false;
		e.consume();
	}  
	//It is called during motion when no buttons are down  
	public void mouseMoved( MouseEvent e ) {  
		mx = e.getX();
		my = e.getY();
		e.consume();
	}

	// called during motion with buttons down
	public void mouseDragged( MouseEvent e ) {  
		mx = e.getX();
		my = e.getY();
		showStatus( "Mouse at (" + mx + "," + my + ")" );
		e.consume();
	}

	public void actionPerformed(ActionEvent e)
	{
		//when k-means button is pressed
		if (e.getSource() == button_kmeans)
		{
			flagb1 = true;
			repaint();
		}

		if (e.getSource() == button_kde)
		{
			flagb2 = true;
			repaint();
		}

		if (e.getSource() == button_chicago)
		{
			flagb3 = true;
			flagb4 = false;
			repaint();
		}

		if (e.getSource() == button_pune)
		{
			flagb4 = true;
			flagb3 = false;
			repaint();
		}

		if (e.getSource() == button_sqcount)
		{
			if (flagb5 == false)
			{
				flagb5 = true;
			}
			else
			{
				flagb5 = false;
			}
			repaint();
		}
	}

	public void paint(Graphics g)
	{	
		double minLat;
		double minLong;
		double maxLat;
		double maxLong;

		//MAximum and minimum latitude and longitude of chicago city map
		if (flagb3 == true)
		{
			minLat = 41.674936;
			minLong = -87.940857;
			maxLat = 42.01002;
			maxLong = -87.525036;
		}
		//MAximum and minimum latitude and longitude of pune city map
		else
		{
			minLat = 18.322589;
			minLong = 73.54866;
			maxLat = 18.745758;
			maxLong = 74.045684;
		}

		// Map image size (in points)
		int mapHeight = 650;
		int mapWidth = 950;

		// Determine the map scale (points per degree)
		double xScale = mapWidth / (maxLong - minLong);
		double yScale = mapHeight / (maxLat - minLat);

		// Position of map image for point
		this.setSize(1300, mapHeight);

		if (flagb3 == true)
		{
			map = getImage("Chicago.jpg");
		}
		else
		{
			map = getImage("pune.jpg");
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(map, 0, 0, mapWidth, mapHeight, this);
		// Added for making squares

		g.setColor(Color.black);
		Font myFont = new Font("TimesRoman", Font.BOLD, 20);
		g.setFont(myFont);
		g.drawString("CRIME ANALYSIS SYSTEM", 960, 30);
		g.drawString("K means", 970, 105);
		myFont = new Font("TimesRoman", Font.BOLD, 14);
		g.setFont(myFont);
		g.drawString("ENTER NO. OF CLUSTERS(Minimum 1) :", 970, 140);
		g.drawString("CRIME TYPES :", 970, 185);
		g.drawString("============================================", 950, 80);
		g.drawString("============================================", 950, 350);
		myFont = new Font("TimesRoman", Font.BOLD, 20);
		g.setFont(myFont);
		g.drawString("KDE", 970, 370);
		myFont = new Font("TimesRoman", Font.BOLD, 14);
		g.setFont(myFont);
		g.drawString("GENERATE HOTSPOTS :", 970, 400);
		g.drawString("ENTER X AND Y COORDINATES FOR KDE :", 970, 440);
		g.drawString("X:", 970, 475);
		g.drawString("Y:", 1140, 475);
		g.drawString("PROBABILITY DENSITY:", 970, 520);
		setBackground(Color.lightGray);
		String fileName;

		if (flagb3 == true)
		{
			fileName = "chicagof.csv";
			thresholdval = 100;
		}
		else
		{
			fileName = "finish.csv";
			thresholdval = 50;
		}

		File file = new File(fileName);
		Scanner inputStream = null;
		try
		{
			inputStream = new Scanner(file).useDelimiter("\\n");
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}

		String data = inputStream.next();
		count2 = 0;
		
		//To initialize the arrSquare matrix which contains the number of crimes for every square.
		for (count3 = 0; count3 < 20; count3++)
		{
			for (count4 = 0; count4 < 15; count4++)
			{
				arrSquare[count3][count4] = 0;
			}
		}



		while (inputStream.hasNext())
		{
			n1[count2] = new dataList();
			data = inputStream.next();
			String[] values = data.split(",");
			double lat;
			double lon;
			
			//lat long for Chicago data
			if (flagb3 == true)
			{
				lat = Double.parseDouble(values[18]);
				lon = Double.parseDouble(values[20]);
			}
			
			//lat long for pune data
			else
			{
				lat = Double.parseDouble(values[19]);
				lon = Double.parseDouble(values[20]);
			}

			// for counting the number of crimes in every square
			//If the whole database is selected then all types of crimes are scanned and put into structure
			
			if (checkbox_db.getState() == true)
			{
				n1[count2].latitude = lat;
				n1[count2].longitude = lon;
				double x = (lon - minLong) * xScale;
				double y = (maxLat - lat) * yScale;
				int xint = (int) (x);
				int yint = (int) (y);
				arrSquare[xint / 50][yint / 50] = arrSquare[xint / 50][yint / 50] + 1;
				count2++;
				continue;
			}
			
			//selected crimes are put into structure
			else if (checkbox_select.getState() == true)
			{
				for (count5 = 0; count5 < 4; count5++)
				{
					if (crimeTypes[count5].getState() == true)
					{
						if (crimeTypes[count5].getLabel().equalsIgnoreCase(values[5]))
						{
							n1[count2].latitude = lat;
							n1[count2].longitude = lon;
							count2++;
						}
					}
				}
			}
		}

		inputStream.close();
		noData = count2;

		for (count1 = 0; count1 < noData; count1++)
		{
			n1[count1].visited = false;
		}

		for (count2 = 0; count2 < 500; count2++)
		{
			notrms[count2] = 0;
		}

		//To Display the Count of Crime in every square
		if (flagb5 == true) 
		{
			g.setColor(Color.green);
			int line = mapHeight;

			while (line > 0)
			{
				g.drawLine(0, line, mapWidth, line);
				line = line - 50;
			}
			line = mapWidth;
			while (line > 0) 
			{
				g.drawLine(line, 0, line, mapHeight);
				line = line - 50;
			}
			g.setColor(Color.black);
			for (int count3 = 0; count3 < 19; count3++)
			{
				for (int count4 = 0; count4 < 14; count4++) 
				{
					String str = Integer.toString(arrSquare[count3][count4]);
					g.drawString(str, count3 * 50 + 20, count4 * 50 + 45);
				}
			}

		}

		int km;

		//This is to draw the points of different clusters in different colors
		if (flagb1 == true)
		{
			String dummy = textbox_kvalue.getText();
			if (!dummy.isEmpty())
			{
				try {
					
					km = Integer.parseInt(dummy);
					if (km > 0)
					{
						int[] ac = kMeans(km);

						count2 = 0;
						while (count2 < noData)
						{
							double lat = n1[count2].latitude;
							double lon = n1[count2].longitude;
							double x = (lon - minLong) * xScale;
							double y = (maxLat - lat) * yScale;

							for (int kmeanscounter = 0; kmeanscounter < km; kmeanscounter++)
							{
								if (amed[kmeanscounter] == ac[count2])
								{
									Color myNewCol = new Color(
											(89 * kmeanscounter) % 256,
											(32 * kmeanscounter) % 256,
											(71 * kmeanscounter) % 256);
									g.setColor((myNewCol));
								}
							}
							g.fillOval((int) (x), (int) (y), 7, 7);
							count2++;
						}
					}
					else
					{
						g.drawString("ERROR : NEGATIVE NUMBER OR ZERO", 970,160);
					}
				} catch (NumberFormatException e) {
					g.drawString("WARNING : ENTER VALID NUMBER", 970, 160);
				}
			}
			else
			{
				g.drawString("WARNING : ENTER NUMBER OF CLUSTER", 970, 160);
			}
			flagb1 = false;
		}
		
		//To calculate the probability density at any coordinate
		String xcord = textbox_xcord.getText();
		String ycord = textbox_ycord.getText();
		double probdens = 0;
		if (!xcord.isEmpty() && !ycord.isEmpty())
		{
			double xd = Double.parseDouble(xcord);
			double yd = Double.parseDouble(ycord);
			probdens = kdeprobdens(xd, yd);
			textbox_kde.setText(String.valueOf(probdens/40));
		}
		
		
		//If kde button is pressed
		if (flagb2 == true)
		{
			kde();
			//Different dense clusters are displayed in different colors.
			for (count2 = 0; count2 < noclust; count2++)
			{
				Color myNewCol2 = new Color((89 * count2) % 256, (32 * count2) % 256,(71 * count2) % 256);
				g.setColor(myNewCol2);
				if (notrms[count2] > thresholdval)
				{
					for (count3 = 0; count3 < notrms[count2]; count3++) {
						double lat = n1[n2[count2][count3]].latitude;
						double lon = n1[n2[count2][count3]].longitude;
						double x = (lon - minLong) * xScale;
						double y = (maxLat - lat) * yScale;
						g.fillOval((int) (x), (int) (y), 7, 7);
					}
				}
			}
			flagb2 = false;
		}
	}

	
	
	public void kde()
	{
		double probdens = 0;
		for (count3 = 0; count3 < noData; count3++)
			probdens = kde(n1[count3].latitude, n1[count3].longitude, count3);

		int n = 0,m = 0;
		n2[0][0] = 0;
		notrms[0] = 1;
		noclust = 1;
		n1[0].visited = true;
		int z;

		for (count4 = 1; count4 < noData; count4++)
		{
			probdens = kdeprobdens(n1[count4].latitude, n1[count4].longitude);
			if (probdens > 20)
			{				
				//If the condition st+st'>(distance bw xtstar and ststar') is true for all the points in any cluster 
				//then the point is added in the existing cluster
				for (m = 0; m < noclust; m++)
				{
					for (n = 0; n < notrms[m]; n++)
					{
						z = n2[m][n];
						double d1 = n1[count4].xtstar - n1[z].xtstar;
						double d2 = n1[count4].ytstar - n1[z].ytstar;
						double temp1 = (Math.sqrt(Math.pow(d1, 2) + Math.pow(d2, 2)));
						double temp2 = n1[count4].st + n1[z].st;
						 //If st+st'<(distance between xtstar and xtstar') then break
						if (temp1 > temp2)
							break;
					}

					if (n == notrms[m]) 
					{
						n2[m][n] = count4;
						notrms[m]++;
						n1[count4].visited = true;
						break;
					}
				}
				
				//If the above condition is not true for any of the cluster than the new cluster is created and
				//the point is added
				if (n1[count4].visited == false)
				{
					n1[count4].visited = true;
					n2[m][0] = count4;
					noclust++;
				}
			}
		}	
	}
	
	
	public boolean action(Event e, Object o)
	{
		repaint();
		return true;
	}

	// FUNCTION TO FIND THE DISTANCE
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) 
					  * Math.sin(deg2rad(lat2))
					  + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
					  * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = dist * 6378.38;
		return (dist);
	}

	private double deg2rad(double deg) 
	{
		return (deg * Math.PI / 180.0);
	}

	// FUNCTION TO FIND THE PROBABILITY DENSITY
	//K(u)=((2*pi())^(-d/2).exp[-u^2/2)
	//p(x)=1/(Nh^d)sum(t=1 to N)K(x-xt/h)
	private double kdeprobdens(double x, double y)
	{
		double[] u = new double[Global.totalPoints];
		double sum1 = 0, prob_density = 0;
		
		try{
			for (int count1 = 0; count1 < noData; count1++) {
				double d1 = x - n1[count1].latitude;
				double d2 = y - n1[count1].longitude;
				double temp1 = (Math.sqrt((Math.pow(d1, 2) + Math.pow(d2, 2))) / h);
				u[count1] = Math.exp(Math.pow(temp1, 2) / (-2)) / (2 * Math.PI);
				sum1 = sum1 + u[count1];
			}
			prob_density =(1 / (noData * Math.pow(h, 2))) * sum1;
		}
		catch(NumberFormatException nfe){}
		return prob_density;
	}

	// KERNEL DENSITY ESTIMATION ALGORITHM
	private double kde(double x, double y, int z)
	{
		double[] u = new double[Global.totalPoints];
		double[] u1 = new double[Global.totalPoints];
		double[] u2 = new double[Global.totalPoints];
		double d1, d2, temp1, pd = 0, temp2, temp3, xl1, yl1, oldpd = 0, change;
		double sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0, sum5 = 0;
		try{
			int k = 0;
			double[] steps = new double[2];
			steps[0] = 0;
			steps[1] = 0;
			while (true)
			{
				sum1 = sum2 = sum3 = sum4 = sum5 = 0;
				for (count1 = 0; count1 < noData; count1++) {
					d1 = x - n1[count1].latitude;
					d2 = y - n1[count1].longitude;
					
					//To calculate probability density
					temp1 = (Math.sqrt((Math.pow(d1, 2) + Math.pow(d2, 2))) / h);  //temp1=(x(l)-xt)/h
					u[count1] = Math.exp(Math.pow(temp1, 2) / (-2)) / (2 * Math.PI); //k(temp1) 
					sum1 = sum1 + u[count1];
					
					//To get the next x coordinate (x(l+1))
					temp2 = (Math.exp(Math.pow(d1 / h, 2) / (-2))) / (2 * Math.PI);
					u1[count1] = temp2 * n1[count1].latitude;
					sum2 = sum2 + u1[count1];
					sum3 = sum3 + temp2;
					
					//To get the next y coordinate (y(l+1))
					temp3 = (Math.exp(Math.pow(d2 / h, 2) / (-2))) / (2 * Math.PI);
					u2[count1] = temp3 * n1[count1].longitude;
					sum4 = sum4 + u2[count1];
					sum5 = sum5 + temp3;
				}
				pd = (1 / (noData * Math.pow(h, 2))) * sum1;
				xl1 = sum2 / sum3;
				yl1 = sum4 / sum5;

				if (k > 0)
				{
					change = (pd - oldpd) / pd;
					
					//change in probability density should be greater than or equal to 0.01
					if (change <= changeval)						
					{
						break;
					}
				}
				
				oldpd = pd;
				d1 = xl1 - x;
				d2 = yl1 - y;
				temp1 = (Math.sqrt(Math.pow(d1, 2) + Math.pow(d2, 2)));
				steps[k % 2] = temp1;
				x = xl1;
				y = yl1;
				k++;
			}
			n1[z].xtstar = xl1;
			n1[z].ytstar = yl1;
			
			//sum of k last step size. here k=2
			n1[z].st = (steps[0] + steps[1]);
		}
		catch(NumberFormatException nfe){}
		return oldpd;
	}

	// K MEANS CLUSTERING ALGORITHM
	public int[] kMeans(int km)
	{
		boolean changed;
		int c;
		int[] ac = new int[noData];
		Random randomGenerator = new Random();

		for (count1 = 0; count1 < km; count1++)
		{
			amed[count1] = Math.abs(randomGenerator.nextInt(noData));
		}

		for (count1 = 0; count1 < noData; count1++)
		{
			ac[count1] = findNearestMedoid(n1, count1, amed, km);
		}

		do
		{
			changed = false;
			for (count1 = 0; count1 < km; count1++)
			{
				c = findMedoid(n1, ac, amed[count1]);
				if (c != amed[count1])
				{
					changed = true;
					amed[count1] = c;
				}
			}
			for (count1 = 0; count1 < noData; count1++)
			{
				ac[count1] = findNearestMedoid(n1, count1, amed, km);
			}
		}while (changed);

		return ac;
	}

	// FUNCTION TO FIND THE MEDOID
	private int findMedoid(dataList[] n1, int[] ac, int c)
	{
		double dx, dMin = Double.POSITIVE_INFINITY;
		int m = 0;
		dataList n2 = new dataList();
		n2 = c1Mean(ac, c);
		for (count1 = 0; count1 < noData; count1++)
		{
			if (ac[count1] != c)
			{
				continue;
			}
			dx = distance(n2.latitude, n2.longitude, n1[count1].latitude,
					n1[count1].longitude);
			if (dx < dMin) {
				dMin = dx;
				m = count1;
			}
		}
		return (m);
	}

	private dataList c1Mean(int[] ac, int c) {
		dataList n3 = new dataList();
		double sumLat = 0;
		double sumLon = 0;
		int num = 0;
		for (count1 = 0; count1 < noData; count1++)
		{
			if (ac[count1] != c)
			{
				continue;
			}
			sumLat = sumLat + n1[count1].latitude;
			sumLon = sumLon + n1[count1].longitude;
			num++;
		}
		n3.latitude = sumLat / num;
		n3.longitude = sumLon / num;
		return (n3);
	}

	// FUNCTION TO FIND THE NEAREST MEDOID
	private int findNearestMedoid(dataList n1[], int i, int amed[], int km)
	{
		int m = -1;
		double dx, dMin = Double.POSITIVE_INFINITY;
		for (count2 = 0; count2 < km; count2++) {
			dx = distance(n1[i].latitude, n1[i].longitude,
					n1[amed[count2]].latitude, n1[amed[count2]].longitude);
			if (dx < dMin)
			{
				dMin = dx;
				m = amed[count2];
			}
		}
		return m;
	}

	// FUNCTION TO OBTAIN IMAGE
	public Image getImage(String path)
	{
		Image tempImage = null;
		try
		{
			URL imageURL = KDEKmeans.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			System.out.println("An error occured - " + e.getMessage());
		}
		return tempImage;
	}
}