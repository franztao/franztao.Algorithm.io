package data;

public class HashGenerationMatrice {
	public Column[] row;
	public int r,c;
	public HashGenerationMatrice(int r,int c) {
		// TODO Auto-generated constructor stub
		this.r=r;
		this.c=c;
		row=new Column[this.r];
		for(int i=0;i<r;i++){
			Column e=new Column(c);
			row[i]=e;
		}
	}

}
