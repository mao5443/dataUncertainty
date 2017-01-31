package classification.dataHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import classification.dataHandler.ProbabilityMatrix.Element;
import classification.ui.Root;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.filter.function.Classifier;
import org.geotools.filter.function.RangedClassifier;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;



public class DataReader {
	private String fileName;
        private ArrayList<String> attributeNames;
        private ArrayList<String> attributeTypes;
        private SimpleFeatureCollection collection;
        private SimpleFeatureSource source;

	/**
	 * @param args
	 */
	public DataReader(String FileName)
	{
	this.fileName=FileName;
        this.attributeNames = new ArrayList();
        this.attributeTypes = new ArrayList();
	}



        public void ShpReader() throws IOException
        {
            
            File file = new File(this.fileName);
                ShapefileDataStore shpDataStore = new ShapefileDataStore(file.toURI().toURL());
               String[] names = shpDataStore.getTypeNames();
               String typeName = "";
                for(int i=0; i<names.length;i++)
               {
                    typeName = typeName + names[i];
               }
                SimpleFeatureSource featureSource = shpDataStore.getFeatureSource(typeName);
                this.source = featureSource;
                SimpleFeatureType sft = featureSource.getSchema();
                List<AttributeDescriptor> attriDes = sft.getAttributeDescriptors();
                int attriCount = sft.getAttributeCount();

                for(int i =0; i < attriCount; i++)
                {
                    AttributeDescriptor attribute = attriDes.get(i);
                    String attriName = attribute.getName().toString();
                    AttributeType attriType = attribute.getType();
                    String type = attriType.getBinding().getName();
                    this.attributeNames.add(attriName);
                    this.attributeTypes.add(type);
                }
                collection = featureSource.getFeatures();    
        }

        public static SimpleFeatureSource ShpReaderAuxi(String filePath) throws IOException
        {

            File file = new File(filePath);
                ShapefileDataStore shpDataStore = new ShapefileDataStore(file.toURI().toURL());
               String[] names = shpDataStore.getTypeNames();
               String typeName = "";
                for(int i=0; i<names.length;i++)
               {
                    typeName = typeName + names[i];
               }
                SimpleFeatureSource featureSource = shpDataStore.getFeatureSource(typeName);
                return featureSource;
        }

         /**
     * Retrieve information about the feature geometry
     */
    private enum GeomType { POINT, LINE, POLYGON };

    public String getGeometry() {

        String geometryType;
        GeometryDescriptor geomDesc = this.source.getSchema().getGeometryDescriptor();
        String geometryAttributeName = geomDesc.getLocalName();

        Class<?> clazz = geomDesc.getType().getBinding();

        if (Polygon.class.isAssignableFrom(clazz) ||
                MultiPolygon.class.isAssignableFrom(clazz)) {
            geometryType = "POLYGON";

        } else if (LineString.class.isAssignableFrom(clazz) ||
                MultiLineString.class.isAssignableFrom(clazz)) {

            geometryType = "LINE";

        } else {
            geometryType = "POINT";
        }

        return geometryType;

    }


        public ArrayList<String> getAttributeNames()
        {
            return this.attributeNames;
        }

        public ArrayList<String> getAttributeTypes()
        {
            return this.attributeTypes;
        }

        public SimpleFeatureCollection getCollection()
        {
            return this.collection;
        }

        public FeatureSource getSource()
        {
            return (FeatureSource) this.source;
        }
      

