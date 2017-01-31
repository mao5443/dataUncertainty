package classification.dataHandler;

public class Domain {
	private int leftID;
	private int rightID;
	private double CL;
	private double intersectPoint;
	private int lowestCLLeftID;
	private int lowestCLRightID;
	private boolean selected;
        
	public int getLeftID() {
		return leftID;
	}

	public int getLowestCLLeftID() {
		return lowestCLLeftID;
	}

	public void setLowestCLLeftID(int lowestCLLeftID) {
		this.lowestCLLeftID = lowestCLLeftID;
	}

	public int getLowestCLRightID() {
		return lowestCLRightID;
	}

	public void setLowestCLRightID(int lowestCLRightID) {
		this.lowestCLRightID = lowestCLRightID;
	}

	public void setLeftID(int leftID) {
		this.leftID = leftID;
	}

	public int getRightID() {
		return rightID;
	}

	public void setRightID(int rightID) {
		this.rightID = rightID;
	}

	public double getCL() {
		return CL;
	}

	public void setCL(double cL) {
		CL = cL;
	}

	public double getIntersectPoint() {
		return intersectPoint;
	}

	public void setIntersectPoint(double intersectPoint) {
		this.intersectPoint = intersectPoint;
	}

        public void SetSelectionStatus(boolean s)
        {
            selected = s;
        }
        
        public boolean GetSelectionStation()
        {
            return selected;
        }

	public Domain() {
		selected = false;
	}

}
