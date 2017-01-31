/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Aggregation.java
 *
 * Created on Feb 4, 2013, 5:47:09 PM
 */

package classification.ui;

import classification.dataHandler.AggregationAttributes;
import classification.dataHandler.Classification;
import classification.dataHandler.DataWriter;
import classification.dataHandler.Utils;
import classification.mapping.Map2D;
import classification.minsplots.ScatterPlotCV;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.Filter;
import org.geotools.filter.function.RangedClassifier;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContext;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.InfoAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;

/**
 *
 * @author cisc
 */
public class AggregationMap extends javax.swing.JInternalFrame {
     private Root parentFrame;
     private int numberOfData =0;
     private JMapPane[] mapPane;
     private Map2D[] maps; //the two maps in the panes, each map has two layers
     private JCheckBox[][] layerVisibilityCheckBox;

    /** Creates new form Aggregation */
    public AggregationMap(Root root) {
        initComponents();
        parentFrame = root;

        mapPane = new JMapPane[2];
        for(int i=0; i<2; i++)
            mapPane[i] = new JMapPane();
        maps = new Map2D[2];

        JButton zoomInBtn;
        JButton zoomOutBtn;
        JButton panBtn;
        JButton infoBtn;
        zoomInBtn = new JButton(new ZoomInAction(mapPane[0]));
        jToolBar1.add(zoomInBtn);
        zoomOutBtn = new JButton(new ZoomOutAction(mapPane[0]));
        jToolBar1.add(zoomOutBtn);
        panBtn = new JButton(new PanAction(mapPane[0]));
        jToolBar1.add(panBtn);
        infoBtn = new JButton(new InfoAction(mapPane[0]));
        jToolBar1.add(infoBtn);
        jToolBar1.getParent().validate();
        jToolBar1.repaint();
        zoomInBtn = new JButton(new ZoomInAction(mapPane[1]));
        jToolBar2.add(zoomInBtn);
        zoomOutBtn = new JButton(new ZoomOutAction(mapPane[1]));
        jToolBar2.add(zoomOutBtn);
        panBtn = new JButton(new PanAction(mapPane[1]));
        jToolBar2.add(panBtn);
        infoBtn = new JButton(new InfoAction(mapPane[1]));
        jToolBar2.add(infoBtn);
        jToolBar2.getParent().validate();
        jToolBar2.repaint();

        
        layerVisibilityCheckBox = new JCheckBox[2][4];
        layerVisibilityCheckBox[0][0] = jCheckBox1;
        layerVisibilityCheckBox[0][1] = jCheckBox2;
        layerVisibilityCheckBox[0][2] = jCheckBox5;
        layerVisibilityCheckBox[0][3] = jCheckBox6;
        layerVisibilityCheckBox[1][0] = jCheckBox3;
        layerVisibilityCheckBox[1][1] = jCheckBox4;
        layerVisibilityCheckBox[1][2] = jCheckBox7;
        layerVisibilityCheckBox[1][3] = jCheckBox8;

    }

