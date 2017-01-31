/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aggregation.spatialcluster;

import classification.dataHandler.ProCalculator;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.MathException;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author min
 */
public class CriterionCalculators {
    FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;
    FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
    public void ShpReader(String filepath) throws IOException
    {
        
        File file = new File(filepath);

        
           //DataStore dataStore = DataStoreFinder.getDataStore(map);
            ShapefileDataStore shpDataStore = new ShapefileDataStore(file.toURI().toURL());
           String[] names = shpDataStore.getTypeNames();
           String typeName = "";
            for(int i=0; i<names.length;i++)
           {
                typeName = typeName + names[i];
           }
            featureSource = shpDataStore.getFeatureSource(typeName);
            
            collection = featureSource.getFeatures();

   }

    //get the compactness
    public static double getCompactness(Geometry geometry ){
            
            double area = geometry.getArea();
            double perimeter = geometry.getLength();
            double idealRadius = perimeter/(2*Math.PI);
            double idealArea = Math.PI*Math.pow(idealRadius, 2);
            double compactness = Math.abs(area/idealArea -1);
           // compactness = area/perimeter;

            return compactness;
    }

    public static double getIntersectionArea(Geometry aggregatedGeometry, Geometry upper_level_geometry){
        //large intersection area represents the overlap between upper level polygon and
        //lower level polygon (candidates) is better
        double aggregatedArea = aggregatedGeometry.getArea();
       Geometry intersection = upper_level_geometry.intersection(aggregatedGeometry);
        double intersected_area = intersection.getArea();
        //the percentage of the part in aggregated area, which falls into upper level polygon
        return intersected_area/aggregatedArea*100;
    }

    //get contiguity
    public static boolean ifPairContiguity(SimpleFeature feature1, SimpleFeature feature2)
    {
        boolean contiguity=false;
        Geometry geometry1 = (Geometry) feature1.getDefaultGeometry();
        Geometry geometry2 = (Geometry) feature2.getDefaultGeometry();
        contiguity = geometry1.disjoint(geometry2);  //this is one-point touch joint
        return contiguity;
    }

    public boolean ifGroupContiguity(ArrayList<SimpleFeature> features, SimpleFeature feature)
    {
        boolean contiguity = true;
        ArrayList<SimpleFeature> ring = new ArrayList<SimpleFeature>();
        ArrayList<SimpleFeature> rest = new ArrayList<SimpleFeature>();
        ring.add(feature);
        rest = features;
        ArrayList<SimpleFeature> ring1 = new ArrayList<SimpleFeature>();
        ArrayList<SimpleFeature> rest1 = new ArrayList<SimpleFeature>();

        while(contiguity && rest.size()>0)
        {
            ring1.clear();
            rest1.clear();
            for(int i=0; i <rest.size(); i++)
            {
               for(int j=0; j< ring.size(); j++)
               {
                   if(ifPairContiguity(ring.get(j), rest.get(i)))
                   {
                       ring1.add(rest.get(i));
                       break;
                   }else
                       rest1.add(rest.get(i));
               }
            }

            if(ring1.size()==0)
                   contiguity=false;
            rest=rest1;
            ring=ring1;
        }
 
        return contiguity;
    }

    public static ArrayList<SimpleFeature> getAdjecentPolygons(ArrayList<SimpleFeature> centralPolygons, FeatureCollection collection)
    {
        ArrayList<SimpleFeature> ring = new ArrayList<SimpleFeature>();
        SimpleFeatureIterator iter = (SimpleFeatureIterator) collection.features();
        while(iter.hasNext()){
            SimpleFeature feature=iter.next();
            for(int i=0; i<centralPolygons.size();i++)
            {
                if(!centralPolygons.get(i).getID().equals(feature.getID()))
                {
                    if(!ifPairContiguity(feature,centralPolygons.get(i)))
                    {
                        ring.add(feature);
                        //System.out.println(feature.getID()+" is adjecent to "+ centralPolygons.get(i).getID());
                    }
                }
            }
        }
        return ring;
    }

    public static ArrayList<SimpleFeature> getAdjecentPolygons(SimpleFeature centralPolygon, FeatureCollection collection)
    {
        ArrayList<SimpleFeature> ring = new ArrayList<SimpleFeature>();
        SimpleFeatureIterator iter = (SimpleFeatureIterator) collection.features();
        while(iter.hasNext()){
            SimpleFeature feature=iter.next();
            if(!centralPolygon.getID().equals(feature.getID()))
            {
                if(!ifPairContiguity(feature,centralPolygon))
                {
                    ring.add(feature);
                    //System.out.println(feature.getID()+" is adjecent to "+ centralPolygon.getID());
                }
            }

        }
        return ring;
    }

    //get attribute proximity, for nomial and ordinal attribute
    public boolean getSimilaity()
    {
        boolean ifsame = false;

        return ifsame;
    }


    public double getAttrProximity(double[] attributes, double[] moes )
    {
        //attributes[0] is the value for the original feature
        //the final proximity is the averager of the prbability that two attributes value are not significantly different
        //the larger the better
        double averagepro=0;
        for(int i=1; i<attributes.length; i++)
        {
            try {
                averagepro = averagepro + ProCalculator.getProbability(attributes[i], moes[i], attributes[0], moes[0]);

            } catch (MathException ex) {
                Logger.getLogger(CriterionCalculators.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        averagepro= averagepro/(attributes.length-1);
        return averagepro=0;
    }
    
    public static double getBias(double[] ests, double newEst)
    {
        double bias =0;
        double estSum = 0;
        for(int i=0; i<ests.length; i++)
        {
            bias += Math.pow(newEst-ests[i],2)*ests[i];
            estSum += ests[i];
        }
        bias = Math.sqrt(bias)/estSum;
        
        return bias;
    }

}
