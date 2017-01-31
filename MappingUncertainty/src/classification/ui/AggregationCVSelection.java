/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AggregationCriteria.java
 *
 * Created on Feb 16, 2013, 5:57:14 PM
 */

package classification.ui;

import classification.dataHandler.AggregationAttributes;
import classification.dataHandler.Utils;
import classification.minsplots.ScatterPlotCV;
import classification.minsplots.ScatterPlotCVZoomin;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 *
 * @author cisc
 */
public class AggregationCVSelection extends javax.swing.JInternalFrame {
    private Root parentFrame;

    public double[] CVthresholds = new double[2];
    private int datadimension =0;
   
    /** Creates new form AggregationCriteria */
    public AggregationCVSelection(Root root) {
        initComponents();
        this.parentFrame=root;
    }

    private ScatterPlotCV cvplot;
    public void paintCVscatterplot(ArrayList<ArrayList<AggregationAttributes>> attridata)
    {
        ArrayList<Double> cv1 = new ArrayList<Double>();
        ArrayList<Double> cv2 = new ArrayList<Double>();

        for(int i=0; i<attridata.get(0).size();i++)
            cv1.add(attridata.get(0).get(i).cv);
        for(int i=0; i<attridata.get(1).size();i++)
            cv2.add(attridata.get(1).get(i).cv);
//        for(int i=0; i<attridata.size();i++)
//        {
//            double cv = attridata.get(i).cv;
//            if(attridata.get(i).fileindex==0)
//                cv1.add(cv);
//            else if(attridata.get(i).fileindex==1)
//                cv2.add(cv);
//        }
       // Utils.sortSmalltoLargeDouble(cv1);
        this.datadimension=1;
        if(cv2.size()>0)
        {
            this.datadimension=2;
        }

        int datanumber=cv1.size();
        cvplot = new ScatterPlotCV(cv1,cv2,datanumber, datadimension);
        cvplot.setSize(jPanel1.getSize());

        jPanel1.removeAll();
        jPanel1.add(cvplot);
        jPanel1.getParent().validate();
        jPanel1.repaint();


    }

    class UnacceptUnits{
        int FID;
        double max_cv;
        double sum_cv;
        double[] cv = new double[2];
        String axis;
    }

