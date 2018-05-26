/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class Blue implements Color
{

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.Color#fill()
     */
    @Override
    public void fill()
    {
        System.out.println("Inside Blue::fill() method.");
    }

}
