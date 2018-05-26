/**
 * 
 */
package creationalBuildPattern;

/**
 * @author Taoheng
 *
 */
public class ChickenBurger extends Burger
{

    /* (non-Javadoc)
     * @see buildPattern4.Burger#price()
     */
    @Override
    public float price()
    {
        return 50.5f;
    }

    /* (non-Javadoc)
     * @see buildPattern4.Item#name()
     */
    @Override
    public String name()
    {
        return "Chicken Burger";
    }

   

}
