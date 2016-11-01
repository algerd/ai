
package test;

public class Sub extends SubAbstr {
    
    String str = "Sub";

    @Override
    void getSub() {
    }

    @Override
    void getSuper() {
    }
    /*
    void setStr(String s) {
        this.str = s;
    }
    */
    void get() {
        System.out.println(str);
    }
    
}
