
public class findIntersection {

	public findIntersection() {
		
		// TODO Auto-generated constructor stub
	}

	// return phi(x) = standard Gaussian pdf
    public static double phi(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }

    // return phi(x, mu, signma) = Gaussian pdf with mean mu and stddev sigma
    public static double phi(double x, double mu, double sigma) {
        return phi((x - mu) / sigma) / sigma;
    }

    // return Phi(z) = standard Gaussian cdf using Taylor approximation
    public static double Phi(double z) {
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
    public static double Phi(double z, double mu, double sigma) {
        return Phi((z - mu) / sigma);
    } 

    // Compute z such that Phi(z) = y via bisection search
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
    private static double getMiddle(double x1, double x2)
    {
    	return (x1+x2)/2;
    }
    public static double getThreshold()
    {
    	double thres;
    	return ((21162+26449)/2)/1000000;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double mu1= 21162.0;
		double mu2= 26449.0;
		double sigma1 = 825/1.645;
		double sigma2 = 1247/1.645;
		
		double x1=21162;
		double x2=26449;
		
		double threshold = getThreshold();
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
			System.out.println(middleC);
		}
		
		
		
		
		/*double dif=1000;
		double x1=21162-10;
		double x2=26449+10;
		while (dif >0.000000001 && x1<x2)
		{
			x1=x1+10;
			x2=x2-10;
			double result1=phi(x1,mu1,sigma1);
			double result2=phi(x2,mu2,sigma2);
			dif= Math.abs(result1-result2);
		}
		System.out.println("x1 is:"+x1);
		System.out.println("x2 is:"+x2);*/
		}
	

}
