/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class Circle implements Shape
{

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.Shape#draw()
     */
    @Override
    public void draw()
    {
        System.out.println("Inside Circle::draw() method.");
    }

}
