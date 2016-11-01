
package test;

public class Test {

    public static void main(String[] args) {
   
        SuperAbstr superAbstr = new Sub();
        superAbstr.getSuper();
        //superAbstr.getSub();  // error
            
        SubAbstr subAbstr = new Sub();
        subAbstr.getSuper();
        subAbstr.getSub();
               
        //Sub sub = new Sub();
        //sub.getSuper();
        //sub.getSub();
         
        // str в Sub раскомментирован
        //superAbstr.get();   // Sub
        //subAbstr.get();     // Sub
        //sub.get();          // Sub
        //sub.setStr("set");
        //superAbstr.get();   // Sub
        //subAbstr.get();     // Sub
        //sub.get();          // set
        
        System.out.println("-----------------------------------"); 
        /*
        // str в Sub закомментирован
        superAbstr.get();   // SubAbstr
        subAbstr.get();     // SubAbstr
        sub.get();          // SubAbstr
        sub.setStr("set");
        superAbstr.get();   // SubAbstr
        subAbstr.get();     // SubAbstr
        sub.get();          // set
        */
        
        
        
        Sub sub = new Sub();
        sub.setStr("set");
        
        subAbstr.get();   
        sub.get();
        
    }
    
}
