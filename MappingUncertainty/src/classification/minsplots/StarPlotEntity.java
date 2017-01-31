/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.minsplots;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *
 * @author min
 */


public class StarPlotEntity {
    private ArrayList<Line2D> lines = new ArrayList<Line2D>();
    private int numClass;
    private Color color;

    public void setLines(Line2D line)
    {
        this.lines.add(line);
    }

    public void setNumberClass(int n)
    {
        this.numClass=n;
    }

    public void setColor(Color c)
    {
        this.color = c;
    }
    public ArrayList<Line2D> getLines()
    {
        return this.lines;
    }
    public int getNumberClass()
    {
        return this.numClass;
    }
    public Color getColor()
    {
        return this.color;
    }
}
