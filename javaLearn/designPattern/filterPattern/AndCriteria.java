/**
 * 
 */
package filterPattern;

import java.util.List;

/**
 * @author Taoheng
 *
 */
public class AndCriteria implements Criteria {

	/*
	 * (non-Javadoc)
	 * 
	 * @see filterPattern.Criteria#meetCriteria(java.util.List)
	 */
	private Criteria criteria;
	private Criteria otherCriteria;

	public AndCriteria(Criteria criteria, Criteria otherCriteria) {
		this.criteria = criteria;
		this.otherCriteria = otherCriteria;
	}

	@Override
	public List<Person> meetCriteria(List<Person> persons) {
		List<Person>firstCriteriaPersons=criteria.meetCriteria(persons);
		return this.otherCriteria.meetCriteria(firstCriteriaPersons); 
	}

}
