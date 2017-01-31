/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

import java.util.ArrayList;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author cisc
 */
public class AggregateCandidate {
        public int orginalid =0;
        public ArrayList<Integer> id = new ArrayList<Integer>();
        public ArrayList<SimpleFeature> feature = new ArrayList<SimpleFeature>();
        public double[] newEstimate = new double[2];
        public double[] newError = new double[2];
        public double[] newCV = new double[2];
        public double compactness;
        public double[] themeproximity = new double[2];
        public double[] bias = new double[2];
        //public double popuequality;
        public double spatialhierarchy;  //the percentage of areal unit locating insider upper administrative boundary
}
