/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AggregationCriteria.java
 *
 * Created on Mar 7, 2013, 12:17:34 PM
 */

package classification.ui;

import aggregation.spatialcluster.CriterionCalculators;
import classification.dataHandler.AggregateCandidate;
import classification.dataHandler.AggregationAttributes;
import classification.dataHandler.NewEstimateCalculator;
import classification.dataHandler.ProCalculator;
import classification.dataHandler.Utils;
import classification.minsplots.ParallelPlotPane_Aggregation;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math.MathException;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
/**
 *
 * @author cisc
 */
public class AggregationCriteria extends javax.swing.JInternalFrame {
    private Root parentFrame;
    private int optionalCriteriaNum = 0;
    int[][] adjectationMatrix;


    /** Creates new form AggregationCriteria */
    public AggregationCriteria(Root root) {
        initComponents();
        parentFrame=root;
        this.jCheckBox1.setSelected(true);
        this.jCheckBox2.setSelected(true);
        this.optionalCriteriaNum=2;
    }

    public void initializeSpatialWeightMatrix() throws IOException
    {
        SimpleFeatureCollection collection = (SimpleFeatureCollection) this.parentFrame.data_aggregation.collection_aggregation.get(0);
        int featuresize = collection.size();
        this.adjectationMatrix= new int[featuresize][featuresize];

        ArrayList<SimpleFeature> featurelist = new ArrayList<SimpleFeature>();
        SimpleFeatureIterator iter=collection.features();
        while(iter.hasNext()){
            featurelist.add(iter.next());
        }

        /*******create a half spatial weight matrix************/
        for(int i=0; i<featuresize;i++)
        {
             SimpleFeature fi= featurelist.get(i);
            for(int j=i+1; j<featuresize;j++)
            {
                SimpleFeature fj= featurelist.get(j);
                if(CriterionCalculators.ifPairContiguity(fi, fj))
                {
                    this.adjectationMatrix[i][j]=0;
                }else
                    this.adjectationMatrix[i][j]=1;
            }
        }
    }

    private SimpleFeature getFeatureById(SimpleFeatureSource featureSource, int FID) throws IOException
    {

            FilterFactory filterFactory = (FilterFactory) CommonFactoryFinder.getFilterFactory(null);
           // String filename= featureSource.getName().toString().split(":")[2];
            String filename= featureSource.getName().toString();
            String GID2 = filename+"."+FID;
            Set<FeatureId> IDs = new HashSet<FeatureId>();
            FeatureId id = new FeatureIdImpl(GID2);
            IDs.add(id);
            Filter filter = filterFactory.id(IDs);

            SimpleFeatureCollection features = featureSource.getFeatures(filter);
            SimpleFeatureIterator iter=features.features();
            SimpleFeature f = null;
            while(iter.hasNext()){
                f=iter.next();
            }
            return f;
    }
    
    private ArrayList<SimpleFeature> getFirstOrderNeighbours(int id, SimpleFeatureSource source) throws IOException
    {
        //id in matrix is 1 smaller than the fid in featurecollection
        ArrayList<SimpleFeature> neighbours = new ArrayList<SimpleFeature>();
        for(int i=0; i<id;i++)
        {
            if(this.adjectationMatrix[i][id]==1)
            {
                SimpleFeature currentneighbour = getFeatureById(source,(i+1));
                neighbours.add(currentneighbour);
            }

        }
        for(int j=id+1; j<source.getFeatures().size();j++)
        {
            if(this.adjectationMatrix[id][j]==1)
            {
                SimpleFeature currentneighbour = getFeatureById(source,(j+1));
                neighbours.add(currentneighbour);
            }
        }
        return neighbours;

    }


    AggregateCandidate minvalues = new AggregateCandidate();
    AggregateCandidate maxvalues = new AggregateCandidate();
    ArrayList<String> selectedCriteriaNames = new ArrayList<String>();
    ArrayList<AggregateCandidate> aggreGroupList = new ArrayList<AggregateCandidate>();   //this is the list where store the aggregation candidate
    AggregateCandidate selectedAggreGroup;  //aggregation combine group that is selected by user
    
