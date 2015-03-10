// @wolfram77
package org.event;

// required modules
import java.util.*;
import java.lang.invoke.*;
import java.lang.reflect.*;



public class Reaction implements Reactable {
    
    // data
    Object obj;
    MethodHandle mthd;
    
    
    // Reaction (cls, mthd)
    // - create an eventer (static)
    @SuppressWarnings("UseSpecificCatch")
    public Reaction(Class cls, String mthd) {
        try {
            Method m = cls.getMethod(mthd, String.class, Map.class);
            this.mthd = MethodHandles.lookup().unreflect(m);
        }
        catch(Exception e) { new StimuliException(e).exit(); }
    }
    
    
    // Reaction (obj, mthd)
    // - create an eventer (instance)
    public Reaction(Object obj, String mthd) {
        this(obj.getClass(), mthd);
        this.obj = obj;
    }
    
    
    // On (event, args)
    // - listens to an event and forwards it to method
    @Override
    public void on(String event, Map args) {
        try {
            if(mthd == null) ((Reactable)obj).on(event, args);
            else if(obj == null) mthd.invokeExact(event, args);
            else mthd.invoke(obj, event, args);
        }
        catch(Throwable e) { new StimuliException(e).exit(); }
    }
}
