package data;

public class InputData {
	//public byte[] inputdata_byte;
	public int inputdata_int;
	
	public int inputdatabitlength;
	
	public int seq;
	public InputData(int input_int,int inputdatebitlength,int seq){
		this.inputdata_int=input_int;
		this.inputdatabitlength=inputdatebitlength;
		//this.inputdata_byte=new byte[4];
		//this.inputdata_byte[0]=(byte) (this.inputdata_int& 0xFF);
		//this.inputdata_byte[1]=(byte) ((this.inputdata_int>>8)& 0xFF);
		//this.inputdata_byte[2]=(byte) ((this.inputdata_int>>16)& 0xFF);
		//this.inputdata_byte[3]=(byte) ((this.inputdata_int>>24)& 0xFF);
		this.seq=seq;
	}
}
