/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class Green implements Color
{

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.Color#fill()
     */
    @Override
    public void fill()
    {
        System.out.println("Inside Green::fill() method.");
    }

}
