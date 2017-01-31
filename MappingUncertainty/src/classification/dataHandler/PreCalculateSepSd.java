/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

import java.util.ArrayList;
import org.geotools.data.simple.SimpleFeatureCollection;

/**
 *
 * @author min
 */
public class PreCalculateSepSd {
    private int maxClassnum;
    private int minClassnum;
    private ArrayList<SepSdClassnum> sepSDpair = new ArrayList<SepSdClassnum>();
    
    private ArrayList<DataTable> data;
    private ProbabilityMatrix matrix;
    private String selectedClassificationMethod;
    private SimpleFeatureCollection collection;
    private String selectedFieldName;

    public PreCalculateSepSd(String method, SimpleFeatureCollection collection,  String fieldName,ArrayList<DataTable> data, ProbabilityMatrix matrix)
    {
        this.data=data;
        this.matrix = matrix;
        this.selectedClassificationMethod=method;
        this.collection=collection;
        this.selectedFieldName=fieldName;
    }

    public void calculateAllowedClassnum()
    {
        this.minClassnum=2;

        int unitNum= this.data.size();
        if(unitNum/4<9)
        {
            this.maxClassnum = unitNum/4;
        }else
        {
            this.maxClassnum=9;
        }
    }
    
    public void updateSepSdPairs(ArrayList<Domain> currentbreakpoint, int classNum)
    {
        SepSdClassnum current = this.sepSDpair.get(classNum-2);
        current.setMinCL(1.0);
        current.setSumSD(0.0);
        
        ArrayList<Double> numOfClassMembers = new ArrayList<Double>();
        for(int j = 0; j< currentbreakpoint.size()+1;j++)
        {
            ArrayList<Double> members = new ArrayList<Double>();
            members=getClassMembers(j, currentbreakpoint);
            double sd=InnerSD.sdKnuth(members);
            if(j!=currentbreakpoint.size())
            {
                if(current.getMinCL()>currentbreakpoint.get(j).getCL())
                {
                    current.setMinCL(currentbreakpoint.get(j).getCL());
                }
            }
            current.setSumSD(current.getSumSD()+sd);
//                    if(sd> current.getMaxSD())
//                    {
//                        current.setMaxSD(sd);
//                    }
            numOfClassMembers.add((double)members.size());
        }
        current.setSumSD(current.getSumSD()/current.getClassnum());
        current.setdeviationNumOfClassMember(InnerSD.sdKnuth(numOfClassMembers));
        
    }

