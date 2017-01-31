/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.minsplots;

import classification.dataHandler.SepSdClassnum;
import java.awt.BasicStroke;
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
 * @author cisc
 */
public class StarPlotPane_1 extends JPanel implements MouseListener{

    static final double pi = 3.1415926;
    static final int edgeWidth = 260;
    static final double angle = pi/3;
    static final int leftOffset = 40;
    static final int topOffset = 40;
    static final String topAxis = "Class Number";
    static final String leftAxis = "Separability";
    static final String rightAxis = "Dispersion";
    static final String bottomAxis = "Equality";

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

    public StarPlotPane_1(ArrayList<SepSdClassnum> pairs)
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
        //draw outline of triangle
        int leftX=0+leftOffset;
        int leftY=edgeWidth/2+topOffset;
        int rightX = leftX+edgeWidth;
        int rightY =leftY;
        int topX = leftX + edgeWidth/2;
        int topY = 0+topOffset;
        int bottomX = topX;
        int bottomY = edgeWidth+topOffset;
        g2.setColor(Color.black);
        //g2.drawLine(leftX, leftY, rightX, rightY);
        //g2.drawLine(leftX, leftY,topX, topY);
       // g2.drawLine(topX, topY, rightX, rightY);

        //draw three axises in the triangle
         int middleX = leftX+edgeWidth/2;
         int middleY = edgeWidth/2+topOffset;
         g2.setColor(Color.black);
         g2.drawLine(leftX, leftY, rightX, rightY);
         g2.drawLine(topX, topY,bottomX, bottomY);


         //draw the name of three axi
         g2.setColor(Color.black);
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
        g2.drawString(topAxis, topX-30, topY-20);
        g2.drawString(bottomAxis, bottomX-30, bottomY+20);
        g2.drawString(leftAxis, leftX-30, leftY+30);
        g2.drawString(rightAxis, rightX-20, rightY+30);

        if(!this.values.isEmpty())
        {
            this.clearValues();
            this.getLocationOffsetsOfValues();
            g2.setColor(Color.black);
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            g2.drawString(String.format("%1$,.0f", this.maxClassnum), topX, topY-6);
            g2.drawString(String.format("%1$,.0f", this.minClassnum), middleX+3,middleY-3);
            g2.drawString(String.format("%1$,.2f", this.maxSep), leftX-10, leftY+18);
            g2.drawString(String.format("%1$,.2f", this.minSep), middleX-26, middleY-3);
            g2.drawString(String.format("%1$,.2f", this.minSd), rightX-10, rightY+16);
            g2.drawString(String.format("%1$,.2f", this.maxSd), middleX+6, middleY+16);
            g2.drawString(String.format("%1$,.2f", this.minEqu), bottomX-10, bottomY+4);
            g2.drawString(String.format("%1$,.2f", this.maxEqu), middleX-26, middleY+16);
            //get Locations
            for(int i=0; i<this.offsets.size()/4; i++)
            {

                int x = topX;
                int y = (int) (topY + this.offsets.get(4 * i) * (middleY-topY));
                this.classNumberLocatons.add(x);
                this.classNumberLocatons.add(y);

                x = (int) (leftX + this.offsets.get(4 * i + 1) * (middleX-leftX));
                y = leftY;
                this.sepLocations.add(x);
                this.sepLocations.add(y);

                x=(int)(rightX - this.offsets.get(4 * i + 2) * (rightX-middleX));
                y = rightY;
                this.sdLocations.add(x);
                this.sdLocations.add(y);

                x = bottomX;
                y = (int) (bottomY - this.offsets.get(4 * i+3) * (bottomY-middleY));
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
                g2.drawLine(x2, y2, x4, y4);
                g2.drawLine(x4, y4, x3, y3);
                g2.drawLine(x1, y1, x3, y3);

                
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
            offset = (this.values.get(i).getSumSD()-minSd)/(maxSd-minSd);
            this.offsets.add(offset);

            offset = (this.values.get(i).getDeviationNumOfClassMember()-minEqu)/(maxEqu-minEqu);
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
