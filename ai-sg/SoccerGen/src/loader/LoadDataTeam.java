
package loader;

import generator.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс загружает(напр из бд) по id клуба его данные: название, цвет маек и т.д. и массивы игроков.
 */
public class LoadDataTeam {
    
    private int id;
    private String name;
    private List<Integer> idPlayerList = new ArrayList<>();
    
    public LoadDataTeam(int id) {
        // в тестовом режиме прописываем данные клуба напрямую, минуя бд
        this.id = id;
        if (id == 1) {
            name = "Milan";            
            idPlayerList.add(1);
            /*idPlayerList.add(2);
            idPlayerList.add(3);
            idPlayerList.add(4);
            idPlayerList.add(5);
            idPlayerList.add(6);*/
        } 
        else {
            name = "Real";
            idPlayerList.add(7);
            /*idPlayerList.add(8);
            idPlayerList.add(9);
            idPlayerList.add(10);
            idPlayerList.add(11);
            idPlayerList.add(12);*/
        }
                          
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getIdPlayerList() {
        return idPlayerList;
    }
           
}
