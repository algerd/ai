
package generator;

public class Generator {
    
    private Match match;
    
    public final static int countTact = 10000; // количество тактов матча
    private int tact;                           // номер такта матча
    
    public Generator(Match match) {
         this.match = match;   
    }
    
    /**
     * Run generator.
     */
    public void run() {
        for (tact = 0; tact < countTact; ++tact) {
            match.update();                    
        }    
    }
         
}
