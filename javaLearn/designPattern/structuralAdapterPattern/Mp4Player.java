/**
 * 
 */
package structuralAdapterPattern;

/**
 * @author Taoheng
 *
 */
public class Mp4Player implements AdvancedMediaPlayer
{

    /*
     * (non-Javadoc)
     * 
     * @see structuralAdapterPattern.AdvancedMediaPlayer#playVlc(java.lang.String)
     */
    @Override
    public void playVlc(String fileName)
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see structuralAdapterPattern.AdvancedMediaPlayer#playMp4(java.lang.String)
     */
    @Override
    public void playMp4(String fileName)
    {
        System.out.println("Playing mp4 file. Name: " + fileName);
    }

}
