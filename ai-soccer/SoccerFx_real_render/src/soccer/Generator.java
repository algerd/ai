
package soccer;

public class Generator {
    
    private SoccerPitch soccerPitch;
    
    public final static int countTact = 10000; // количество тактов матча
    private int tact;                           // номер такта матча
    
    public Generator(SoccerPitch soccerPitch) {
         this.soccerPitch = soccerPitch;   
    }
    
    /**
     * Run generator.
     */
    public void run() {
        for (tact = 0; tact < countTact; ++tact) {
            soccerPitch.update();                    
        }    
    }
         
}
