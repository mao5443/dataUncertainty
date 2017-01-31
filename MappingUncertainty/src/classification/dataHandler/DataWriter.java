/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;

/**
 *
 * @author cisc
 */
public class DataWriter {

    public void updateField(FeatureSource source, ArrayList<AggregationAttributes> data ) throws IOException
    {
        String modifiedField = "cv";
        SimpleFeatureSource source1 = (SimpleFeatureSource) source;
          SimpleFeatureStore store = (SimpleFeatureStore) source1;
             Transaction transaction = new DefaultTransaction("Example1");
            //SimpleFeatureStore store = (SimpleFeatureStore) featStore;
            store.setTransaction( transaction );

            FilterFactory ff = CommonFactoryFinder.getFilterFactory( GeoTools.getDefaultHints() );


            FilterFactory filterFactory = (FilterFactory) CommonFactoryFinder.getFilterFactory(null);
            //String filename= source.getName().toString().split(":")[2];
            String filename= source.getName().toString();





            for(int i=0; i<data.size();i++)
            {
                
                String currentid = Integer.toString(data.get(i).FID);
                String GID2 = filename+"."+currentid;
                Set<FeatureId> IDs = new HashSet<FeatureId>();
                FeatureId id = new FeatureIdImpl(GID2);
                IDs.add(id);
                System.out.println("updating "+ id);
                Filter filter = filterFactory.id(IDs);

                //Filter filter = ff.id( Collections.singleton( ff.featureId("fred")));

                //SimpleFeatureType featureType = store.getSchema();
                   store.modifyFeatures( modifiedField, new Double(data.get(i).cv), filter );
                   transaction.commit();
            }
    }

}
