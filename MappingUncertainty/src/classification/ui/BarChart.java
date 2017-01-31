/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BarChart.java
 *
 * Created on Oct 25, 2011, 4:22:00 PM
 */

package classification.ui;

import classification.dataHandler.Classification;
import classification.dataHandler.DataReader;
import classification.dataHandler.DataTable;
import classification.dataHandler.Domain;
import classification.dataHandler.InnerSD;
import classification.dataHandler.PreCalculateSepSd;
import classification.dataHandler.ProCalculator;
import classification.dataHandler.ProbabilityMatrix;
import classification.dataHandler.Utils;
import classification.mapping.Map2D;
import classification.mapping.MapLegend;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import javax.swing.JSlider;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.math.MathException;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.function.Classifier;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.map.MapContext;
import org.geotools.styling.Graphic;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.TextSymbolizer2;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.style.GraphicLegend;
import org.opengis.style.GraphicalSymbol;


/**
 *
 * @author cisc
 */
public class BarChart extends javax.swing.JInternalFrame {
    private Root parentFrame;
    private ArrayList<Domain> breakpoint = new ArrayList<Domain>();
    private ArrayList<Double> SDs = new ArrayList<Double>();

    private double intersectThreshold;
    private double CLthreshold;

    private String selectedClassificationMethod = "Class Separability";
    private int selectedClassNumber = 0;
    private String classificationTool = "slider";

    //Set up animation parameters.
    static final int FPS_MIN = 0;
    static final int FPS_MAX = 100;
    static final int FPS_INIT = 95;    //initial frames per second
    private int oBreakPoint = 0; ///0-normal; 1-add; 2-delete

    DemoPanel demopanel;
    
    /** Creates new form BarChart */
    public BarChart(Root root) {
        initComponents();
        parentFrame = root;
        

        //jSlider1 = new JSlider(JSlider.HORIZONTAL,FPS_MIN, FPS_MAX, FPS_INIT);
        jSlider1.setMinimum(FPS_MIN);
        jSlider1.setMaximum(FPS_MAX);
        jSlider1.setValue(FPS_INIT);
        jSlider1.setMajorTickSpacing(10);
        jSlider1.setMinorTickSpacing(1);
        jSlider1.setPaintTicks(true);



       // jSlider1.addChangeListener(this);

        //Create the label table.
        Hashtable<Integer, JLabel> labelTable =
            new Hashtable<Integer, JLabel>();
        //PENDING: could use images, but we don't have any good ones.
        labelTable.put(new Integer( 0 ),
                       new JLabel("0%") );
                     //new JLabel(createImageIcon("images/stop.gif")) );
        for(int i =1; i<10;i++)
        {
        labelTable.put(new Integer( (FPS_MAX/10)*i ),
                       new JLabel(i+"0%") );

        }
                     //new JLabel(createImageIcon("images/slow.gif")) );
        labelTable.put(new Integer( FPS_MAX ),
                       new JLabel("100%") );
                     //new JLabel(createImageIcon("images/fast.gif")) );
        jSlider1.setLabelTable(labelTable);

        jSlider1.setUI(new MySliderUI(jSlider1,1));
        jSlider1.setPaintLabels(true);

       //set typein disabled-------------
        jTextField1.setEnabled(false);
        jButton1.setEnabled(false);
        jComboBox1.setEnabled(false);
        //-------------------------------
    }

    
    class DemoPanel extends JPanel implements ChartMouseListener
    {

        private MyBarRenderer renderer;

        public void chartMouseMoved(ChartMouseEvent chartmouseevent)
        {
               //System.out.println("move");

        }

        public void chartMouseClicked(ChartMouseEvent chartmouseevent)
        {
                // System.out.println("mouseover");

                /*
                if (!(chartentity instanceof CategoryItemEntity))
                {
                        renderer.setHighlightedItem(-1, -1);
                        return;
                } else
                {
                        CategoryItemEntity categoryitementity = (CategoryItemEntity)chartentity;
                        CategoryDataset categorydataset = categoryitementity.getDataset();
                        renderer.setHighlightedItem(categorydataset.getRowIndex(categoryitementity.getRowKey()), categorydataset.getColumnIndex(categoryitementity.getColumnKey()));

                        int sortedId = categorydataset.getColumnIndex(categoryitementity.getColumnKey());

                        String GID;
                        GID = "";
                        for(int i= 0; i<parentFrame.attriData.size();i++)
                        {
                            if( i == sortedId-1)
                            {
                                GID = Integer.toString(parentFrame.attriData.get(i).getGID());

                            }
                        }

                        String GID2 = parentFrame.filename.replace("shp", "")+GID;
                        Set<FeatureId> IDs = new HashSet<FeatureId>();
                        FeatureId id = new FeatureIdImpl(GID2);
                        IDs.add(id);

                         parentFrame.map.displaySelectedFeatures(IDs);

                        return;
                }
                */
        }

        public DemoPanel(MyBarRenderer mybarrenderer)
        {
                super(new BorderLayout());
                renderer = mybarrenderer;
        }

        public MyBarRenderer getRenderer()
        {
            return this.renderer;
        }

        public void setRenderer(int row, int col)
        {
            renderer.setHighlightedItem(row, col);

        }
    }
    
    
    public void changeRender(int GID)
    {
        this.demopanel.setRenderer(0, GID-1);
        return;

    }

    static class MyBarRenderer extends StatisticalBarRenderer
    {

        private int highlightRow;
        private int highlightColumn;

        public void setHighlightedItem(int i, int j)
        {
                if (highlightRow == i && highlightColumn == j)
                {
                        return;
                } else
                {
                        highlightRow = i;
                        highlightColumn = j;

                        //record the highlighted item
                       // parentFrame.selectedVariable.sethighlightedItemIndex(highlightColumn);
                       // System.out.println(parentFrame.selectedVariable.gethighlightedItemIndex());

                        notifyListeners(new RendererChangeEvent(this));
                        return;
                }
        }

