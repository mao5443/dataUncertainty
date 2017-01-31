/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

/**
 *
 * @author min
 */
public class SepSdClassnum {
    private int classnum;
    private double minCL;
    private double sumSD;
    private String classificationMethod;
    private double deviationNumOfClassMember;


    public void setdeviationNumOfClassMember(double dev)
    {
        this.deviationNumOfClassMember=dev;
    }


    public void setClassnum(int num)
    {
        this.classnum=num;
    }

    public void setMinCL(double min)
    {
        this.minCL=min;
    }

    public void setSumSD(double sum)
    {
        this.sumSD=sum;
    }

    public int getClassnum()
    {
        return this.classnum;
    }

    public double getMinCL()
    {
        return this.minCL;
    }

    public double getSumSD()
    {
        return this.sumSD;
    }
    public double getDeviationNumOfClassMember()
    {
        return this.deviationNumOfClassMember;
    }
}
