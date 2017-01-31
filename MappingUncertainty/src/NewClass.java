/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cisc
 */
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.filter.Filter;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FilterFactory;
import org.opengis.feature.simple.SimpleFeature;

public class NewClass {
  public static void main(String[] a) throws MalformedURLException, IOException {

    FilterFactory filterFactory = (FilterFactory) CommonFactoryFinder.getFilterFactory(null);
    Filter filter = filterFactory.equals(filterFactory.property("test"), filterFactory.literal("test1"));


    File file = new File("C:\\Users\\cisc\\Documents\\NJ0608_joined_Project1_upperlevel.shp");
    ShapefileDataStore shpDataStore = new ShapefileDataStore(file.toURI().toURL());
    String[] names = shpDataStore.getTypeNames();
    String typeName = "";
    for(int i=0; i<names.length;i++)
    {
        typeName = typeName + names[i];
    }
    SimpleFeatureSource featureSource = shpDataStore.getFeatureSource(typeName);

    SimpleFeatureCollection features = featureSource.getFeatures(filter);
    SimpleFeatureIterator iter=features.features();
    SimpleFeature f = null;
    while(iter.hasNext()){
        f=iter.next();
    }
    System.out.println(f);
  }
}