        public Paint getItemOutlinePaint(int i, int j)
        {
           // System.out.println("row is: "+i+" column is: "+j);
            if (i == highlightRow && j == highlightColumn)
                        return Color.magenta;
                else
                        return super.getItemOutlinePaint(i, j);
        }

        MyBarRenderer()
        {
                highlightRow = -1;
                highlightColumn = -1;
        }
    }

 

    /* 
    private ChartPanel createNewChartPanel ()
    {
        final StatisticalCategoryDataset dataset = createDataset(0);

        final CategoryAxis xAxis = new CategoryAxis(null);
        xAxis.setLowerMargin(0.01d); // percentage of space before first bar
        xAxis.setUpperMargin(0.01d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.4d); // percentage of space between categories
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        //xAxis.setLabelAngle(1.57);
       // xAxis.setTickLabelsVisible(false);
        final ValueAxis yAxis = new NumberAxis(null);

        // define the plot
       final CategoryItemRenderer renderer = new StatisticalBarRenderer();


       // renderer.setSeriesPaint(0, new Paint[]{})

       // int n = this.breakpoint.size();
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer, this.breakpoint);
        //add for display break line
        plot.setBackgroundPaint(new Color(202,202,202));
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);
        //plot.setDomainGridlinePosition(CategoryAnchor.END);


        final JFreeChart chart = new JFreeChart(null,
                                          new Font("Helvetica", Font.BOLD, 14),
                                          plot,
                                          false);

        //set bar paint
        BarRenderer barrenderer = (BarRenderer) renderer;
        barrenderer.setDrawBarOutline(false);
        //renderer.setSeriesPaint(0, new Color(211,211,211));
        renderer.setSeriesPaint(0, Color.white);
        renderer.setSeriesOutlinePaint(0, Color.GRAY);


        //chart.setBackgroundPaint(Color.white);
        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new java.awt.Dimension(837, 488));

        return chartPanel;
    }
    */

