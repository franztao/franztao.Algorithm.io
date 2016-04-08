package advancedalgorithm;

import java.io.FileInputStream;
import java.util.Random;

/**
 * @author from http://www.cnblogs.com/biaoyu/archive/2012/09/26/2704456.html
 *
 */
public class ACO {
	private Ant[] ants;
	private int antNum;
	private int cityNum;
	private int MAX_Inter;
	private float[][] pheromone;
	private int[][] distance;
	private int bestLength;
	private int[] bestTour;
	
	private float alpha;
	private float beta;
	private float rho;
	/**
	 * @param n city number
	 * @param m ant number
	 * @param g	iterate number
	 * @param a alpha
	 * @param b	beta
	 * @param r rho
	 */
	public ACO(int n,int m,int g,float a,float b,float r) {
		// TODO Auto-generated constructor stub
		this.cityNum=n;
		this.antNum=m;
		this.ants=new Ant[this.antNum];
		this.MAX_Inter=g;
		this.alpha=a;
		this.beta=b;
		this.rho=r;
		this.distance=new int[this.cityNum][this.cityNum];
	}
	
	public void init(){
		Random r=new Random();
		for(int i=0;i<this.cityNum;i++){
			this.distance[i][i]=0;
			for(int j=i+1;j<this.cityNum;j++){
				this.distance[i][j]=r.nextInt(1000);
				this.distance[j][i]=this.distance[i][j];
			}
		}
		for(int i=0;i<this.cityNum;i++){
			for(int j=0;j<this.cityNum;j++){
				System.out.print(this.distance[i][j]+" ");
			}
			System.out.println();
			
		}
		pheromone=new float[this.cityNum][this.cityNum];
		for(int i=0;i<this.cityNum;i++){
			for(int j=0;j<this.cityNum;j++){
				pheromone[i][j]=0.1f;
			}
		}
		this.bestLength=Integer.MAX_VALUE;
		this.bestTour=new int[this.cityNum+1];
		for(int i=0;i<this.antNum;i++){
			this.ants[i]=new Ant(this.cityNum);
			this.ants[i].init(this.distance, this.alpha, this.beta);
		}
		
	}
	public void solve(){
		for(int g=0;g<this.MAX_Inter;g++){
			for(int i=0;i<this.antNum;i++){
				for(int j=1;j<this.cityNum;j++){
					ants[i].selectNextCity(pheromone);
				}
				this.ants[i].getTabu().add(ants[i].getFirstCity());
				if(ants[i].getTourLength()<this.bestLength){
					this.bestLength=ants[i].getTourLength();
					for(int k=0;k<=this.cityNum;k++){
						this.bestTour[k]=ants[i].getTabu().get(k).intValue();
					}
				}
				for(int j=0;j<this.cityNum;j++){
					ants[i].getDelta()[ants[i].getTabu().get(j).intValue()][ants[i].getTabu().get(j+1).intValue()]=(float)(1./ants[i].getTourLength());
					ants[i].getDelta()[ants[i].getTabu().get(j+1).intValue()][ants[i].getTabu().get(j).intValue()]=(float)(1./ants[i].getTourLength());
						
				}
			}
			//Update Pheromone
			updatePheromone();
			//restart init ants
			for(int i=0;i<this.antNum;i++){
				this.ants[i].init(distance, this.alpha, this.beta);
			}
		}
		printOptimal();
	}

	private void updatePheromone() {
		// TODO Auto-generated method stub
		for(int i=0;i<this.cityNum;i++)
			for(int j=0;j<this.cityNum;j++){
				this.pheromone[i][j]=this.pheromone[i][j]*(1-this.rho);
			}
		for(int i=0;i<this.cityNum;i++)
			for(int j=0;j<this.cityNum;j++){
				for(int k=0;k<this.antNum;k++){
					this.pheromone[i][j]+=ants[k].getDelta()[i][j];
				}
			}
	}
	private void printOptimal(){
		System.out.println("The optimal length is:"+bestLength);
		System.out.println("The optimal tour is:");
		for(int i=0;i<=this.cityNum;i++){
			System.out.print(this.bestTour[i]+" ");
		}
	}

}