    public void setUnacceptedCVlist()
    {
        
        ArrayList<UnacceptUnits> arrayindexUnaccepctedList= new ArrayList<UnacceptUnits>();

         ArrayList<ArrayList<AggregationAttributes>> temp_data = new ArrayList<ArrayList<AggregationAttributes>>();
         for (int i =0; i<parentFrame.attridata.size(); i++)
         {
             ArrayList<AggregationAttributes> temp = (ArrayList<AggregationAttributes>) parentFrame.attridata.get(i).clone();
             Utils.sortSmalltoLargeAggregationAttributes(temp, "FID");
             temp_data.add(temp);
         }

         if(datadimension==1)
        {
             for(int i=0; i<temp_data.get(0).size(); i++)
             {
                double currentcv = temp_data.get(0).get(i).cv;
                 if(currentcv>this.CVthresholds[0])
                {
                     UnacceptUnits thisunit = new UnacceptUnits();
                     thisunit.max_cv = currentcv;
                     thisunit.FID = temp_data.get(0).get(i).FID;
                     thisunit.axis = "X";
                     thisunit.cv[0] = currentcv;
                     arrayindexUnaccepctedList.add(thisunit);
                }

             }
             setCVThresholdValueLabel(CVthresholds[0], 0.000);
        }else if(this.datadimension==2)
        {
            for(int i=0; i<temp_data.get(0).size(); i++)
             {
                double currentcv1 = temp_data.get(0).get(i).cv;
                double currentcv2 = temp_data.get(1).get(i).cv;
                UnacceptUnits thisunit = new UnacceptUnits();

                 if(currentcv1>this.CVthresholds[0] && currentcv2>this.CVthresholds[1])
                {
                    thisunit.FID = temp_data.get(0).get(i).FID;
                    thisunit.axis = "Both";
                    thisunit.max_cv = Math.max(currentcv1, currentcv2);
                    thisunit.cv[0] = currentcv1;
                    thisunit.cv[1] = currentcv2;
                    arrayindexUnaccepctedList.add(thisunit);
                    
                }else if(currentcv1>this.CVthresholds[0] && currentcv2<=this.CVthresholds[1])
                {
                    thisunit.FID = temp_data.get(0).get(i).FID;
                    thisunit.axis = "X";
                    thisunit.max_cv = currentcv1;
                    thisunit.cv[0] = currentcv1;
                    thisunit.cv[1] = currentcv2;
                    arrayindexUnaccepctedList.add(thisunit);
                    
                }else if(currentcv1<=this.CVthresholds[0] && currentcv2>this.CVthresholds[1])
                {
                    thisunit.FID = temp_data.get(0).get(i).FID;
                    thisunit.axis = "Y";
                    thisunit.max_cv = currentcv2;
                    thisunit.cv[0] = currentcv1;
                    thisunit.cv[1] = currentcv2;
                    arrayindexUnaccepctedList.add(thisunit);
                    
                }
                 
             }
            setCVThresholdValueLabel(CVthresholds[0], CVthresholds[1]);
        }else
        {
            setCVThresholdValueLabel(0.000, 0.000);
        }


//        for(int i=parentFrame.attridata.size()-1; i>=0;i--)
//        {
//            double currentcv = parentFrame.attridata.get(i).cv;
//            if(this.datadimension==1)
//            {
//                if(currentcv>=this.CVthresholds[0])
//                {
//                    this.arrayindexUnacceptedX=this.arrayindexUnacceptedX+i+";";
//                    nrow++;
//                }
//                setCVThresholdValueLabel(CVthresholds[0], 0.000);
//            }else if(this.datadimension==2)
//            {
//                if(currentcv>=this.CVthresholds[0] && currentcv>=this.CVthresholds[1])
//                {
//                    this.arrayindexUnaccepctedBoth=this.arrayindexUnaccepctedBoth+i+";";
//                    nrow++;
//                }else if(currentcv>=this.CVthresholds[0] && currentcv<this.CVthresholds[1])
//                {
//                    this.arrayindexUnacceptedX=this.arrayindexUnacceptedX+i+";";
//                    nrow++;
//                }else if(currentcv<this.CVthresholds[0] && currentcv>=this.CVthresholds[1])
//                {
//                    this.arrayindexUnacceptedY=this.arrayindexUnacceptedY+i+";";
//                    nrow++;
//                }
//                setCVThresholdValueLabel(CVthresholds[0], CVthresholds[1]);
//            }else
//            {
//                setCVThresholdValueLabel(0.000, 0.000);
//                break;
//            }
//        }
         
         parentFrame.aggregationcontrolwin.intializeControlTable(arrayindexUnaccepctedList);

    }

    public void setJPanel2ZoominPlot(ScatterPlotCVZoomin zoominPlot)
    {
        zoominPlot.setSize(jPanel2.getSize());
       jPanel2.removeAll();
        jPanel2.add(zoominPlot);
        jPanel2.getParent().validate();
        jPanel2.repaint();
    }

    public void setCVThresholdValueLabel(double thres1, double thres2)
    {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.0000");
        String threshold1 = decimalFormat.format(thres1);
        String threshold2 = decimalFormat.format(thres2);
        if(!threshold1.equals("0.0000"))
            jTextField1.setText(threshold1);
        else
            jTextField1.setText("");
        if(!threshold2.equals("0.0000"))
            jTextField2.setText(threshold2);
        else
            jTextField2.setText("");
    }

   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setResizable(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(420, 420));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel1MouseDragged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 422, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 419, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(260, 260));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );

        jLabel1.setText("X-axis (CV1) threshold: ");

        jLabel2.setText("Y-axis (CV2) threshold:");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });

        jButton1.setText("Reset Threshold");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(30, 30, 30))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(20, 20, 20)
                        .addComponent(jButton1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jPanel1MousePressed

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jPanel1MouseReleased

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
        // TODO add your handling code here:
       
    }//GEN-LAST:event_jPanel1MouseDragged

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jTextField1KeyTyped

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jTextField2KeyTyped

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(!jTextField1.getText().equals(""))
        {
            double threshold1 = Double.valueOf(jTextField1.getText());
            cvplot.resetThresholdLocation(threshold1, "X");
            this.CVthresholds[0] = threshold1;
        }
        if(!jTextField2.getText().equals(""))
        {
            double threshold2 = Double.valueOf(jTextField2.getText());
            cvplot.resetThresholdLocation(threshold2, "Y");
            this.CVthresholds[1] = threshold2;
        }
        setUnacceptedCVlist();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

}
