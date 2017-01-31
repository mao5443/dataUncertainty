/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.mapping;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.text.DecimalFormat;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.geotools.legend.DefaultGlyphFactory;
import org.geotools.legend.Drawer;
import org.geotools.legend.GlyphFactory;
import org.geotools.map.MapContext;
import org.geotools.styling.Style;
import org.opengis.geometry.Geometry;
/**
 *
 * @author cisc
 */
public class MapLegend extends JPanel {
    private Color[] colors;
    private int classnum;
    private double[] CL;
    private double[] mins;
    private double[] maxs;

    private static final int legendWidth = 30;
    private static final int legendHeight = 28;
    private static final int leftOffset = 4;
    private static final int topOffset = 30;
    private static final int intervalOffset = 0;
    private Dimension size;
    private boolean ifhasmissingvalue =false;
    //for draw the interval between classes in the legend
    private int[] CLOrder;
    int classIntervalWidthOffset=30;
    int classIntervalColorOffset=160;



    public MapLegend(Color[] colors, int classnum, double[] CL, double[] mins, double[] maxs, Dimension size, int missvaluenumber)
    {
        if(missvaluenumber>0)
        {
            this.ifhasmissingvalue=true;
            this.colors=new Color[colors.length-1];
            for(int i=0; i<this.colors.length;i++)
                this.colors[i]=colors[i+1];
        }else
        {
            this.colors=colors;
            this.ifhasmissingvalue=false;
        }
        this.classnum= classnum;
        this.CL = CL;
        this.mins = mins;
        this.maxs = maxs;
        this.size = size;
        
        this.CLOrder =new int[classnum];
        getCLOrder(classnum);
        
    }

    private void getCLOrder(int num)
    {
        for(int i=0; i<num;i++ )
        {
            this.CLOrder[i]=i;
        }
        for(int i=1; i<num; i++)
            for(int j=i+1; j<num; j++)
            {
                if(this.CL[i-1]>this.CL[j-1])
                {
                    int temp=this.CLOrder[i];
                    this.CLOrder[i]=this.CLOrder[j];
                    this.CLOrder[j]=temp;
                }

            }

    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.white);
        g2.setColor(Color.white);
       g2.fillRect(0, 0, this.size.width, this.size.height);
       //draw the title of the legend
       g2.setColor(Color.black);
        g2.setFont(g2.getFont().deriveFont(14f));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
        //g2.drawString("Median Household Income ($)", leftOffset, topOffset - 20);
       //draw title of CL
       g2.setColor(Color.black);
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
        g2.drawString("CL", leftOffset+6, topOffset - 2);

        //draw title of estimate
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.drawString("Estimate", leftOffset+56+legendWidth+6, topOffset - 2);

        //draw legend without interval between classes to show separability
        for(int i = 0; i < this.classnum; i++)
        {
            Color current = colors[i];
            g2.setColor(current);
            g2.fillRect(leftOffset+56, topOffset+i*(legendHeight+intervalOffset), legendWidth, legendHeight);
            g2.setColor(Color.black);
            //g2.setStroke(null);
            g2.drawRect(leftOffset+56, topOffset+i*(legendHeight+intervalOffset), legendWidth, legendHeight);

            //draw text--------------------
            // draw confidence level
            if(i == this.classnum-1)
            {
            }else
            {
                DecimalFormat fmt = new DecimalFormat("0.0");
                String temp1 = fmt.format(CL[i]*100)+"%";

                g2.setFont(g2.getFont().deriveFont(12f));
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
                g2.drawString(temp1, leftOffset + 10, topOffset+(legendHeight-5)+12+i*(legendHeight+intervalOffset));

                //draw the samll tick marker for CL
                g2.setColor(Color.black);
                g2.drawLine(leftOffset+52, topOffset+(i+1)*(legendHeight+intervalOffset), leftOffset+56, topOffset+(i+1)*(legendHeight+intervalOffset));
            }
            //draw label
                DecimalFormat fmt = new DecimalFormat("#,###");
                String temp2 = fmt.format(mins[i])+ " - " + fmt.format(maxs[i]);

                g2.setFont(g2.getFont().deriveFont(12f));
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
                g2.drawString(temp2, leftOffset+56+legendWidth+6, topOffset+18+i*(legendHeight+intervalOffset));

            //-----------------------
        }
        if(this.ifhasmissingvalue)
        {
            g2.setColor(new Color(230,230,230));
            g2.fillRect(leftOffset+56, topOffset+this.classnum*(legendHeight+intervalOffset), legendWidth, legendHeight);
            g2.setColor(Color.black);
            //g2.setStroke(null);
            g2.drawRect(leftOffset+56, topOffset+this.classnum*(legendHeight+intervalOffset), legendWidth, legendHeight);

            g2.setFont(g2.getFont().deriveFont(12f));
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
            g2.drawString("missing value", leftOffset+56+legendWidth+6, topOffset+18+this.classnum*(legendHeight+intervalOffset));
        }

//        /*draw legend with interval between classes to show separability*/
//        int intervalsum=(0+this.classnum-1)*this.classnum/2;
//        int previousInterval =0;
//        for(int i = 0; i < this.classnum; i++)
//        {
//            //draw interval between classes
//            int intervalHeight = this.classIntervalWidthOffset*this.CLOrder[i]/intervalsum;
//            int colorvalue = this.classIntervalColorOffset*(this.classnum-1-this.CLOrder[i])/(2*intervalsum);
//            Color intervalColor = new Color(colorvalue,colorvalue,colorvalue);
//            g2.setColor(intervalColor);
//            g2.fillRect(leftOffset+56, topOffset+i*(legendHeight+intervalOffset)+previousInterval, legendWidth, intervalHeight);
//
//            ////////////////////////////////////
//            Color current = colors[i];
//            g2.setColor(current);
//            g2.fillRect(leftOffset+56, topOffset+i*(legendHeight+intervalOffset)+intervalHeight+previousInterval, legendWidth, legendHeight);
//            g2.setColor(Color.black);
//            //g2.setStroke(null);
//            g2.drawRect(leftOffset+56, topOffset+i*(legendHeight+intervalOffset)+intervalHeight+previousInterval, legendWidth-1, legendHeight-1);
//            previousInterval=intervalHeight;
//            //draw text--------------------
//            // draw confidence level
//            if(i == this.classnum-1)
//            {
//            }else
//            {
//                DecimalFormat fmt = new DecimalFormat("0.00");
//                String temp1 = fmt.format(CL[i]*100)+"%";
//
//                g2.setFont(g2.getFont().deriveFont(12f));
//                g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
//                g2.drawString(temp1, leftOffset, topOffset+(legendHeight-5)+12+i*(legendHeight+intervalOffset));
//
//              }
//            //draw label
//                DecimalFormat fmt = new DecimalFormat("0.0");
//                String temp2 = fmt.format(mins[i])+ " - " + fmt.format(maxs[i]);
//
//                g2.setFont(g2.getFont().deriveFont(12f));
//                g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
//                g2.drawString(temp2, leftOffset+56+legendWidth+6, topOffset+18+i*(legendHeight+intervalOffset));
//
//            //-----------------------
//        }
        
  }

     public void getLegend(MapContext map)
    {
      // DefaultGlyphFactory test = DefaultGlyphFactory;
    // Icon aa = test.icon(map.getLayer(0));
    // int bb = 0;

    }

}
