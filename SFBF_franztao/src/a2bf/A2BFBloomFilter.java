package a2bf;

public class A2BFBloomFilter {
	public byte[][] activebloomfiltervalue;
	// public byte[] active2bloomfiltervalue;
	public int bloomfiltervaluebitlength;

	public A2BFBloomFilter(int mi) {

		this.bloomfiltervaluebitlength = mi >> 1;
		this.activebloomfiltervalue = new byte[2][this.bloomfiltervaluebitlength >> 3];
		// this.active2bloomfiltervalue=new
		// byte[this.bloomfiltervaluebitlength>>4];
	}

}
