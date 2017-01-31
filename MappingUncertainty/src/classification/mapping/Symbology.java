/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.mapping;

import classification.dataHandler.AggregationAttributes;
import classification.dataHandler.Utils;
import java.awt.Color;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;
import java.util.ArrayList;
import java.util.Set;
import org.geotools.brewer.color.ColorBrewer;
import org.geotools.brewer.color.StyleGenerator;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.function.Classifier;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
/**
 *
 * @author min
 */
public class Symbology {
    
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
    private static final Color SELECTED_COLOUR = Color.CYAN;
    private static final float OPACITY = 0.0f;
    private static final float LINE_WIDTH = 5.0f;
    private static final float POINT_SIZE = 10.0f;

    private FeatureTypeStyle fts;
    private int symbol_rule_number = 0;

    public Symbology()
    {
        
    }
    
    public static Color[] brewerColor(String name, int classnum)
    {
        // STEP 0 Set up Color Brewer
        ColorBrewer brewer = ColorBrewer.instance();
    // STEP 2 - look up a predefined palette from color brewer
        //YlGn
        Color[] colors = brewer.getPalette(name).getColors(classnum);

        return colors;
    }

    public static Color[] colorScaleBySeparability(int classnumber, ArrayList<Double> reliabilities)
    {
       
        Color[] colors = new Color[classnumber];
        double RGBfromLAB[] = new double[3];
        double sumSeparability =0.0;

        //total separability/confidence level
        for(int i=0; i<classnumber-1;i++)
        {
            sumSeparability = sumSeparability+ reliabilities.get(i);
        }

        RGBfromLAB[0] = 100;
        RGBfromLAB[1] = 35;
        RGBfromLAB[2] = 54;
        RGBfromLAB = ColorModels.CIELabtoRGB(RGBfromLAB);
        colors[0] = new Color((int)RGBfromLAB[0],(int)RGBfromLAB[1], (int)RGBfromLAB[2]);
        //colors[0]=Color.getHSBColor(0.45f, colorscale, 0.55f);
        for(int i=0; i<classnumber-1;i++)
        {
            double colorscale = 0.0;
            for(int j=0; j<=i;j++)
            {
                colorscale = colorscale+ reliabilities.get(j);
            }
            colorscale = colorscale/sumSeparability;
            RGBfromLAB[0] = (1-colorscale)*100;
            RGBfromLAB[1] = 35;
            RGBfromLAB[2] = 54;
            RGBfromLAB = ColorModels.CIELabtoRGB(RGBfromLAB);
            colors[i+1] = new Color((int)RGBfromLAB[0],(int)RGBfromLAB[1], (int)RGBfromLAB[2]);
           // colors[i]=Color.getHSBColor(0.45f, colorscale, 0.55f);
        }

        return colors;
    }

    public static Color[] colorScaleByEstimate(ArrayList<AggregationAttributes> data)
    {
        ArrayList<AggregationAttributes> data_copy = (ArrayList<AggregationAttributes>) data.clone();
        Utils.sortSmalltoLargeAggregationAttributes(data_copy, "estimate");
        Color[] colors = new Color[data.size()];
        double min = data_copy.get(0).oldest;
        double max = data_copy.get(data.size()-1).oldest;

        for(int i =0; i<data_copy.size(); i++)
        {
            int color = (int)((data_copy.get(i).oldest-min)/(max-min)*255);
            colors[i] = new Color(color,color,color);
        }
        return colors;
    }

    public static Color[] colorSchemaWithCV(int classnumber, double[] estimate_maxs,
                                            double[] estimate_mins, ArrayList<AggregationAttributes> data)
    {
        ArrayList<AggregationAttributes> data_copy = (ArrayList<AggregationAttributes>) data.clone();
        Utils.sortSmalltoLargeAggregationAttributes(data_copy, "estimate");
        double cv_min = data.get(0).cv;
        double cv_max = data.get(data.size()-1).cv;
        
        int[][] colors = new int[][]{{94,61,152},
                                           {136,127,158},
                                           {255,163,105},
                                           {255,101,0}};
        float[][] HSVcolors = new float[4][];
        for(int i=0; i<colors.length; i++)
        {
             float[] hsb = Color.RGBtoHSB(colors[i][0], colors[i][1],
                                                    colors[i][2], null);
             HSVcolors[i] = new float[3];
             HSVcolors[i] = hsb;
        }

        Color[] rendering_colors = new Color[data.size()];

        int elem_iterator=0;
        for(int i=0; i<estimate_maxs.length; i++)
        {
                while(elem_iterator<data.size() && data_copy.get(elem_iterator).oldest <= estimate_maxs[i])
                {
                    float h = HSVcolors[i][0];
                    float s = HSVcolors[i][1];
                    float v = HSVcolors[i][2];
                    double adjustedV = (data_copy.get(elem_iterator).cv - cv_min)/(cv_max-cv_min)*(v-0.1)+0.1;
//                    int rgb = Color.HSBtoRGB(h, s, (float)adjustedV);
//                    int red = (rgb>>16)&0xFF;
//                    int green = (rgb>>8)&0xFF;
//                    int blue = rgb&0xFF;
                    Color c = Color.getHSBColor(h, s, (float)adjustedV);
                    rendering_colors[elem_iterator] = c;
                    elem_iterator++;
                }
        }
        return rendering_colors;
    }