    private JPanel createNewChartPanel ()
    {
        final StatisticalCategoryDataset dataset = createDataset(0);

        int recordnum = dataset.getColumnCount();

        final CategoryAxis xAxis = new CategoryAxis(null);
        xAxis.setLowerMargin(0.001d); // percentage of space before first bar
        xAxis.setUpperMargin(0.001d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.45d); // percentage of space between categories
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarksVisible(false);

        //xAxis.setLabelAngle(1.57);
       // xAxis.setTickLabelsVisible(false);
        final ValueAxis yAxis = new NumberAxis(null);

        // define the plot
     // final CategoryItemRenderer renderer = new StatisticalBarRenderer();

        MyBarRenderer renderer = new MyBarRenderer();
       // renderer.setSeriesPaint(0, new Paint[]{})

       // int n = this.breakpoint.size();

        final CategoryPlot plot;
        if(recordnum > 80)
            plot = new CategoryPlot(dataset, xAxis, yAxis, renderer, this.breakpoint, 2);
        else
            plot = new CategoryPlot(dataset, xAxis, yAxis, renderer, this.breakpoint, 1);

        //System.out.println(this.getWidth());
        //System.out.println(plot.getWeight());
        //add for display break line
        plot.setBackgroundPaint(new Color(202,202,202));
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        //plot.setDomainGridlinePosition(CategoryAnchor.END);

        final JFreeChart chart = new JFreeChart(null,
                                          new Font("Helvetica", Font.PLAIN, 12),
                                          plot,
                                          false);

        renderer.setSeriesPaint(0, Color.white);
        renderer.setSeriesOutlinePaint(0, Color.white);
        /****only set for the data with more than 80 records???***********/
        if(recordnum>80)
            renderer.setMaximumBarWidth(0.003);

        /**********************/
        // renderer.setItemMargin(2);

        //set bar paint
        //BarRenderer barrenderer = (BarRenderer) renderer;
        // MyBarRenderer barrenderer =  (MyBarRenderer) renderer;
        //barrenderer.setDrawBarOutline(false);
        renderer.setDrawBarOutline(true);
        //renderer.setMaximumBarWidth(0.005);
        
        final ChartPanel chartPanel = new ChartPanel(chart){
            
            private static final long serialVersionUID = -4857405671081534981L;
            private Point2D zoomPoint = null;
            private Rectangle2D zoomRectangle = null;
            private boolean fillZoomRectangle = true;
            private JPopupMenu popup;
            private boolean pressed = false;
            private boolean dragged = false;

            private Paint zoomOutlinePaint = Color.blue;
            private Paint zoomFillPaint = new Color(0, 0, 255, 63);

            public void mousePressed(MouseEvent e) {
                
                int px = e.getX();
                int py = e.getY();
                Rectangle2D screenDataArea = getScreenDataArea();
                double count = plot.getDataset().getColumnCount(); 
                double intervalSpace = screenDataArea.getWidth() / (count - 1);
                int pt = (int)((px - screenDataArea.getX()) / intervalSpace);
                int leftID = pt;
                int rightID = pt + 1;
                
                if(pt == 0)
                {
                    leftID = 0;
                    rightID = 1;
                }
                
                if(oBreakPoint == 1)
                {
                    Domain newOne = new Domain();
                    newOne.setLeftID(leftID);
                    newOne.setRightID(rightID);

                    newOne.setIntersectPoint((parentFrame.attriData.get(leftID).getEstimate().get(0)+parentFrame.attriData.get(rightID).getEstimate().get(0))/2);
                    double cl = ProCalculator.getBreakpointCL(pt, rightID, parentFrame.matrix);
                    newOne.setCL(cl);
                    boolean bExited = false;
                    for(int i = 0; i < breakpoint.size(); i++)
                    {
                        Domain current = breakpoint.get(i);
                        if(current.getLeftID() == newOne.getLeftID() && current.getRightID() == newOne.getRightID())
                        {
                            bExited = true;
                            break;
                        }
                    }
                    if(!bExited)
                        breakpoint.add(newOne);
                    else
                        System.out.println("the breakpoint is exited");
                    
                    updateInfoAfterAdjustBreaks();
                    //oBreakPoint = 0;
                    pressed = false;
                    dragged = false;
                    this.getParent().repaint();
                    return;
                }
                
                if(oBreakPoint == 2)
                {
                    Domain newOne = new Domain();
                    newOne.setLeftID(leftID);
                    newOne.setRightID(rightID);
                    for(int i = 0; i < breakpoint.size(); i++)
                    {
                        Domain current = breakpoint.get(i);
                        if(current.getLeftID() == newOne.getLeftID() && current.getRightID() == newOne.getRightID())
                        {
                            breakpoint.remove(i);
                            updateInfoAfterAdjustBreaks();
                            break;
                        }
                    }
                    
                    pressed = false;
                    dragged = false;
                    this.getParent().repaint();
                    return;
                }
                
                if(pressed && dragged)
                {
                    if(oBreakPoint == 0)
                    {
                        for(int i = 0; i < breakpoint.size(); i++)
                        {
                            Domain current = breakpoint.get(i);
                            if(current.GetSelectionStation())
                            {
                                current.setLeftID(leftID);
                                current.setRightID(rightID);
                                //reset CL
                                current.setIntersectPoint((parentFrame.attriData.get(leftID).getEstimate().get(0)+parentFrame.attriData.get(rightID).getEstimate().get(0))/2);
                                double cl = ProCalculator.getBreakpointCL(pt, rightID, parentFrame.matrix);
                                current.setCL(cl);
                                if(sepSdCalculator != null)
                                {
                                    sepSdCalculator.updateSepSdPairs(breakpoint, breakpoint.size()+1);
                                    parentFrame.sepSdClassnum = sepSdCalculator.getSepSdClassnumPair();
                                    parentFrame.statplot.addPlotPanel(); //draw the paris relationship
                                }

                                current.SetSelectionStatus(false);
                            }
                        }

                        //update map synchronously ---------------------------------------------
                        if(breakpoint.size() < 9)
                        {
                            breakpoint = Utils.sortSmalltoLargeAsPointValue(breakpoint);
                            jLabel3.setText("");
                            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
                            PropertyName propteryExpression = ff.property(parentFrame.selectedVariable.getSelectedVariable());
                            double min = parentFrame.attriData.get(0+parentFrame.missingValueNumber).getEstimate().get(0);
                            int datarecordnum = parentFrame.attriData.size();
                            double max = parentFrame.attriData.get(datarecordnum-1).getEstimate().get(0);
                            Classifier groups = Classification.breakPointToClassifier(breakpoint, min, max, "Class Separability", parentFrame.missingValueNumber);
                            Map2D map = new Map2D("demo", parentFrame.collection,parentFrame.source, breakpoint.size()+1,
                                    groups, propteryExpression, breakpoint, parentFrame.missingValueNumber);
                            parentFrame.map.flatmap = map;
                            MapContext mapcontext =  parentFrame.map.flatmap.newMap();
                            // Rule rule=map.getRule();
                            //  PolygonSymbolizer sym = (PolygonSymbolizer) rule.getSymbolizers()[0];
                            double[] cl = new double[breakpoint.size()];
                            double[] mins = new double[breakpoint.size()+1];
                            double[] maxs = new double[breakpoint.size()+1];


                            mins[0] = min;

                            for (int i = 0; i < breakpoint.size(); i++)
                            {
                                cl[i] = breakpoint.get(i).getCL();
                                mins[i+1] = breakpoint.get(i).getIntersectPoint();
                                maxs[i] =breakpoint.get(i).getIntersectPoint()-1;
                            }
                            maxs[breakpoint.size()] = max;
                            Color[] colors= map.getColors();

                            MapLegend legendWithCL = new MapLegend(colors, breakpoint.size()+1, cl, mins, maxs, parentFrame.map.getjPanel2().getSize(),parentFrame.missingValueNumber);
                            parentFrame.map.displayLegend(legendWithCL);
                            parentFrame.map.displayShapefile(mapcontext);
                        }
                        else
                        {
                            jLabel3.setText("Too many classes for map!");
                        }
                        
                    }
                    
                    
                    
                    pressed = false;
                    dragged = false;
                    this.getParent().repaint();
                }              
                else if(!pressed)
                {
                    ///selecting
                    for(int i = 0; i < breakpoint.size(); i++)
                    {
                        Domain current = breakpoint.get(i);
                        if(pt >= current.getLeftID() && pt < current.getRightID())
                        {
                            current.SetSelectionStatus(true);
                            pressed = true;
                        }
                    }
                    this.getParent().repaint();
                    return;
                }
                
            }
            private void updateInfoAfterAdjustBreaks()
            {
                //update map ans star plot synchronously ---------------------------------------------
                
                /*if(sepSdCalculator != null)
                {
                    sepSdCalculator.updateSepSdPairs(breakpoint, breakpoint.size()+1);
                    parentFrame.sepSdClassnum = sepSdCalculator.getSepSdClassnumPair();
                    parentFrame.statplot.addPlotPanel(); //draw the paris relationship
                }*/
                //don't make changes on the star plot when add or remove break points


                if(breakpoint.size() < 9)
                {
                    breakpoint = Utils.sortSmalltoLargeAsPointValue(breakpoint);
                    jLabel3.setText("");
                    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
                    PropertyName propteryExpression = ff.property(parentFrame.selectedVariable.getSelectedVariable());
                    double min = parentFrame.attriData.get(0+parentFrame.missingValueNumber).getEstimate().get(0);
                    int datarecordnum = parentFrame.attriData.size();
                    double max = parentFrame.attriData.get(datarecordnum-1).getEstimate().get(0);
                    Classifier groups = Classification.breakPointToClassifier(breakpoint, min, max, "Class Separability", parentFrame.missingValueNumber);
                    Map2D map = new Map2D("demo", parentFrame.collection,parentFrame.source, breakpoint.size()+1,
                            groups, propteryExpression, breakpoint, parentFrame.missingValueNumber);
                    parentFrame.map.flatmap = map;
                    MapContext mapcontext =  parentFrame.map.flatmap.newMap();
                    // Rule rule=map.getRule();
                    //  PolygonSymbolizer sym = (PolygonSymbolizer) rule.getSymbolizers()[0];
                    double[] cl = new double[breakpoint.size()];
                    double[] mins = new double[breakpoint.size()+1];
                    double[] maxs = new double[breakpoint.size()+1];


                    mins[0] = min;

                    for (int i = 0; i < breakpoint.size(); i++)
                    {
                        cl[i] = breakpoint.get(i).getCL();
                        mins[i+1] = breakpoint.get(i).getIntersectPoint();
                        maxs[i] =breakpoint.get(i).getIntersectPoint()-1;
                    }
                    maxs[breakpoint.size()] = max;
                    Color[] colors= map.getColors();

                    MapLegend legendWithCL = new MapLegend(colors, breakpoint.size()+1, cl, mins, maxs, parentFrame.map.getjPanel2().getSize(),parentFrame.missingValueNumber);
                    parentFrame.map.displayLegend(legendWithCL);
                    parentFrame.map.displayShapefile(mapcontext);
                }
                else
                {
                    jLabel3.setText("Too many classes for map!");
                }
                //////////////////////////////////////////////////////////////
            }

            public void mouseReleased(MouseEvent e) {

                /*
                if (e.isPopupTrigger()) {
                if(popup == null) {
                popup = createPopupMenu(true,true,true,true);
                }
                if (this.popup != null) {
                displayPopupMenu(e.getX(), e.getY());
                zoomRectangle = null;
                return;
                }
                }
                if(this.getChart().getCategoryPlot().getDataset().getColumnCount() < 2) {
                repaint();
                zoomRectangle = null;
                return;
                }
                if (zoomRectangle == null) {
                // do nothing
                } else {
                // do something here. zoom rectangle with data
                CategoryDataset dataset = this.getChart().getCategoryPlot().getDataset();
                Comparable[] rowKeys = new Comparable[dataset.getRowCount()];
                rowKeys[0] = dataset.getRowKey(0);
                // rowKeys[1] = dataset.getRowKey(1);
                Comparable[] columnKeys = new Comparable[dataset.getColumnCount()];
                for(int i=0; i<columnKeys.length; i++ ) {
                // remove something here
                DefaultStatisticalCategoryDataset defaultDataset = (DefaultStatisticalCategoryDataset)dataset;
                //defaultDataset.remove(rowKeys[0], columnKeys[i]);
                System.out.print("remove the one with value of "+defaultDataset.getValue(0, i));
                defaultDataset.removeColumn(i);
                
                //defaultDataset.removeValue(rowKeys[1], columnKeys[i]);
                }
                zoomRectangle = null;
                }  
                */
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                
                if(pressed)
                {   
                    dragged = true;
                    int px = e.getX();
                    int py = e.getY();
                    Rectangle2D screenDataArea = getScreenDataArea();
                    Graphics2D g2 = (Graphics2D) getGraphics();
                    g2.drawLine(px, (int)screenDataArea.getY(), px, (int)screenDataArea.getHeight());

                    this.getParent().repaint();
                }
                //g2.draw(screenDataArea);
                
                // if no initial zoom point was set, ignore dragging...
                /*
                if (this.zoomPoint == null) {
                return;
                }

                Graphics2D g2 = (Graphics2D) getGraphics();
                Rectangle2D scaledDataArea = getScreenDataArea((int) this.zoomPoint.getX(), (int) this.zoomPoint.getY());
                double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
                double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
                this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), this.zoomPoint.getY(),
                xmax - this.zoomPoint.getX(), ymax - this.zoomPoint.getY());
                repaint();
                g2.dispose();
                */
            }

            public Paint getBreakPointPaint(double cl){
                Paint paint = Color.blue;
                if(cl<0.2)
                {
                    paint = new Color(240,59,32);
                }else if(cl>=0.2 && cl<0.4)
                {
                    paint = new Color(254,178,76);
                }else if(cl>=0.4 && cl<0.6)
                {
                    paint = new Color(237,248,177);
                }else if(cl>=0.6 && cl<0.8)
                {
                    paint = new Color(127,205,187);
                }else if(cl>=0.8 )
                {
                    paint = new Color(44,127,184);
                }

                return paint;
            }
                
            public void paintComponent(Graphics g) {
                
                
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                drawZoomRectangle(g2, false);
                
                
                Rectangle2D screenDataArea = getScreenDataArea();
                
                int domainLen = parentFrame.attriData.size()-parentFrame.missingValueNumber-1;
                
                ArrayList<Domain> breakpoints = ProCalculator.getBreakpoints(null, 0, domainLen, parentFrame.matrix);
                int count = breakpoints.size();
                double intervalSpace = screenDataArea.getWidth()/(double)count;
                //System.out.println(screenDataArea.getWidth());
                //double intervalSpace = 1201/count;
                for(int i = 0; i < breakpoints.size(); i++)
                {
                    Domain breakpoint = breakpoints.get(i);
                    int left = (int)(breakpoint.getLeftID() * intervalSpace);
                    int right = (int)(breakpoint.getRightID() * intervalSpace);
                    
                    //System.out.println(Integer.toString(breakpoint.getLeftID()) + " " + Integer.toString(breakpoint.getRightID()));
                    Paint bkPaint = getBreakPointPaint(breakpoint.getCL());
                    g2.setPaint(bkPaint);
                    BasicStroke bs = new BasicStroke(8, BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
                    g2.setStroke(bs);
                    //System.out.println(screenDataArea.getX() + right);
                    g2.drawLine((int)screenDataArea.getX() + left, (int)screenDataArea.getHeight() + 10, (int)screenDataArea.getX() + right, (int)screenDataArea.getHeight() + 10);
                }
                g2.dispose();
            }

            private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
                double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
                double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
                return new Point2D.Double(xx, yy);
            }
            
            private void drawZoomRectangle(Graphics2D g2, boolean xor) 
            {
                if (this.zoomRectangle != null) {
                    if (xor) {
                        // Set XOR mode to draw the zoom rectangle
                        g2.setXORMode(Color.gray);
                    }
                    if (this.fillZoomRectangle) {
                        g2.setPaint(this.zoomFillPaint);
                        g2.fill(this.zoomRectangle);
                    }
                    else {
                        g2.setPaint(this.zoomOutlinePaint);
                        g2.draw(this.zoomRectangle);
                    }
                    if (xor) {
                        // Reset to the default 'overwrite' mode
                        g2.setPaintMode();
                    }
                }
            }

        };

        //chartPanel.setDomainZoomable(true);
        //chartPanel.setRangeZoomable(true);

        chartPanel.setPreferredSize(new java.awt.Dimension(837, 488));

        MyBarRenderer mybarrenderer = (MyBarRenderer)plot.getRenderer();
        demopanel = new DemoPanel(mybarrenderer);

        chartPanel.addChartMouseListener(demopanel);
        demopanel.add(chartPanel);;
        return demopanel;
                
    }

