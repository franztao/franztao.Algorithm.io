/**
 * 
 */
package structuralFilterPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Taoheng
 *
 */
public class CriteriaMale implements Criteria
{

    /*
     * (non-Javadoc)
     * 
     * @see filterPattern.Criteria#meetCriteria(java.util.List)
     */
    @Override
    public List<Person> meetCriteria(List<Person> persons)
    {
        List<Person> malePersons = new ArrayList<Person>();
        for (Person person : persons)
        {
            if (person.getGender().equalsIgnoreCase("MALE"))
            {
                malePersons.add(person);
            }
        }
        return malePersons;
    }

}
