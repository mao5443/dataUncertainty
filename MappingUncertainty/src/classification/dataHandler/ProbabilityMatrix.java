package classification.dataHandler;

import java.util.ArrayList;

public class ProbabilityMatrix {

	private ArrayList<ArrayList> rows;
    private ArrayList<Element> columns;
	public ProbabilityMatrix() {
		// TODO Auto-generated constructor stub
		this.rows = new ArrayList<ArrayList>();
		this.columns = new ArrayList<Element>();
	}
    public ArrayList<ArrayList> getRows() {
		return rows;
	}
	public void setRows(ArrayList<ArrayList> rows) {
		this.rows = rows;
	}
	
	public void addRows(ArrayList<Element> row) {
		this.rows.add(row);
	}
	public ArrayList<Element> getColumns() {
		return columns;
	}
	public void setColumns(ArrayList<Element> columns) {
		this.columns = columns;
	}
	
	public Element createNewElement(){
		Element current = new Element();
		return current;
	}
	 class Element{
		private double value;
		private int ID1;
		private int ID2;
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		public int getID1() {
			return ID1;
		}
		public void setID1(int iD1) {
			ID1 = iD1;
		}
		public int getID2() {
			return ID2;
		}
		public void setID2(int iD2) {
			ID2 = iD2;
		}

		
		
	}

}