    //for create the bar chart
    private StatisticalCategoryDataset createDataset(int fieldIndex) {

        final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
        int rownum = parentFrame.attriData.size();
        for(int i = 0+parentFrame.missingValueNumber; i < rownum; i++)
        {
            double est = parentFrame.attriData.get(i).getEstimate().get(fieldIndex);
            double moe = parentFrame.attriData.get(i).getMoe().get(fieldIndex);
            //String name = parentFrame.attriData.get(i).getUnitName();
            String name = Integer.toString(i);
            result.add(est, moe, "", name);
        }
        return result;
    }

    public ArrayList<Domain> getBreakpoints()
    {
        return this.breakpoint;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
  /*
        jSlider1 = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setResizable(true);

        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel1.setText("Enter number of classes:");

        jTextField1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField1CaretUpdate(evt);
            }
        });

        jLabel2.setText("Choose a classification method:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Class Separability", "Jenk's Natural Break", "Equal Interval", "Quantile" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Apply");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
        );

        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Using slider");
        jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton1StateChanged(evt);
            }
        });
        jRadioButton1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRadioButton1FocusGained(evt);
            }
        });

        jRadioButton2.setText("Entering number of classes");
        jRadioButton2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRadioButton2FocusGained(evt);
            }
        });

        jLabel4.setText("Determining classes by:");

        jLabel5.setText("Choose a confidence level");

        jButton2.setText("Add/New");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jButton3.setText("Delete");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        jLabel6.setText("Breakpoint");

        jButton4.setText("Move");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel5))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 1205, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(27, 27, 27))
                        .addComponent(jLabel4)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton2)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4))
                    .addComponent(jLabel6))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton4))))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleParent(this);

        pack();*/
    	jSlider1 = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setResizable(true);

        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel1.setText("Enter number of classes:");

        jTextField1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField1CaretUpdate(evt);
            }
        });

        jLabel2.setText("Choose a classification method:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Class Separability", "Jenk's Natural Break", "Equal Interval", "Quantile" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Apply");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
        );

        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Using slider");
        jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton1StateChanged(evt);
            }
        });
        jRadioButton1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRadioButton1FocusGained(evt);
            }
        });

        jRadioButton2.setText("Entering number of classes");
        jRadioButton2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRadioButton2FocusGained(evt);
            }
        });

        jLabel4.setText("Determining classes by:");

        jLabel5.setText("Choose a confidence level");

        jButton2.setText("Add/New");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jButton3.setText("Delete");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        jLabel6.setText("Breakpoint");

        jButton4.setText("Move");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel5))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 825, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(27, 27, 27))
                        .addComponent(jLabel4)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton2)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4))
                    .addComponent(jLabel6))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton4))))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleParent(this);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        // TODO add your handling code here:
        if(this.classificationTool == "slider")
        {
            JSlider source = (JSlider)evt.getSource();

            if (!source.getValueIsAdjusting()) 
            {
                double fps = (double)source.getValue();
                this.CLthreshold = fps/100;
                int domainLen = parentFrame.attriData.size()-parentFrame.missingValueNumber-1;
                //get the corresponding break points location and confidence level, between id1 and id2
                this.breakpoint = ProCalculator.getBreakpoints(this.breakpoint, CLthreshold, domainLen, parentFrame.matrix);
                System.out.println("break point number is:"+ this.breakpoint.size());
                //get the corresponding break point value
                for (int i =0; i < this.breakpoint.size(); i++) 
                {
                    int id1 = this.breakpoint.get(i).getLowestCLLeftID()+parentFrame.missingValueNumber;
                    int id2 = this.breakpoint.get(i).getLowestCLRightID()+parentFrame.missingValueNumber;
                    double est1 = parentFrame.attriData.get(id1).getEstimate().get(0);
                    double est2 = parentFrame.attriData.get(id2).getEstimate().get(0);
                    double moe2 = parentFrame.attriData.get(id2).getMoe().get(0);
                    double moe1 = parentFrame.attriData.get(id2).getMoe().get(0);
                    double point = ProCalculator.getInstersction(this.intersectThreshold, est1, moe1, est2, moe2);
                    this.breakpoint.get(i).setIntersectPoint(point);
                    System.out.println("breakp point caculated from "+(this.breakpoint.get(i).getLowestCLLeftID()+parentFrame.missingValueNumber)+" and "+(this.breakpoint.get(i).getLowestCLRightID()+parentFrame.missingValueNumber)+" is: "+ this.breakpoint.get(i).getIntersectPoint());
                }

                if((parentFrame.attriData.size()-parentFrame.missingValueNumber)>0  )
                {
                   //update map synchronously ---------------------------------------------
                    if(this.breakpoint.size() < 9)
                    {

                       this.breakpoint = Utils.sortSmalltoLargeAsPointValue(this.breakpoint);
                       jLabel3.setText("");
                       FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
                       PropertyName propteryExpression = ff.property(parentFrame.selectedVariable.getSelectedVariable());
                       double min = parentFrame.attriData.get(0+parentFrame.missingValueNumber).getEstimate().get(0);
                       int datarecordnum = parentFrame.attriData.size();
                       double max = parentFrame.attriData.get(datarecordnum-1).getEstimate().get(0);
                       Classifier groups = Classification.breakPointToClassifier(this.breakpoint, min, max, "Class Separability", parentFrame.missingValueNumber);
                       Map2D map = new Map2D("demo", parentFrame.collection,parentFrame.source, this.breakpoint.size()+1,
                                   groups, propteryExpression, this.breakpoint, parentFrame.missingValueNumber);
                       parentFrame.map.flatmap = map;
                       MapContext mapcontext =  parentFrame.map.flatmap.newMap();
                      // Rule rule=map.getRule();
                     //  PolygonSymbolizer sym = (PolygonSymbolizer) rule.getSymbolizers()[0];
                       double[] cl = new double[this.breakpoint.size()];
                       double[] mins = new double[this.breakpoint.size()+1];
                       double[] maxs = new double[this.breakpoint.size()+1];


                       mins[0] = min;

                       for (int i = 0; i < this.breakpoint.size(); i++)
                       {
                           cl[i] = this.breakpoint.get(i).getCL();
                           mins[i+1] = this.breakpoint.get(i).getIntersectPoint();
                           maxs[i] =this.breakpoint.get(i).getIntersectPoint()-1;
                       }
                       maxs[this.breakpoint.size()] = max;

                      // Stroke stroke =(Stroke) sym.getStroke();
       //                GraphicLegend gl = rule.getLegend();
       //                TextSymbolizer2 test = (TextSymbolizer2) rule.symbolizers().get(0);

       //                List<GraphicalSymbol> aa =gl.graphicalSymbols();
       //                GraphicalSymbol bb = aa.get(0);

                       Color[] colors= map.getColors();

                      MapLegend legendWithCL = new MapLegend(colors, this.breakpoint.size()+1, cl, mins, maxs, parentFrame.map.getjPanel2().getSize(),parentFrame.missingValueNumber);
                      parentFrame.map.displayLegend(legendWithCL);
                      parentFrame.map.displayShapefile(mapcontext);
                    }
                    else
                    {
                        jLabel3.setText("Too many classes for map!");
                    }
               //-----------------------------------------------------------------
                }

               jPanel1.removeAll();
               JPanel chartPanel = createNewChartPanel();
               //System.out.println(jPanel1.getWidth() + " " + jPanel1.getHeight());

               chartPanel.setSize(jPanel1.getSize());
               jPanel1.add(chartPanel);
               jPanel1.getParent().validate();
               jPanel1.repaint();


   //           String[] strings= new String[SDs.size()];
   //           for(int i=0; i<SDs.size();i++)
   //                    {
   //                        strings[i]=SDs.get(i).toString();
   //                    }
   //            jList1= new JList(strings);

              if(this.breakpoint.size()>0)
                  updateEvaluation();

            }
        }
    }//GEN-LAST:event_jSlider1StateChanged

    private void updateEvaluation()
    {
         this.SDs=getClassInnerSD();
         
         this.parentFrame.clsevaluation.updataTable(this.SDs);
         this.parentFrame.clsevaluation.TestRobustness(this.parentFrame.attriData, this.breakpoint);

          
    }

    private ArrayList<Double> getClassInnerSD()
    {
        ArrayList<Double> deviation = new ArrayList<Double>();

        int estimateindex=0;
        for(int i= 0; i<this.breakpoint.size()+1;i++)
        {
            ArrayList<Double> classMembers = new ArrayList<Double>();
            if(i!=this.breakpoint.size())
            {
                
                while(parentFrame.attriData.get(estimateindex).getEstimate().get(0)<this.breakpoint.get(i).getIntersectPoint())
                {
                    double value=parentFrame.attriData.get(estimateindex).getEstimate().get(0);
                    classMembers.add(value);
                    estimateindex++;
                }
                
            }else
            {
               
                while(estimateindex<parentFrame.attriData.size())
                {
                    double value=parentFrame.attriData.get(estimateindex).getEstimate().get(0);
                    classMembers.add(value);
                    estimateindex++;
                }
                
            }
            deviation.add(InnerSD.sdKnuth(classMembers));
        }
        return deviation;

    }

    private void jTextField1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField1CaretUpdate
        // TODO add your handling code here:
        // JTextField textfield = (JTextField)evt.getSource();

        String inputstr = jTextField1.getText();
        Pattern pattern = Pattern.compile("[0-9]*");
        if(!inputstr.isEmpty()) {
            if (pattern.matcher(inputstr).matches()) {
                int input = Integer.parseInt(inputstr);
                if(input <= parentFrame.attriData.size()) {

                    this.selectedClassNumber = input;
                    parentFrame.selectedVariable.setSelectedClassNum(input);

                }else{
                    System.out.println("number exceed the upper limit!");
                }

            } else{
                System.out.println("input is not numbers");
            }
        }
    }//GEN-LAST:event_jTextField1CaretUpdate
    private PreCalculateSepSd sepSdCalculator;
    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        String value = (String)jComboBox1.getSelectedItem();
        this.selectedClassificationMethod = value;
        parentFrame.selectedVariable.setSelectedMethod(value);
        sepSdCalculator  = new PreCalculateSepSd(value,parentFrame.collection,parentFrame.selectedVariable.getSelectedVariable(),parentFrame.attriData, parentFrame.matrix);
        sepSdCalculator.calculateSepSdPairs();
        parentFrame.sepSdClassnum = sepSdCalculator.getSepSdClassnumPair();
        parentFrame.statplot.addPlotPanel(); //draw the paris relationship
        //parentFrame.paraplot.addPlotPanel();
}//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String method = this.selectedClassificationMethod;
        int num = this.selectedClassNumber;
        makeClassification(num, method);
    }//GEN-LAST:event_jButton1ActionPerformed
    
     public void makeClassificationFromStarPlot(int classnum)
    {
        String method = this.selectedClassificationMethod;
        makeClassification(classnum, method);
    }

    private void makeClassification(int classnum, String classmethod)
    {
        // TODO add your handling code here:
        Classification classifier = new Classification();
        if(classmethod == "Class Separability") {
            int domainLen = parentFrame.attriData.size()-parentFrame.missingValueNumber-1;
            this.breakpoint = ProCalculator.getBreakpoints(this.breakpoint, 0, domainLen, parentFrame.matrix);
            Utils.sortSmalltoLarge(this.breakpoint);
            while(this.breakpoint.size()> (classnum -1)) {
                this.breakpoint.remove(0);
            }
            for (int i =0; i < this.breakpoint.size(); i++) {
                int id1 = this.breakpoint.get(i).getLowestCLLeftID()+parentFrame.missingValueNumber;
                int id2 = this.breakpoint.get(i).getLowestCLRightID()+parentFrame.missingValueNumber;
                double est1 = parentFrame.attriData.get(id1).getEstimate().get(0);
                double est2 = parentFrame.attriData.get(id2).getEstimate().get(0);
                double moe2 = parentFrame.attriData.get(id2).getMoe().get(0);
                double moe1 = parentFrame.attriData.get(id1).getMoe().get(0);
                double point = ProCalculator.getInstersction(this.intersectThreshold, est1, moe1, est2, moe2);
                this.breakpoint.get(i).setIntersectPoint(point);
                
//
//                System.out.println("left unit is: "+ (this.breakpoint.get(i).getLeftID()+parentFrame.missingValueNumber));
//                System.out.println("right unit is: "+ (this.breakpoint.get(i).getRightID()+parentFrame.missingValueNumber));
//                System.out.println("lowest CL is: "+ this.breakpoint.get(i).getCL());
                System.out.println("breakp point caculated from "+(this.breakpoint.get(i).getLowestCLLeftID()+parentFrame.missingValueNumber)+" and "+(this.breakpoint.get(i).getLowestCLRightID()+parentFrame.missingValueNumber)+" is: "+ this.breakpoint.get(i).getIntersectPoint());
            }

        }else if (classmethod == "Jenk's Natural Break") {
            this.breakpoint=classifier.JenksNaturalBreaks(parentFrame.collection,classnum,parentFrame.selectedVariable.getSelectedVariable(),parentFrame.attriData, parentFrame.matrix);

        }else if (classmethod == "Quantile") {
             this.breakpoint=classifier.Quantile(parentFrame.collection,classnum,parentFrame.selectedVariable.getSelectedVariable(),parentFrame.attriData, parentFrame.matrix);

        }else if (classmethod == "Equal Interval") {
            this.breakpoint=classifier.EqualInterval(parentFrame.collection,classnum,parentFrame.selectedVariable.getSelectedVariable(),parentFrame.attriData, parentFrame.matrix);

        }else if (classmethod == "Standard Deviation")
        {
            this.breakpoint=classifier.StandardDeviation(parentFrame.collection,classnum,parentFrame.selectedVariable.getSelectedVariable(),parentFrame.attriData, parentFrame.matrix);

        }

        //update map synchronously ---------------------------------------------
        if(this.breakpoint.size()<9)
        {
            if(classmethod == "Class Separability")
            {
                this.breakpoint = Utils.sortSmalltoLargeAsPointValue(this.breakpoint);
            }
            jLabel3.setText("");
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
            PropertyName propteryExpression = ff.property(parentFrame.selectedVariable.getSelectedVariable());
            double min = parentFrame.attriData.get(0+parentFrame.missingValueNumber).getEstimate().get(0);
            int datarecordnum = parentFrame.attriData.size();
            double max = parentFrame.attriData.get(datarecordnum-1).getEstimate().get(0);
            Classifier groups = Classification.breakPointToClassifier(this.breakpoint, min, max, classmethod, parentFrame.missingValueNumber);
            Map2D map = new Map2D("demo", parentFrame.collection,parentFrame.source, classnum,
                        groups, propteryExpression, this.breakpoint, parentFrame.missingValueNumber);
            parentFrame.map.flatmap = map;
            MapContext mapcontext =  parentFrame.map.flatmap.newMap();

            double[] cl = new double[this.breakpoint.size()];
            double[] mins = new double[this.breakpoint.size()+1];
            double[] maxs = new double[this.breakpoint.size()+1];


            mins[0] = min;

            for (int i = 0; i < this.breakpoint.size(); i++)
            {
                cl[i] = this.breakpoint.get(i).getCL();
                mins[i+1] = this.breakpoint.get(i).getIntersectPoint();
                maxs[i] =this.breakpoint.get(i).getIntersectPoint()-1;
            }
            maxs[this.breakpoint.size()] = max;

           // Stroke stroke =(Stroke) sym.getStroke();
//                GraphicLegend gl = rule.getLegend();
//                TextSymbolizer2 test = (TextSymbolizer2) rule.symbolizers().get(0);

//                List<GraphicalSymbol> aa =gl.graphicalSymbols();
//                GraphicalSymbol bb = aa.get(0);

            Color[] colors= map.getColors();
            MapLegend legendWithCL = new MapLegend(colors, this.breakpoint.size()+1, cl, mins, maxs, parentFrame.map.getjPanel2().getSize(),parentFrame.missingValueNumber);
            parentFrame.map.displayLegend(legendWithCL);
//            MapLegend legendWithoutCL = new MapLegend();
//            legendWithoutCL.getLegend(mapcontext);
            parentFrame.map.displayShapefile(mapcontext);
        }else
        {
            jLabel3.setText("Too many classes for map!");
        }
        //-----------------------------------------------------------------
        
        jPanel1.removeAll();
        JPanel chartPanel = createNewChartPanel();
        chartPanel.setSize(jPanel1.getSize());
        jPanel1.add(chartPanel);
        jPanel1.getParent().validate();
        jPanel1.repaint();
        
        //get the minimum confidence level and update thumb in the slider
        double minCL=1;
        for(int i =0; i <this.breakpoint.size(); i++)
        {
            if(this.breakpoint.get(i).getCL()<minCL)
                minCL=this.breakpoint.get(i).getCL();
        }
        jSlider1.setValue((int)(minCL*100));
        ///////////////////////////////////////////////////////////////
        
        updateEvaluation();
    }
    
    private void jRadioButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1StateChanged

    private void jRadioButton1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jRadioButton1FocusGained
        // TODO add your handling code here:
         //set typein disabled-------------
        this.classificationTool="slider";
        jTextField1.setEnabled(false);
        jButton1.setEnabled(false);
        jComboBox1.setEnabled(false);
        //-------------------------------
        jSlider1.setEnabled(true);
        jSlider1.setUI(new MySliderUI(jSlider1,1));
        jRadioButton2.setSelected(false);

    }//GEN-LAST:event_jRadioButton1FocusGained

    private void jRadioButton2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jRadioButton2FocusGained
        // TODO add your handling code here:
        this.classificationTool="inputbox";
        jSlider1.setEnabled(false);
        jSlider1.setUI(new MySliderUI(jSlider1,0));



         //set typein disabled-------------
        jTextField1.setEnabled(true);
        jButton1.setEnabled(true);
        jComboBox1.setEnabled(true);
        //-------------------------------
        jRadioButton1.setSelected(false);
     
        
    }//GEN-LAST:event_jRadioButton2FocusGained

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
       
        oBreakPoint = 1;
        this.jButton2.setBackground(Color.RED);
        this.jButton4.setBackground(Color.GRAY);
        this.jButton3.setBackground(Color.GRAY);
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked

        oBreakPoint = 2;
        this.jButton3.setBackground(Color.RED);
        this.jButton4.setBackground(Color.GRAY);
        this.jButton2.setBackground(Color.GRAY);        
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        oBreakPoint = 0;
        this.jButton4.setBackground(Color.RED);
        this.jButton2.setBackground(Color.GRAY);
        this.jButton3.setBackground(Color.GRAY);
        
    }//GEN-LAST:event_jButton4MouseClicked

     class MySliderUI extends javax.swing.plaf.basic.BasicSliderUI 
     {
        private int ifabled=0;  //dtermine if the slider bar is disabled, if disabled, then the color will be in gray
        public MySliderUI(JSlider b, int ifable) 
        {
		super(b);
                this.ifabled=ifable;
	}


        public void paintThumb(Graphics g) 
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if(this.ifabled==1)
            {
                g2d.setPaint(Color.BLACK);
            }else
            {
                g2d.setPaint(Color.darkGray);
               // g2d.setColor(new Color(255,255,255,0));
            }

            g2d.fillRect(thumbRect.x+4, thumbRect.y, thumbRect.width-8,
                            thumbRect.height);

            //g2d.drawImage(image, thumbRect.x, thumbRect.y, thumbRect.width,thumbRect.height,null);
        }

        public void paintTrack(Graphics g) 
        {
            int cy, cw;
            Rectangle trackBounds = trackRect;
            if (slider.getOrientation() == JSlider.HORIZONTAL) 
            {
                Graphics2D g2 = (Graphics2D) g;
                cy = (trackBounds.height / 2) - 3;
                cw = trackBounds.width;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(trackBounds.x, trackBounds.y + cy);
//
//	            // background is gray
               // g2.setPaint(Color.GRAY);
               // g2.fillRect(0, -cy-2, cw, cy * 2);
//	            System.out.println(cw);

                int trackLeft = 0;

                int trackRight = 0;

                trackRight = trackRect.width - 1;

                int middleOfThumb = 0;

                int fillLeft = 0;

                int fillRight = 0;

                //coordinate tranlate
                middleOfThumb = thumbRect.x + (thumbRect.width / 2);

                middleOfThumb -= trackRect.x;
                //System.out.println(middleOfThumb);
                if (!drawInverted()) {
                    fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
                    fillRight = middleOfThumb;
                } else {
                    fillLeft = middleOfThumb;
                    fillRight = !slider.isEnabled() ? trackRight - 1
                            : trackRight - 2;
                }
                // set gradient
//	            float[] dist = { 0.1f, 0.5f, 0.9f };
//	            Color[] colors = { new Color(255, 0, 0), new Color(0, 255, 0),
//	                new Color(0, 0, 255) };
//		        Point2D start = new Point2D.Float(0, -cy);
//		        Point2D end = new Point2D.Float(0, cy+cw);
//		        LinearGradientPaint p = new LinearGradientPaint(start, end, dist,
//		                colors);
//
//		        g2.setPaint(p);
//		        g2.fillRect(0, -cy, cw, cy * 2);


               // g2.setPaint(new GradientPaint(0, 0, new Color(255, 0, 0), cw, 0,
                     // new Color(0, 0, 255),  true));
                //g2.fillRect(0, -cy, fillRight - fillLeft, cy * 2);
                int point1 = (int) (cw*0.2);
                //System.out.println("point1 is: "+point1);
                int point2 = point1 + (int) (cw*0.2);
                //System.out.println("point2 is: "+point2);
                int point3 = point2 + (int) (cw*0.2);
                //System.out.println("point3 is: "+point3);
                int point4 = point3 + (int) (cw*0.2);
               // System.out.println("point4 is: "+point4);


                g2.setPaint(new Color(240,59,32));
                g2.fillRect(0, -cy, point1, cy * 2);
                g2.setPaint(new Color(254,178,76));
                g2.fillRect(point1, -cy, (point2-point1), cy * 2);
                g2.setPaint(new Color(237,248,177));
                g2.fillRect(point2, -cy, (point3-point2), cy * 2);

                g2.setPaint(new Color(127,205,187));
                g2.fillRect(point3, -cy, (point4-point3), cy * 2);

                g2.setPaint(new Color(44,127,184));
                g2.fillRect(point4, -cy, (cw-point4), cy * 2);


                //g2.fillRect(0, -cy, cw, cy * 2);
//	            g2.setPaint(slider.getBackground());
//	            Polygon polygon = new Polygon();
//	            polygon.addPoint(0, cy);
//	            polygon.addPoint(0, -cy);
//	            polygon.addPoint(cw, -cy);
//	            g2.fillPolygon(polygon);
//	            polygon.reset();
                if(this.ifabled==1)
                {
                    g2.setPaint(Color.black);
                }else
                {
                    g2.setPaint(Color.LIGHT_GRAY);
                }

                //g2.drawLine(0, cy, cw - 1, cy);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.translate(-trackBounds.x, -(trackBounds.y + cy));
            } 
            else 
            {
                super.paintTrack(g);
            }
        }
            
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
