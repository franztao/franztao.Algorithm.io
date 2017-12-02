/**
 * 
 */
package commandPattern;

/**
 * @author Taoheng
 *
 */
public class BuyStock implements Order {
	private Stock abcStock;

	public BuyStock(Stock abcStock) {
		 this.abcStock = abcStock;
	}

	/* (non-Javadoc)
	 * @see commandPattern.Order#execute()
	 */
	@Override
	public void execute() {
		 abcStock.buy();
	}

}
