// @wolfram77
package org.event;

// required modules
import org.data.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.concurrent.*;



public class EventEmitter extends ConcurrentHashMap<String, Set<Eventable>> {
    
    // static data
    public static Eventable fallback = new DefEventer();
    
    
    // _ToHyphenCase (str)
    // - convert a string to hyphen case
    String _toHyphenCase(String str) {
        StringBuilder s = new StringBuilder();
        for(int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if(c >= 'A' && c<='Z') {
                if(i > 0) s.append('-');
                s.append((char)(c-'A'+'a'));
            }
            else s.append(c);
        }
        return s.toString();
    }
    
    
    // _AddClass (obj, cls)
    // - add static / instance methods of a class as eventers
    private void _addClass(Object obj, Class cls) {
        boolean bestatic = obj==null;
        for(Method m : cls.getMethods()) {
            // need static or instance?
            String name = m.getName();
            boolean isstatic = Modifier.isStatic(m.getModifiers());
            if(!name.startsWith("on") || ((!isstatic) && bestatic)) continue;
            // save appropriately
            String event = _toHyphenCase(name.substring(2));
            if(bestatic) add(event, new Eventer(cls, name));
            else add(event, new Eventer(obj, name));
        }
    }
    
    
    // _InitEventSet (event)
    // - initialize event set before adding
    void _initEventSet(String event) {
        if(get(event) != null) return;
        put(event, Collections.newSetFromMap(new ConcurrentHashMap<Eventable, Boolean>()));
    }
    
    
    // EventEmitter ()
    // - create event emitter
    public EventEmitter() {
    }
    
    
    // EventEmitter (cls)
    // - create event emitter from class
    public EventEmitter(Class cls) {
        _addClass(null, cls);
    }
    
    
    // EventEmitter (obj)
    // - create event emitter from object
    public EventEmitter(Object obj) {
        _addClass(obj, obj.getClass());
    }
    
    
    // Emit (event, args)
    // - emit an event
    public EventEmitter emit(String event, Map args) {
        Set<Eventable> eventers = get(event);
        if(eventers == null) fallback.absorb(event, args);
        else eventers.stream().forEach((e) -> {
            e.absorb(event, args);
        });
        return this;
    }
    
    
    // Emit (event, args...)
    // - emit an event
    public EventEmitter emit(String event, Object... args) {
        return emit(event, Coll.map(args));
    }
    
    
    // On (event, eventer)
    // - add an eventer to an event
    public EventEmitter add(String event, Eventable eventer) {
        _initEventSet(event);
        get(event).add(eventer);
        return this;
    }
    
    
    // On (event, eventers)
    // - add eventers to an event
    public EventEmitter add(String event, Collection<Eventable> eventers) {
        _initEventSet(event);
        get(event).addAll(eventers);
        return this;
    }
    
    
    // On (events, eventer)
    // - add eventer to events
    public EventEmitter add(Collection<String> events, Eventable eventer) {
        events.stream().forEach((event) -> {
            add(event, eventer);
        });
        return this;
    }
    
    
    // On (map)
    // - add eventers from map
    public EventEmitter add(Map<String, Set<Eventable>> map) {
        map.keySet().stream().forEach((event) -> {
            add(event, map.get(event));
        });
        return this;
    }
    
    
    // NotOn (event, eventer)
    // - remove an eventer from an event
    public EventEmitter remove(String event, Eventable eventer) {
        Set<Eventable> e = get(event);
        if(e != null) e.remove(eventer);
        return this;
    }
    
    
    // NotOn (event, eventers)
    // - remove eventers from an event
    public EventEmitter remove(String event, Collection<Eventable> eventers) {
        Set<Eventable> e = get(event);
        if(e != null) e.removeAll(eventers);
        return this;
    }
    
    
    // NotOn (events, eventer)
    // - remove eventer from events
    public EventEmitter remove(Collection<String> events, Eventable eventer) {
        events.stream().forEach((event) -> {
            remove(event, eventer);
        });
        return this;
    }
    
    
    // NotOn (event, eventesr)
    // - remove eventers from map
    public EventEmitter remove(Map<String, Set<Eventable>> map) {
        map.keySet().stream().forEach((event) -> {
            remove(event, map.get(event));
        });
        return this;
    }
    
    
    // NotOn (event)
    // - remove all eventers of an event
    public EventEmitter remove(String event) {
        super.remove(event);
        return this;
    }
    
    
    // notOn ()
    // - remove all eventers
    public EventEmitter remove() {
        clear();
        return this;
    }
}
