
package common.FSM;

import common.Messaging.Telegram;

public abstract class State<T> {

  //this will execute when the state is entered
  abstract public void enter(T e);

  //this is the state's normal update function
  abstract public void execute(T e);

  //this will execute when the state is exited.
  abstract public void exit(T e);
  
  //this executes if the agent receives a message from the message dispatcher
  abstract public boolean onMessage(T e, Telegram t);
  
}
