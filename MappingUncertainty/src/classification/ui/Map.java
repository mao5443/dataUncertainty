/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Map.java
 *
 * Created on Nov 8, 2011, 12:10:27 AM
 */

package classification.ui;

import classification.mapping.Map2D;
import classification.mapping.MapLegend;
import classification.mapping.Symbology;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;


import org.opengis.filter.FilterFactory;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.InfoAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;



import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;

import org.geotools.styling.StyleFactory;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.opengis.feature.simple.SimpleFeature;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import org.opengis.filter.identity.FeatureId;


/**
 *
 * @author min
 */
public class Map extends javax.swing.JInternalFrame 
{
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    static FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);


    private Root parentFrame;
    private JMapPane mapPane;
    private JButton zoomInBtn;
    private JButton zoomOutBtn;
    private JButton panBtn;
    private JButton selectFeatureBtn;
    private JButton clearSelectFeatureBtn;
    private JButton infoBtn;


   

    public Map2D flatmap;

    private static final Color LINE_COLOUR = Color.BLUE;
    private static final Color FILL_COLOUR = Color.CYAN;
    private static final Color SELECTED_COLOUR = Color.YELLOW;
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 10.0f;
    public boolean firstTimeSelection = true;


    /** Creates new form Map */
    public Map(Root root) {
        this.parentFrame = root;
        initComponents();

        mapPane = new JMapPane();
        jPanel1.add(mapPane);
        jPanel1.getParent().validate();
        jPanel1.repaint();

        //ButtonGroup cursorToolGrp = new ButtonGroup();
        zoomInBtn = new JButton(new ZoomInAction(mapPane));
//        zoomInBtn.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                new ZoomInAction(mapPane);
//                System.out.println("~~~~~~~~~~~~~~~~");
//        }
//        });


        jToolBar1.add(zoomInBtn);
       // cursorToolGrp.add(zoomInBtn);
       // selectBtn = bew JButton(new )
        zoomOutBtn = new JButton(new ZoomOutAction(mapPane));
        jToolBar1.add(zoomOutBtn);
        //cursorToolGrp.add(zoomOutBtn);
        panBtn = new JButton(new PanAction(mapPane));
        jToolBar1.add(panBtn);

        selectFeatureBtn = new JButton("Select");
        jToolBar1.add(selectFeatureBtn);
        clearSelectFeatureBtn = new JButton("Clear");
        jToolBar1.add(clearSelectFeatureBtn);

        infoBtn = new JButton(new InfoAction(mapPane));
        jToolBar1.add(infoBtn);
//        infoBtn = new JButton("Info");
//        jToolBar1.add(infoBtn);

        jToolBar1.getParent().validate();
        jToolBar1.repaint();


        selectFeatureBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                mapPane.setCursorTool(
                        new CursorTool() {

                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                                selectFeatures(ev);
                            }
                        });
            }
        });

        clearSelectFeatureBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                clearSelectFeatures(e);
            }
        });

//        infoBtn.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                mapPane.setCursorTool(
//                        new CursorTool() {
//
//                            @Override
//                            public void onMouseClicked(MapMouseEvent ev) {
//                                getFeatureInfo(ev);
//                            }
//                        });
//            }
//        });

    }

    void clearSelectFeatures(ActionEvent e)
    {

        Set<FeatureId> IDs = new HashSet<FeatureId>();
        FeatureId id = new FeatureIdImpl("-1");
        IDs.add(id);
        displaySelectedFeatures(IDs);
        int sortedID = -1;
        parentFrame.barchart.changeRender(sortedID);
    }


