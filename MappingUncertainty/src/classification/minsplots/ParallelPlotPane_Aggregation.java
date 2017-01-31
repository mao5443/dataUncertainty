/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.minsplots;

import classification.dataHandler.AggregateCandidate;
import classification.ui.AggregationCriteria;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JPanel;

/**
 *
 * @author min
 */
public class ParallelPlotPane_Aggregation extends JPanel implements MouseListener,MouseMotionListener  {
    static final int totalLengthOfAxis = 400;
    static final int totalLengthOfGraph = 460;
    static final int leftOffset = 40;
    static final int topOffset = 40;

    ArrayList<String> criterianames ;
    ArrayList<AggregateCandidate> groups;
    AggregateCandidate minvalues;
    AggregateCandidate maxvalues;
    int data_dimension;
   
    ArrayList<Integer> criteriaaxislocations;
    ArrayList<int[]> normalizedCritriaLocationsOnAxis = new ArrayList<int[]>();

    //record if draw the candidate on the parallel plot, judege accroding to the range of two ticks on the axis
    boolean[] drawableMarks;

    //x locations of the two ticks on each axis
    ArrayList<int[]> ticksOnAxis = new ArrayList<int[]>(); //int[0] x location for the lef tick

    Color[] colors= {new Color(141,211,199), new Color(255,255,179),
                     new Color(190,186,218), new Color(251,128,114),
                     new Color(128,177,211), new Color(253, 180, 98),
                     new Color(179,222,105), new Color(252,205,229),
                     new Color(217,217,217), new Color(188,128,189),
                     new Color(204,235,197), new Color(255,237,111),
                     new Color(255,255,255), new Color(255,255,255),
                     new Color(255,255,255), new Color(255,255,255),
                     new Color(255,255,255), new Color(255,255,255)};

    public ParallelPlotPane_Aggregation(ArrayList<AggregateCandidate> groups, 
                                        ArrayList<String> dimensionName,
                                        AggregateCandidate minvalues,
                                        AggregateCandidate maxvalues,
                                        int input_data_dimension)
    {
        addMouseListener(this) ;
        addMouseMotionListener(this);

        this.criterianames=dimensionName;
        this.groups=groups;
        this.minvalues=minvalues;
        this.maxvalues=maxvalues;
        data_dimension = input_data_dimension;
 
        this.criteriaaxislocations = new ArrayList<Integer>();
        this.drawableMarks = new boolean[groups.size()];


        intializeCriteriaLocationsOnAxis();
    }

public void intializeCriteriaLocationsOnAxis()
    {
            for(int i=0; i<this.criterianames.size(); i++) //arraylist size ==axis size == criteria size
            {
                String tempname = this.criterianames.get(i);
                String dataDimension;
                if(tempname.equals("compactness"))
                    dataDimension = "";
                else 
                    dataDimension = tempname.substring(tempname.length()-1);
                int[] templocations = new int[groups.size()];
                for(int j =0; j<groups.size();j++) //int array size == cadidate size
                {
                    if(this.criterianames.get(i).equals("new CV"+dataDimension))  //the smaller the better -> more left
                    {   int k = Integer.valueOf(dataDimension)-1;
                        templocations[j]=(int) ((groups.get(j).newCV[k] - minvalues.newCV[k]) /
                                                   (maxvalues.newCV[k] - minvalues.newCV[k]) * totalLengthOfAxis);
                    }
                    else if(this.criterianames.get(i).equals("compactness"+dataDimension))  //the smaller the better, more left
                        templocations[j]=(int) ((groups.get(j).compactness - minvalues.compactness) /
                                                   (maxvalues.compactness - minvalues.compactness) * totalLengthOfAxis);
                    else if(this.criterianames.get(i).equals("similarity"+dataDimension)) //the larger the better, more left
//                        templocations[j]=((int) (totalLengthOfAxis-(groups.get(j).themeproximity[k] - proximity_min_max[0]) /
//                                                    (proximity_min_max[1] - proximity_min_max[0]) * totalLengthOfAxis));
                     {   int k = Integer.valueOf(dataDimension)-1;
                        templocations[j]=((int) ((groups.get(j).themeproximity[k] - minvalues.themeproximity[k]) /
                                                    (maxvalues.themeproximity[k] - minvalues.themeproximity[k]) * totalLengthOfAxis));
                    }
                    else if(this.criterianames.get(i).equals("bias"+dataDimension)) //the smaller the better, more left
                    {   int k = Integer.valueOf(dataDimension)-1;
                        templocations[j]=((int) ((groups.get(j).bias[k] - minvalues.bias[k]) /
                                                    (maxvalues.bias[k] - minvalues.bias[k]) * totalLengthOfAxis));
                    }
                    else if(this.criterianames.get(i).equals("intersection area")) //the larger the better, more left
                        templocations[j]=((int) (totalLengthOfAxis-(groups.get(j).spatialhierarchy - minvalues.spatialhierarchy) /
                                                    (maxvalues.spatialhierarchy - minvalues.spatialhierarchy) * totalLengthOfAxis));

                    //initialize the mark for judging if draw this candidate on the plot or not
                    if(i==0)
                        drawableMarks[j]=true;

                }
                //temp.add(templocations);
                normalizedCritriaLocationsOnAxis.add(templocations);   //the order aligns with the order of criteria name

            }


        for(int i=0; i<criterianames.size(); i++)
        {
            criteriaaxislocations.add(totalLengthOfGraph/(criterianames.size()-1)*i);
            int[] tempticklocations = new int[2];
            tempticklocations[0]=0; tempticklocations[1] = totalLengthOfAxis;
            ticksOnAxis.add(tempticklocations);
        }

    }

