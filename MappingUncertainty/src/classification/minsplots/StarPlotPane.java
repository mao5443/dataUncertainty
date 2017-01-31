/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.minsplots;

import classification.dataHandler.SepSdClassnum;
import classification.ui.Root;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author cisc
 */
public class StarPlotPane extends JPanel implements MouseListener{
    private Root parentFrame;

    static final double pi = 3.1415926;
    static final int edgeWidth = 300;
    static final double angle = pi/3;
    static final int leftOffset = 40;
    static final int topOffset = 300;
    static final String topAxis = "Class Number";
    static final String leftAxis = "Separability";
    static final String rightAxis = "Dispersion";

    private static final int HIT_BOX_SIZE = 6;

    private ArrayList<Color> colors;

    private ArrayList<SepSdClassnum> values;

    private ArrayList<Integer> classNumberLocatons;  //x1,y1,x2,y2...
    private ArrayList<Integer>  sepLocations;
    private ArrayList<Integer> sdLocations;
    private ArrayList<Double> offsets; //offset for classnum + offset for CL + offset for SD
    private double minSep;
    private double maxSep;
    private double minSd;
    private double maxSd;
    private double minClassnum;
    private double maxClassnum;
    private double minEqu;
    private double maxEqu;

    private ArrayList<StarPlotEntity> shapes;
    private int clickedClassnum;

    private int paintTimes;
    ArrayList<Line2D> clickedLine;
    public StarPlotPane(ArrayList<SepSdClassnum> pairs, Root root)
    {
        this.values=pairs;
        this.parentFrame=root;
        this.classNumberLocatons= new ArrayList<Integer>();
        this.sepLocations= new ArrayList<Integer>();
        this.sdLocations= new ArrayList<Integer>();
        this.offsets = new ArrayList<Double>();
        this.colors = new ArrayList<Color>();
        this.shapes = new ArrayList<StarPlotEntity>();
        
         this.addColors();
         this.paintTimes=1;
         addMouseListener(this) ;

         this.clickedLine= new ArrayList<Line2D>();
//         addMouseListener(new MouseAdapter() {
//            @Override
//          public void mouseClicked(MouseEvent e) {
//            System.out.println(e);
//            int x = e.getX();
//            int y = e.getY();
//          }
//        });
    }

