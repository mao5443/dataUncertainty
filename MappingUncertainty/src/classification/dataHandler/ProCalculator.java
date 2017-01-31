package classification.dataHandler;

import java.util.ArrayList;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class ProCalculator {

	
	public ProCalculator() {
		// TODO Auto-generated constructor stub
	}

	
	public static double Ttest(double estimate1, double moe1, double estimate2, double moe2) throws MathException
	{
		double t =  Math.abs((estimate1-estimate2)/Math.sqrt((moe1/1.645)*(moe1/1.645)+(moe2/1.645)*(moe2/1.645)));
		return t;		
	}
	
	public static double getProbability(float zscore) throws MathException
	{
		
		NormalDistributionImpl normal = new NormalDistributionImpl(); //change to t distribution 
		float pro=(float) normal.cumulativeProbability(-zscore,zscore);
		return pro;
	}
	
	public static double getProbability(double estimate1, double moe1, double estimate2, double moe2) throws MathException
	{
		double zscore = Ttest(estimate1, moe1, estimate2, moe2);
		NormalDistributionImpl normal = new NormalDistributionImpl(); 
		double pro= normal.cumulativeProbability(-zscore,zscore);
		return pro;
                
	}

        public ArrayList<Double> getCV(ArrayList<DataTable> data, int fieldIndex, int missvaluenumber)
        {
            ArrayList<Double> cv = new ArrayList<Double>();
            for(int i = missvaluenumber; i<data.size(); i++)
            {
                double estimate = data.get(i).getEstimate().get(fieldIndex);
                double error = data.get(i).getMoe().get(fieldIndex);
                double currentcv = (error/1.645)/estimate;
                cv.add(currentcv);
            }
            
            return cv;
        }
	
	public ProbabilityMatrix calculateProMatrix(ArrayList<DataTable> data, int fieldIndex, int startindex) throws MathException{
		ProbabilityMatrix pros =  new ProbabilityMatrix();
		
		ArrayList<Double> tmpEst = new ArrayList<Double>();
		ArrayList<Double> tmpMOE = new ArrayList<Double>();
		int rownum = data.size();
		for(int i =startindex; i < rownum; i++)
		{
			tmpEst.add(data.get(i).getEstimate().get(0));
			tmpMOE.add(data.get(i).getMoe().get(0));
			
		}
		
		
		for(int i = 0; i < rownum-startindex; i++)
		{
			ArrayList<ProbabilityMatrix.Element> currentCol = new ArrayList<ProbabilityMatrix.Element>();
			
			//for(int j = i ; j < rownum; j++)
			for(int j = 0 ; j < rownum-startindex; j++)
			{
				ProbabilityMatrix.Element currentValue = pros.createNewElement();
				currentValue.setID1(i);
				currentValue.setID2(j);
				double currentPro = getProbability((Double)tmpEst.get(i),(Double)tmpMOE.get(i),(Double)tmpEst.get(j),(Double)tmpMOE.get(j));
				currentValue.setValue(currentPro);
				currentCol.add(currentValue);
			}
			//pros.setColumns(currentCol);
			pros.addRows(currentCol);
			
		}
		return pros;
		
	}
	
	public static double getInstersction(double threshold, double mu1, double sigma1, double mu2, double sigma2)
	{
		
		sigma1 = sigma1/1.645;
		sigma2 = sigma2/1.645;
		
		double x1=mu1;
		double x2=mu2;
		
		double middleP= 0;
		double middleC= getMiddle(x1,x2);
		while(Math.abs(middleP-middleC)>threshold)
		{
			if (phi(middleC,mu1,sigma1)> phi(middleC,mu2,sigma2))
			{
				x1=middleC;
			}
			else if (phi(middleC,mu1,sigma1)< phi(middleC,mu2,sigma2))
			{
				x2=middleC;
			}
			middleP=middleC;
			middleC=getMiddle(x1,x2);
			//System.out.println(middleC);
		}
		
		return middleP;
		
		
	}
 
	
    private static double getMiddle(double x1, double x2)
    {
    	return (x1+x2)/2;
    }
    public  static double getIntersectThreshold(ArrayList<DataTable> data, int fieldIndex)
    {
    	double thres;
    	int len= data.size();
    	double sum=0;
    	for (int i = 0; i < len; i++)
    	{
    		sum = sum + data.get(i).getEstimate().get(fieldIndex);
    	}
    	return (sum/len)/1000000;
    }
    
    public static ArrayList<Domain> getBreakpoints(ArrayList<Domain> breakpoints, double threshold, int domainLength, ProbabilityMatrix matrix)
    {
    	ArrayList<Domain> newBreakpoints = new ArrayList<Domain>();
    	

        //clear the domain in the breakpoint list which has low CL than the current threshold
      	for(int j = 0; j < newBreakpoints.size(); j++)
      	{
      		double tmpCL = newBreakpoints.get(j).getCL();
      		
      		if (tmpCL < threshold)
      		{
      			newBreakpoints.remove(j);
      		}
      	} 
      	
          for (int i =0; i < domainLength; i++)
          {
          	int beforeID = i;
          	int afterID = i+1;
          	double CL = 1.0;
          	boolean continueflag = true;
          	boolean sigflag = true;
          	int lowestBeforeID = i;
          	int lowestAfterID = i+1;
          	
          	
          	//check if the domian with Cl higher than the threshold is already in the breakpoint list then don't add it again
          	for(int j = 0; j < newBreakpoints.size(); j++)
          	{
          		int id1 = newBreakpoints.get(j).getLeftID();
          		int id2 = newBreakpoints.get(j).getRightID();
          		
          		if (beforeID == id1 && afterID == id2)
          		{
          			continueflag = false;
          			break;
          		}
          	}
          	if (continueflag)
          	{
          		ArrayList<ProbabilityMatrix.Element> currentCol = new ArrayList<ProbabilityMatrix.Element>();
          		currentCol = matrix.getRows().get(beforeID);
          		for(int k = beforeID+1; k < currentCol.size(); k++)  //check the right side of beforeID
          		{
          			double pvalue = currentCol.get(k).getValue();
          			if (pvalue < threshold)
          			{
          				sigflag = false;
          				break;
          			}else
          			{
          				if (pvalue < CL)
          				{
          					CL=pvalue; //get the lowest Confidence level of the domain
          					lowestBeforeID = beforeID;
          					lowestAfterID = k;
          					
          				}
          			}
          		}
          		if (sigflag)
          		{
          			currentCol=matrix.getRows().get(afterID);
              		for(int k = afterID - 1; k >= 0 ; k--) //check the left side of afterID
              		{
              			double pvalue = currentCol.get(k).getValue();
              			if (pvalue < threshold)
              			{
              				sigflag = false;
              				break;
              			}
              			else
              			{
              				if (pvalue < CL)
              				{
              					CL=pvalue; //get the lowest Confidence level of the domain
              					lowestBeforeID = k;
              					lowestAfterID = afterID;
              					
              				}
              			}
              		}
          		}
          		
          		if(sigflag)
          		{
          			Domain currentDomain = new Domain();
          			currentDomain.setLeftID(beforeID);
          			currentDomain.setRightID(afterID);
          			currentDomain.setCL(CL);
          			currentDomain.setLowestCLLeftID(lowestBeforeID);
          			currentDomain.setLowestCLRightID(lowestAfterID);
          			newBreakpoints.add(currentDomain);
          			//double intersectPoint = getInstersction(intersectThreshold, );
          			//currentDomain.setIntersectPoint(intersectPoint);
          		}
          			
          		
          	}else 
          		break;
          	
          }
    	return newBreakpoints;
    }

    public static double getBreakpointCL(int leftID, int rightID, ProbabilityMatrix matrix)
    {

         int length = matrix.getRows().size();
         double CL = 1.0;
         int lowestBeforeID =leftID;
          int lowestAfterID = rightID;
          for (int i =0; i < length-1; i++)
          {
          	int beforeID = i;
          	int afterID = i+1;
                if(leftID == beforeID && rightID == afterID)
                {
                    ArrayList<ProbabilityMatrix.Element> currentCol = new ArrayList<ProbabilityMatrix.Element>();
                    currentCol = matrix.getRows().get(beforeID);
                    for(int k = beforeID+1; k < currentCol.size(); k++)  //check the right side of beforeID
                    {
                            double pvalue = currentCol.get(k).getValue();
                            if (pvalue < CL)
                            {
                                CL=pvalue; //get the lowest Confidence level of the domain
                                lowestBeforeID = beforeID;
          			lowestAfterID = k;
                            }

                    }
                    currentCol=matrix.getRows().get(afterID);
                    for(int k = afterID - 1; k >= 0 ; k--) //check the left side of afterID
                    {
                            double pvalue = currentCol.get(k).getValue();
                            if (pvalue < CL)
                            {
                                CL=pvalue; //get the lowest Confidence level of the domain
                                lowestBeforeID = beforeID;
          			 lowestAfterID = k;
                            }

                    }
    	
                }
            }
          System.out.print("the CL is calculated from "+ lowestBeforeID + "and "+ lowestAfterID);
         return CL ;
    }

     //get robustness indicator according to Xiap's paper
    public static ArrayList<Double> getMaxRobustness(ArrayList<Domain> breaks, ArrayList<DataTable> attriData) 
    {
        ArrayList<Double> robustness = new ArrayList<Double>();
        //for each observations
        for(int i=0; i < attriData.size(); i++)
        {
            double mu = attriData.get(i).getEstimate().get(0);
            double sigma = attriData.get(i).getMoe().get(0);
            double maxpro=0;
            for(int j=0; j<breaks.size()+1;j++) //loop all classes
            {
                double pro;
                if(j==0)
                {
                    double breakpoint=breaks.get(0).getIntersectPoint();
                    pro= Phi(breakpoint,mu,sigma);
                }else if(j==breaks.size())
                {
                    double breakpoint=breaks.get(j-1).getIntersectPoint();
                    pro= 1-Phi(breakpoint,mu,sigma);
                }else
                {
                    double breakpoint1=breaks.get(j-1).getIntersectPoint();
                    double breakpoint2=breaks.get(j).getIntersectPoint();
                    pro= Phi(breakpoint2,mu,sigma)-Phi(breakpoint1,mu,sigma);
                }
                if(pro>maxpro) maxpro=pro;
            }
            robustness.add(maxpro);
        }

          return robustness;
    }

     //get robustness indicator similar to Xiap's paper. the probability of the observation belonging to the class it is assigned
    public static ArrayList<Double> getRobustness(ArrayList<Domain> breaks, ArrayList<DataTable> attriData)  
    {
        ArrayList<Double> robustness = new ArrayList<Double>();
        //for each observations
        for(int i=0; i < attriData.size(); i++)
        {
            double mu = attriData.get(i).getEstimate().get(0);
            double sigma = attriData.get(i).getMoe().get(0);
            double pro=0;
            for(int j=0; j<breaks.size()+1;j++) //loop all classes
            {

                if(j==0)
                {
                    double breakpoint=breaks.get(0).getIntersectPoint();
                    if(breakpoint>mu) 
                    {pro= Phi(breakpoint,mu,sigma);
                     break;
                    }
                }else if(j==breaks.size())
                {
                    double breakpoint=breaks.get(j-1).getIntersectPoint();
                    if(breakpoint<=mu)
                    {pro= 1-Phi(breakpoint,mu,sigma);
                     break;
                    }
                }else
                {
                    double breakpoint1=breaks.get(j-1).getIntersectPoint();
                    double breakpoint2=breaks.get(j).getIntersectPoint();
                    if(breakpoint1<=mu && breakpoint2>mu)
                    {pro= Phi(breakpoint2,mu,sigma)-Phi(breakpoint1,mu,sigma);
                     break;
                    }
                }
                
            }
            robustness.add(pro);
        }

          return robustness;
    }


 // return phi(x) = standard Gaussian pdf
    private static double phi(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }

    // return phi(x, mu, signma) = Gaussian pdf with mean mu and stddev sigma
    private static double phi(double x, double mu, double sigma) {
        return phi((x - mu) / sigma) / sigma;
    }

    // return Phi(z) = standard Gaussian cdf using Taylor approximation
    private static double Phi(double z) {
        if (z < -8.0) return 0.0;
        if (z >  8.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * phi(z);
    }

    // return Phi(z, mu, sigma) = Gaussian cdf with mean mu and stddev sigma
    private static double Phi(double z, double mu, double sigma) {
        return Phi((z - mu) / sigma);
    } 

    // Compute z such that Phi(z) = y via bisection search  //one-tail: 0.975 ->1.96
    public static double PhiInverse(double y) {
        return PhiInverse(y, .00000001, -8, 8);
    } 

    // bisection search
    private static double PhiInverse(double y, double delta, double lo, double hi) {
        double mid = lo + (hi - lo) / 2;
        if (hi - lo < delta) return mid;
        if (Phi(mid) > y) return PhiInverse(y, delta, lo, mid);
        else              return PhiInverse(y, delta, mid, hi);
    }
    public static double zscoreInverse(double zscore, double mu, double sigma)
    {
    	double estimate = zscore*(sigma/1.645) + mu;
    	return estimate;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
