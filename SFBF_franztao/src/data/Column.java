package data;

import java.util.Random;

public class Column {
	//Q(R*w) w==bitlength
	public int bitlength;
	//Q(R*w) colum's storage byte array
	public byte[] colbit;
	
	public int colbit_int;
	
	//every column occupy how length byte. 
	public int bytelength;
	//problem:vector<byte>colbit  ?**
	public Column(int c) {
		// TODO Auto-generated constructor stub
		this.bitlength=c;
		if((this.bitlength%8)==0)
			this.bytelength=this.bitlength/8;
		else
			this.bytelength=(this.bitlength/8)+1;
		colbit=new byte[this.bytelength];
		generationrandomhashcolkey(this.colbit);
		Random random = new Random();
		this.colbit_int=random.nextInt();
		
	}
	
	private void generationrandomhashcolkey(byte[] colbit2) {
		// TODO Auto-generated method stub
		Random random = new Random();
		random.nextBytes(colbit2);
	}

}