    public void getCollectionCV(int index, String layername, String est, String err, String estType, String errorType) throws IOException
    {
        FeatureCollection collection = parentFrame.data_aggregation.collection_aggregation.get(index);
            SimpleFeatureIterator iterator = (SimpleFeatureIterator) collection.features();


            try {
                int i = 0;
                while( iterator.hasNext() ){
                    AggregationAttributes currentattr = new AggregationAttributes();
                    SimpleFeature feature = iterator.next();
                    String tempid=feature.getID();
                   // System.out.println("FID is"+ tempid);
                    tempid = tempid.substring((layername.length()+1));
                    currentattr.FID=Integer.parseInt(tempid);
                    currentattr.fileindex=index;

                    if( estType == "java.lang.Integer")
                    {

                        double estimate = ((Integer)feature.getAttribute(est)).doubleValue();
                        currentattr.oldest= estimate;

                    }else if (estType == "java.lang.Long")
                    {
                        double estimate = ((Long)feature.getAttribute(est)).doubleValue();
                        currentattr.oldest= estimate;
                    }else if (estType == "java.lang.Double")
                    {
                        double estimate = ((Double)feature.getAttribute(est)).doubleValue();
                        currentattr.oldest= estimate;
                    }else if (estType == "java.lang.Float")
                    {
                        double estimate = ((Float)feature.getAttribute(est)).doubleValue();
                        currentattr.oldest= estimate;
                    }

                   if(errorType == "java.lang.Integer")
                   {
                       double error = ((Integer)feature.getAttribute(err)).doubleValue();
                       currentattr.olderror=error;
                   }else if (errorType == "java.lang.Long")
                    {
                        double error = ((Long)feature.getAttribute(err)).doubleValue();
                        currentattr.olderror=error;
                    }else if (errorType == "java.lang.Double")
                    {
                        double error = ((Double)feature.getAttribute(err)).doubleValue();
                        currentattr.olderror=error;
                    }else if (errorType == "java.lang.Float")
                    {
                        double error = ((Float)feature.getAttribute(err)).doubleValue();
                        currentattr.olderror=error;
                    }
                    currentattr.cv = (currentattr.olderror/1.645)/currentattr.oldest*100;

                    parentFrame.attridata.get(index).add(currentattr);
                     i++;
                }
            }
            finally {
                iterator.close();
            }
            
            Utils.sortSmalltoLargeAggregationAttributes(parentFrame.attridata.get(index), "cv");

            //store the cv back to the collection in the featuresource
            DataWriter dataWriter = new DataWriter();

            dataWriter.updateField(parentFrame.data_aggregation.source_aggregation.get(index), parentFrame.attridata.get(index));
            parentFrame.aggregationassistantwin.paintCVscatterplot(parentFrame.attridata);

    }


//    public void generateMap(FeatureCollection collection, FeatureSource source,String fieldName, int classNumber, int mappaneindex)
//    {
//        //generate the map with class colors are determined by both estimate and cv dimension
//        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
//        Function classify = ff.function("Jenks", ff.property(fieldName), ff.literal(classNumber));
//        RangedClassifier groups = (RangedClassifier) classify.evaluate(collection);
//
//        classify = ff.function("Jenks", ff.property(fieldName), ff.literal(4));
//        RangedClassifier colorgroups = (RangedClassifier) classify.evaluate(collection);
//
//        PropertyName propteryExpression = ff.property(fieldName);
//        Map2D map = new Map2D("", collection, source, classNumber,
//                            groups, colorgroups, propteryExpression, parentFrame.attridata.get(mappaneindex));
//        MapContext mapcontext =  map.newMap();
//        maps[mappaneindex] = map;
//        this.displayShapefile(mapcontext, mappaneindex);
//        //layerVisibilityCheckBox[mappaneindex][0].setSelected(true);
//
//    }

    public void generateMap(FeatureCollection collection, FeatureSource source,String fieldName, int classNumber, int mappaneindex)
    {
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        Function classify = ff.function("Jenks", ff.property(fieldName), ff.literal(classNumber));
        RangedClassifier groups = (RangedClassifier) classify.evaluate(collection);

        PropertyName propteryExpression = ff.property(fieldName);
        Map2D map = new Map2D("", collection, source, classNumber,
                            groups, propteryExpression, parentFrame.attridata.get(mappaneindex));
        MapContext mapcontext =  map.newMap();
        maps[mappaneindex] = map;
        this.displayShapefile(mapcontext, mappaneindex);
        layerVisibilityCheckBox[mappaneindex][0].setSelected(true);

    }

    public void addLayerToMap(FeatureCollection collection, FeatureSource source,
                                String fieldName, int classNumber, int mappaneindex,int checkboxIndex)
    {
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        Function classify = ff.function("Jenks", ff.property(fieldName), ff.literal(classNumber));
        RangedClassifier groups = (RangedClassifier) classify.evaluate(collection);
        PropertyName propteryExpression = ff.property(fieldName);
        maps[mappaneindex].addLayer(collection, source, classNumber, groups,
                                    propteryExpression);
        layerVisibilityCheckBox[mappaneindex][checkboxIndex].setSelected(true);
    }

