
package FSM;

import FSM.MinerStates.MinerStateManagerEnum;

public class Miner extends BaseGameEntity {
    
    /** the amount of gold a miner must have before he feels comfortable. */
    final public static int COMFORT_LEVEL = 5;    
    /** the amount of nuggets a miner can carry. */   
    final public static int MAX_NUGGETS = 3;   
    /** above this value a miner is thirsty. */
    final public static int THIRST_LEVEL = 5;  
    /** above this value a miner is sleepy. */
    final public static int TIREDNESS_LEVEL = 5;
    
    private StateMachine<Miner> stateMachine;   
    /** the place where the miner is currently situated. */
    private LocationEnum location;   
    /** how many nuggets the miner has in his pockets. */
    private int goldCarried;   
    /** how much money the miner has deposited in the bank. */
    private int moneyInBank;   
    /** the higher the value, the thirstier the miner. */
    private int thirst;   
    /** the higher the value, the more tired the miner. */
    private int fatigue;

    public Miner(EntityEnum entity) {
        super(entity);
        this.location = LocationEnum.SHACK;       
        this.stateMachine = new StateMachine<>(this);
        this.stateMachine.setCurrentState(MinerStateManagerEnum.GO_HOME_TO_SLEEP.getState());
    }
    
    @Override
    public void update() {
        this.thirst += 1;
        this.stateMachine.update();
    }
    
    @Override
    public boolean handleMessage(Telegram msg) {
      return this.stateMachine.HandleMessage(msg);
    }
   
    public void addToGoldCarried(int val) {
        this.goldCarried += val;
        if (this.goldCarried < 0) {
            this.goldCarried = 0;
        }
    }

    public void addToWealth(int val) {
        this.moneyInBank += val;
        if (this.moneyInBank < 0) {
            this.moneyInBank = 0;
        }
    }
    
    public void buyAndDrinkAWhiskey() {
        this.thirst = 0;
        this.moneyInBank -= 2;
    }
    
    public void decreaseFatigue() {
        this.fatigue -= 1;
    }

    public void increaseFatigue() {
        this.fatigue += 1;
    }

    public boolean isThirst() {
        return this.thirst >= THIRST_LEVEL;
    }

    public boolean isFatigued() {
        return this.fatigue > TIREDNESS_LEVEL;
    }
    
    public boolean isPocketsFull() {
        return this.goldCarried >= MAX_NUGGETS;
    }
    
    public StateMachine<Miner> getStateMachine() {
        return this.stateMachine;
    }
   
    public LocationEnum getLocation() {
        return this.location;
    }

    public void setLocation(LocationEnum loc) {
        this.location = loc;
    }

    public int getGoldCarried() {
        return this.goldCarried;
    }

    public void setGoldCarried(int val) {
        this.goldCarried = val;
    }

    public int getMoneyInBank() {
        return this.moneyInBank;
    }

    public void setMoneyInBank(int val) {
        this.moneyInBank = val;
    }
  
}