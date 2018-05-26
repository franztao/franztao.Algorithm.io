/**
 * 
 */
package creationalBuildPattern;

/**
 * @author Taoheng
 *
 */
public class Pepsi extends ColdDrink
{

    /*
     * (non-Javadoc)
     * 
     * @see buildPattern4.Item#name()
     */
    @Override
    public String name()
    {
        return "Pepsi";
    }

    /*
     * (non-Javadoc)
     * 
     * @see buildPattern4.ColdDrink#price()
     */
    @Override
    public float price()
    {
        return 35.0f;
    }

}
