/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class Red implements Color
{

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.Color#fill()
     */
    @Override
    public void fill()
    {
        System.out.println("Inside Red::fill() method.");
    }

}