    public void setSelectedCandidateAggreGroup(int id)
    {
        selectedAggreGroup = aggreGroupList.get(id);
        String infoStr = "new CV: ";
        for(int k=0; k<parentFrame.input_data_dimension; k++)
            infoStr =  infoStr + String.format("%1$,.4f", selectedAggreGroup.newCV[k])+ "; ";
        jLabel1.setText(infoStr);

        if(jCheckBox2.isSelected())
        {
            infoStr = "attribute similarity: ";
            for(int k=0; k<parentFrame.input_data_dimension; k++)
                infoStr = infoStr + String.format("%1$,.2f", selectedAggreGroup.themeproximity[k])+ ";";
            jLabel4.setText(infoStr);
        }
        if(jCheckBox1.isSelected())
        {
            infoStr = "compactness: " + String.format("%1$,.2f", selectedAggreGroup.compactness);
            jLabel2.setText(infoStr);
        }
        if(jCheckBox3.isSelected())
        {
            /*******for spatial hierachy*****/
            //infoStr = "intersection area: " + String.format("%1$,.2f", selectedAggreGroup.spatialhierarchy) + "%";
            /***********for bias*******/
            infoStr = "Bias: ";
            for(int k=0; k<parentFrame.input_data_dimension; k++)
                infoStr = infoStr + String.format("%1$,.2f", selectedAggreGroup.bias[k])+ ";";
            
            jLabel3.setText(infoStr);
            
        }
        highlightAggreCandidateInMapTable();
        parentFrame.aggregationcontrolwin.updateSeedColumn(selectedAggreGroup);
    }

    public void highlightAggreCandidateInMapTable()
    {
        for(int k=0; k<parentFrame.input_data_dimension; k++)
        {
            Set<FeatureId> IDs = new HashSet<FeatureId>();
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
            for(int i=0; i<this.selectedAggreGroup.id.size(); i++)
            {
                String tempid = parentFrame.data_aggregation.layername.get(k)+"."+selectedAggreGroup.id.get(i);
                FeatureId tempfeatureid = (ff.featureId(tempid));
                IDs.add(tempfeatureid);
            }
            parentFrame.aggregationwin.displaySelectedFeatures(IDs,k);
        }
//        Set<FeatureId> IDs = new HashSet<FeatureId>();
//        for(int i=0; i<this.selectedAggreGroup.feature.size(); i++)
//        {
//            IDs.add(this.selectedAggreGroup.feature.get(i).getIdentifier());
//        }
//         parentFrame.aggregationwin.displaySelectedFeatures(IDs,0);

    }

    private int getFIDfromIdentifier(String id, int index)
    {
        String layername = parentFrame.data_aggregation.layername.get(index);
        id = id.substring(layername.length()+1);
        return Integer.parseInt(id);
    }