//    void getFeatureInfo(MapMouseEvent ev)
//    {
//
//    }
//
//    private void displayFeatureInfo(int id)
//    {
//
//    }
    /**
     * This method is called by our feature selection tool when
     * the user has clicked on the map.
     *
     * @param pos map (world) coordinates of the mouse cursor
     */
    void selectFeatures(MapMouseEvent ev) {

        String geometryAttributeName = parentFrame.source.getSchema().getGeometryDescriptor().getLocalName();

        System.out.println("Mouse click at: " + ev.getMapPosition());

        /*
         * Construct a 5x5 pixel rectangle centred on the mouse click position
         */
        Point screenPos = ev.getPoint();
        Rectangle screenRect = new Rectangle(screenPos.x-2, screenPos.y-2, 5, 5);

        /*
         * Transform the screen rectangle into bounding box in the coordinate
         * reference system of our map context. Note: we are using a naive method
         * here but GeoTools also offers other, more accurate methods.
         */
        AffineTransform screenToWorld = mapPane.getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox = new ReferencedEnvelope(
                worldRect,
                this.flatmap.map.getCoordinateReferenceSystem());
       System.out.println(bbox.centre());

        /*
         * Create a Filter to select features that intersect with
         * the bounding box
         */
       // FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        Filter filter = filterFactory.intersects(filterFactory.property(geometryAttributeName), filterFactory.literal(bbox));

        /*
         * Use the filter to identify the selected features
         */
        try {
            SimpleFeatureSource featureSource = (SimpleFeatureSource) parentFrame.source;

            SimpleFeatureCollection selectedFeatures =
                    featureSource.getFeatures(filter);

            SimpleFeatureIterator iter = selectedFeatures.features();
            Set<FeatureId> IDs = new HashSet<FeatureId>();
            try {
                while (iter.hasNext()) {
                    SimpleFeature feature = iter.next();
                    IDs.add(feature.getIdentifier());

                    System.out.println("   " + feature.getIdentifier());
                }
      
            } finally {
                iter.close();
            }

            if (IDs.isEmpty()) {
                System.out.println("   no feature selected");
            }

            displaySelectedFeatures(IDs);

            //-----get selected feature's id in attriData
            Iterator it=IDs.iterator();
            FeatureId FID;
            String tempFID;
            String[] ids;
            int id = 0;
            while(it.hasNext())
             {
                 FID = (FeatureId)it.next();
                 tempFID = FID.getID();
                 ids = tempFID.split("\\.");
                 id = Integer.parseInt(ids[ids.length-1]);
              
            }
            int sortedID = 0;
            for(int i= 0; i<parentFrame.attriData.size();i++)
            {
                if(parentFrame.attriData.get(i).getGID() == id)
                {
                    sortedID = i+1;
                }
            }
            //----------------------
            parentFrame.barchart.changeRender(sortedID);  //data linked to the barchart

            } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    public void displaySelectedFeatures(Set<FeatureId> IDs) {
        MapContext map = this.flatmap.rereshMap(IDs, parentFrame.geometryType, parentFrame.source);
        mapPane.setMapContent(map);
        mapPane.repaint();
    }

     



    public Map() {
        initComponents();
    }

    public JPanel getMapPanel()
    {
        return jPanel3;
    }

    public void displayLegend(MapLegend legend)
    {

        legend.setSize(jPanel2.getSize());
        jPanel2.removeAll();
        jPanel2.add(legend);
        jPanel2.setBackground(Color.white);
        jPanel2.getParent().validate();
        jPanel2.repaint();
    }

    public JPanel getjPanel2()
    {
        return jPanel2;
    }

    public void displayShapefile(MapContext map) 
    {
        //this.currentMap = map;

        
       /* mapPane = new JMapPane();
        zoomInBtn.setAction(new ZoomInAction(mapPane));
        zoomOutBtn.setAction(new ZoomOutAction(mapPane));
        panBtn.setAction(new PanAction(mapPane));
        infoBtn.setAction(new InfoAction(mapPane));

        mapPane.setRenderer( new StreamingRenderer() );*/
        mapPane.setMapContent(map);
        mapPane.setSize(jPanel1.getSize());
        mapPane.repaint();


        jPanel1.removeAll();
        jPanel1.add(mapPane);
        jPanel1.getParent().validate();
        jPanel1.repaint();

        //JMapFrame.showMap(map);

//        File file = JFileDataStoreChooser.showOpenFile("shp", null);
//        if (file == null) {
//            return;
//        }
//
//       FileDataStore store = FileDataStoreFinder.getDataStore(file);
//       FeatureSource featureSource = store.getFeatureSource();
//        FeatureCollection featureCollection =  featureSource.getFeatures();
//        //FeatureSource featureSource = parentFrame.source;
//       // FeatureCollection featureCollection = parentFrame.collection;
//
//        // Create a map context and add our shapefile to it
//        MapContext map = new DefaultMapContext();
//        map.setTitle("StyleLab");
//
//        // STEP 0 Set up Color Brewer
//        ColorBrewer brewer = ColorBrewer.instance();
//
//    // STEP 1 - call a classifier function to summarise your content
//        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
//        //String typename = parentFrame.selectedVariable.getSelectedVariable();
//        String typename = "B19301_1_E";
//        PropertyName propteryExpression = ff.property(typename);
//
//        String method = "Quantile";
//        int classnum = 5;
//        Function classify = ff.function(method, propteryExpression, ff.literal(classnum));
//        Classifier groups = (Classifier) classify.evaluate(featureCollection);
//
//    // STEP 2 - look up a predefined palette from color brewer
//            String paletteName = "GrBu";
//            Color[] colors = brewer.getPalette("YlGn").getColors(classnum);
//
//    // STEP 3 - ask StyleGenerator to make a set of rules for the Classifier
//    // assigning features the correct color based on height
//            FeatureTypeStyle fts = StyleGenerator.createFeatureTypeStyle(
//            groups,
//            propteryExpression,
//            colors,
//            "Generated FeatureTypeStyle for GreeBlue",
//            featureCollection.getSchema().getGeometryDescriptor(),
//            StyleGenerator.ELSEMODE_IGNORE,
//            0.95,
//            null);
//
//        // Create a basic Style to render the features
//        Style style = createStyle2(fts,featureSource);
//
//         map.addLayer(featureSource, (Style) style);
            
    }

    public static void main(String[] args) throws Exception {
        //Map me = new Map();
        //me.displayShapefile();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setClosable(true);
        setResizable(true);
        setTitle("Map");

        jToolBar1.setRollover(true);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 223, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 517, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 635, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 579, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(861, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getAccessibleContext().setAccessibleParent(this);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}