        public static ArrayList<DataTable> transferShpToAttriData(Root root, String est, String error, String estType, String errorType)
        {
            ArrayList<DataTable> records = new ArrayList<DataTable>();
            SimpleFeatureCollection collection=root.collection;
            SimpleFeatureIterator iterator = collection.features();
            try {
                int i = 1;
                while( iterator.hasNext() ){
                    SimpleFeature feature = iterator.next();
                    DataTable record = new DataTable();
                    record.setGID(i);
                    i++;
                    double estimate=0;
                    if( estType == "java.lang.Integer")
                    {
                         estimate= ((Integer)feature.getAttribute(est)).doubleValue();
                        record.addEstimate(estimate);

                    }else if (estType == "java.lang.Long")
                    {
                        estimate = ((Long)feature.getAttribute(est)).doubleValue();
                        record.addEstimate(estimate);
                    }else if (estType == "java.lang.Double")
                    {
                        estimate = ((Double)feature.getAttribute(est)).doubleValue();
                        record.addEstimate(estimate);
                    }else if (estType == "java.lang.Float")
                    {
                        estimate = ((Float)feature.getAttribute(est)).doubleValue();
                        record.addEstimate(estimate);
                    }

                    if(estimate==-9999.0)
                        root.missingValueNumber++;

                    
                   if(errorType == "java.lang.Integer")
                   {
                       double err = ((Integer)feature.getAttribute(error)).doubleValue();
                        record.addMoe(err);
                   }else if (errorType == "java.lang.Long")
                    {
                        double err = ((Long)feature.getAttribute(error)).doubleValue();
                        record.addMoe(err);
                    }else if (errorType == "java.lang.Double")
                    {
                        double err = ((Double)feature.getAttribute(error)).doubleValue();
                        record.addMoe(err);
                    }else if (errorType == "java.lang.Float")
                    {
                        double err = ((Float)feature.getAttribute(error)).doubleValue();
                        record.addMoe(err);
                    }

                    
                    records.add(record);

                }
            }
            finally {
                iterator.close();
            }

            Utils.sortSmalltoLarge(records, 0);

            return records;
        }
	 
	public ArrayList<DataTable> ReadFile()
	{
	
		ArrayList<DataTable> records = new ArrayList<DataTable>();
		
	try {
	//storeValues.clear();//just in case this is the second call of the ReadFile Method./
		BufferedReader br = new BufferedReader( new FileReader(this.fileName));
	 
		StringTokenizer st = null;
		int lineNumber = 0, tokenNumber = 0;
	    String lineContent;
	    
	    ArrayList<String> fieldNames = new ArrayList<String>();
		while( (lineContent = br.readLine()) != null)
		{
			lineNumber++;
			//System.out.println(this.fileName);
			//storeValues.add(this.fileName);
			//break comma separated line using ","
			st = new StringTokenizer(lineContent, ",");
			if(lineNumber == 1 )
			{
				while(st.hasMoreTokens())
				{
					tokenNumber++;
		 
					String cellvalue=st.nextToken();
					if(tokenNumber > 4)
					{
						fieldNames.add(cellvalue);
						st.nextToken();
					}
				}
			}else if (lineNumber > 2)
			{
				DataTable record = new DataTable();
				while(st.hasMoreTokens())
				{
					tokenNumber++;
		 
					String cellvalue=st.nextToken();
					if(tokenNumber == 2)
					{
						record.setGID(Integer.parseInt(cellvalue));
					}else if(tokenNumber == 4)
					{
						record.setUnitName(cellvalue);
					}else if(tokenNumber > 4)
					{
						double estimate = Double.parseDouble(cellvalue);
						record.addEstimate(estimate);
						cellvalue=st.nextToken();
						double moe = Double.parseDouble(cellvalue);
						record.addMoe(moe);
					}
				}
					record.setFieldname(fieldNames);
					records.add(record);
			}
				
 	//reset token number
			tokenNumber = 0;

		}
	} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	 
	 return records;
	}
	

	
	
	public static void WriteToFile (ProbabilityMatrix matrix, String newfilename)
	{
		PrintWriter pw = null;

	    try {

	      pw = new PrintWriter(new FileWriter(newfilename));
	      

	      for (int i = 0; i < matrix.getRows().size(); i++) {
	    	String line="";
	    	ArrayList<Element> currentrow = matrix.getRows().get(i);
	    	for (int j = 0; j < currentrow.size(); j++)
	    	{
	    		line = line + currentrow.get(j).getValue()+ ",";
	    	}
	        pw.println(line);

	      }
	      pw.flush();

	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }
	    finally {
	      
	      //Close the PrintWriter
	      if (pw != null)
	        pw.close();
	      
	    }
		
	}
	 
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DataReader reader = new DataReader("I:\\projects\\MappingUncertainty\\data\\NJ0608_joined.shp");
                reader.ShpReader();
	}

}
