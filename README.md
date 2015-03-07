# java-event-emitter

What is `java-event-emitter` you ask? Wait, lets first read a short story.

> Imagine that you are in the middle chemistry class, jotting down notes on **Radicals** from the
> blackboard. The master is endlessly scribbling symbols of elements, of various valencies, with
> an indicated charge quantity on top. **Harish**, your benchmate is yet to enter the class!

> He is waiting for **Sambhu**, the master, to somehow get distracted, so he can find a way to sneak
> into the class and have his attendence marked. But, that is utterly impossible. **Sambhu** is a
> very alert man. He has his eyes and ears on everyone. So, **Harish** is waiting at the other end of
> the building, else **Sambhu** might see him.

> *Woof, Woofoof!* **Sambhu** cries coughing out smoke from his mouth, or is it? *"I will be back
> in a minute. Let there be pinpoint silence"*, **Sambhu** somehow speaks out. This is an `event`!
> You now `emit` this `event` to **Harish**, who `absorbs` this event. He then jumps to action.
> Rapidly tiptoeing he enters the class and in a minute he is beside you. Opens up a notebook with
> some previously scribbled pages, and a pen suddenly pops into his hand. You, the `event emitter`,
> and **Harish**, the `event absorber` have to wait for a full five minutes until **Sambhu** is back.

> *Huh!* **Sambhu** is shocked, and suddenly he turns into a tiger with his paws open. Didn't i tell he has
> his eyes and ears on everyone? *BAM! BAM!*

Okay, that was bad. But atleast you now know that we use events in our life. In fact we use it
all the time. If you have ever heard of `interrupts`, they are essentially `hardware triggered events`,
which provide a great solution to monitoring something without repeated polling. `Software events`
are similar, except that the triggering of the event is performed by software as well.

`Emitting` an event is similar to *making a function call*, and `absorbing` is the act of
*getting a function call*. How is it different from a function call then? Well, with the idea
of events, there can be any number of *event absorbers*, and as many *types of events* as you like,
and they can be added and removed at runtime. A more `dynamic form of function call`.



## Usage

Shall we get started?  Go through the [examples](https://github.com/wolfram77/java-event-emitter#examples),
download the source code `src/org/data` and `src/org/event`, add it to your *project* and follow the
[reference](https://github.com/wolfram77/java-event-emitter#reference).


## Examples

### Hello World!
```java
// required modules
import org.event.*;

public class Main {
    
    public static void main(String[] args) {
        EventEmitter event = new EventEmitter();    // create event emitter
        event.emit("hello", "msg", "Hello World!"); // DefaultEventAbsorber listens
    }
}
```
```
[hello] : {msg=Hello World!}
```

### Error Event

```java
// required modules
import org.event.*;

public class Main {
    
    public static void main(String[] args) {
        EventEmitter event = new EventEmitter();
        try { int a = 1 / 0; }
        catch(Exception e) { event.emit("hello", "err", e, "msg", "Hello World!"); }
        // err argument indicates it is an error event
    }
}
```

```
[hello] : {msg=Hello World!, err=java.lang.ArithmeticException: / by zero}
java.lang.ArithmeticException: / by zero
	at main.Main.main(Main.java:13)
Java Result: -1
```

### Event Absorber Class

```java
// required modules
import java.util.*;
import org.event.*;

class HelloTeller implements IEventAbsorber {
    
    @Override
    public void listen(String event, Map args) {
        System.out.println("Hello event "+event);
    }
}

class ByeTeller implements IEventAbsorber {
    
    @Override
    public void listen(String event, Map args) {
        System.out.println("Bye event "+event);
    }
}

public class Main {
    
    public static void main(String[] args) {
        HelloTeller hello = new HelloTeller();
        ByeTeller bye = new ByteTeller();
        EventEmitter event = new EventEmitter();
        event.add("action", hello).add("action", bye);
        event.emit("action");
    }
}
```

```
Hello event action
Bye event action
```

### Event Absorber Method

```java
// required modules
import java.util.*;
import org.event.*;

public class Main {
    
    public static void helloTeller(String event, Map args) {
    	System.out.println("Hello event "+event);
    }
    
    public void byeTeller(String event, Map args) {
    	System.out.println("Bye event "+event);
    }
    
    public static void main(String[] args) {
    	Main main = new Main();
        EventEmitter event = new EventEmitter();
        event.add("action", new EventAbsorber(Main.class, "helloTeller"));
        event.add("action", new EventAbsorber(main, "helloTeller");
        event.emit("action");
    }
}
```

```
Hello event action
Bye event action
```


## Reference

| `class EventEmitter` | `extends HashMap<String, Set<IEventAbsorber>>` |
|----------------------|------------------------------------------------|
| **EventEmitter** <br/> `()`                                                                                                | create event emitter <br/>                                                                                               `EventEmitter event = new EventEmitter()` |
| **add** <br/> `(event, absorber)`                                                                                          | add absorber to an event <br/>                                                                                   `event.add("write-done", writeDoneAbsorber);` |
| **emit** <br/> `(event, arg, val, ...)`                                                                                    | emit an normal or error event <br/>                                                                                  `event.emit("write", "time", new Date(), "data", data);` <br/>                                              `event.emit("write", "err", e, "data", data);` |
| **fallback** <br/> `()`, `(absorbers)`                                                                                      | get or set fallback absorber (for events with no absorbers) <br/>                                               `DefaultEventAbsorber` is default fallback <br/>                                                                             `IEventAbsorber absorber = event.fallback();` <br/>                                                                          `event.fallback(myFallbackAbsorber);` |
| **remove** <br/> `()`, `(event)`, <br/> `(event, absorber)`                                                                | remove all absorbers / all of specific event / specific <br/>                                                      `event.remove("write", writeDoneAbsorber);` <br/>                                                            `event.remove("write");` |

| `interface IEventAbsorber`   |                        |
|------------------------------|------------------------|
| **absorb** <br/> `(event, args)`                                                                                           | called when object implementing this interface is set as absorber <br/>                                                   one absorber can be attached to multiple events, hence `event`|

| `class EventAbsorber` | `implements IEventAbsorber`  |
|-----------------------|------------------------------|
| **EventAbsorber** <br/> `(cls, mthd)`, <br/> `(obj, mthd)`                                                                 | create an event absorber from a static on instance method <br/>                                                            `event.add("event0", new EventAbsorber(MthdCls.class, "mthd"));` <br/>                                                 `event.add("event0", new EventAbsorber(MthdObj, "mthd"));` |
| **absorb** <br/> `(event, args)`                                                                                           | absorbs an event and forwards it to method |

| `class DefaultEventAbsorber` | `implements IEventAbsorber`  |
|------------------------------|------------------------------|
| **absorb** <br/> `(event, args)`                                                                                           | absorbs event with no absorbers |
