
package common.misc;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

final public class CppToJava {

    public static class DoubleRef extends AtomicReference<Double> {

        public DoubleRef(double ref) {
            super(ref);
        }

        public double toDouble() {
            return this.get();
        }
    }
    
    public static class ObjectRef<T extends Object> extends AtomicReference<T> {
        public ObjectRef(T ref) {
            super(ref);
        }

        public ObjectRef() {
            super();
        }
        public T getValue() {
            return (T) this.get();
        }
    }

    public static <T extends Object> List<T> clone(List<T> list) {
        try {
            List<T> c = list.getClass().newInstance();
            for (T t : list) {
                T copy = (T) t.getClass().getDeclaredConstructor(t.getClass()).newInstance(t);
                c.add(copy);
            }
            return c;
        } catch (Exception e) {
            throw new RuntimeException("List cloning unsupported", e);
        }
    }
}
