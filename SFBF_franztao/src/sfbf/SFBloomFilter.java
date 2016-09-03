package sfbf;

public class SFBloomFilter {
	public byte[] bloomfiltervalue;
	public int bloomfiltervaluebitlength;

	public SFBloomFilter(int mi) {
		// TODO Auto-generated constructor stub
		this.bloomfiltervaluebitlength = mi;
		if ((this.bloomfiltervaluebitlength % 8) == 0) {
			this.bloomfiltervalue = new byte[bloomfiltervaluebitlength / 8];
		} else {
			this.bloomfiltervalue = new byte[(bloomfiltervaluebitlength / 8) + 1];
		}
	}

	public void flush() {
		for (int i = 0; i < this.bloomfiltervalue.length; i++) {
			this.bloomfiltervalue[i] = 0;
		}

	}
}
