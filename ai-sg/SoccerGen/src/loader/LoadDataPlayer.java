
package loader;

import utils.Vector;

/**
 * Класс загружает(напр. из бд) по id игрока его данные: название, скиллы и т.д.
 */
public class LoadDataPlayer {
    
    private int id;
    private String name;
    private short mass;
    private double speed;           // m/sec
    private double acceleration;    // m/(sec*sec)
    private double angleRotateTact; // rad/sec
    
    // этот параметр должен загружаться из бд из таблицы расстановки игроков команды, которая содержит id игроков команды и по их позиции
    private Vector startPosition;
    
    public LoadDataPlayer(int id) {
        this.id = id;
        // в тестовом режиме прописываем данные игрока напрямую, минуя бд
        switch (id) { 
            case 1:
                startPosition = new Vector(50, 10);
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break;
                
            case 2:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break;
                
            case 3:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break; 
                
            case 4:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break; 
                
            case 5:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break; 
                
            case 6:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break; 
                
            case 7:
                startPosition = new Vector(60, 50);
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break;
                
            case 8:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break;
                
            case 9:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break;
                
            case 10:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break;
                
            case 11:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break;
                
            case 12:
                startPosition = new Vector();
                mass = 100;
                speed = 10;
                acceleration = 5;
                angleRotateTact = 10;
                name = "";
                break;               
        }     
    }

    public int getId() {
        return id;
    }

    public short getMass() {
        return mass;
    }

    public String getName() {
        return name;
    }

    public Vector getStartPosition() {
        return startPosition;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getAngleRotateTact() {
        return angleRotateTact;
    }
                    
}
