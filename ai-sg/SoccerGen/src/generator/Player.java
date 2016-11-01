
package generator;

import fsm.StateMachine;
import fsm.playerstate.PlayerStateEnum;
import fsm.playerstate.PlayerStateManager;
import loader.LoadDataPlayer;
import utils.Matrix;
import utils.Vector;

public class Player implements Updatable {
    
    // количество тактов паузы после удара
    private final int PAUSE_KICK = 4;
    
    // игровые параметры (изменяемые) игрока
    private Vector position = new Vector();
    private Vector direction = new Vector();   // a normalized vector
    private Vector velocity = new Vector();
    private double distSqBall;    
    private int kickTimer;  // счётчик тактов после удара
    private final Team team;
    private final StateMachine<PlayerStateEnum> stateMachine;
    private final PlayerMotion playerMotion;
    
    // индивидуальные параметры (неизменные) игрока
    private final int id;
    private final String name;
    private final short mass;
    private final double massForce;
    private final double maxSpeed;
    private final double maxAcceleration;
    private final double angleRotateTact;    // rad/sec скорость поворота игрока за такт
    private final Vector startPosition = new Vector();
    private final Vector startDirection = new Vector();
    
    public Player(int id, Team club) {
        team = club;               
        this.id = id;
        // загружаем данные игрока
        LoadDataPlayer dataPlayer = new LoadDataPlayer(id);
        name = dataPlayer.getName();
        mass = dataPlayer.getMass();
        massForce = Params.PLAYER_COEFF_FORCE / mass;
        maxSpeed = dataPlayer.getSpeed() * Params.TIME_TACT;
        maxAcceleration = dataPlayer.getAcceleration() * Params.TIME_TACT;
        angleRotateTact = dataPlayer.getAngleRotateTact() * Params.TIME_TACT;
        
        startPosition.set(dataPlayer.getStartPosition());
        position.set(startPosition);
        
        startDirection.set(team.getOpponentGoal().getCenter().subn(startPosition).normalize());     
        direction.set(startDirection);
        
        // сохранять такой порядок инициализации playerMotion и stateMachine в конце конструктора
        playerMotion = new PlayerMotion(this);
        stateMachine = new StateMachine(new PlayerStateManager(this), PlayerStateEnum.CHASE_BALL);
  
    }
    
    @Override
    public void update() {
        ++kickTimer;
        findDistSqBall();
        stateMachine.update();
        playerMotion.calculateAcceleration();
        rotate(playerMotion.getTarget());        
        // величина ускорения зависит от угла между direction и вектором ускорения и массой игрока
        Vector acceleration = direction.muln(direction.dot(playerMotion.getAcceleration()) * massForce);
        velocity.add(acceleration);
        position.add(velocity);
              
    }
    
    private boolean rotate(Vector target) {
        Vector toTarget = target.subn(position).normalize();
        double angle = Math.acos(direction.dot(toTarget));
        if (Double.isNaN(angle) || angle < 0.0001) { 
            return false;
        } 
        if (angle > angleRotateTact) {
            angle = angleRotateTact;
        } 
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.rotate(angle * direction.sign(toTarget));
        rotationMatrix.transformVector2Ds(direction);
        rotationMatrix.transformVector2Ds(velocity);
        return true;
    }
    
    public void resetKickTimer() {
        this.kickTimer = 0;
    }
    
    public boolean isReadyToKick() {
        return kickTimer > PAUSE_KICK;          
    }
    
    public boolean isBallWithinTakeRange() {
        return distSqBall < Params.PLAYER_DIST_SQ_TAKE_BALL;
    }
    
    public void findDistSqBall() {
        distSqBall = position.distSq(getBall().getPos().getVectorXY());
    }
    
    public double getDistSqBall() {
        return distSqBall;
    }
    
    public Ball getBall() {
        return team.getMatch().getBall();
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Vector getDirection() {
        return direction;
    }

    public int getId() {
        return id;
    }    
    
    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    public PlayerMotion getPlayerMotion() {
        return playerMotion;
    }

    public StateMachine<PlayerStateEnum> getStateMachine() {
        return stateMachine;
    }
                               
}
