/**
 * 
 */
package behavioralCommandPattern;

/**
 * @author Taoheng
 *
 */
public class SellStock implements Order
{
    private Stock abcStock;

    public SellStock(Stock abcStock)
    {
        this.abcStock = abcStock;
    }

    /*
     * (non-Javadoc)
     * 
     * @see commandPattern.Order#execute()
     */
    @Override
    public void execute()
    {
        abcStock.sell();
    }

}
