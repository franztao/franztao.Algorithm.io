/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class ShapeFactory extends AbstractFactory
{

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.AbstractFactory#getColor(java.lang.String)
     */
    @Override
    public Color getColor(String color)
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see abstractFactoryPattern.AbstractFactory#getShape(java.lang.String)
     */
    @Override
    public Shape getShape(String shapeType)
    {
        if (shapeType == null)
        {
            return null;
        }
        if (shapeType.equalsIgnoreCase("CIRCLE"))
        {
            return new Circle();
        } else if (shapeType.equalsIgnoreCase("RECTANGLE"))
        {
            return new Rectangle();
        } else if (shapeType.equalsIgnoreCase("SQUARE"))
        {
            return new Square();
        }
        return null;
    }

}