    private ArrayList<ArrayList<AggregationAttributes>> temp_data = new ArrayList<ArrayList<AggregationAttributes>>();
    public void intializeAggregateCandidate(int FID, String[] usedUnits) throws CQLException, IOException, MathException
    {
        
         for (int i =0; i<parentFrame.attridata.size(); i++)
         {
             ArrayList<AggregationAttributes> temp = (ArrayList<AggregationAttributes>) parentFrame.attridata.get(i).clone();
             Utils.sortSmalltoLargeAggregationAttributes(temp, "FID");
             temp_data.add(temp);
         }

        String usedUnitsStr = ";";
        for(int i=0; i<usedUnits.length; i++)
            if(usedUnits[i]!=null)
                usedUnitsStr = usedUnitsStr + usedUnits[i] +";";

        ArrayList<AggregateCandidate> aggreGroupList1 = new ArrayList<AggregateCandidate>();
        ArrayList<AggregateCandidate> finalAggreGroupList = new ArrayList<AggregateCandidate>();
        minvalues.compactness=10000;
        maxvalues.compactness = 0;
        minvalues.spatialhierarchy=10000;
        maxvalues.spatialhierarchy =0;
        for(int i=0; i<parentFrame.input_data_dimension; i++)
        {
            minvalues.themeproximity[i]=10000;
            minvalues.newCV[i]=10000;
            minvalues.bias[i] = 10000;
            
            maxvalues.themeproximity[i]=0;
            maxvalues.newCV[i]=0;
            maxvalues.bias[i] = 0;
        }
        

//        ArrayList<String> estName = parentFrame.data_aggregation.selectedVar_aggregation;
//        ArrayList<String> errorName = parentFrame.data_aggregation.selectedError_aggregation;
//        String varType = parentFrame.data_aggregation.selectedVar_aggregationType;
  
       /* step 1: get the original unit and get first order neighborhoods of it, put all neightbours into aggreGroupList*/

//            //selecte feauture by id////////////////////////
            aggreGroupList.clear();
            SimpleFeatureSource featureSource=(SimpleFeatureSource) parentFrame.data_aggregation.source_aggregation.get(0);
            SimpleFeature f = getFeatureById(featureSource, FID);
            int f_id = getFIDfromIdentifier(f.getID(), 0);
            double[] estimateOriginal= new double[2];
            double[] errorOriginal= new double[2];
            for(int k=0; k<parentFrame.input_data_dimension; k++)
            {
                estimateOriginal[k] = temp_data.get(k).get(f_id -1).oldest;
                errorOriginal[k] = temp_data.get(k).get(f_id-1).olderror;
            }
            ArrayList<SimpleFeature> firstOrderNeighbor = getFirstOrderNeighbours((FID-1),
                                                          (SimpleFeatureSource) parentFrame.data_aggregation.source_aggregation.get(0));
           
            /***********************/
            /*step 2: go though all the polygons on the first-order circle, get the new CV and store them to a data structure named aggreGroupList
             sort the list
             step 3: find the polygon with the smallest new CV, chech if the new CV smaller user's acceptance level*/
// for one variable
//            for(int i=0; i<firstOrderNeighbor.size(); i++)
//            {
//                SimpleFeature current = firstOrderNeighbor.get(i);
//                System.out.println("the first order neighs are: " + current.getID());
//                double estimate=this.getValueFromAttribute(varType, estName.get(0), current);
//                double error=this.getValueFromAttribute(varType, errorName.get(0), current);
//
//                ArrayList<Double> oldEstimate = new ArrayList<Double>();
//                ArrayList<Double> oldError = new ArrayList<Double>();
//                oldEstimate.add(estimateOriginal);
//                oldEstimate.add(estimate);
//                oldError.add(errorOriginal);
//                oldError.add(error);
//                double[] newEstError = NewEstimateCalculator.NewErrorCountData(oldEstimate, oldError);
//                double newCV = newEstError[1]/1.645/newEstError[0];
//                AggregateCandidate currentPolygon = new AggregateCandidate();
//                currentPolygon.newCV=newCV;
//                currentPolygon.newEstimate=newEstError[0];
//                currentPolygon.newError=newEstError[1];
//                currentPolygon.feature.add(f);
//                currentPolygon.id.add(FID);
//                currentPolygon.feature.add(current);
//                String current_id =
//                           current.getID().substring(parentFrame.data_aggregation.layername.get(0).length()+1);
//                currentPolygon.id.add(Integer.valueOf(current_id));
//                if(!usedUnitsStr.contains(";"+current_id +";"))
//                    currentPolygon.orginalid = FID;
//                    aggreGroupList.add(currentPolygon);
//            }

            for(int i=0; i<firstOrderNeighbor.size(); i++)
            {
                AggregateCandidate currentPolygon = new AggregateCandidate();
                SimpleFeature current = firstOrderNeighbor.get(i);
                int current_id = getFIDfromIdentifier(current.getID(), 0);
                //flag if any new cv larger than thresholds, false == has cv larger than threshold
                boolean new_cv_flag = true;
                for(int k=0; k<parentFrame.input_data_dimension; k++)
                {
                    double estimate=temp_data.get(k).get(current_id-1).oldest;
                    double error=temp_data.get(k).get(current_id-1).olderror;

                    ArrayList<Double> oldEstimate = new ArrayList<Double>();
                    ArrayList<Double> oldError = new ArrayList<Double>();
                    oldEstimate.add(estimateOriginal[k]);
                    oldEstimate.add(estimate);
                    oldError.add(errorOriginal[k]);
                    oldError.add(error);
                    
                    //precalculate the aggregated data
                    /*****************/
                    
                    /******for count data****************/
                    //double[] newEstError = NewEstimateCalculator.NewErrorCountData(oldEstimate, oldError);
                    /******for proportion data*********************/
                    ArrayList<SimpleFeature> feats = new ArrayList<SimpleFeature>();
                    SimpleFeatureSource source=(SimpleFeatureSource) parentFrame.data_aggregation.source_aggregation.get(k);
                    SimpleFeature feat1 = getFeatureById(source, f_id);
                    feats.add(feat1); 
                    SimpleFeature feat2 = getFeatureById(source, current_id);
                    feats.add(feat2);
                    double[] newEstError;
                    if(k==0) //put first varilable as poverty proportion
                        newEstError= NewEstimateCalculator.NewErrorProportionData(feats);
                    else  //second variable as sex ratio
                        newEstError= NewEstimateCalculator.NewErrorRatioData(feats);
                    /**************/
                    double newCV = newEstError[1]/1.645/newEstError[0];
                    if(newCV > parentFrame.aggregationassistantwin.CVthresholds[k])
                        new_cv_flag = false;

                    currentPolygon.newCV[k]=newCV;
                    currentPolygon.newEstimate[k]=newEstError[0];
                    currentPolygon.newError[k]=newEstError[1];
                    
                }
                currentPolygon.orginalid = FID;
                currentPolygon.feature.add(f);
                currentPolygon.id.add(FID);
                currentPolygon.feature.add(current);
//                String current_id =
//                           current.getID().substring(parentFrame.data_aggregation.layername.get(0).length()+1);
//                currentPolygon.id.add(Integer.valueOf(current_id));
                currentPolygon.id.add(current_id);
                if(!usedUnitsStr.contains(";"+current_id +";"))                  
                    aggreGroupList.add(currentPolygon);
                    if(new_cv_flag)
                        finalAggreGroupList.add(currentPolygon);
            }

//            //sort according to new CVs of first layer
//            Utils.sortSmalltoLargeAggregateCandidate(aggreGroupList);

            /*if even the smallest newCV cannot satisfy the CV threshold, more polygons need to be invovled
             searth the contigous polygons surring the polygon with the smallest CV*/

              //while(aggreGroupList.get(0).newCV[0]>parentFrame.aggregationassistantwin.CVthresholds[0])
                while(finalAggreGroupList.size()< 3) //if there is no satisfied candidates
                {
                    System.out.println("first order neigh cannot get samll cv");
                    //copy aggreGroupList and clear it in order to restore new candidate for the another round of scan
                    aggreGroupList1=(ArrayList<AggregateCandidate>) aggreGroupList.clone();
                    aggreGroupList.clear();
                    /*for each candidate, pick up the unit on the out ring of the original candidate, go through the same step of searching neighbour (step2)
                    append all the neighbour perspectively to the last aggreGroupList, which is coped to aggreGroupList1, to create a new aggreGroupList
                     */
                    for(int i=0; i<aggreGroupList1.size();i++)
                    {
                        //first get the last leefunit in the combination group and find all neighbours of this leef unit
                        SimpleFeature rootLeefUnit= aggreGroupList1.get(i).feature.get(aggreGroupList1.get(i).feature.size()-1);
                        String unitIdStr = rootLeefUnit.getID().substring(this.parentFrame.data_aggregation.layername.get(0).length()+1);
                        int rootLeefUnitId = Integer.valueOf(unitIdStr);
                        firstOrderNeighbor = getFirstOrderNeighbours((rootLeefUnitId-1), (SimpleFeatureSource) this.parentFrame.data_aggregation.source_aggregation.get(0));
                        //firstOrderNeighbor = CriterionCalculators.getAdjecentPolygons(rootLeefUnit, parentFrame.data_aggregation.collection_aggregation.get(0));

                        //for each neighour, with the exsiting members in the combination group, create a new combination group
                        for(int j=0; j<firstOrderNeighbor.size(); j++)
                        {

                            SimpleFeature currentneighbor = firstOrderNeighbor.get(j);
                            int currentneighbor_id = getFIDfromIdentifier(currentneighbor.getID(),0);
                            /*check if the new neighbor is the same as the feature which already locate in the combination group*/
                            
                            boolean ifduplicate=false;
                            for(int m=0; m<aggreGroupList1.get(i).feature.size();m++)
                            {
                                if(currentneighbor.getID().equals(aggreGroupList1.get(i).feature.get(m).getID()))
                                {
                                    ifduplicate=true;
                                    break;
                                }
                            }
                            if(!ifduplicate)  
                            //if(!ifduplicate || ifduplicate)
                            {
                                boolean new_cv_flag = true;
                                AggregateCandidate currentPolygon = new AggregateCandidate();
                                for(int m=0; m<aggreGroupList1.get(i).feature.size();m++)
                                {
                                    SimpleFeature leefUnit= aggreGroupList1.get(i).feature.get(m);
                                    currentPolygon.feature.add(leefUnit);
                                    currentPolygon.id.add(getFIDfromIdentifier(leefUnit.getID(), 0));
                                }
                                currentPolygon.feature.add(currentneighbor);
                                //String currentneighbor_id = currentneighbor.getID().substring(parentFrame.data_aggregation.layername.get(0).length()+1);
                                currentPolygon.id.add(currentneighbor_id);
                                currentPolygon.orginalid = FID;

                                for(int k=0; k<parentFrame.input_data_dimension; k++)
                                {

                                    ArrayList<Double> oldEstimate = new ArrayList<Double>();
                                    ArrayList<Double> oldError = new ArrayList<Double>();

                                    double estimateneighbor=temp_data.get(k).get(currentneighbor_id -1).oldest;
                                    double errorneighbor=temp_data.get(k).get(currentneighbor_id -1).olderror;
                                    for(int m=0; m<aggreGroupList1.get(i).feature.size();m++)
                                    {
                                        SimpleFeature leefUnit= aggreGroupList1.get(i).feature.get(m);
                                        oldEstimate.add(temp_data.get(k).get(getFIDfromIdentifier(leefUnit.getID(),0)-1).oldest);
                                        oldError.add(temp_data.get(k).get(getFIDfromIdentifier(leefUnit.getID(),0)-1).olderror);
                                    }
                                    oldEstimate.add(estimateneighbor);
                                    oldError.add(errorneighbor);
                                    /***for count data*****/
                                    //double[] newEstError = NewEstimateCalculator.NewErrorCountData(oldEstimate, oldError);
                                    /******for proportion data*********************/
                                    ArrayList<SimpleFeature> feats = new ArrayList<SimpleFeature>();
                                    SimpleFeatureSource source=(SimpleFeatureSource) parentFrame.data_aggregation.source_aggregation.get(k);
                                    for(int kk=0; kk<currentPolygon.id.size(); kk++)
                                    {
                                        feats.add(getFeatureById(source, currentPolygon.id.get(kk)));
                                    }
                                    double[] newEstError;
                                    if(k==0) //put first varilable as poverty proportion
                                        newEstError= NewEstimateCalculator.NewErrorProportionData(feats);
                                    else  //second variable as sex ratio
                                        newEstError= NewEstimateCalculator.NewErrorRatioData(feats);
                                    /**************/
                                    double newCV = newEstError[1]/1.645/newEstError[0];
                                    if(newCV > parentFrame.aggregationassistantwin.CVthresholds[k])
                                        new_cv_flag = false;
                                    currentPolygon.newCV[k]=newCV;
                                    currentPolygon.newEstimate[k]=newEstError[0];
                                    currentPolygon.newError[k]=newEstError[1];
                                }
                                
                                //if the current unit has been used in other seed group, give up
                                if(!usedUnitsStr.contains(";"+currentneighbor_id +";"))                             
                                    aggreGroupList.add(currentPolygon);
                                    if(new_cv_flag)
                                        finalAggreGroupList.add(currentPolygon);

                                for(int m=0; m<currentPolygon.feature.size();m++)
                                {
                                    System.out.print(currentPolygon.feature.get(m).getID()+";");
                                }
                                System.out.println(" ");
                            }

                        }
                    }
                    //Utils.sortSmalltoLargeAggregateCandidate(aggreGroupList); //sort according to newCV
                }
             
                /*manually add the group of 29, 25, 5 */
                int[] tempgroup = {29,5,25};
                if(f_id == 29)
                {
                    boolean new_cv_flag = true;
                    AggregateCandidate currentPolygon = new AggregateCandidate();
       
                    
                    currentPolygon.orginalid = 29;
                    for(int k=0; k<parentFrame.input_data_dimension; k++)
                   {

//                       ArrayList<Double> oldEstimate = new ArrayList<Double>();
//                       ArrayList<Double> oldError = new ArrayList<Double>();
                       ArrayList<SimpleFeature> feats = new ArrayList<SimpleFeature>();
                       SimpleFeatureSource source=(SimpleFeatureSource) parentFrame.data_aggregation.source_aggregation.get(k);
                       for(int ii =0; ii < tempgroup.length; ii++)
                       {
//                           double estimateneighbor=temp_data.get(k).get(tempgroup[ii] -1).oldest;
//                           double errorneighbor=temp_data.get(k).get(tempgroup[ii] -1).olderror;                            
//                           oldEstimate.add(estimateneighbor);
//                           oldError.add(errorneighbor);
                           feats.add(getFeatureById(source, tempgroup[ii]));
                           if(k==0)
                           {
                                currentPolygon.id.add(tempgroup[ii]);
                                currentPolygon.feature.add(getFeatureById(source, tempgroup[ii]));
                           }
                           
                       }
                      
                       /******for proportion data*********************/
                       double[] newEstError;
                       if(k==0) //put first varilable as poverty proportion
                           newEstError= NewEstimateCalculator.NewErrorProportionData(feats);
                       else  //second variable as sex ratio
                           newEstError= NewEstimateCalculator.NewErrorRatioData(feats);
                       /**************/
                       double newCV = newEstError[1]/1.645/newEstError[0];
                       if(newCV > parentFrame.aggregationassistantwin.CVthresholds[k])
                           new_cv_flag = false;
                       currentPolygon.newCV[k]=newCV;
                       currentPolygon.newEstimate[k]=newEstError[0];
                       currentPolygon.newError[k]=newEstError[1];
                   }
                    finalAggreGroupList.add(currentPolygon);
                  }
                /*end = manually add the group of 29, 25, 5 */
                
            aggreGroupList = finalAggreGroupList;
            for(int i=0; i<parentFrame.input_data_dimension; i++)
            {
                Utils.sortSmalltoLargeAggregateCandidate(aggreGroupList, i); //sort according to newCV
                minvalues.newCV[i] = aggreGroupList.get(0).newCV[i];                
                maxvalues.newCV[i]=aggreGroupList.get(aggreGroupList.size()-1).newCV[i];
            }

         //while we find a combination which has smaller cv than the threshold, we will stop and won't involve more units.
                

        if(this.optionalCriteriaNum>0)
        {
          /* set up table that put multiple criteria
            //set up the table view
          DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            for(int i =0; i<model.getRowCount();i++)
              {
                model.removeRow(0);
              }
            model.setRowCount(aggreGroupList.size());
           */
            //calculate the optional criteria;
            for(int i=0; i<aggreGroupList.size();i++)
            {
                calculateOptionalCriteria(aggreGroupList.get(i));
                /*set up table with values of optional criteria
                model.setValueAt(aggreGroupList.get(i).feature.size(), i, 0);
                model.setValueAt(aggreGroupList.get(i).newCV, i, 1);
                if(jCheckBox1.isSelected())
                    model.setValueAt(aggreGroupList.get(i).compactness, i, 2);
                if(jCheckBox2.isSelected())
                    model.setValueAt(aggreGroupList.get(i).themeproximity, i, 3);
                if(jCheckBox3.isSelected())
                    model.setValueAt(aggreGroupList.get(i).spatialhierarchy, i, 5);
                 *
                 */
            }
            System.out.println("The values of criteria for seed "+ FID);
            for(int k=0; k<parentFrame.input_data_dimension; k++)
            {
                System.out.println("Variable: "+ parentFrame.data_aggregation.layername.get(k));
                System.out.println("Bias range: "+ minvalues.bias[k]+" ~ "+maxvalues.bias[k]);
                System.out.println("Compact range: "+ minvalues.compactness+" ~ "+maxvalues.compactness);
                System.out.println("Similarity range: "+ minvalues.themeproximity[k]+" ~ "+maxvalues.themeproximity[k]);
                System.out.println("new CV range:"+minvalues.newCV[k]+" ~ "+maxvalues.newCV[k]);
                
            }
            
            this.selectedCriteriaNames.clear();
            //add the name of select criterion
//            this.selectedCriteriaNames.add("new CV1");
//            this.selectedCriteriaNames.add("new CV2");
//            if(jCheckBox1.isSelected())
//                selectedCriteriaNames.add("compactness");
//            if(jCheckBox2.isSelected())
//            {
//                selectedCriteriaNames.add("similarity1");
//                selectedCriteriaNames.add("similarity2");
//            }
//            if(jCheckBox3.isSelected())
//                //selectedCriteriaNames.add("intersection area");
//            {   selectedCriteriaNames.add("bias1");
//                selectedCriteriaNames.add("bias2");}
            
            this.selectedCriteriaNames.add("new CV1");
            this.selectedCriteriaNames.add("new CV2");
            selectedCriteriaNames.add("compactness");
            selectedCriteriaNames.add("similarity1");
            selectedCriteriaNames.add("bias1");
            selectedCriteriaNames.add("similarity2");
            selectedCriteriaNames.add("bias2");

            //initialize the graph for evaluating the trade-offs between multiple criteria
            
            ParallelPlotPane_Aggregation ppplot = new ParallelPlotPane_Aggregation(aggreGroupList,
                                                                       selectedCriteriaNames,minvalues,
                                                                       maxvalues, parentFrame.input_data_dimension);
            ppplot.setBackground(Color.BLACK);
            ppplot.setSize(jPanel1.getSize());
            jPanel1.removeAll();
            jPanel1.add(ppplot);
            jPanel1.getParent().validate();
            jPanel1.repaint();
        }else
        {
            //the candiate with the smallest when no other criterion is selected 
//                if(aggreGroupList.get(0).newCV<=parentFrame.aggregationassistantwin.CVthresholds[0])
//                {
//                   // aggreCadidate.add(aggreGroupList.get(0));
//                }
        }
            


    }