    public void calculateSepSdPairs()
    {
        ArrayList<Domain> breakpoint = new ArrayList<Domain>();
        int currentClassnum;

        Classification classifier = new Classification();
        calculateAllowedClassnum();
        if(this.selectedClassificationMethod == "Class Separability") {
            int domainLen = this.data.size()-1;
            breakpoint = ProCalculator.getBreakpoints(breakpoint, 0, domainLen, this.matrix);
            Utils.sortSmalltoLarge(breakpoint);

            for(int i = this.minClassnum-1; i< this.maxClassnum;i++)
            {
                SepSdClassnum current = new SepSdClassnum();
                current.setClassnum(i+1);
                ArrayList<Domain> currentbreakpoint = new ArrayList<Domain>();
                current.setMinCL(1.0);
                current.setSumSD(0.0);

                for(int j = breakpoint.size()-1; j>breakpoint.size()-1-i; j--)
                {

                    
                    /***calculate the break point value**/
                    int id1 = breakpoint.get(j).getLowestCLLeftID();
                    int id2 = breakpoint.get(j).getLowestCLRightID();
                    double est1 = this.data.get(id1).getEstimate().get(0);
                    double est2 = this.data.get(id2).getEstimate().get(0);
                    double moe2 = this.data.get(id2).getMoe().get(0);
                    double moe1 = this.data.get(id1).getMoe().get(0);
                    double point = ProCalculator.getInstersction(0.0, est1, moe1, est2, moe2);
                    breakpoint.get(j).setIntersectPoint(point);
                    /**********/
                    currentbreakpoint.add(breakpoint.get(j));
                    if(current.getMinCL()>breakpoint.get(j).getCL())
                    {
                        current.setMinCL(breakpoint.get(j).getCL());
                    }
                }

                Utils.sortSmalltoLargeAsPointValue(currentbreakpoint); //sort breakpoint again as estiamte value ascendingly
                ArrayList<Double> numOfClassMembers = new ArrayList<Double>();  //store the number of observations in each class, for calcualting deviation of observation numbers
                int classSDnotZero=0;
                for(int j = 0; j< currentbreakpoint.size()+1;j++)
                {
                    ArrayList<Double> members = new ArrayList<Double>();
                    members=getClassMembers(j, currentbreakpoint);
                    double sd=InnerSD.sdKnuth(members);
                    if(sd!=0)
                        classSDnotZero++;
                    current.setSumSD(current.getSumSD()+sd);
//                    if(sd> current.getMaxSD())
//                    {
//                        current.setMaxSD(sd);
//                    }

                    numOfClassMembers.add((double)members.size());
                }
                current.setSumSD(current.getSumSD()/classSDnotZero);
               // System.out.println("current class number is: "+ i+ "; average of SD is: "+ current.getSumSD());
                current.setdeviationNumOfClassMember(InnerSD.sdKnuth(numOfClassMembers));
                System.out.println("current class number is: "+ i+ "; average of SD is: "+ current.getSumSD()+"; equality is: "+current.getDeviationNumOfClassMember());

                this.sepSDpair.add(current);
                
                
            }

        }else if (this.selectedClassificationMethod == "Jenk's Natural Break") {
            for(int i = this.minClassnum-1; i< this.maxClassnum;i++)
            {
                SepSdClassnum current = new SepSdClassnum();
                current.setClassnum(i+1);
                ArrayList<Domain> currentbreakpoint = new ArrayList<Domain>();
                current.setMinCL(1.0);
                current.setSumSD(0.0);
                currentbreakpoint=classifier.JenksNaturalBreaks(this.collection,i+1,this.selectedFieldName,this.data, this.matrix);
                ArrayList<Double> numOfClassMembers = new ArrayList<Double>();
                for(int j = 0; j< currentbreakpoint.size()+1;j++)
                {
                    ArrayList<Double> members = new ArrayList<Double>();
                    members=getClassMembers(j, currentbreakpoint);
                    double sd=InnerSD.sdKnuth(members);
                    if(j!=currentbreakpoint.size())
                    {
                        if(current.getMinCL()>currentbreakpoint.get(j).getCL())
                        {
                            current.setMinCL(currentbreakpoint.get(j).getCL());
                        }
                    }
                    current.setSumSD(current.getSumSD()+sd);
                
//                    if(sd> current.getMaxSD())
//                    {
//                        current.setMaxSD(sd);
//                    }
                    numOfClassMembers.add((double)members.size());
                }

                current.setSumSD(current.getSumSD()/current.getClassnum());
                System.out.println("current class number is: "+ i+ "; average of SD is: "+ current.getSumSD());
                current.setdeviationNumOfClassMember(InnerSD.sdKnuth(numOfClassMembers));
                 this.sepSDpair.add(current);

            }
        }else if (this.selectedClassificationMethod == "Quantile") {
             for(int i = this.minClassnum-1; i< this.maxClassnum;i++)
            {
                SepSdClassnum current = new SepSdClassnum();
                current.setClassnum(i+1);
                ArrayList<Domain> currentbreakpoint = new ArrayList<Domain>();
                current.setMinCL(1.0);
                current.setSumSD(0.0);
                currentbreakpoint=classifier.Quantile(this.collection,i+1,this.selectedFieldName,this.data, this.matrix);

                ArrayList<Double> numOfClassMembers = new ArrayList<Double>();
                for(int j = 0; j< currentbreakpoint.size()+1;j++)
                {
                    ArrayList<Double> members = new ArrayList<Double>();
                    members=getClassMembers(j, currentbreakpoint);
                    double sd=InnerSD.sdKnuth(members);
                    if(j!=currentbreakpoint.size())
                    {
                        if(current.getMinCL()>currentbreakpoint.get(j).getCL())
                        {
                            current.setMinCL(currentbreakpoint.get(j).getCL());
                        }
                    }
                    current.setSumSD(current.getSumSD()+sd);
//                    if(sd> current.getMaxSD())
//                    {
//                        current.setMaxSD(sd);
//                    }
                    numOfClassMembers.add((double)members.size());
                }
                current.setSumSD(current.getSumSD()/current.getClassnum());
                System.out.println("current class number is: "+ i+ "; average of SD is: "+ current.getSumSD());
                current.setdeviationNumOfClassMember(InnerSD.sdKnuth(numOfClassMembers));
                 this.sepSDpair.add(current);

            }
        }else if (this.selectedClassificationMethod == "Equal Interval") {
            for(int i = this.minClassnum-1; i< this.maxClassnum;i++)
            {
                SepSdClassnum current = new SepSdClassnum();
                current.setClassnum(i+1);
                ArrayList<Domain> currentbreakpoint = new ArrayList<Domain>();
                current.setMinCL(1.0);
                current.setSumSD(0.0);
                currentbreakpoint=classifier.EqualInterval(this.collection,i+1,this.selectedFieldName,this.data, this.matrix);

                ArrayList<Double> numOfClassMembers = new ArrayList<Double>();
                for(int j = 0; j< currentbreakpoint.size()+1;j++)
                {
                    ArrayList<Double> members = new ArrayList<Double>();
                    members=getClassMembers(j, currentbreakpoint);
                    double sd=InnerSD.sdKnuth(members);
                    if(j!=currentbreakpoint.size())
                    {
                        if(current.getMinCL()>currentbreakpoint.get(j).getCL())
                        {
                            current.setMinCL(currentbreakpoint.get(j).getCL());
                        }
                    }
                    current.setSumSD(current.getSumSD()+sd);
//                    if(sd> current.getMaxSD())
//                    {
//                        current.setMaxSD(sd);
//                    }
                    numOfClassMembers.add((double)members.size());
                }
                current.setSumSD(current.getSumSD()/current.getClassnum());
                System.out.println("current class number is: "+ i+ "; average of SD is: "+ current.getSumSD());
                current.setdeviationNumOfClassMember(InnerSD.sdKnuth(numOfClassMembers));
                 this.sepSDpair.add(current);

            }
        }else if (this.selectedClassificationMethod == "Standard Deviation")
        {
            for(int i = this.minClassnum-1; i< this.maxClassnum;i++)
            {
                SepSdClassnum current = new SepSdClassnum();
                current.setClassnum(i+1);
                ArrayList<Domain> currentbreakpoint = new ArrayList<Domain>();
                current.setMinCL(1.0);
                current.setSumSD(0.0);
                currentbreakpoint=classifier.StandardDeviation(this.collection,i+1,this.selectedFieldName,this.data, this.matrix);

                ArrayList<Double> numOfClassMembers = new ArrayList<Double>();
                for(int j = 0; j< currentbreakpoint.size()+1;j++)
                {
                    ArrayList<Double> members = new ArrayList<Double>();
                    members=getClassMembers(j, currentbreakpoint);
                    double sd=InnerSD.sdKnuth(members);
                    if(j!=currentbreakpoint.size())
                    {
                        if(current.getMinCL()>currentbreakpoint.get(j).getCL())
                        {
                            current.setMinCL(currentbreakpoint.get(j).getCL());
                        }
                    }
                    current.setSumSD(current.getSumSD()+sd);
//                    if(sd> current.getMaxSD())
//                    {
//                        current.setMaxSD(sd);
//                    }
                    numOfClassMembers.add((double)members.size());
                }
                current.setSumSD(current.getSumSD()/current.getClassnum());
                System.out.println("current class number is: "+ i+ "; average of SD is: "+ current.getSumSD());
                current.setdeviationNumOfClassMember(InnerSD.sdKnuth(numOfClassMembers));
                 this.sepSDpair.add(current);

            }
        }
    }

    private ArrayList<Double> getClassMembers(int classindex, ArrayList<Domain> breakpoint)
    {
        ArrayList<Double> classMembers = new ArrayList<Double>();
            if(classindex==breakpoint.size())
            {

               for(int i = breakpoint.get(classindex-1).getRightID(); i<=this.data.size()-1;i++)
                {
                    double value = this.data.get(i).getEstimate().get(0);
                    classMembers.add(value);
                }

            }else if(classindex==0)  //for class 0
            {
                for(int i = 0; i<=breakpoint.get(classindex).getLeftID();i++)
                {
                    double value = this.data.get(i).getEstimate().get(0);
                    classMembers.add(value);
                }
                

            }else
            {
                for(int i = breakpoint.get(classindex-1).getRightID(); i<=breakpoint.get(classindex).getLeftID();i++)
                {
                    double value = this.data.get(i).getEstimate().get(0);
                    classMembers.add(value);
                }
            }

        return classMembers;


    }

    public ArrayList<SepSdClassnum> getSepSdClassnumPair()
    {
        return this.sepSDpair;
    }
    

}
