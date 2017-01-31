package classification.dataHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import classification.dataHandler.ProbabilityMatrix.Element;

public class SAdistribution {
	private ArrayList<DataTable> data = new ArrayList<DataTable>();
	

	public SAdistribution(ArrayList<DataTable> NJdata) {
		// TODO Auto-generated constructor stub
		this.data=NJdata;
		PrintWriter pw = null;

	    try {

	      pw = new PrintWriter(new FileWriter("C:\\study\\myeclipseWorkspace\\MappingUncertainty\\estimates.txt"));
	      
	      String name="";
	      for (int i = 0; i < this.data.size(); i++) {
	    	
	    	name = name + this.data.get(i).getUnitName()+ ",";
	        
	      }
	      pw.println(name);
	      
	      for (int i = 0; i < 255; i++){
				//ArrayList<Double> estiamtes = new ArrayList<Double>();
				String line="";
				for(int j =0; j < this.data.size(); j++)
				{
					double mu = this.data.get(j).getEstimate().get(0);
					double sigma = this.data.get(j).getMoe().get(0);
					double generatedEst = CreateRandomEstiamte(mu, sigma);
					line = line + (int)generatedEst + ",";
					//estiamtes.add(generatedEst);
				}
				//int k =0;
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
	
	private double CreateRandomEstiamte(double mu, double sigma)
	{
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(1000000);
	     double random = ((double)randomInt)/1000000;
		double zscore = ProCalculator.PhiInverse(random);
	    double estimate = ProCalculator.zscoreInverse(zscore, mu, sigma);
	    return estimate;
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<DataTable> NJData = new ArrayList<DataTable>();
		ProbabilityMatrix matrix = new ProbabilityMatrix();
		
		
		DataReader1 reader = new DataReader1("C:\\study\\myeclipseWorkspace\\MappingUncertainty\\NJ2008percapitaincomeCounty.csv");
		NJData = reader.ReadFile();
		SAdistribution test = new SAdistribution(NJData);
		//reader.sortSmalltoLarge(this.NJData, 0);
		//ProCalculator calculator = new ProCalculator();
		//this.matrix = calculator.calculateProMatrix(this.NJData, 0);
	}

}
