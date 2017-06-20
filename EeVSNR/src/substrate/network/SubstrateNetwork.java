package substrate.network;

import java.util.HashMap;
import java.util.Map;

public class SubstrateNetwork {
	public int nodeSize;
	public int edgeSize;
	public boolean topology[][];

	public int nodeComputationCapacity[];
	public int edgeBandwithCapacity[][];
	public int usedNodeCurrentComputationCapacity[];
	public int usedEdgeCurrentBandwithCapacity[][];
	
	public int serviceNumber;
	public boolean nodeServiceType[][];
	
	public String nodeLabel[];
	public Map<String,Integer> labeltoID;
	
	public SubstrateNetwork(int nodeSize,int edgeSize,int serviceNumber){
		this.nodeSize=nodeSize;
		this.serviceNumber=serviceNumber;
		
		topology=new boolean[nodeSize][nodeSize];
		
		nodeComputationCapacity=new int[nodeSize];
		edgeBandwithCapacity=new int[nodeSize][nodeSize];
		usedNodeCurrentComputationCapacity=new int[nodeSize];
		usedEdgeCurrentBandwithCapacity=new int[nodeSize][nodeSize];
		
		nodeServiceType=new boolean[nodeSize][serviceNumber+1];
		nodeLabel=new String[nodeSize];
		labeltoID=new HashMap<String,Integer>();
	}

	public void faultInit() {
		// TODO Auto-generated method stub
		nodeComputationCapacity[0]=5;
		nodeComputationCapacity[1]=7;
		nodeComputationCapacity[2]=7;
		nodeComputationCapacity[3]=10;
		nodeComputationCapacity[4]=8;
		nodeComputationCapacity[5]=6;
		nodeComputationCapacity[6]=6;
		nodeComputationCapacity[7]=9;
		nodeComputationCapacity[8]=8;
		
		
		
		for(int i=0;i<nodeSize;i++){
			for(int j=0;j<nodeSize;j++){
				topology[i][j]=true;
				edgeBandwithCapacity[i][j]=30;
			}
		}
		
		nodeServiceType[0][1]=true;
		nodeServiceType[1][2]=true;
		nodeServiceType[1][3]=true;
		nodeServiceType[2][3]=true;
		nodeServiceType[3][4]=true;
		nodeServiceType[4][2]=true;
		nodeServiceType[4][3]=true;
		nodeServiceType[5][1]=true;
		nodeServiceType[5][2]=true;
		nodeServiceType[6][2]=true;
		nodeServiceType[6][3]=true;
		nodeServiceType[7][1]=true;
		nodeServiceType[7][4]=true;
		nodeServiceType[8][4]=true;
		
		nodeLabel[0]="S1";
		nodeLabel[1]="S2";
		nodeLabel[2]="S3";
		nodeLabel[3]="S4";
		nodeLabel[4]="S5";
		nodeLabel[5]="S6";
		nodeLabel[6]="S7";
		nodeLabel[7]="S8";
		nodeLabel[8]="S9";

		labeltoID.put("S1",0);
		labeltoID.put("S2",1);
		labeltoID.put("S3",2);
		labeltoID.put("S4",3);
		labeltoID.put("S5",4);
		labeltoID.put("S6",5);
		labeltoID.put("S7",6);
		labeltoID.put("S8",7);
		labeltoID.put("S9",8);
	}

}
