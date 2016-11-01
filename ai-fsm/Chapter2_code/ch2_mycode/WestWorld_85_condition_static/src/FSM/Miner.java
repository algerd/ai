
package FSM;

import FSM.MinerStates.GoHome;

public class Miner extends BaseGameEntity {   
    final public static int COMFORT_LEVEL = 6;    
    final public static int MAX_NUGGETS = 4; 
    final public static int THIRST_LEVEL = 15; 
    final public static int TIREDNESS_LEVEL = 15;
    final public static int REST_LEVEL = 0;
    final public static int HUNGER_LEVEL = 20;
      
    private State currentState;
    private LocationEnum location;
    private int goldCarried;
    private int moneyInBank; 
    private int thirst; 
    private int hunger;
    private int fatigue;

    public Miner(EntityEnum entity) {
        super(entity);
        this.location = LocationEnum.SHACK;
        this.currentState = new GoHome();
    }
    
    @Override
    public void update() {
        this.currentState.execute(this);
    }
   
    public void changeState(State newState) {
        this.currentState.exit(this);
        this.currentState = newState;
        this.currentState.enter(this);
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
   
    public LocationEnum getLocation() {
        return this.location;
    }
  
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
    
    public void setLocation(LocationEnum loc) {
        this.location = loc;
    }
             
}
