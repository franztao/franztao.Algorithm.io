/**
 * 
 */
package filterPattern;

import java.util.List;

/**
 * @author Taoheng
 *
 */
public interface Criteria {
	public List<Person>meetCriteria(List<Person>persons);
}
