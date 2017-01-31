/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

import java.util.ArrayList;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.function.RangedClassifier;
import org.geotools.filter.function.Classifier;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;

/**
 *
 * @author cisc
 */
public class Classification {
         public Classification()
	{

	}

//         public Classification initClassifier()
//         {
//             Comparable min[] = new Comparable[25];
//            Comparable max[] = new Comparable[25];
//            for (int i = 0; i < 25; i++) {
//             min[i] = (char) ('A' + i);
//             max[i] = (char) ('B' + i);
//            }
//             RangedClassifier newgroup = new RangedClassifier(min, max);
//
//             return newgroup;
//         }

         public ArrayList<Domain> JenksNaturalBreaks(SimpleFeatureCollection collection, int classNumber, String fieldName, ArrayList<DataTable> data, ProbabilityMatrix matrix)
         {
             ArrayList<Domain> breakpoints = new ArrayList<Domain>();

              FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
              Function classify = ff.function("Jenks", ff.property(fieldName), ff.literal(classNumber));
              RangedClassifier groups = (RangedClassifier) classify.evaluate(collection);

              for (int i = 0; i < classNumber-1; i++)
              {
                  Domain breakpoint = new Domain();
                  Double value = (Double)groups.getMax(i)+1;
                  breakpoint.setIntersectPoint(value);
                 for (int j = 0; j < data.size()-1; j++)
                  {
                    if((data.get(j).getEstimate().get(0) < value) && (data.get(j+1).getEstimate().get(0)>= value))
                    {
                        breakpoint.setLeftID(j);
                        breakpoint.setRightID(j+1);
                        double cl = ProCalculator.getBreakpointCL(j, j+1, matrix);
                        breakpoint.setCL(cl);
                    }

                  }
                  breakpoints.add(breakpoint);
              }

             return breakpoints;
         }

          public ArrayList<Domain> Quantile(SimpleFeatureCollection collection, int classNumber, String fieldName, ArrayList<DataTable> data, ProbabilityMatrix matrix)
         {
             ArrayList<Domain> breakpoints = new ArrayList<Domain>();

              FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
              Function classify = ff.function("Quantile", ff.property(fieldName), ff.literal(classNumber));

              RangedClassifier groups = (RangedClassifier) classify.evaluate(collection);

              for (int i = 1; i < classNumber; i++)
              {
                  Domain breakpoint = new Domain();
                  Double value = (Double)groups.getMin(i)-1;
                  breakpoint.setIntersectPoint(value);
                 for (int j = 0; j < data.size()-1; j++)
                  {
                    if((data.get(j).getEstimate().get(0) < value) && (data.get(j+1).getEstimate().get(0)>= value))
                    {
                        breakpoint.setLeftID(j);
                        breakpoint.setRightID(j+1);
                        double cl = ProCalculator.getBreakpointCL(j, j+1, matrix);
                        breakpoint.setCL(cl);
                    }

                  }
                  breakpoints.add(breakpoint);
              }

             return breakpoints;
         }

           public ArrayList<Domain> EqualInterval(SimpleFeatureCollection collection, int classNumber, String fieldName, ArrayList<DataTable> data, ProbabilityMatrix matrix)
         {
             ArrayList<Domain> breakpoints = new ArrayList<Domain>();

              FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
              Function classify = ff.function("EqualInterval", ff.property(fieldName), ff.literal(classNumber));
              RangedClassifier groups = (RangedClassifier) classify.evaluate(collection);

              for (int i = 0; i < classNumber-1; i++)
              {
                  Domain breakpoint = new Domain();
                  Double value = (Double)groups.getMax(i)+1;
                  breakpoint.setIntersectPoint(value);
                 for (int j = 0; j < data.size()-1; j++)
                  {
                    if((data.get(j).getEstimate().get(0) < value) && (data.get(j+1).getEstimate().get(0)>= value))
                    {
                        breakpoint.setLeftID(j);
                        breakpoint.setRightID(j+1);
                        double cl = ProCalculator.getBreakpointCL(j, j+1, matrix);
                        breakpoint.setCL(cl);
                    }

                  }
                  breakpoints.add(breakpoint);
              }

             return breakpoints;
         }

           public ArrayList<Domain> StandardDeviation(SimpleFeatureCollection collection, int classNumber, String fieldName, ArrayList<DataTable> data, ProbabilityMatrix matrix)
         {
             ArrayList<Domain> breakpoints = new ArrayList<Domain>();

              FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
              Function classify = ff.function("StandardDeviation", ff.property(fieldName), ff.literal(classNumber));
              RangedClassifier groups = (RangedClassifier) classify.evaluate(collection);

              for (int i = 0; i < classNumber-1; i++)
              {
                  Domain breakpoint = new Domain();
                  Double value = (Double)groups.getMin(i)-1;
                  breakpoint.setIntersectPoint(value);
                 for (int j = 0; j < data.size()-1; j++)
                  {
                    if((data.get(j).getEstimate().get(0) < value) && (data.get(j+1).getEstimate().get(0)>= value))
                    {
                        breakpoint.setLeftID(j);
                        breakpoint.setRightID(j+1);
                        double cl = ProCalculator.getBreakpointCL(j, j+1, matrix);
                        breakpoint.setCL(cl);
                    }

                  }
                  breakpoints.add(breakpoint);
              }

             return breakpoints;
         }

          public static Classifier breakPointToClassifier(ArrayList<Domain> breakpoints, double minvalue, double maxvalue, String method, int missvaluenumber)
          {
              ArrayList<Domain> points = breakpoints;

              int len = points.size();

              if(missvaluenumber>0)  //when the file has missing values, add one more class to put missing values
              {
                 Comparable min[] = new Comparable[len+2];
                 Comparable max[] = new Comparable[len+2];
                 min[0]=-9999.0;
                 max[0]=minvalue;
                 min[1] = minvalue;
                  for (int i = 0; i < len; i++) {
                      min[i+2] = points.get(i).getIntersectPoint();
                      max[i+1] = points.get(i).getIntersectPoint();
                    }
                  max[len+1] =  maxvalue;

                  Classifier groups = new RangedClassifier(min, max);
                return groups;

              }else
              {
                  Comparable min[] = new Comparable[len+1];
                Comparable max[] = new Comparable[len+1];
                  min[0] = minvalue;
                  for (int i = 0; i < len; i++) {
                      min[i+1] = points.get(i).getIntersectPoint();
                      max[i] = points.get(i).getIntersectPoint();
                    }
                  max[len] =  maxvalue;
                  Classifier groups = new RangedClassifier(min, max);
                return groups;
              }
                
          }

          public static Classifier BreakvaluesToClassifier(double[] points, double minvalue, double maxvalue)
          {

//              if( method.equals("Statistically Isolated"))
//              {
//                points = DataReader.sortSmalltoLargeAsPointValue(breakpoints);
//              }
              int len = points.length;
              Comparable min[] = new Comparable[len+1];
              Comparable max[] = new Comparable[len+1];

              min[0] = minvalue;
              for (int i = 0; i < len; i++) {
                  min[i+1] = points[i];
                  max[i] = points[i];
                }
              max[len] =  maxvalue;
                Classifier groups = new RangedClassifier(min, max);
                return groups;
          }
}
