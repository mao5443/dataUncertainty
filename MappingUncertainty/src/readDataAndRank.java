import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.lang.Math;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class readDataAndRank {

	/**
	 * @param args
	 */
	String fileName;
	 
	ArrayList <String>storeValues = new ArrayList<String>();
	ArrayList <Vector>attrValues = new ArrayList<Vector>();
	int rank[];
	int breakvalue[];
	ArrayList <Vector>breakcases = new ArrayList<Vector>();
	
	
	public readDataAndRank(String FileName)
	{
	this.fileName=FileName;
	}
	 
	public void ReadFile()
	{
	try {
	//storeValues.clear();//just in case this is the second call of the ReadFile Method./
	BufferedReader br = new BufferedReader( new FileReader(fileName));
	 
	StringTokenizer st = null;
	int lineNumber = 0, tokenNumber = 0;
	 
	while( (fileName = br.readLine()) != null)
	{
	lineNumber++;
	System.out.println(fileName);
	storeValues.add(fileName);
	//break comma separated line using ","
	st = new StringTokenizer(fileName, ",");
	Vector tmp = new Vector();
	
	while(st.hasMoreTokens())
	{
	 tokenNumber++;
	 
	 String cellvalue=st.nextToken();
	 if(tokenNumber>=4)
	 {
		 if(lineNumber>=3){
		 tmp.add(Integer.parseInt(cellvalue));}
	 }
//System.out.println("Line # " + lineNumber +", Token # " + tokenNumber+ ", Token : "+ st.nextToken());
	 
	}
	 
	//reset token number
	tokenNumber = 0;
	if(lineNumber>=3){
	attrValues.add(tmp);}
	 
	}
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	 
	 
	}
	 
	 
	 
	//mutators and accesors
	public void setFileName(String newFileName)
	{
	this.fileName=newFileName;
	}
	public String getFileName()
	{
	return fileName;
	}
	public ArrayList getFileValues()
	{
	return this.storeValues;
	}
	public void displayArrayList()
	{
	for(int x=0;x<this.storeValues.size();x++)
	{
	System.out.println(storeValues.get(x));
	}
	}
	public int getElementNumber()
	{
		return this.attrValues.size();
	}
	
	public void sortSmalltoLarge(){
		Collections.sort(this.attrValues,new Comparator<Vector>() {
            
			public int compare(Vector Values, Vector otherValues) {
				// TODO Auto-generated method stub
				int val1 = (Integer)Values.get(0);
			    int val2 = (Integer)otherValues.get(0);
			    return val1 < val2 ? -1 : val1 == val2 ? 0 : 1;
				
			}
		});

	}
	public void getRank(int elementNumber)
	{
		this.sortSmalltoLarge();
		this.rank=new int[elementNumber-1];
		this.breakvalue=new int[elementNumber-1];
		for(int i=0; i<this.attrValues.size()-1; i++) //for each domain between two estimates
		{ 
			int d1 = (Integer) this.attrValues.get(i).get(0);
			int d2 = (Integer) this.attrValues.get(i+1).get(0);
			Vector<Integer> cases= new Vector<Integer>();
			Vector<Integer> breakpoint = new Vector<Integer>();
			for(int j=0; j<this.attrValues.size(); j++)
			{
				int point=  (Integer)this.attrValues.get(j).get(0);
				int moe = (Integer)this.attrValues.get(j).get(1);
				int upperbound  = point+moe;
				int lowerbound = point - moe;
				if(upperbound > d1 && lowerbound < d1)
				{
					if(!cases.contains(j))
					{
						cases.add(j);
						breakpoint.add(upperbound);
					}
				}
				
				if(upperbound > d2 && lowerbound < d2)
				{
					if(!cases.contains(j))
					{
						cases.add(j);
						breakpoint.add(lowerbound);
					}
				}				
				
			}
			int tempRank=10000;
			int tempBreakpoint=0;
			Vector<Integer> tempBreakcases = new Vector<Integer>();
			for(int z=0; z<cases.size();z++)
			{
				int currentPoint = (Integer) breakpoint.get(z);
				int currentRank=0;
				Vector<Integer> currentBreakcases = new Vector<Integer>();
				currentBreakcases.add((Integer)cases.get(z));
				
				if(d1 > currentPoint || d2 < currentPoint)
				{
					currentPoint = (d1+d2)/2;  //if the uperbound or lowerBound is out of the current domain
					currentRank = 1;
				}
					
				for(int m=0; m<cases.size();m++)
				{
					if(m != z)
					{
						int point=  (Integer)this.attrValues.get((Integer)cases.get(m)).get(0);
						int moe = (Integer)this.attrValues.get((Integer)cases.get(m)).get(1);
						if((point+moe)>currentPoint && (point-moe)<currentPoint)
						{
							currentRank++;
							currentBreakcases.add((Integer)cases.get(m));
						}
					}
				}
				if(currentRank<tempRank)
				{
					tempRank=currentRank;
					tempBreakpoint=currentPoint;
					tempBreakcases=currentBreakcases;
				}
				
				
			}
			rank[i]=tempRank;
			System.out.println("rank "+i+"is: "+tempRank);
			breakvalue[i]=tempBreakpoint;
			System.out.println("Breakpoint "+i+"is: "+tempBreakpoint);
			breakcases.add(tempBreakcases);
			
		}
	}
	
	public void reRankbyTtest() throws MathException
	{
		for (int i=0; i<this.rank.length;i++)
		{
			if(rank[i]== 1)
			{
				int case1 = (Integer)this.breakcases .get(i).get(0);
				int case2 = (Integer) this.breakcases.get(i).get(1);
				if(Ttest(case1, case2))
				{
					rank[i] =0;
				}
					
				
			}
			
		}
		
	}
	
	public boolean Ttest(int case1, int case2) throws MathException
	{
		int estimate1 = (Integer)this.attrValues.get(case1).get(0);
		int estimate2 = (Integer)this.attrValues.get(case2).get(0);
		int moe1 = (Integer)this.attrValues.get(case1).get(1);
		int moe2 = (Integer)this.attrValues.get(case2).get(1);
		float t = (float) Math.abs((estimate1-estimate2)/Math.sqrt((moe1/1.645)*(moe1/1.645)+(moe2/1.645)*(moe2/1.645)));
		if(t>1.96)  //the two cases are signigicantly different
			return true;
		else
			getProbability(t);
			return false;
		
	}
	
	public void getProbability(float zscore) throws MathException
	{
		
		NormalDistributionImpl normal = new NormalDistributionImpl(); 
		double pro=normal.cumulativeProbability(-zscore,zscore);
	}
	
	public int[] getRankValues()
	{
		return this.rank;
	}
	public int[] getBreakpointValues()
	{
		return this.breakvalue;
	}	
	
	public static void main(String[] args) throws MathException {
		// TODO Auto-generated method stub
		readDataAndRank dataReader=new readDataAndRank("C:\\study\\myeclipseWorkspace\\mappingclassify\\NJ2008percapitaincomeCounty.csv");
		//readDataAndRank dataReader=new readDataAndRank("C:\\study\\myeclipseWorkspace\\mappingclassify\\dt_acs_2009_5yr_g00__data1.csv");
		dataReader.ReadFile();
		int elementNumber=dataReader.getElementNumber();
		int rank[]=new int[elementNumber-1];
		dataReader.getRank(elementNumber);
		dataReader.reRankbyTtest();
	}
	
	
	

}
