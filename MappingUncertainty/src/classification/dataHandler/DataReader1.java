package classification.dataHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

import classification.dataHandler.ProbabilityMatrix.Element;

public class DataReader1 {
	private String fileName;
	 
	/**
	 * @param args
	 */
	public DataReader1(String FileName)
	{
	this.fileName=FileName;
	}
	 
	public ArrayList<DataTable> ReadFile()
	{
	
		ArrayList<DataTable> records = new ArrayList<DataTable>();
		
	try {
	//storeValues.clear();//just in case this is the second call of the ReadFile Method./
		BufferedReader br = new BufferedReader( new FileReader(this.fileName));
	 
		StringTokenizer st = null;
		int lineNumber = 0, tokenNumber = 0;
	    String lineContent;
	    
	    ArrayList<String> fieldNames = new ArrayList<String>();
		while( (lineContent = br.readLine()) != null)
		{
			lineNumber++;
			//System.out.println(this.fileName);
			//storeValues.add(this.fileName);
			//break comma separated line using ","
			st = new StringTokenizer(lineContent, ",");
			if(lineNumber == 1 )
			{
				while(st.hasMoreTokens())
				{
					tokenNumber++;
		 
					String cellvalue=st.nextToken();
					if(tokenNumber > 4)
					{
						fieldNames.add(cellvalue);
						st.nextToken();
					}
				}
			}else if (lineNumber > 2)
			{
				DataTable record = new DataTable();
				while(st.hasMoreTokens())
				{
					tokenNumber++;
		 
					String cellvalue=st.nextToken();
					if(tokenNumber == 2)
					{
						record.setGID(Integer.parseInt(cellvalue));
					}else if(tokenNumber == 4)
					{
						record.setUnitName(cellvalue);
					}else if(tokenNumber > 4)
					{
						double estimate = Double.parseDouble(cellvalue);
						record.addEstimate(estimate);
						cellvalue=st.nextToken();
						double moe = Double.parseDouble(cellvalue);
						record.addMoe(moe);
					}
				}
					record.setFieldname(fieldNames);
					records.add(record);
			}
				
 	//reset token number
			tokenNumber = 0;

		}
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	 
	 return records;
	}
	

	public void sortSmalltoLarge(ArrayList<DataTable> records, int sortIndex){
		
		Collections.sort(records,new Comparator<DataTable>() {
			private int sortIndex;
            public int compare(DataTable o1, DataTable o2) {
				// TODO Auto-generated method stub
				double val1 = o1.getEstimate().get(this.sortIndex);
				double val2 = o2.getEstimate().get(this.sortIndex);
				return val1 < val2 ? -1 : val1 == val2 ? 0 : 1;
				
			}

		});

	}
	
	public static void WriteToFile (ProbabilityMatrix matrix, String newfilename)
	{
		PrintWriter pw = null;

	    try {

	      pw = new PrintWriter(new FileWriter(newfilename));
	      

	      for (int i = 0; i < matrix.getRows().size(); i++) {
	    	String line="";
	    	ArrayList<Element> currentrow = matrix.getRows().get(i);
	    	for (int j = 0; j < currentrow.size(); j++)
	    	{
	    		line = line + currentrow.get(j).getValue()+ ",";
	    	}
	        pw.println(line);

	      }
	      pw.flush();

	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }
	    finally {
	      
	      //Close the PrintWriter
	      if (pw != null)
	        pw.close();
	      
	    }
		
	}
	 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