    private void calculateOptionalCriteria(AggregateCandidate current) throws MathException, IOException
    {

        Geometry all = null;
        for(int i=0; i<current.feature.size(); i++)
        {
            Geometry geometry = (Geometry) current.feature.get(i).getAttribute(0);
            if( geometry == null ) continue;
            if( all == null ){
                all = geometry;
            }
            else {
                all = all.union( geometry );
            }

        }
        if(jCheckBox1.isSelected())
        {
           //try to union the aggregation candidate for calculating the spatial compactness
           current.compactness = CriterionCalculators.getCompactness(all);

           if(current.compactness<minvalues.compactness)
               minvalues.compactness=current.compactness;
           if(current.compactness>maxvalues.compactness)
               maxvalues.compactness=current.compactness;
        }
        if(jCheckBox2.isSelected())
        {
           
            for(int k =0; k<parentFrame.input_data_dimension; k++)
           {
               double sumTtest = 0;
               int numOfPair = 0;
               for(int i=0; i<current.feature.size()-1; i++)
               {
                   for(int j=i+1; j<current.feature.size(); j++)
                   {
                       SimpleFeature f1= current.feature.get(i);
                       SimpleFeature f2= current.feature.get(j);
                        double est1=temp_data.get(k).get(getFIDfromIdentifier(f1.getID(),0)-1).oldest;
                        double err1=temp_data.get(k).get(getFIDfromIdentifier(f1.getID(),0)-1).olderror;
                        double est2=temp_data.get(k).get(getFIDfromIdentifier(f2.getID(),0)-1).oldest;
                        double err2=temp_data.get(k).get(getFIDfromIdentifier(f2.getID(),0)-1).olderror;
                        //sumTtest = sumTtest+(1-ProCalculator.getProbability(est1, err1, est2, err2))*100;
                        sumTtest = sumTtest+ProCalculator.getProbability(est1, err1, est2, err2)*100;
                        numOfPair ++;
                   }
                   
               }
                
                current.themeproximity[k]=sumTtest/numOfPair;
               if(current.themeproximity[k]<minvalues.themeproximity[k])
                   minvalues.themeproximity[k]=current.themeproximity[k];
               if(current.themeproximity[k]>maxvalues.themeproximity[k])
                   maxvalues.themeproximity[k]=current.themeproximity[k];
               }

        }
        if(jCheckBox3.isSelected())
        {
            //bias
            /*********calculate new estimate*********/
           double[] estimates = new double[current.feature.size()];
           for(int k =0; k<parentFrame.input_data_dimension; k++)
           {
               for(int i=0; i<current.feature.size(); i++)
               {
                   SimpleFeature f= current.feature.get(i);
                   estimates[i]=temp_data.get(k).get(getFIDfromIdentifier(f.getID(),0)-1).oldest;
                   
               }
               current.bias[k] = CriterionCalculators.getBias(estimates, current.newEstimate[k]);
               if(current.bias[k]<minvalues.bias[k])
                   minvalues.bias[k]=current.bias[k];
               if(current.bias[k]>maxvalues.bias[k])
                   maxvalues.bias[k]=current.bias[k];
               
            }
           
           
            /*****spatial hierachy*************/
//            SimpleFeature originFeature = 
//                    getFeatureById((SimpleFeatureSource) parentFrame.data_aggregation.source_aggregation.get(0),
//                    current.orginalid);
//            String joint_key = (String) originFeature.getAttribute("prim_geoid");
//            SimpleFeature upper_level_polygon =
//                    getUpperPolygonByKey(parentFrame.data_aggregation.source_auxi.get(0),
//                    joint_key);
//            Geometry upper_level_geometry = (Geometry)upper_level_polygon.getAttribute(0);
//            current.spatialhierarchy = CriterionCalculators.getIntersectionArea(all,
//                                                          upper_level_geometry);
//            if(current.spatialhierarchy<minvalues.spatialhierarchy)
//               minvalues.spatialhierarchy=current.spatialhierarchy;
//            if(current.spatialhierarchy>maxvalues.spatialhierarchy)
//               maxvalues.spatialhierarchy=current.spatialhierarchy;
            /*****spatial hierachy*************/
        }
        

    }

