
package common.misc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AutoList<T extends Object> {
   
    static private Map<Class<?>, Object> memberMap = new HashMap<>();
    private Class<?> classType = null;

    private List<T> getList(Class<?> type) {
        if (type == null) {
            return null;
        }
        if (!memberMap.containsKey(type)) {
            classType = type;
            memberMap.put(type, new LinkedList<>());
        }
        return (List<T>)memberMap.get(type);
    }

    public void add(T o) {
        getList(o.getClass()).add(o);
    }

    public void remove(T o) {
        getList(o.getClass()).remove(o);
    }

    public List<T> getAllMembers() {
        List<T> list = getList(classType);
        if (list != null) {
            return list;
        }
        return new LinkedList<>();
    }
}