    private Color createColor(int val, int dimension_index)
    {
        if(dimension_index==0)  //use red for layer 1
            return new Color(255,val, val);
        else if(dimension_index==1) //blue for layer 2
            return new Color(val, val, 255);
        else
            return Color.black;
    }
    private Color createColor2(Color rgb, int dimension_index)
    {
        float[] hsb = Color.RGBtoHSB(rgb.getRGB(),rgb.getGreen(),rgb.getBlue(), null);

        return  Color.getHSBColor(hsb[0], hsb[1], (float) (hsb[2] * (1-0.1*dimension_index)));
    }

    @Override

    //parallel plot is in vertical orientation, axises are horizontal
    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        Graphics2D g2 = (Graphics2D) g;
        Stroke g2_default_stroke = g2.getStroke();

        //draw data
        
        for(int i=0; i<groups.size();i++)
         {
             if(drawableMarks[i])
             {
                 g2.setColor(createColor2(colors[i],0));
                 Stroke s = new BasicStroke(1.5f,// Width
                               BasicStroke.CAP_SQUARE,    // End cap
                               BasicStroke.JOIN_MITER,    // Join style
                               1f,                     // Miter limit
                               new float[] {2f}, // Dash pattern
                               0.0f);              // Dash phase
                     g2.setStroke(s);
                 if(this.selectedLineGroup==i)
                 {
                     g2.setColor(Color.yellow);
                     s = new BasicStroke(2.0f,// Width
                               BasicStroke.CAP_SQUARE,    // End cap
                               BasicStroke.JOIN_MITER,    // Join style
                               1f,                     // Miter limit
                               new float[] {2f}, // Dash pattern
                               0.0f);              // Dash phase
                     g2.setStroke(s);
                     //System.out.println("highlighted: "+this.selectedLineGroup);
                 }
             }else
             {
                 
                 g2.setColor(Color.darkGray);
                 Stroke s = new BasicStroke(1.5f,// Width
                               BasicStroke.CAP_SQUARE,    // End cap
                               BasicStroke.JOIN_MITER,    // Join style
                               1f,                     // Miter limit
                               new float[] {2f}, // Dash pattern
                               0.0f);              // Dash phase
                     g2.setStroke(s);
             }
            for(int j=0;j<this.criterianames.size()-1; j++)
            {
                int x1=normalizedCritriaLocationsOnAxis.get(j)[i]+leftOffset;
                int y1=criteriaaxislocations.get(j)+topOffset;
                int x2 = normalizedCritriaLocationsOnAxis.get(j+1)[i]+leftOffset;
                int y2 = criteriaaxislocations.get(j+1)+topOffset;

                g2.drawLine(x1, y1, x2, y2);
            }

         }
     
