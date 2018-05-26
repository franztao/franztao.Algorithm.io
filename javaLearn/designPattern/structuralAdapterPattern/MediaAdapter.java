/**
 * 
 */
package structuralAdapterPattern;

/**
 * @author Taoheng
 *
 */
public class MediaAdapter implements MediaPlayer
{
    AdvancedMediaPlayer advancedMusicPlayer;

    public MediaAdapter(String audioType){
        if(audioType.equalsIgnoreCase("vlc") ){
           advancedMusicPlayer = new VlcPlayer();            
        } else if (audioType.equalsIgnoreCase("mp4")){
           advancedMusicPlayer = new Mp4Player();
        }    
     }
    
    /*
     * (non-Javadoc)
     * 
     * @see structuralAdapterPattern.MediaPlayer#play(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void play(String audioType, String fileName)
    {
        if (audioType.equalsIgnoreCase("vlc"))
        {
            advancedMusicPlayer.playVlc(fileName);
        } else if (audioType.equalsIgnoreCase("mp4"))
        {
            advancedMusicPlayer.playMp4(fileName);
        }
    }

}
