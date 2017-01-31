/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import classification.dataHandler.AggregationAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cisc
 */
public class Utils {
     public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String shp = "shp";
    public final static String csv = "csv";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }



    public static void sortSmalltoLarge(ArrayList<DataTable> records, int sortIndex){

		Collections.sort(records,new Comparator<DataTable>() {
			private int sortIndex;
            public int compare(DataTable o1, DataTable o2) {
				// TODO Auto-generated method stub
				double val1 = o1.getEstimate().get(this.sortIndex);
				double val2 = o2.getEstimate().get(this.sortIndex);
				return val1 < val2 ? -1 : val1 == val2 ? 0 : 1;

			}

		});

	}

	public static ArrayList<Domain> sortSmalltoLarge(ArrayList<Domain> records){

		Collections.sort(records,new Comparator<Domain>() {
		        public int compare(Domain o1, Domain o2) {
				// TODO Auto-generated method stub
				double val1 = o1.getCL();
				double val2 = o2.getCL();
				return val1 < val2 ? -1 : val1 == val2 ? 0 : 1;

			}

		});
                return records;

	}

        public static ArrayList<Domain> sortSmalltoLargeAsPointValue(ArrayList<Domain> records){

		Collections.sort(records,new Comparator<Domain>() {
		        public int compare(Domain o1, Domain o2) {
				// TODO Auto-generated method stub
				double val1 = o1.getIntersectPoint();
				double val2 = o2.getIntersectPoint();
				return val1 < val2 ? -1 : val1 == val2 ? 0 : 1;

			}

		});
                return records;

	}


    public static ArrayList<Double> sortSmalltoLargeDouble(ArrayList<Double> records){
		Collections.sort(records,new Comparator<Double>() {
		        public int compare(Double val1, Double val2) {
				// TODO Auto-generated method stub
				return val1 < val2 ? -1 : val1 == val2 ? 0 : 1;
			}
		});
                return records;

	}

    public static ArrayList<AggregationAttributes> sortSmalltoLargeAggregationAttributes(ArrayList<AggregationAttributes> records, String field){

        if(field.equals("cv"))
        {
            Collections.sort(records, new Comparator<AggregationAttributes>(){
             public int compare(AggregationAttributes o1, AggregationAttributes o2) {
                return o1.cv<o2.cv ? -1:o1.cv == o2.cv ? 0:1;
            }
           });
        }else if(field.equals("estimate"))
        {
            Collections.sort(records, new Comparator<AggregationAttributes>(){
             public int compare(AggregationAttributes o1, AggregationAttributes o2) {
                return o1.oldest<o2.oldest ? -1:o1.oldest == o2.oldest ? 0:1;
            }
           });
        }
        else if(field.equals("FID"))
        {
            Collections.sort(records, new Comparator<AggregationAttributes>(){
             public int compare(AggregationAttributes o1, AggregationAttributes o2) {
                return o1.FID<o2.FID ? -1:o1.FID == o2.FID ? 0:1;
            }
           });
        }
        return records;

    }

    public static ArrayList<AggregateCandidate> sortSmalltoLargeAggregateCandidate(ArrayList<AggregateCandidate> records,
                                                                                   int dimension_index){
       if(dimension_index ==0)
       {
           Collections.sort(records, new Comparator<AggregateCandidate>(){
             public int compare(AggregateCandidate o1, AggregateCandidate o2) {
                return o1.newCV[0]<o2.newCV[0] ? -1:o1.newCV[0] == o2.newCV[0] ? 0:1;
            }
           });
       }else if(dimension_index == 1)
       {
           Collections.sort(records, new Comparator<AggregateCandidate>(){
             public int compare(AggregateCandidate o1, AggregateCandidate o2) {
                return o1.newCV[1]<o2.newCV[1] ? -1:o1.newCV[1] == o2.newCV[1] ? 0:1;
            }
           });
       }
        return records;

    }



}
