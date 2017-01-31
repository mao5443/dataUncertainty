/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

import java.util.ArrayList;

/**
 *
 * @author cisc
 */
public class InnerSD {

    public static double sdKnuth ( ArrayList<Double> data )
    {

//    final int n = data.size();
//      if ( n < 2 )
//         {
//         return Double.NaN;
//         }
//      double avg = data.get(0);
//      double sum = 0;
//      for ( int i = 1; i < data.size(); i++ )
//         {
//         double newavg = avg + ( data.get(i) - avg ) / ( i + 1 );
//         sum += ( data.get(i) - avg ) * ( data.get(i) -newavg ) ;
//         avg = newavg;
//         }
//      // Change to ( n - 1 ) to n if you have complete data instead of a sample.
//      return Math.sqrt( sum / ( n - 1 ) );
//    }
        int n= data.size();
        if(n>0)
        {
            double sum =0;
            for(int i=0; i<n;i++)
            {
                sum=sum+data.get(i);
            }
            double avg= sum/n;
            sum=0;
            for(int i=0; i<n; i++)
            {
                sum=sum+(data.get(i)-avg)*(data.get(i)-avg);
            }
            return Math.sqrt(sum/n);
        }else
        {
            return Double.NaN;
        }

    }

}
