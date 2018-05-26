/**
 * 
 */
package creationProxyPattern;

/**
 * @author Taoheng
 *
 */
public class Rectangle extends Shape
{
    public Rectangle()
    {
        type = "Rectangle";
    }

    /*
     * (non-Javadoc)
     * 
     * @see creationProxyPattern5.Shape#draw()
     */
    @Override
    void draw()
    {
        System.out.println("Inside Rectangle::draw() method.") ;       
    }

}
