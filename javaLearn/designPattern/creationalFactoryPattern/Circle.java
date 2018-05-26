/**
 * 
 */
package creationalFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class Circle implements Shape
{

    /*
     * (non-Javadoc)
     * 
     * @see factoryPattern1.Shape#draw()
     */
    @Override
    public void draw()
    {
        System.out.println("Inside Circle::draw() method.");

    }

}
