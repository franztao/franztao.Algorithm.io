/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class Square implements Shape
{

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.Shape#draw()
     */
    @Override
    public void draw()
    {
        System.out.println("Inside Square::draw() method.");
    }

}
