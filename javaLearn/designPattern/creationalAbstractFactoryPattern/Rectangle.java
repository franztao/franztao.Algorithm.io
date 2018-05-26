/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class Rectangle implements Shape
{

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.Shape#draw()
     */
    @Override
    public void draw()
    {
        System.out.println("Inside Rectangle::draw() method.");
    }

}
