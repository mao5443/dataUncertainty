/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.mapping;

import classification.dataHandler.AggregationAttributes;
import java.awt.Color;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.function.Classifier;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;

import classification.dataHandler.Classification;
import classification.dataHandler.Domain;
import classification.dataHandler.Utils;
import java.util.ArrayList;
import java.util.Set;
import org.geotools.filter.function.RangedClassifier;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;

/**
 *
 * @author min
 */
public class Map2D {
     public MapContext map;
     private FeatureCollection featureCollection;
     private FeatureSource featureSource;
     private int classnum;
     private Classifier groups;
     private PropertyName propteryExpression;
     private Rule rule;
     private Color[] colors;
     private Symbology[] symbol;
     //private Symbology[] aggregationSymbol;
     private ArrayList<Domain> breaks;


     public Map2D(String maptitle, FeatureCollection collection,FeatureSource source, int num, 
                    Classifier group, PropertyName proptery, ArrayList<Domain> breakpoints,int missvaluenumber)
     {
        featureCollection = collection;
        featureSource = source;
       
        groups = group;
        propteryExpression = proptery;

        symbol = new Symbology[1];
        symbol[0] = new Symbology();
        breaks = breakpoints;
        classnum = num;
        if(missvaluenumber>0) //when data have missing value, I need to add a gray color to show polygons with missing value
        {
            Color[] tempcolors = symbol[0].brewerColor("YlGn",classnum);
            colors= new Color[tempcolors.length+1];
            colors[0]=new Color(230,230,230);
            for(int i=0; i<tempcolors.length; i++)
            {
                colors[i+1]=tempcolors[i];
            }
            
        }else  //for normal data
        {
            colors = symbol[0].brewerColor("YlGn",classnum);
            
        }
       // colors = symbol.colorScaleBySeparability(classnum, breaks);
         map = new MapContext();
         map.setTitle(maptitle);
       
     }

      public Map2D(String maptitle, FeatureCollection collection,FeatureSource source, int num,
                    Classifier group, PropertyName proptery)
     {
        featureCollection = collection;
        featureSource = source;
        classnum = num;
        groups = group;
        propteryExpression = proptery;

        symbol = new Symbology[1];
        symbol[0] = new Symbology();
        colors = symbol[0].brewerColor("YlGn",classnum);
         map = new MapContext();
         map.setTitle(maptitle);

     }

      /**
      Map2D for the aggregation module
       */
      public Map2D(String maptitle, FeatureCollection collection,FeatureSource source,
              int num,Classifier group, RangedClassifier colorgroups, PropertyName proptery, ArrayList<AggregationAttributes> data)
     {
        symbol = new Symbology[2];
        for(int i=0; i< 2; i++)
            symbol[i] = new Symbology();
         featureCollection = collection;
        featureSource = source;
        classnum = num;
        groups = group;
        propteryExpression = proptery;

        //colors = symbol[0].colorScaleSquential(num, data, "estimate");
        double[] colorgroup_maxval = new double[colorgroups.getSize()];
        double[] colorgroup_minval = new double[colorgroups.getSize()];

        for(int i=0; i<colorgroups.getSize(); i++)
        {
            colorgroup_maxval[i] = (Double)colorgroups.getMax(i);
            colorgroup_minval[i] = (Double)colorgroups.getMin(i);
        }

        colors = symbol[0].colorSchemaWithCV(4,colorgroup_maxval,colorgroup_minval, data);
         map = new MapContext();
         map.setTitle(maptitle);

     }

       public Map2D(String maptitle, FeatureCollection collection,FeatureSource source,
                int num,Classifier group, PropertyName proptery, ArrayList<AggregationAttributes> data)
     {
        symbol = new Symbology[2];
        for(int i=0; i< 2; i++)
            symbol[i] = new Symbology();
         featureCollection = collection;
        featureSource = source;
        classnum = num;
        groups = group;
        propteryExpression = proptery;
        
        colors = symbol[0].colorScaleByEstimate(data);
         map = new MapContext();
         map.setTitle(maptitle);

     }


     public MapContext newMap()
     {
      
         
        
        Style style = symbol[0].setSymbols(groups, propteryExpression, colors, featureCollection, featureSource);
        this.rule = style.featureTypeStyles().get(0).getRules()[0];
        map.dispose();
        map = new MapContext();
        map.addLayer(featureSource, (Style) style);
        //this.rule =symbol.getRule();
        return map;
     }

     public void addLayer(FeatureCollection collection,FeatureSource source, int num, Classifier group, PropertyName proptery, ArrayList<AggregationAttributes> data)
     {
        Color[] colors1 = symbol[1].colorScaleSquential(num, data, "cv");
         Style style = symbol[1].setSymbols(group, proptery, colors1, collection, source);
         map.addLayer(source, (Style) style);
     }
     public void addLayer(FeatureCollection collection,FeatureSource source, int num, Classifier group, PropertyName proptery)
     {
        colors = symbol[0].brewerColor("YlGn",classnum);
         Style style = symbol[1].setSymbols(group, proptery, colors, collection, source);
         map.addLayer(source, (Style) style);
     }

     public void removeLayer(int layer)
     {
         if(map.getLayerCount()> layer)
            map.removeLayer(layer);
     }

     public void setLayerVisibility(int layerindex, boolean visible)
     {
         map.getLayer(layerindex).setVisible(visible);
     }
     public MapContext rereshMap(Set<FeatureId> IDs, String geometryType, FeatureSource source)
     {
        Style style;
       for(int i=0; i<this.map.layers().size(); i++)
       {
            if (IDs.isEmpty())
                style = this.map.getLayer(i).getStyle();
            else
                style = symbol[i].createSelectedSymbol(IDs, geometryType, source);

            Layer layer = this.map.layers().get(i);
            ((FeatureLayer) layer).setStyle(style);
        }
        

         return map;
     }

     public Color[] getColors()
     {
         return this.colors;
     }






}
