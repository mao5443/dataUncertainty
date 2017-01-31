/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

import java.util.ArrayList;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;

/**
 *
 * @author cisc
 */
public class PublicVariableForAggregation {

              //variables for aggregation operations
    public ArrayList<String> layername = new ArrayList<String>();
    public ArrayList<FeatureSource> source_aggregation = new ArrayList<FeatureSource>();
    public ArrayList<FeatureCollection> collection_aggregation = new ArrayList<FeatureCollection>();
    public ArrayList<SimpleFeatureSource> source_auxi= new ArrayList<SimpleFeatureSource>();
    public ArrayList<SimpleFeatureCollection> collection_auxi = new ArrayList<SimpleFeatureCollection>();

    public ArrayList<String> selectedVar_aggregation = new ArrayList<String>();
    public ArrayList<String> selectedError_aggregation= new ArrayList<String>();
    public String selectedVar_aggregationType;
    public ArrayList<String> selectedVar_auxi= new ArrayList<String>();
    public ArrayList<String> selectedError_auxi = new ArrayList<String>();
    public String selectedVar_auxiType;

}