    public void removeAggregatedLayer(int mappaneindex)
    {
        //layer 0 and 1 are layers of original data
        //aggregated data can only be added as layer 2 and 3
        maps[mappaneindex].removeLayer(2);
        maps[mappaneindex].removeLayer(3);
    }

    public void displayShapefile(MapContext map, int index)
    {
        mapPane[index].setMapContent(map);
        if(index==0)
        {
            mapPane[0].setSize(jPanel1.getSize());
            mapPane[0].repaint();

            jPanel1.removeAll();
            jPanel1.add(mapPane[0]);
            jPanel1.getParent().validate();
            jPanel1.repaint();
        }else if(index==1)
        {
            mapPane[1].setSize(jPanel2.getSize());
            mapPane[1].repaint();

            jPanel2.removeAll();
            jPanel2.add(mapPane[1]);
            jPanel2.getParent().validate();
            jPanel2.repaint();
        }

        
    }

    public void displaySelectedFeatures(Set<FeatureId> IDs, int map_pane_index)
    {
        //defaut geometry type is: "com.vividsolutions.jts.geom.MultiPolygon"
        MapContext map = maps[map_pane_index].rereshMap(IDs, "POLYGON", parentFrame.data_aggregation.source_aggregation.get(map_pane_index));
        mapPane[map_pane_index].setMapContent(map);
        mapPane[map_pane_index].repaint();
    }
  

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(820, 520));

        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(810, 420));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 400));
        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentResized(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 166, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 411, Short.MAX_VALUE)
        );

        jToolBar1.setRollover(true);

        jCheckBox1.setText("Estimate");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("Error");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox5.setText("Result: Error");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        jCheckBox6.setText("Result: Estimate");
        jCheckBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE))
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckBox2)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox5)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox6))
                .addGap(19, 19, 19))
        );

        jSplitPane1.setLeftComponent(jPanel3);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(800, 400));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 581, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 412, Short.MAX_VALUE)
        );

        jToolBar2.setRollover(true);

        jCheckBox3.setText("Estimate");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jCheckBox4.setText("Error");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });

        jCheckBox7.setText("Result: Error");
        jCheckBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox7ActionPerformed(evt);
            }
        });

        jCheckBox8.setText("Result: Estimate");
        jCheckBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox4)
                            .addComponent(jCheckBox3))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCheckBox8, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox7, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox8))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleParent(this);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentResized
        // TODO add your handling code here:
        mapPane[0].setSize(jPanel1.getSize());
//        ReferencedEnvelope currentArea = mapPane.getDisplayArea();
//        mapPane.setDisplayArea(currentArea);
//        mapPane.repaint();
        // http://www.opensourcejavaphp.net/java/geotools/org/geotools/swing/JMapPane.java.html
    }//GEN-LAST:event_jPanel1ComponentResized

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox1.isSelected())
            maps[0].setLayerVisibility(0, true);
        else
            maps[0].setLayerVisibility(0, false);
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox2.isSelected())
            maps[0].setLayerVisibility(1, true);
        else
            maps[0].setLayerVisibility(1, false);
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox3.isSelected())
            maps[1].setLayerVisibility(0, true);
        else
            maps[1].setLayerVisibility(0, false);
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox4.isSelected())
            maps[1].setLayerVisibility(1, true);
        else
            maps[1].setLayerVisibility(1, false);
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox5.isSelected())
            maps[0].setLayerVisibility(3, true);
        else
            maps[0].setLayerVisibility(3, false);
    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void jCheckBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox6ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox6.isSelected())
            maps[0].setLayerVisibility(2, true);
        else
            maps[0].setLayerVisibility(2, false);
    }//GEN-LAST:event_jCheckBox6ActionPerformed

    private void jCheckBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox7ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox7.isSelected())
            maps[1].setLayerVisibility(3, true);
        else
            maps[1].setLayerVisibility(3, false);
    }//GEN-LAST:event_jCheckBox7ActionPerformed

    private void jCheckBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox8ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox8.isSelected())
            maps[1].setLayerVisibility(2, true);
        else
            maps[1].setLayerVisibility(2, false);
    }//GEN-LAST:event_jCheckBox8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables

}
