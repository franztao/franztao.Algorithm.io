/**
 * 
 */
package creationProxyPattern;

/**
 * @author Taoheng
 *
 */
public class Circle extends Shape
{

    public Circle()
    {
        type = "Circle";
    }

    /*
     * (non-Javadoc)
     * 
     * @see creationProxyPattern5.Shape#draw()
     */
    @Override
    void draw()
    {
        System.out.println("Inside Circle::draw() method.");
    }

}
