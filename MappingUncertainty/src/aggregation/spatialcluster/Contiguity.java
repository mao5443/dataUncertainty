package aggregation.spatialcluster;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


import com.vividsolutions.jts.geom.Geometry;


public class Contiguity {

	/**
	 * @param args
	 */
	public Contiguity()
	{
		
	}
	
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
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = shpDataStore.getFeatureSource(typeName);
            
            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures();
			FeatureIterator<SimpleFeature> iterator = collection.features();
			Geometry previousgeo = null;
			while(iterator.hasNext())
			{
				SimpleFeature feature = iterator.next();

				Geometry geometry = (Geometry) feature.getDefaultGeometry();
                                System.out.println(geometry.getArea());
				if(previousgeo!=null)
				{
					System.out.println(geometry.disjoint(previousgeo));  //judege if two polygon disjoint or not
				}
				previousgeo = geometry;
					
			}
            
            
            

//            SimpleFeatureType sft = featureSource.getSchema();
//           
//            List<AttributeDescriptor> attriDes = sft.getAttributeDescriptors();
//            int attriCount = sft.getAttributeCount();
//
//            for(int i =0; i < attriCount; i++)
//            {
//                AttributeDescriptor attribute = attriDes.get(i);
//                String attriName = attribute.getName().toString();
//                AttributeType attriType = attribute.getType();
//                String type = attriType.getBinding().getName();
//                this.attributeNames.add(attriName);
//                this.attributeTypes.add(type);
//            }



//            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
//            Function classify = ff.function("Jenks", ff.property("B19301_1_E"), ff.literal(5));
//            RangedClassifier groups = (RangedClassifier) classify.evaluate(collection);      
    }
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Contiguity gp = new Contiguity();
		gp.ShpReader("I:\\projects\\MappingUncertainty_BarryMethod\\data\\NJ0608_joined.shp");
		
	}

}
