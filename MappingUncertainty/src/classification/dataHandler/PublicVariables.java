/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

/**
 *
 * @author cisc
 */
public class PublicVariables {

    private String SelectedVariable;
    private String SelectedError;
    private double intersectThreshold;  //threshold for calculating the intersection point between each pair of estimates
    private String selectedMappingMethod;
    private int selectedClassNum;
    private int highlightedItemIndex;  //data linking, highlighted item

    public void sethighlightedItemIndex(int i)
    {
        this.highlightedItemIndex = i;
    }

    public int gethighlightedItemIndex()
    {
        return this.highlightedItemIndex;
    }


    public void setSelectedVariable(String name)
    {
        this.SelectedVariable = name;
    }

    public String getSelectedVariable()
    {
        return this.SelectedVariable;
    }


    public void setSelectedMethod(String name)
    {
        this.selectedMappingMethod = name;
    }

    public String getSelectedMethod()
    {
        return this.selectedMappingMethod;
    }

     public void setSelectedClassNum(int a)
    {
        this.selectedClassNum = a;
    }

    public int getSelectedClassNum()
    {
        return this.selectedClassNum;
    }

    public void setSelectedError(String name)
    {
        this.SelectedError = name;
    }

    public String getSelectedError()
    {
        return this.SelectedError;
    }

    public void setintersectThreshold(double value)
    {
        this.intersectThreshold = value;
    }

    public double getintersectThreshold()
    {
        return this.intersectThreshold;
    }

}
