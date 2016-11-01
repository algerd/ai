
package loader;

public class LoadDataMatch {
    
    private int idMatch; 
    private int idHomeTeam;
    private int idGuestTeam;
    
    public LoadDataMatch(int idMatch) {
        this.idMatch = idMatch;
        // в тестовом режиме прописываем данные клуба напрямую, минуя бд
        this.idHomeTeam = 1;
        this.idGuestTeam = 2;
    }

    public int getIdMatch() {
        return idMatch;
    }

    public int getIdHomeTeam() {
        return idHomeTeam;
    }

    public int getIdGuestTeam() {
        return idGuestTeam;
    }
           
}
