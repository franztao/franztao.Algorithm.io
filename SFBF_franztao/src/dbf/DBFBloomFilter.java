package dbf;

public class DBFBloomFilter {
	public byte[] bloomfiltervalue;
	public int bloomfiltervaluebitlength;
	public DBFBloomFilter(int mi){
		this.bloomfiltervaluebitlength=mi;
		if((this.bloomfiltervaluebitlength%8)==0){
			bloomfiltervalue=new byte[bloomfiltervaluebitlength/8];
		}
		else{
			bloomfiltervalue=new byte[(bloomfiltervaluebitlength/8)+1];
		}
	}
	public void flush(){
		for(int i=0;i<this.bloomfiltervalue.length;i++){
			this.bloomfiltervalue[i]=0;
		}
		
	}

}
