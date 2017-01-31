/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.minsplots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author cisc
 */
public class ScatterPlotCVZoomin extends JPanel {
    /**variables for drawing the cv scatter plot
     **/
    double maxCV1, maxCV2, minCV1,minCV2;
    int[] cvXPosition,cvYPosition;
    Color[] pointColor;
    int[] xTickPosition,yTickPosition;
    int datanumber;
    //margin on the top of y axis, margin on the left of x axis
    int plotTopMargin = 5, plotLeftMargin = 5;
    int rangeXZerotoMax = 250, rangeYZerotoMax = 250;
    int tickMarkNumber =10;

    int cv1LineX,cv2LineY;
    int[] cv1LineYs = new int[2];
    int[] cv2LIneXs = new int[2];

    int datadimension =0;

    public ScatterPlotCVZoomin(ArrayList<Integer> cutcv1Positions,
                               ArrayList<Integer> cutcv2Positions,
                               int cutXMin, int cutXMax, int cutYMin, int cutYMax,
                               double cvedge1Min,double cvedge1Max,double cvedge2Min,double cvedge2Max,
                               int[] linePositions,ArrayList<Color> pointColors, int datadimension)
    {
        this.setBackground(Color.black);
        this.maxCV1=cvedge1Max;
        this.minCV1=cvedge1Min;
        this.maxCV2=cvedge2Max;
        this.minCV2=cvedge2Min;
        this.datadimension=datadimension;
        this.pointColor = pointColors.toArray(new Color[pointColors.size()]);
        initialData(cutcv1Positions,cutcv2Positions,cutXMin,cutXMax,cutYMin, cutYMax,linePositions);
    }
    public void initialData(ArrayList<Integer> cutcv1Positions,ArrayList<Integer> cutcv2Positions,
                           int cutXMin, int cutXMax, int cutYMin, int cutYMax, int[] linePositions)
    {
        datanumber=cutcv1Positions.size();
        if(this.datadimension==1 || this.datadimension==2)
        {
            this.cvXPosition= new int[this.datanumber];
            this.cvYPosition= new int[this.datanumber];
            for(int i=0; i<cutcv1Positions.size();i++)
            {
                this.cvXPosition[i]=(int)(plotLeftMargin + (cutcv1Positions.get(i)-cutXMin)*rangeXZerotoMax/(cutXMax-cutXMin));
                this.cvYPosition[i]=(int) (this.plotTopMargin + (cutcv2Positions.get(i)-cutYMin)*rangeYZerotoMax/(cutYMax-cutYMin));
          
            }
           
        }

        //the positions for threshold lines
        this.cv1LineX=(int)(plotLeftMargin + (linePositions[0]-cutXMin)*rangeXZerotoMax/(cutXMax-cutXMin));
        this.cv2LineY=(int) (this.plotTopMargin + (linePositions[1]-cutYMin)*rangeYZerotoMax/(cutYMax-cutYMin));

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int yheight=this.size().height;
        Graphics2D g2= (Graphics2D) g;
        g2.setColor(Color.RED);
        //draw x and y axis
        g2.drawRect(plotLeftMargin, plotTopMargin, rangeXZerotoMax, rangeYZerotoMax);
        //draw scatter points
        for(int i=0; i<this.datanumber;i++)
        {
            g2.setColor(pointColor[i]);
            g2.drawOval(cvXPosition[i], yheight-cvYPosition[i], 4, 4);
        }
        //draw threshold lines
        g2.setColor(Color.CYAN);
        if(this.datadimension==1)
            g2.drawLine(cv1LineX,(yheight-plotLeftMargin) , cv1LineX, (yheight-(plotTopMargin+this.rangeYZerotoMax)));
        if(this.datadimension==2)
        {
            g2.drawLine(cv1LineX,(yheight-plotLeftMargin) , cv1LineX, (yheight-(plotTopMargin+rangeYZerotoMax)));
            g2.drawLine(this.plotLeftMargin, (yheight-this.cv2LineY), this.plotLeftMargin+this.rangeXZerotoMax, (yheight-this.cv2LineY));
        }
    }


}
