/**
 * 
 */
package structuralAdapterPattern;

/**
 * @author Taoheng
 *
 */
public class VlcPlayer implements AdvancedMediaPlayer
{

    /* (non-Javadoc)
     * @see structuralAdapterPattern.AdvancedMediaPlayer#playVlc(java.lang.String)
     */
    @Override
    public void playVlc(String fileName)
    {
        System.out.println("Playing vlc file. Name: "+ fileName);               
    }

    /* (non-Javadoc)
     * @see structuralAdapterPattern.AdvancedMediaPlayer#playMp4(java.lang.String)
     */
    @Override
    public void playMp4(String fileName)
    {
        // TODO Auto-generated method stub
        
    }

}
