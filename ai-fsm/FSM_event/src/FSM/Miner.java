
package FSM;

import FSM.MinerStates.MinerStateEnum;
import FSM.MinerStates.MinerStateManager;

public class Miner extends GameEntity {   
    final public static int COMFORT_LEVEL = 6;    
    final public static int MAX_NUGGETS = 4; 
    final public static int THIRST_LEVEL = 15; 
    final public static int TIREDNESS_LEVEL = 15;
    final public static int REST_LEVEL = 0;
    final public static int HUNGER_LEVEL = 20;
    
    // стартовое состояние персонажа
    private MinerStateEnum startStateEnum;
    
    private int goldCarried;
    private int moneyInBank; 
    private int thirst; 
    private int hunger;
    private int fatigue;

    public Miner(String name, MinerStateEnum startStateEnum) {
        super(name);
        this.startStateEnum = startStateEnum;
    }
    
    @Override
    public StateMachine getStateMachine() {
        return new StateMachine(new MinerStateManager(this), startStateEnum);
    }
    
    //-------------------------------- is -------------------------------------
    
    public boolean isThirst() {
        return this.thirst >= THIRST_LEVEL;
    }

    public boolean isFatigued() {
        return this.fatigue > TIREDNESS_LEVEL;
    }
    
    public boolean isRested() {
        return this.fatigue <= REST_LEVEL;
    }
    
    public boolean isHungry() {
        return this.hunger > HUNGER_LEVEL;
    }
    
    public boolean isPocketsFull() {
        return this.goldCarried >= MAX_NUGGETS;
    }
    
    public boolean isEnoughMoney() {
        return this.moneyInBank >= COMFORT_LEVEL;
    }
    
    
    //---------------------------- getters & setters ---------------------------------
    
    public int getGoldCarried() {
        return this.goldCarried;
    }

    public int getMoneyInBank() {
        return this.moneyInBank;
    }

    public int getFatigue() {
        return this.fatigue;
    }

    public int getHunger() {
        return this.hunger;
    }

    public int getThirst() {
        return this.thirst;
    }

    public void setMoneyInBank(int money) {
        if(money < 0) money = 0;
        this.moneyInBank = money;
    }
    
    public void setThirst(int thirst) {
        if(thirst < 0) thirst = 0;
        this.thirst = thirst;
    }

    public void setHunger(int hunger) {
        if(hunger < 0) hunger = 0;
        this.hunger = hunger;
    }

    public void setFatigue(int fatigue) {
        if(fatigue < 0) fatigue = 0;
        this.fatigue = fatigue;
    }
    
    public void setGoldCarried(int gold) {
        if(gold < 0) gold = 0;
        this.goldCarried = gold;
    }
                
}
