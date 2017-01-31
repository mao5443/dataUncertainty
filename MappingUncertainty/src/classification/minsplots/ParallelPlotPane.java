/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.minsplots;

import classification.dataHandler.SepSdClassnum;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author min
 */
public class ParallelPlotPane extends JPanel implements MouseListener {
    static final int axisLength = 200;
    static final int interval = 140;
  
    static final int leftOffset = 40;
    static final int topOffset = 40;
    static final String axis1 = "Class Number";
    static final String axis2 = "Separability";
    static final String axis3 = "Dispersion";
    static final String axis4 = "Equality";

    private ArrayList<Color> colors;

    private ArrayList<SepSdClassnum> values;

    private ArrayList<Integer> classNumberLocatons;  //x1,y1,x2,y2...
    private ArrayList<Integer>  sepLocations;
    private ArrayList<Integer> sdLocations;
    private ArrayList<Integer> equLocations;
    private ArrayList<Double> offsets; //offset for classnum + offset for CL + offset for SD
    private double minSep;
    private double maxSep;
    private double minSd;
    private double maxSd;
    private double minClassnum;
    private double maxClassnum;
    private double minEqu;
    private double maxEqu;

    public ParallelPlotPane(ArrayList<SepSdClassnum> pairs)
    {
        this.values=pairs;
        this.classNumberLocatons= new ArrayList<Integer>();
        this.sepLocations= new ArrayList<Integer>();
        this.sdLocations= new ArrayList<Integer>();
        this.equLocations= new ArrayList<Integer>();
        this.offsets = new ArrayList<Double>();
        this.colors = new ArrayList<Color>();
         this.addColors();
    }

    public void setValues(ArrayList<SepSdClassnum> pairs)
    {
        this.values=pairs;
    }
    private void addColors()
    {
        this.colors.add(Color.red);
        this.colors.add(Color.yellow);
        this.colors.add(Color.blue);
        this.colors.add(Color.GREEN);
        this.colors.add(Color.MAGENTA);
        this.colors.add(Color.ORANGE);
        this.colors.add(Color.PINK);
        this.colors.add(Color.CYAN);


    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        Graphics2D g2 = (Graphics2D) g;
        

        //draw three axises in the triangle
         int axis1TopX = leftOffset; int axis1BtmX = leftOffset;
         int axis1TopY = topOffset; int axis1BtmY = topOffset+axisLength;
         int axis2TopX = leftOffset+interval; int axis2BtmX = leftOffset+interval;
         int axis2TopY = topOffset; int axis2BtmY = topOffset+axisLength;
         int axis3TopX = leftOffset+interval*2; int axis3BtmX = leftOffset+interval*2;
         int axis3TopY = topOffset; int axis3BtmY = topOffset+axisLength;
         int axis4TopX = leftOffset+interval*3; int axis4BtmX = leftOffset+interval*3;
         int axis4TopY = topOffset; int axis4BtmY = topOffset+axisLength;

         g2.setColor(Color.black);
         g2.drawLine(axis1TopX,axis1TopY,axis1BtmX, axis1BtmY);
         g2.drawLine(axis2TopX,axis2TopY,axis2BtmX, axis2BtmY);
         g2.drawLine(axis3TopX,axis3TopY,axis3BtmX, axis3BtmY);
         g2.drawLine(axis4TopX,axis4TopY,axis4BtmX, axis4BtmY);


         //draw the name of three axi
         g2.setColor(Color.black);
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
        g2.drawString(axis1, axis1TopX, axis1TopY-20);
        g2.drawString(axis2, axis2TopX, axis2TopY-20);
        g2.drawString(axis3, axis3TopX, axis3TopY-20);
        g2.drawString(axis4, axis4TopX, axis4TopY-20);

        if(!this.values.isEmpty())
        {
            this.clearValues();
            this.getLocationOffsetsOfValues();
            g2.setColor(Color.black);
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            g2.drawString(String.format("%1$,.0f", this.maxClassnum), axis1TopX, axis1TopY-4);
            g2.drawString(String.format("%1$,.0f", this.minClassnum), axis1BtmX, axis1BtmY+8);
            g2.drawString(String.format("%1$,.2f", this.maxSep), axis2TopX, axis2TopY-4);
            g2.drawString(String.format("%1$,.2f", this.minSep), axis2BtmX, axis2BtmY+8);
            g2.drawString(String.format("%1$,.2f", this.minSd), axis3BtmX, axis3BtmY+8);
            g2.drawString(String.format("%1$,.2f", this.maxSd), axis3TopX, axis3TopY-4);
            g2.drawString(String.format("%1$,.2f", this.minEqu), axis4BtmX, axis4BtmY+8);
            g2.drawString(String.format("%1$,.2f", this.maxEqu), axis4TopX, axis4TopY-4);
            //get Locations
            for(int i=0; i<this.offsets.size()/4; i++)
            {

                int x = axis1TopX;
                int y = (int) (axis1TopY + this.offsets.get(4 * i) * (axis1BtmY-axis1TopY));
                this.classNumberLocatons.add(x);
                this.classNumberLocatons.add(y);

                x = axis2TopX;
                y = (int) (axis2TopY + this.offsets.get(4 * i+1) * (axis2BtmY-axis2TopY));
                this.sepLocations.add(x);
                this.sepLocations.add(y);

                x=axis3TopX;
                y = (int) (axis3TopY + this.offsets.get(4 * i+2) * (axis3BtmY-axis3TopY));
                this.sdLocations.add(x);
                this.sdLocations.add(y);

                x = axis4TopX;
                y = (int) (axis4TopY + this.offsets.get(4 * i+3) * (axis4BtmY-axis4TopY));
                this.equLocations.add(x);
                this.equLocations.add(y);

            }

            int colorindex=0;
            //draw locations with lines
            for (int i =0; i<this.classNumberLocatons.size()/2; i++)
            {
                int x1= this.classNumberLocatons.get(2*i);
                int y1 = this.classNumberLocatons.get(2*i+1);
                int x2 = this.sepLocations.get(2*i);
                int y2 = this.sepLocations.get(2*i+1);
                int x3= this.sdLocations.get(2*i);
                int y3 = this.sdLocations.get(2*i+1);
                int x4= this.equLocations.get(2*i);
                int y4 = this.equLocations.get(2*i+1);

                //g2.setStroke(new BasicStroke(8-i));
               // System.out.println("color index "+ colorindex);
                g2.setColor(this.colors.get(colorindex));
                colorindex=colorindex+1;
                //g2.setColor(Color.red);
                g2.drawLine(x1, y1, x2, y2);
                g2.drawLine(x2, y2, x3, y3);
                g2.drawLine(x3, y3, x4, y4);
                


                g2.fillOval(x1-3, y1-3, 6, 6);
                g2.fillOval(x2-3, y2-3, 6, 6);
                g2.fillOval(x3-3, y3-3, 6, 6);
                g2.fillOval(x4-3, y4-3, 6, 6);



            }
        }


    }

