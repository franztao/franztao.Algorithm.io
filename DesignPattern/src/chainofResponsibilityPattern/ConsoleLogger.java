/**
 * 
 */
package chainofResponsibilityPattern;

/**
 * @author Taoheng
 *
 */
public class ConsoleLogger extends AbstractLogger {
	public ConsoleLogger(int level) {
		this.level = level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chainofResponsibilityPattern.AbstractLogger#write(java.lang.String)
	 */
	@Override
	protected void write(String message) {
		System.out.println("Standard Console::Logger: " + message);
	}

}
