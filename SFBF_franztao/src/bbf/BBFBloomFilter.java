package bbf;

public class BBFBloomFilter {
	public byte[] bloomfiltervalue;
	public int bloomfiltervaluebitlength;
	public BBFBloomFilter(int mi){
		this.bloomfiltervaluebitlength=mi;
		if((this.bloomfiltervaluebitlength%8)==0){
			bloomfiltervalue=new byte[bloomfiltervaluebitlength/8];
		}
		else{
			bloomfiltervalue=new byte[(bloomfiltervaluebitlength/8)+1];
		}
	}
}
