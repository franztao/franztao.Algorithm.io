/**
 * 
 */
package creationalBuildPattern;

/**
 * @author Taoheng
 *
 */
public class Coke extends ColdDrink
{

    /*
     * (non-Javadoc)
     * 
     * @see buildPattern4.ColdDrink#price()
     */
    @Override
    public float price()
    {
        return 30.0f;
    }

    /*
     * (non-Javadoc)
     * 
     * @see buildPattern4.Item#name()
     */
    @Override
    public String name()
    {
        return "Coke";
    }

}