    public static Color[] colorScaleSquential(int classnumber, ArrayList<AggregationAttributes> data, String field) //color for the map without classification
    {
        //classnumber == data.size()

        ArrayList<AggregationAttributes> data1 = (ArrayList<AggregationAttributes>) data.clone();
        Utils.sortSmalltoLargeAggregationAttributes(data1, field); //"estimate" or "cv"
        double minestimate = data1.get(0).oldest; double maxestimate = data1.get(data1.size()-1).oldest;
        Color[] colors = new Color[classnumber];
        double RGBfromLAB[] = new double[3];
        for(int i=0; i<classnumber;i++)
        {
            double colorscale = 100-(data1.get(i).oldest-minestimate)/(maxestimate-minestimate)*100;

            RGBfromLAB[0] = colorscale;
            if(field.equals("estimate"))
            {
                RGBfromLAB[1] = 35;
                RGBfromLAB[2] = 54;
            }else if(field.equals(("cv")))
            {
                RGBfromLAB[1] = 50;
                RGBfromLAB[2] = 50;
            }
            RGBfromLAB = ColorModels.CIELabtoRGB(RGBfromLAB);
            colors[i] = new Color((int)RGBfromLAB[0],(int)RGBfromLAB[1], (int)RGBfromLAB[2]);
        }


        return colors;
    }


    public  Style setSymbols(Classifier groups, PropertyName propteryExpression, Color[] colors, FeatureCollection featureCollection, FeatureSource featureSource)
    {
        // boundary color of polygons
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.GRAY),
                filterFactory.literal(0.1),  //boundary width
                filterFactory.literal(0.2)); //boundary opacity

        fts = StyleGenerator.createFeatureTypeStyle(
            groups,
            propteryExpression,
            colors,  //polygon fill color
            "Generated FeatureTypeStyle for GreeBlue",
            featureCollection.getSchema().getGeometryDescriptor(),
            StyleGenerator.ELSEMODE_IGNORE,
            0.9,  //polygon fill color opacity
            stroke);
        //record the number of rules originally created when setting up the layer
        symbol_rule_number = fts.rules().size();

        // Create a basic Style to render the features
        Style style =  styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

     /**
     * Create a Style where features with given IDs are painted
     * yellow, while others are painted with the default colors.
     */
    public Style createSelectedSymbol(Set<FeatureId> IDs,String geometryType,FeatureSource featureSource) {
        String geometryAttributeName = featureSource.getSchema().getGeometryDescriptor().getLocalName();
        Rule selectedRule = createRule(SELECTED_COLOUR, SELECTED_COLOUR, geometryType, geometryAttributeName);
        selectedRule.setFilter(filterFactory.id(IDs));

        if(fts.rules().size() == symbol_rule_number)
        {
            // if first time selection
            fts.rules().add(selectedRule);
        }else
        {
            fts.rules().remove(fts.rules().size()-1);
            fts.rules().add(selectedRule);
        }

        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    private Rule createRule(Color outlineColor, Color fillColor, String geometryType, String geometryAttributeName) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = styleFactory.createStroke(filterFactory.literal(outlineColor), filterFactory.literal(LINE_WIDTH));

        if(geometryType == "POLYGON")
        {
           
            fill = styleFactory.createFill(filterFactory.literal(fillColor), filterFactory.literal(OPACITY));
                symbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
        }else if(geometryType == "LINE")
        {
                symbolizer = styleFactory.createLineSymbolizer(stroke, geometryAttributeName);
        }else if(geometryType == "POINT")
        {
                fill = styleFactory.createFill(filterFactory.literal(fillColor), filterFactory.literal(OPACITY));

                Mark mark = styleFactory.getCircleMark();
                mark.setFill(fill);
                mark.setStroke(stroke);

                Graphic graphic = styleFactory.createDefaultGraphic();
                graphic.graphicalSymbols().clear();
                graphic.graphicalSymbols().add(mark);
                graphic.setSize(filterFactory.literal(POINT_SIZE));

                symbolizer = styleFactory.createPointSymbolizer(graphic, geometryAttributeName);
        }

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

}