    private SimpleFeature getUpperPolygonByKey(SimpleFeatureSource featureSource,
                                               String fieldvalue) throws IOException
    {

            FilterFactory filterFactory = (FilterFactory) CommonFactoryFinder.getFilterFactory(null);
            Filter filter = filterFactory.equals(filterFactory.property("prim_geoid"), filterFactory.literal(fieldvalue));

            SimpleFeatureCollection features = featureSource.getFeatures(filter);
            SimpleFeatureIterator iter=features.features();
            SimpleFeature f = null;
            while(iter.hasNext()){
                f=iter.next();
            }
            return f;
    }

    private double getValueFromAttribute(String varType, String attributeName, SimpleFeature feature)
    {
        double attribute=0;
        if( varType == "java.lang.Integer")
            {
                 attribute= ((Integer)feature.getAttribute(attributeName)).doubleValue();
            }else if (varType == "java.lang.Long")
            {
                attribute= ((Long)feature.getAttribute(attributeName)).doubleValue();
            }else if (varType == "java.lang.Double")
            {
                attribute= ((Double)feature.getAttribute(attributeName)).doubleValue();
            }else if (varType == "java.lang.Float")
            {
                attribute= ((Float)feature.getAttribute(attributeName)).doubleValue();
            }
        return attribute;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(560, 600));

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("spatial compactness");
        jCheckBox1.setName(""); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setSelected(true);
        jCheckBox2.setText("attribute similarity");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox4.setSelected(true);
        jCheckBox4.setText("spatial contiguity");
        jCheckBox4.setEnabled(false);
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 540, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 636, Short.MAX_VALUE)
        );

        jCheckBox3.setSelected(true);
        jCheckBox3.setText("Bias");
        jCheckBox3.setActionCommand("spatial hierarchy");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox1.isSelected())
        {
            this.optionalCriteriaNum++;

        }
        else
            this.optionalCriteriaNum--;


    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox2.isSelected())
            this.optionalCriteriaNum++;
        else
            this.optionalCriteriaNum--;
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox3.isSelected())
            this.optionalCriteriaNum++;
        else
            this.optionalCriteriaNum--;
    }//GEN-LAST:event_jCheckBox3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
