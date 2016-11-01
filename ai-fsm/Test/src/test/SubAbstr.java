
package test;

public abstract class SubAbstr extends SuperAbstr {
    
    //String str = "SubAbstr";
      
    abstract void getSub();
    
    void get() {
        System.out.println(str);      
    }
    
    void setStr(String s) {
        this.str = s;
    }
}
