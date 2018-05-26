/**
 * 
 */
package creationalBuildPattern;

/**
 * @author Taoheng
 *
 */
public abstract class Burger implements Item
{


    /* (non-Javadoc)
     * @see buildPattern4.Item#packing()
     */
    @Override
    public Packing packing()
    {
        return new Wrapper();
    }

    /* (non-Javadoc)
     * @see buildPattern4.Item#price()
     */
    @Override
    public abstract float price();

}
