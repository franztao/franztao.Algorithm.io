/**
 * 
 */
package chainofResponsibilityPattern;

/**
 * @author Taoheng
 *
 */
public class FileLogger extends AbstractLogger {

	/**
	 * 
	 */

	public FileLogger(int dEBUG) {
		this.level = level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chainofResponsibilityPattern.AbstractLogger#write(java.lang.String)
	 */
	@Override
	protected void write(String message) {
		System.out.println("File::Logger: " + message);
	}

}
