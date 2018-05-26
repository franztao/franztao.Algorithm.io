/**
 * 
 */
package filterPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Taoheng
 *
 */
public class CriteriaSingle implements Criteria {

	/*
	 * (non-Javadoc)
	 * 
	 * @see filterPattern.Criteria#meetCriteria(java.util.List)
	 */
	@Override
	public List<Person> meetCriteria(List<Person> persons) {
		List<Person> singlePersons = new ArrayList<Person>();
		for (Person person : persons) {
			if (person.getMaritalStatus().equalsIgnoreCase("SINGLE")) {
				singlePersons.add(person);
			}
		}
		return singlePersons;
	}

}
