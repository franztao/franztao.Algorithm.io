/**
 * 
 */
package behavioralObserverPattern;

/**
 * @author Taoheng
 *
 */
public abstract class Observer
{
    protected Subject subject;

    public abstract void update();
}
