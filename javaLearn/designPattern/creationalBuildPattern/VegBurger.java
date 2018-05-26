/**
 * 
 */
package creationalBuildPattern;

/**
 * @author Taoheng
 *
 */
public class VegBurger extends Burger
{

    /*
     * (non-Javadoc)
     * 
     * @see buildPattern4.Item#name()
     */
    @Override
    public String name()
    {
        return "Veg Burger";
    }

    /*
     * (non-Javadoc)
     * 
     * @see buildPattern4.Item#packing()
     */

    /*
     * (non-Javadoc)
     * 
     * @see buildPattern4.Item#price()
     */
    @Override
    public float price()
    {
        return 25.0f;
    }

}
