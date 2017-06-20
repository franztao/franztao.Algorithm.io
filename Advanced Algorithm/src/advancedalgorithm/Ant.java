package advancedalgorithm;

import java.util.Random;
import java.util.Vector;

public class Ant {
	private Vector<Integer>tabu;//tabu list
	private Vector<Integer>allowedCities;//the city is allowed to search
	private float[][] delta;//matrix of pheromone change
	private int[][] distance;//distance matrix
	private float alpha;
	private float beta;
	private int tourLength;//tour length
	private int cityNum;//city number
	private int firstCity;//start city
	private int currentCity;//current city
	
	public Ant() {
		// TODO Auto-generated constructor stub
		this.cityNum=30;
		this.tourLength=0;
	}
	
	public Ant(int num){
		this.cityNum=num;
		this.tourLength=0;
		
	}
	public void init(int[][] distance,float a,float b){
		this.alpha=a;
		this.beta=b;
		this.allowedCities=new Vector<Integer>();
		this.tabu=new Vector<Integer>();
		this.distance=distance;
		this.delta=new float[this.cityNum][this.cityNum];
		for(int i=0;i<this.cityNum;i++){
			Integer integer=new Integer(i);
			this.allowedCities.add(integer);
			for(int j=0;j<this.cityNum;j++){
				this.delta[i][j]=0.f;		
			}
		}
		Random random=new Random(System.currentTimeMillis());
		this.firstCity=random.nextInt(this.cityNum);
		for(Integer i:this.allowedCities){
			if(i.intValue()==this.firstCity){
				this.allowedCities.remove(i);
				break;
			}	
		}
		this.tabu.addElement(Integer.valueOf(this.firstCity));
		this.currentCity=this.firstCity;
	}
	
	public void selectNextCity(float[][] pheromone){
		float[] p=new float[this.cityNum];
		float sum=0.0f;
		for(Integer i:this.allowedCities){
			sum+=Math.pow(pheromone[this.currentCity][i.intValue()], alpha)*Math.pow(1.0/distance[this.currentCity][i],this.beta);
		}
		for(int i=0;i<this.cityNum;i++){
			boolean flag=false;
			for(Integer j:this.allowedCities){
				if(i==j.intValue()){
					p[i]=(float)(Math.pow(pheromone[this.currentCity][i],alpha)*Math.pow(1.0/this.distance[this.currentCity][i],beta))/sum;
					flag=true;
					break;
				}
			}
			if(false==flag){
				p[i]=0.f;
			}
		}
		Random random=new Random(System.currentTimeMillis());
		float selectP=random.nextFloat();
		int selectCity=0;
		float sum1=0.1f;
		for(int i=0;i<this.cityNum;i++){
			sum1+=p[i];
			if(sum1>=selectP){
				selectCity=i;
				break;
			}
		}
		for(Integer i:this.allowedCities){
			if(i.intValue()==selectCity){
				this.allowedCities.remove(i);
				break;
				
			}
		}
		this.tabu.addElement(Integer.valueOf(selectCity));
		this.currentCity=selectCity;
	}
	/**
	 * @return the tabu
	 */
	public Vector<Integer> getTabu() {
		return tabu;
	}

	/**
	 * @return the allowedCities
	 */
	public Vector<Integer> getAllowedCities() {
		return allowedCities;
	}

	/**
	 * @param allowedCities the allowedCities to set
	 */
	public void setAllowedCities(Vector<Integer> allowedCities) {
		this.allowedCities = allowedCities;
	}

	/**
	 * @return the delta
	 */
	public float[][] getDelta() {
		return delta;
	}

	/**
	 * @param delta the delta to set
	 */
	public void setDelta(float[][] delta) {
		this.delta = delta;
	}

	/**
	 * @return the distance
	 */
	public int[][] getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(int[][] distance) {
		this.distance = distance;
	}

	/**
	 * @return the alpha
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return the beta
	 */
	public float getBeta() {
		return beta;
	}

	/**
	 * @param beta the beta to set
	 */
	public void setBeta(float beta) {
		this.beta = beta;
	}

	/**
	 * @return the tourLength
	 */
	public int getTourLength() {
		return tourLength;
	}

	/**
	 * @param tourLength the tourLength to set
	 */
	public void setTourLength(int tourLength) {
		this.tourLength = tourLength;
	}

	/**
	 * @return the cityNum
	 */
	public int getCityNum() {
		return cityNum;
	}

	/**
	 * @param cityNum the cityNum to set
	 */
	public void setCityNum(int cityNum) {
		this.cityNum = cityNum;
	}

	/**
	 * @return the currentCity
	 */
	public int getCurrentCity() {
		return currentCity;
	}

	/**
	 * @param currentCity the currentCity to set
	 */
	public void setCurrentCity(int currentCity) {
		this.currentCity = currentCity;
	}

	/**
	 * @return the firstCity
	 */
	public int getFirstCity() {
		return firstCity;
	}

	/**
	 * @param firstCity the firstCity to set
	 */
	public void setFirstCity(int firstCity) {
		this.firstCity = firstCity;
	}

	/**
	 * @param tabu the tabu to set
	 */
	public void setTabu(Vector<Integer> tabu) {
		this.tabu = tabu;
	}

	private int calculateTourlength(){
		int len=0;
		for(int i=0;i<this.cityNum;i++){
			len+=distance[this.tabu.get(i).intValue()][this.tabu.get(i+1).intValue()];
		}
		return len;
	}

}
