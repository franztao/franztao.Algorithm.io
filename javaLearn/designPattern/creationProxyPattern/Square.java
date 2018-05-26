/**
 * 
 */
package creationProxyPattern;

/**
 * @author Taoheng
 *
 */
public class Square extends Shape
{
    public Square()
    {
        type = "Square";
    }

    /*
     * (non-Javadoc)
     * 
     * @see creationProxyPattern5.Shape#draw()
     */
    @Override
    void draw()
    {
        System.out.println("Inside Square::draw() method.");
    }

}
