/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.minsplots;

import classification.ui.AggregationCVSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author cisc
 */
public class ScatterPlotCV extends JPanel implements MouseListener, MouseMotionListener {
    /**variables for drawing the cv scatter plot
     **/
    double maxCV1, maxCV2, minCV1,minCV2;
    int[] cvXPosition,cvYPosition;
    int[] xTickPosition,yTickPosition;
    int datanumber;
    //margin on the bottom of x axis, margin on the left of y axis
    int plotTopMargin =20, plotLeftMargin = 20;
    int zeroXStart = 5, zeroYStart = 5;
    int rangeXZerotoMax =380, rangeYZerotoMax=380;
    int tickMarkNumber =10;

    int cv1LineX,cv2LineY;
    int[] cv1LineYs = new int[2];
    int[] cv2LIneXs = new int[2];

    int datadimension =0;

    double thresholdX,thresholdY=0;

    public ScatterPlotCV(ArrayList<Double> cv1, ArrayList<Double> cv2, int datanumber, int datadimension) {
        this.setBackground(Color.black);
        this.datadimension=datadimension;
        this.maxCV1=cv1.get(cv1.size()-1);
        this.minCV1=cv1.get(0);
        
        if(this.datadimension==2)
        {
            this.minCV2=cv2.get(0);
            this.maxCV2=cv2.get(cv2.size()-1);
        }
        else
            this.minCV2=this.maxCV2=0;

        this.datanumber=datanumber;
        initialData(cv1,cv2);

        addMouseListener(this) ;
        addMouseMotionListener(this);
        
   
    }


     private void initialData(ArrayList<Double> cv1, ArrayList<Double> cv2)
    {
        
        this.cvXPosition=new int[datanumber];
        this.cvYPosition = new int[datanumber];
        //the position of points
        for(int i=0; i<datanumber; i++)
        {
            this.cvXPosition[i]=(int) (plotLeftMargin + (cv1.get(i)-minCV1) / (maxCV1-minCV1) * (rangeXZerotoMax));
            if(this.datadimension==2)
                this.cvYPosition[i]=(int) (this.plotTopMargin + (cv2.get(i)-minCV2) / (maxCV2-minCV2) * (rangeYZerotoMax));
            else
                this.cvYPosition[i]=(int) (this.plotTopMargin + rangeYZerotoMax/2);
        }
        //the positions for draggable threshold lines
        this.cv1LineX=this.cvXPosition[this.cvXPosition.length/2];
        this.cv2LineY=this.cvYPosition[this.cvYPosition.length/2];
        this.cv1LineYs[0]=this.plotLeftMargin;
        this.cv1LineYs[1]=this.plotLeftMargin+this.rangeYZerotoMax;
        this.cv2LIneXs[0]=this.plotTopMargin;
        this.cv2LIneXs[1]=this.plotTopMargin+this.rangeXZerotoMax;

    }
      public void resetThresholdLocation(double value, String axis)
      {
          if(axis.equals("X"))
              cv1LineX = (int)(plotLeftMargin + (value - this.minCV1)*rangeXZerotoMax/(maxCV1-minCV1));
          if(axis.equals("Y"))
              cv2LineY = (int)(plotTopMargin + (value-minCV2)*rangeYZerotoMax/(maxCV2-minCV2));
          repaint();
      }

     
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int yheight=this.size().height;
        Graphics2D g2= (Graphics2D) g;

