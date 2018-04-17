package sndlib.core.util;

import com.atesio.utils.math.EpsilonDoubleComparator;

/**
 * This class defines an epsilon for floating point comparisons in SNDlib.
 * 
 * @author Roman Klaehne
 */
public class DoubleComparator {

    /**
     * The epsilon used for comparisons.
     */
    public static final double EPSILON = 1e-3;

    private static EpsilonDoubleComparator COMPARATOR = new EpsilonDoubleComparator(
        EPSILON);

    /**
     * Returns the double comparator using the {@link #EPSILON} for 
     * comparisons.
     * 
     * @return the double comparator  
     */
    public static EpsilonDoubleComparator getInstance() {

        return COMPARATOR;
    }

    /**
     * Blocked constructor
     */
    private DoubleComparator() {

    }
}