         g2.setStroke(g2_default_stroke);
         //draw axis and the name of the axis, and min/max value label

         for(int i=0; i<this.criterianames.size(); i++)
         {
            
             //draw axis
             g2.setColor(Color.cyan);
             int yOfcurrentAxis= this.topOffset+this.criteriaaxislocations.get(i);
             g2.drawLine(this.leftOffset,yOfcurrentAxis,this.leftOffset+this.totalLengthOfAxis, yOfcurrentAxis);
                //draw the name of the axis
             g2.setColor(Color.white);
             g2.setFont(g2.getFont().deriveFont(12f));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            AffineTransform fontAT = new AffineTransform();
            fontAT.rotate(90 * java.lang.Math.PI/180);
            g2.setFont(g2.getFont().deriveFont(fontAT));
            if(this.criterianames.get(i).equals("compactness"))
                g2.drawString("compact", this.leftOffset-20, yOfcurrentAxis-20);
            else 
                g2.drawString(this.criterianames.get(i), this.leftOffset-20, yOfcurrentAxis-20);
             //draw the min and max values of the axis
             fontAT = new AffineTransform();
            fontAT.rotate(0);
            g2.setFont(g2.getFont().deriveFont(fontAT));
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
            if(this.criterianames.get(i).equals("new CV1"))
            {
                g2.drawString(String.format("%1$,.3f", minvalues.newCV[0]), this.leftOffset, yOfcurrentAxis+20);
                g2.drawString(String.format("%1$,.3f", maxvalues.newCV[0]), this.leftOffset+this.totalLengthOfAxis, yOfcurrentAxis+20);
            }if(this.criterianames.get(i).equals("new CV2"))
            {
                g2.drawString(String.format("%1$,.3f", minvalues.newCV[1]), this.leftOffset, yOfcurrentAxis+20);
                g2.drawString(String.format("%1$,.3f", maxvalues.newCV[1]), this.leftOffset+this.totalLengthOfAxis, yOfcurrentAxis+20);
            }else if(this.criterianames.get(i).equals("compactness"))
            {
                g2.drawString(String.format("%1$,.3f", this.minvalues.compactness), this.leftOffset, yOfcurrentAxis+20);
                g2.drawString(String.format("%1$,.3f", this.maxvalues.compactness), this.leftOffset+this.totalLengthOfAxis, yOfcurrentAxis+20);
            }else if(this.criterianames.get(i).equals("similarity1"))
            {
                g2.drawString(String.format("%1$,.3f", minvalues.themeproximity[0]), this.leftOffset, yOfcurrentAxis+20);
                g2.drawString(String.format("%1$,.3f", maxvalues.themeproximity[0]), this.leftOffset+this.totalLengthOfAxis, yOfcurrentAxis+20);
            }else if(this.criterianames.get(i).equals("similarity2"))
			{
                g2.drawString(String.format("%1$,.3f", minvalues.themeproximity[1]), this.leftOffset, yOfcurrentAxis+20);
                g2.drawString(String.format("%1$,.3f", maxvalues.themeproximity[1]), this.leftOffset+this.totalLengthOfAxis, yOfcurrentAxis+20);
            }else if(this.criterianames.get(i).equals("bias1"))
            {
                g2.drawString(String.format("%1$,.3f", minvalues.bias[0]), this.leftOffset, yOfcurrentAxis+20);
                g2.drawString(String.format("%1$,.3f", maxvalues.bias[0]), this.leftOffset+this.totalLengthOfAxis, yOfcurrentAxis+20);
            }else if(this.criterianames.get(i).equals("bias2"))
            {
                g2.drawString(String.format("%1$,.3f", minvalues.bias[1]), this.leftOffset, yOfcurrentAxis+20);
                g2.drawString(String.format("%1$,.3f", maxvalues.bias[1]), this.leftOffset+this.totalLengthOfAxis, yOfcurrentAxis+20);
            }
            //draw two ticks on each axis
            g2.setColor(Color.cyan);
            g2.fillRect(this.ticksOnAxis.get(i)[0]+this.leftOffset, yOfcurrentAxis-6, 2, 12);
            g2.fillRect(this.ticksOnAxis.get(i)[1]+this.leftOffset, yOfcurrentAxis-6, 2, 12);
         
         }

         



    }


    String currenttick;
    int cx,cy;  //current mouse position when mousepressed
    int selectedLineGroup=-1;
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        Point p =e.getPoint();
        cx=p.x;
        cy=p.y;


        for(int i=0; i< normalizedCritriaLocationsOnAxis.get(0).length; i++) //for each group of criteries
            for(int j =0; j < criteriaaxislocations.size()-1; j++) //for each line segment between two axises
            {
                int linex1 = normalizedCritriaLocationsOnAxis.get(j)[i]+this.leftOffset;
                int liney1 = criteriaaxislocations.get(j)+this.topOffset;
                int linex2 = normalizedCritriaLocationsOnAxis.get(j+1)[i]+this.leftOffset;
                int liney2 = this.criteriaaxislocations.get(j+1)+this.topOffset;

                boolean hit_line = false;
                if(linex2 == linex1)
                {
                    if(cy>= liney1 && cy<=liney2 && cx==linex1)
                        hit_line = true;
                }else
                {
                    //calculate line equation, two-point, (x-x1)/(x2-x1)=(y-y1)/(y2-y1)
                    float left = ((float)(cx-linex1))/((float)(linex2-linex1));
                    float right = ((float)(cy-liney1))/((float)(liney2-liney1));

                    if((left >= (right-0.01) && left <= (right+0.01)) ||
                            (right >= (left -0.01) && right <= (left+0.01)))
                            hit_line = true;
                }

                if(hit_line)
                {
                    AggregationCriteria criteriawin = (AggregationCriteria)this.getParent().getParent().getParent().getParent().getParent();
                    criteriawin.setSelectedCandidateAggreGroup(i);
                    this.selectedLineGroup =i;
                    repaint();
                    break;
                }
            }

    }

    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        Point p =e.getPoint();
        cx=p.x;
        cy=p.y;

        for(int i=0; i<this.ticksOnAxis.size();i++)
            for(int j=0; j<this.ticksOnAxis.get(i).length;j++)
            {
                if(cx<=this.ticksOnAxis.get(i)[j]+this.leftOffset + 2 && cx>= this.ticksOnAxis.get(i)[j]+this.leftOffset )
                    if(cy<=this.topOffset+this.criteriaaxislocations.get(i)+6 && cy>= this.topOffset+this.criteriaaxislocations.get(i)-6)
                        currenttick= Integer.toString(i)+"_"+Integer.toString(j);
            }
      //  System.out.println("current tick is: "+currenttick);
    }

    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
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
        //ticks is now allowed to move on the y direction

        int tickaxis = Integer.valueOf(currenttick.split("_")[0]);
        int tickorder = Integer.valueOf(currenttick.split("_")[1]);
        if(cx > dx)
                this.ticksOnAxis.get(tickaxis)[tickorder] -= (cx - dx)/10;
        else
                this.ticksOnAxis.get(tickaxis)[tickorder] += (dx - cx)/10 ;
        cx = this.ticksOnAxis.get(tickaxis)[tickorder]+leftOffset;
        //System.out.println("current line x is: "+ cx);

   
            for(int i=0; i<normalizedCritriaLocationsOnAxis.get(0).length;i++)
            {
                drawableMarks[i]=true;
                for(int j=0; j<normalizedCritriaLocationsOnAxis.size();j++)
                    if(normalizedCritriaLocationsOnAxis.get(j)[i]<ticksOnAxis.get(j)[0] ||
                           normalizedCritriaLocationsOnAxis.get(j)[i]>ticksOnAxis.get(j)[1])
                    {
                        drawableMarks[i]=false;
                        break;
                    }
            }

        repaint();
    }

    public void mouseMoved(MouseEvent e) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

}
