package classification.dataHandler;

import java.util.ArrayList;

public class DataTable {
	private ArrayList<Double> estimate; //data record list --estimate
	private ArrayList<Double> moe; //data record list -- moe
	private ArrayList<String> fieldname;
	private int GID;
	private String unitName;
	
	
	public DataTable(ArrayList<Double> est, ArrayList<Double> moe, ArrayList<String> fieldname)
	{
		this.estimate = est;
		this.moe = moe;
		this.fieldname = fieldname;
	}
	public DataTable()
	{
		this.estimate = new ArrayList<Double>();
		this.moe = new ArrayList<Double>();
		this.fieldname = new ArrayList<String>();
	}
	
	
	public int getGID() {
		return GID;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setEstimate(ArrayList<Double> estimate) {
		this.estimate = estimate;
	}
	public void setMoe(ArrayList<Double> moe) {
		this.moe = moe;
	}
	public void setGID(int gID) {
		GID = gID;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public ArrayList<String> getFieldname() {
		return fieldname;
	}

	public void setFieldname(ArrayList<String> fieldname) {
		this.fieldname = fieldname;
	}
	
	public void addFieldname(String name) {
		this.fieldname.add(name);
	}


	
	public ArrayList<Double> getEstimate() {
		return estimate;
	}
	public void setEstimate(double estimate, int index) {
		this.estimate.set(index, estimate);
		
	}
	public void addEstimate(double estimate) {
		this.estimate.add(estimate);
		
	}
	public ArrayList<Double> getMoe() {
		return moe;
	}
	public void setMoe(double moe, int index) {
		this.moe.set(index, moe);
	}
	public void addMoe(double moe) {
		this.moe.add(moe);
	}
	


}
