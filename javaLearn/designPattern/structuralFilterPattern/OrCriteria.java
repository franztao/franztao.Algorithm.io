/**
 * 
 */
package structuralFilterPattern;

import java.util.List;

/**
 * @author Taoheng
 *
 */
public class OrCriteria implements Criteria
{

    /*
     * (non-Javadoc)
     * 
     * @see filterPattern.Criteria#meetCriteria(java.util.List)
     */
    private Criteria criteria;
    private Criteria otherCriteria;

    public OrCriteria(Criteria criteria, Criteria otherCriteria)
    {
        this.criteria = criteria;
        this.otherCriteria = otherCriteria;
    }

    @Override
    public List<Person> meetCriteria(List<Person> persons)
    {
        List<Person> firstCriteriaItems = criteria.meetCriteria(persons);
        List<Person> otherCriteriaItems = otherCriteria.meetCriteria(persons);
        for (Person person : otherCriteriaItems)
        {
            if (!firstCriteriaItems.contains(person))
            {
                firstCriteriaItems.add(person);
            }
        }
        return firstCriteriaItems;
    }

}
