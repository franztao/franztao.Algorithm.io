/**
 * 
 */
package creationalAbstractFactoryPattern;

/**
 * @author Taoheng
 *
 */
public class FactoryProducer
{

    /**
     * @param string
     * @return
     */
    public static AbstractFactory getFactory(String choice)
    {
        if (choice.equalsIgnoreCase("SHAPE"))
        {
            return new ShapeFactory();
        } else
        {
            if (choice.equalsIgnoreCase("COLOR"))
            {
                return new ColorFactory();
            }
            return null;
        }
    }

}
