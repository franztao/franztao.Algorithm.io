/**
 * 
 */
package creationalSingletonPattern;

/**
 * @author Taoheng
 *
 */
public class SingletonPatternDemo
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // 获取唯一可用的对象
        SingleObject object = SingleObject.getInstance();

        // 显示消息
        object.showMessage();
    }

}