    public void setValues(ArrayList<SepSdClassnum> pairs)
    {
        this.values=pairs;
    }
    private void addColors()
    {
        //System.out.println("enter into add colors");
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
        int leftY=0+topOffset;
        int rightX = leftX+edgeWidth;
        int rightY =leftY;
        int topX = leftX + edgeWidth/2;
        int topY = (int) (leftY - (edgeWidth / 2) * Math.sqrt(3.0));
        g2.setColor(Color.black);
        //g2.drawLine(leftX, leftY, rightX, rightY);
       // g2.drawLine(leftX, leftY,topX, topY);
       // g2.drawLine(topX, topY, rightX, rightY);

        //draw three axises in the triangle
         int middleX = leftX+edgeWidth/2;
         int middleY = (int) (leftY - (edgeWidth / 2) * (Math.sqrt(3) / 3));
         g2.setColor(Color.black);
         g2.drawLine(leftX, leftY, middleX, middleY);
         g2.drawLine(topX, topY,middleX, middleY);
         g2.drawLine(rightX, rightY, middleX, middleY);
         


         //draw the name of three axi
        g2.setColor(Color.black);
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
        g2.drawString(topAxis, topX-30, topY-20);
        g2.drawString(leftAxis, leftX-30, leftY+30);
        g2.drawString(rightAxis, rightX-20, rightY+30);

        if(!this.values.isEmpty())
        {

            if(this.paintTimes==1)  //only calculate the location at the first time calling paintComponent function
            {
            //this.clearValues();
            this.getLocationOffsetsOfValues();
            //get Locations
            int colorindex=0;
            for(int i=0; i<this.offsets.size()/4; i++)
            {
                StarPlotEntity temp = new StarPlotEntity();
                temp.setColor(this.colors.get(colorindex));
                colorindex=colorindex+1;

                int x = topX;
                int y = (int) (topY + this.offsets.get(4 * i) * (middleY-topY));
                
                this.classNumberLocatons.add(x);
                this.classNumberLocatons.add(y);

                int x1 = (int) (leftX + this.offsets.get(4 * i + 1) * (edgeWidth / 2));
                int y1 = (int) (leftY - (leftY - middleY) * this.offsets.get(4 * i + 1));

                this.sepLocations.add(x1);
                this.sepLocations.add(y1);

                int x2=(int)(rightX - this.offsets.get(4 * i + 2) * (edgeWidth / 2));
                int y2 = (int)(rightY - (rightY - middleY) * this.offsets.get(4 * i + 2));
                this.sdLocations.add(x2);
                this.sdLocations.add(y2);

                Line2D shape1 = new Line2D.Double(x,y,x1,y1);
                Line2D shape2 = new Line2D.Double(x1,y1,x2,y2);
                Line2D shape3 = new Line2D.Double(x,y,x2,y2);
                temp.setLines(shape1);
                temp.setLines(shape2);
                temp.setLines(shape3);
                temp.setNumberClass(i+2);

                this.shapes.add(temp);

            }
            }
            this.paintTimes++;

            //draw label
            g2.setColor(Color.black);
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            g2.drawString(String.format("%1$,.0f", this.maxClassnum), topX, topY-6);
            g2.drawString(String.format("%1$,.0f", this.minClassnum), middleX,middleY+16);
            g2.drawString(String.format("%1$,.2f", this.maxSep), leftX-10, leftY+16);
            g2.drawString(String.format("%1$,.2f", this.minSep), middleX-26, middleY-3);
            g2.drawString(String.format("%1$,.2f", this.minSd), rightX-10, rightY+16);
            g2.drawString(String.format("%1$,.2f", this.maxSd), middleX+3, middleY-3);
            //draw locations with lines
            //System.out.println(this.classNumberLocatons.size());
            
            for (int i =0; i<this.classNumberLocatons.size()/2; i++)
            {
                
                int x1= this.classNumberLocatons.get(2*i);
                int y1 = this.classNumberLocatons.get(2*i+1);
                int x2 = this.sepLocations.get(2*i);
                int y2 = this.sepLocations.get(2*i+1);
                int x3= this.sdLocations.get(2*i);
                int y3 = this.sdLocations.get(2*i+1);

                //g2.setStroke(new BasicStroke(8-i));
                
                g2.setColor(this.shapes.get(i).getColor());
                
                //System.out.println("current colorindex is: "+ colorindex);
                
                //g2.setColor(Color.red);
                
              
                g2.draw(this.shapes.get(i).getLines().get(0));
                //g2.drawLine(x1, y1, x2, y2);
                g2.draw(this.shapes.get(i).getLines().get(1));
                g2.draw(this.shapes.get(i).getLines().get(2));

                
                
                g2.fillOval(x1-3, y1-3, 6, 6);
                g2.fillOval(x2-3, y2-3, 6, 6);
                g2.fillOval(x3-3, y3-3, 6, 6);



            }
        }
//        for(int i=0; i<this.clickedLine.size();i++)
//        {
//            g2.setStroke(new BasicStroke(3));
//            g2.setColor(Color.cyan);
//            g2.draw(this.clickedLine.get(i));
//        }
       if(this.clickedClassnum!=0){
           ArrayList<Line2D> tempLines = this.shapes.get(this.clickedClassnum-2).getLines();

            for(int i=0;i<tempLines.size();i++)
            {
                g2.setStroke(new BasicStroke(3));
               g2.setColor(Color.cyan);
                g2.draw(tempLines.get(i));
            }
           this.clickedClassnum=0; //clear the clicked number value
        }


    }
    private void clearValues()
    {
        this.classNumberLocatons.clear();
        this.sdLocations.clear();
        this.sepLocations.clear();
        this.offsets.clear();
        this.shapes.clear();

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
       // throw new UnsupportedOperationException("Not supported yet.");
       System.out.println(e);
        int x = e.getX();
        int y = e.getY();
       //int x=e.getXOnScreen();
      // int y=e.getYOnScreen();
         this.clickedLine= getClickedLine(x, y);

        System.out.println("clicked line class number"+this.clickedClassnum);
        this.parentFrame.barchart.makeClassificationFromStarPlot(this.clickedClassnum);
        //this.clickedClassnum=0;

        repaint();
        


    }

    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
       

    }

    /**
* Returns the first line in the collection of lines that
* is close enough to where the user clicked, or null if
* no such line exists
*
*/

    public ArrayList<Line2D> getClickedLine(int x, int y) {
      // ArrayList<Line2D> clickedlines = new ArrayList<Line2D>();
    double boxX = x - HIT_BOX_SIZE / 2;
    double boxY = y - HIT_BOX_SIZE / 2;
    double width = HIT_BOX_SIZE;
    double height = HIT_BOX_SIZE;

    boolean flag=false;

    for (int i=0; i < this.shapes.size();i++) {
            ArrayList<Line2D> temp = this.shapes.get(i).getLines();
            for(int j=0; j<temp.size();j++)
            {
                Line2D line=temp.get(j);

                if(line.intersects(boxX, boxY, width, height)){
                    //flag=true;
                    this.clickedClassnum=this.shapes.get(i).getNumberClass();
                    //clickedlines.add(line);

                   // break;
                }
            }
            if(flag==true)
            {
                break;
            }
    }
    return null;

    }

    public void mouseReleased(MouseEvent e) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
