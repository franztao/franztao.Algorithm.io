/**
 * 
 */
package chainofResponsibilityPattern;

/**
 * @author Taoheng
 *
 */
public class ErrorLogger extends AbstractLogger {
	public ErrorLogger(int level) {
		this.level = level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chainofResponsibilityPattern.AbstractLogger#write(java.lang.String)
	 */

	@Override
	protected void write(String message) {
		System.out.println("Error Console::Logger: " + message);
	}

}