         DecimalFormat fmt = new DecimalFormat("0.0000");
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN));

        g2.setColor(Color.red);
        //draw x and y axis
        g2.drawRect(this.plotLeftMargin, this.plotTopMargin,
                    this.rangeXZerotoMax, this.rangeYZerotoMax);
        //draw scatter points
        for(int i=0; i<this.datanumber;i++)
        {

            if(this.cvXPosition[i]<=this.cv1LineX && this.cvYPosition[i]<=this.cv2LineY)
            {
                g2.setColor(Color.WHITE);
            }else
                g2.setColor(Color.ORANGE);

            g2.drawOval(this.cvXPosition[i], yheight-this.cvYPosition[i], 4, 4);



        }
        //draw threshold lines
        g2.setColor(Color.CYAN);
        if(this.datadimension==1)
        {
            g2.drawLine(this.cv1LineX,(yheight-this.plotTopMargin) , this.cv1LineX, (yheight-(this.plotTopMargin+this.rangeYZerotoMax)));
            if(this.thresholdX!=0)
            {
                String thresholdstr = fmt.format(this.thresholdX);
                g2.drawString(thresholdstr,this.cv1LineX+5,(yheight-(this.plotTopMargin+this.rangeYZerotoMax)+10) );
            }

        }
        if(this.datadimension==2)
        {
            g2.drawLine(this.cv1LineX,(yheight-this.plotTopMargin) , this.cv1LineX, (yheight-(this.plotTopMargin+this.rangeYZerotoMax)));
            g2.drawLine(this.plotLeftMargin, (yheight-this.cv2LineY), this.plotLeftMargin+this.rangeXZerotoMax, (yheight-this.cv2LineY));
            if(this.thresholdX!=0)
            {
                String thresholdstr = fmt.format(this.thresholdX);
                g2.drawString(thresholdstr,this.cv1LineX+5,(yheight-(this.plotTopMargin+this.rangeYZerotoMax)+10) );
            }

            if(this.thresholdY!=0)
            {
                String thresholdstr = fmt.format(thresholdY);
                g2.drawString(thresholdstr,plotLeftMargin + rangeXZerotoMax - 34, yheight-(cv2LineY+8) );
            }
        }

        //draw x, y label
        String xstart = fmt.format(this.minCV1);
        String xend = fmt.format(this.maxCV1);
        g2.drawString(xstart, this.plotLeftMargin,(yheight-this.plotTopMargin+15));
        g2.drawString(xend, this.plotLeftMargin+this.zeroXStart+this.rangeXZerotoMax-22,(yheight-this.plotTopMargin+15));
        if(datadimension ==2)
        {
            xstart = fmt.format(minCV2);
            xend = fmt.format(maxCV2);
            AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(-90));
            g2.setFont((g2.getFont()).deriveFont(at));
            g2.drawString(xstart, this.plotLeftMargin - 5,yheight-this.plotTopMargin);
            g2.drawString(xend, this.plotLeftMargin - 5,this.plotTopMargin + 18);
        }

    }

    
    int isDraggable=0;  //0=not draggable, 1=drag x threshold, 2=drag y threshold
    int cx,cy;  //current mouse position when mousepressed
    

    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.")
    }

    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        Point p =e.getPoint();
        cx=p.x; 
        cy=this.size().height-p.y;

        if(cx<=this.cv1LineX+2 && cx>=this.cv1LineX-2)
        {isDraggable=1;
        }else if(cy<=this.cv2LineY+2 && cy>= this.cv2LineY-2)
        {
            isDraggable=2;
        }
    }

    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        Point p = e.getPoint();
        cx= p.x; cy=this.size().height-p.y;
        
       AggregationCVSelection parentwin = (AggregationCVSelection)this.getParent().getParent().getParent().getParent().getParent();
       double cvthreshold=0;
        if(isDraggable == 1)
        {
            cvthreshold=((double)(cx-this.cvXPosition[0]))/((double)(this.cvXPosition[this.datanumber-1]-this.cvXPosition[0]))*(this.maxCV1-this.minCV1)+this.minCV1;
            parentwin.CVthresholds[isDraggable-1]=this.thresholdX=cvthreshold;
        }

        else if(isDraggable==2)
            if(this.datadimension==2)
            {
                cvthreshold=((double)(cy-this.cvYPosition[0]))/((double)(this.cvYPosition[this.datanumber-1]-this.cvYPosition[0]))*(this.maxCV2-this.minCV2)+this.minCV2;
                parentwin.CVthresholds[isDraggable-1]=this.thresholdY=cvthreshold;
            }
       
       isDraggable=0;
       parentwin.setUnacceptedCVlist();
       repaint();

    }

    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
       
    }

    public void mouseDragged(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        Point p = e.getPoint();
        int dx = p.x;
	int dy = this.size().height-p.y;

        if(isDraggable==1)
        {
            if(cx > dx)
                    this.cv1LineX -= (cx - dx)/10;
            else
                    this.cv1LineX += (dx - cx)/10 ;
            cx = this.cv1LineX;
            //System.out.println("current line x is: "+ cx);
        }
        if(isDraggable==2)
        {
            if(cy > dy)
                    this.cv2LineY -= (cy - dy) / 10;
            else
                    this.cv2LineY += (dy - cy) / 10;
            cy = this.cv2LineY;
        }
        repaint();
    }

    public void mouseMoved(MouseEvent e) {
       // throw new UnsupportedOperationException("Not supported yet.");
              Point p = e.getPoint();
        cx= p.x; cy=this.size().height-p.y;
        ArrayList<Integer> cutcv1Positions= new ArrayList<Integer>();
        ArrayList<Integer> cutcv2Positions= new ArrayList<Integer>();
        ArrayList<Color> pointColor = new ArrayList<Color>();
        double cvedge1Min=0, cvedge1Max=0,cvedge2Min=0,cvedge2Max=0;
//        if(this.datadimension==1 || this.datadimension==2)
//        {
            cvedge1Min = ((double)(cx-40-this.cvXPosition[0]))/((double)(this.cvXPosition[this.datanumber-1]-this.cvXPosition[0]))*(this.maxCV1-this.minCV1)+this.minCV1;
            cvedge1Max = ((double)(cx+40-this.cvXPosition[0]))/((double)(this.cvXPosition[this.datanumber-1]-this.cvXPosition[0]))*(this.maxCV1-this.minCV1)+this.minCV1;
            if(this.datadimension==2)
            {
                cvedge2Min = ((double)(cy-40-this.cvYPosition[0]))/((double)(this.cvYPosition[this.datanumber-1]-this.cvYPosition[0]))*(this.maxCV2-this.minCV2)+this.minCV2;
                cvedge2Max = ((double)(cy+40-this.cvYPosition[0]))/((double)(this.cvYPosition[this.datanumber-1]-this.cvYPosition[0]))*(this.maxCV2-this.minCV2)+this.minCV2;
            }

            for(int i=0; i<this.cvXPosition.length;i++)
            {
                if(this.cvXPosition[i]>=(cx-40) && this.cvXPosition[i]<=(cx+40) && this.cvYPosition[i]>=(cy-40) && this.cvYPosition[i]<=(cy+40))
                {
                    cutcv1Positions.add(cvXPosition[i]);
                    cutcv2Positions.add(cvYPosition[i]);
                    if(cvXPosition[i] <= cv1LineX && cvYPosition[i] <= cv2LineY)
                        pointColor.add(Color.WHITE);
                    else
                        pointColor.add(Color.ORANGE);
                }
            }


            int[] linePositions = new int[2];   //int[0] is lineX, int[1] is lineY
            linePositions[0]=-100; linePositions[1]=-100;
            if(this.cv1LineX>=cx-40 && this.cv1LineX<=cx+40)
            {
                linePositions[0]=this.cv1LineX;
            }
             if(this.cv2LineY>=cy-40 && this.cv2LineY<=cy+40)
            {
                if(this.datadimension==2)
                    linePositions[1]=this.cv2LineY;
            }


        ScatterPlotCVZoomin zoominPlot = new ScatterPlotCVZoomin(cutcv1Positions,cutcv2Positions,
                                                                 cx-40,cx+40,cy-40,cy+40,cvedge1Min,
                                                                 cvedge1Max,cvedge2Min,cvedge2Max, 
                                                                 linePositions, pointColor, datadimension);
        AggregationCVSelection parentwin = (AggregationCVSelection)this.getParent().getParent().getParent().getParent().getParent();
        parentwin.setJPanel2ZoominPlot(zoominPlot);



    }


}
