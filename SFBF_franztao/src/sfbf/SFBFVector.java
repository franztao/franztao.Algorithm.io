package sfbf;

public class SFBFVector {
	public byte[] bitVectorBYTEs;
	public int length4BitVectorBYTEs;

	public SFBFVector(int mi) {
		// TODO Auto-generated constructor stub
		this.length4BitVectorBYTEs = mi;
		if ((this.length4BitVectorBYTEs % 8) == 0) {
			this.bitVectorBYTEs = new byte[length4BitVectorBYTEs / 8];
		} else {
			this.bitVectorBYTEs = new byte[(length4BitVectorBYTEs / 8) + 1];
		}
	}

	public void flush() {
		for (int i = 0; i < this.bitVectorBYTEs.length; i++) {
			this.bitVectorBYTEs[i] = 0;
		}

	}
}
