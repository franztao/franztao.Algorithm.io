/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class ColorFactory extends AbstractFactory
{

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.AbstractFactory#getColor(java.lang.String)
     */
    @Override
    public Color getColor(String color)
    {
        if (color == null)
        {
            return null;
        }
        if (color.equalsIgnoreCase("RED"))
        {
            return new Red();
        } else if (color.equalsIgnoreCase("GREEN"))
        {
            return new Green();
        } else if (color.equalsIgnoreCase("BLUE"))
        {
            return new Blue();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.AbstractFactory#getShape(java.lang.String)
     */
    @Override
    public Shape getShape(String shape)
    {
        return null;
    }

}
