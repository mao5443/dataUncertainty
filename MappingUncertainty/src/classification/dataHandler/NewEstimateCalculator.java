/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;

/**
 *
 * @author cisc
 */
public class NewEstimateCalculator {

    public static double[] NewErrorCountData(ArrayList<Double> oldEstimate, ArrayList<Double> oldError)
    {
        double[] newData = new double[]{0,0};
        int oldDataSize = oldEstimate.size();
        for(int i=0; i<oldDataSize; i++)
        {
            newData[0]=newData[0]+oldEstimate.get(i);
            newData[1]=newData[1]+Math.pow(oldError.get(i), 2);
        }
        newData[1]=Math.sqrt(newData[1]);
        return newData;
    }
    
     public static double[] NewErrorRatioData(ArrayList<SimpleFeature> feats)
    {
        //mark is "male" or "female"
        ArrayList<Double> oldEstimate = new ArrayList<Double>();
        ArrayList<Double> oldError = new ArrayList<Double>();
        for(int i=0; i<feats.size();i++)
        {
            oldEstimate.add(Double.valueOf(feats.get(i).getAttribute("maleEst").toString()));
            oldError.add(Double.valueOf(feats.get(i).getAttribute("maleMoe").toString()));
        }
         
        double[] newCountGender = new double[]{0,0};
        newCountGender = NewErrorCountData(oldEstimate, oldError);
        
        oldEstimate.clear(); oldError.clear();
        for(int i=0; i<feats.size();i++)
        {
            oldEstimate.add(Double.valueOf(feats.get(i).getAttribute("femaleEst").toString()));
            oldError.add(Double.valueOf(feats.get(i).getAttribute("femaleMoe").toString()));
        }
         
        double[] newCountTotal = new double[]{0,0};
        newCountTotal = NewErrorCountData(oldEstimate, oldError);
        
        double[] newProportion = new double[]{0,0};
        newProportion[0] = newCountGender[0]/newCountTotal[0];
        newProportion[1] = Math.sqrt(Math.abs(Math.pow(newCountGender[1], 2)+Math.pow(newProportion[0], 2)*Math.pow(newCountTotal[1], 2)))/newCountTotal[0];
       
        return newProportion;
    }
     
      public static double[] NewErrorProportionData(ArrayList<SimpleFeature> feats)
    {
       
        ArrayList<Double> oldEstimate = new ArrayList<Double>();
        ArrayList<Double> oldError = new ArrayList<Double>();
        for(int i=0; i<feats.size();i++)
        {
            oldEstimate.add(Double.valueOf(feats.get(i).getAttribute("povertyEst").toString()));
            oldError.add(Double.valueOf(feats.get(i).getAttribute("povertyMoe").toString()));
        }
         
        double[] newCountGender = new double[]{0,0};
        newCountGender = NewErrorCountData(oldEstimate, oldError);
        
        oldEstimate.clear(); oldError.clear();
        for(int i=0; i<feats.size();i++)
        {
            oldEstimate.add(Double.valueOf(feats.get(i).getAttribute("totalPopEs").toString()));
            oldError.add(Double.valueOf(feats.get(i).getAttribute("totalPopMo").toString()));
        }
         
        double[] newCountTotal = new double[]{0,0};
        newCountTotal = NewErrorCountData(oldEstimate, oldError);
        
        double[] newProportion = new double[]{0,0};
        newProportion[0] = newCountGender[0]/newCountTotal[0];
        
        double temp = Math.pow(newCountGender[1], 2)-Math.pow(newProportion[0], 2)*Math.pow(newCountTotal[1], 2);
        if(temp<0)
                temp = Math.pow(newCountGender[1], 2)+Math.pow(newProportion[0], 2)*Math.pow(newCountTotal[1], 2);
        newProportion[1] = Math.sqrt(Math.abs(temp))/newCountTotal[0];

        return newProportion;
    }

    public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//        ArrayList<Double> est = new ArrayList<Double>();
//        ArrayList<Double> error = new ArrayList<Double>();
//        est.add(26727.0); error.add(1028.0);
//        est.add(57339.0);error.add(974.0);
//        est.add(116063.0);error.add(1247.0);
//        double[] newdata = NewErrorCountData(est,error);
//        System.out.println(newdata[0]+","+newdata[1]);
        
        //shapefile reader
        File file = new File("J:\\projects\\MappingUncertainty\\data\\test.shp");
        ShapefileDataStore shpDataStore = new ShapefileDataStore(file.toURI().toURL());
       String[] names = shpDataStore.getTypeNames();
       String typeName = "";
        for(int i=0; i<names.length;i++)
       {
            typeName = typeName + names[i];
       }
        SimpleFeatureSource featureSource = shpDataStore.getFeatureSource(typeName);
        SimpleFeatureCollection collection = featureSource.getFeatures();    
        FeatureIterator<SimpleFeature> results = collection.features();
        ArrayList<SimpleFeature> feats = new ArrayList<SimpleFeature>();
        try {
            while (results.hasNext()) {
                feats.add((SimpleFeature) results.next());
            }
        } finally {
            results.close();
        }
        double[] newdata = NewErrorProportionData(feats);
        //

	}

}