    private void clearValues()
    {
        this.classNumberLocatons.clear();
        this.sdLocations.clear();
        this.sepLocations.clear();
        this.offsets.clear();
        //this.shapes.clear();

    }
    public void getLocationOffsetsOfValues()
    {
        double minClassnum=this.minClassnum=this.values.get(0).getClassnum();
        double maxClassnum =this.maxClassnum= this.values.get(this.values.size()-1).getClassnum();
        double minSep = 1.0;
        double maxSep =0.0;
        double minSd = 1000000000000.0;
        double maxSd =0.0;
        double minEqu = 100000000.0;
        double maxEqu = 0.0;
        //get the range of SD and Sep
        for(int i=0; i< this.values.size();i++)
        {
            if(this.values.get(i).getSumSD()>maxSd)
            {
                this.maxSd=maxSd= this.values.get(i).getSumSD();
            }
            if(this.values.get(i).getSumSD()<minSd)
            {
                this.minSd=minSd= this.values.get(i).getSumSD();
            }
            if(this.values.get(i).getMinCL()>maxSep)
            {
                this.maxSep=maxSep= this.values.get(i).getMinCL();
            }
            if(this.values.get(i).getMinCL()<minSep)
            {
                this.minSep=minSep= this.values.get(i).getMinCL();
            }
            if(this.values.get(i).getDeviationNumOfClassMember()>maxEqu)
            {
                this.maxEqu=maxEqu= this.values.get(i).getDeviationNumOfClassMember();
            }
            if(this.values.get(i).getDeviationNumOfClassMember()<minEqu)
            {
                this.minEqu=minEqu= this.values.get(i).getDeviationNumOfClassMember();
            }
        }

        for(int i=0; i< this.values.size();i++)
        {
            //offset ratio of class number off the vertex
            double offset= (maxClassnum-this.values.get(i).getClassnum())/(maxClassnum-minClassnum);
            this.offsets.add(offset);

            //offset ratio of CL
            offset = (maxSep-this.values.get(i).getMinCL())/(maxSep-minSep);
            this.offsets.add(offset);
            //offset ratio of SD
            offset = (maxSd-this.values.get(i).getSumSD())/(maxSd-minSd);
            this.offsets.add(offset);

            offset = (maxEqu-this.values.get(i).getDeviationNumOfClassMember())/(maxEqu-minEqu);
            this.offsets.add(offset);


        }
    }

    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
