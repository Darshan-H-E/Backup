// ---------------------------------------------
// Factory Pattern
// ---------------------------------------------
// defines an interface or abstract class for creating an object, but lets subclasses decide which class to instantiate. It promotes loose coupling by eliminating the need to bind application-specific classes into your code.
// ---------------------------------------------
// - When the creation logic is complex or involves multiple steps
// - When you want to centralize object creation
// - When you need to return objects from a family of related classes
// - When the exact type of object isn’t known until runtime
// ---------------------------------------------
//Real-World Analogy:
// Imagine a logistics company that ships different types of packages: documents, boxes, and crates. Instead of the client creating each package type directly, they ask the factory to give them the right package based on the type.
// ---------------------------------------------

// create common interface
public interface Shape {
  void draw();
}

// implement concrete classes
public class Circle implements Shape {
  public void draw() {
    SOP("Drawing a circle");
  }
}

public class Square implements Shape {
  public void draw() {
    SOP("Drawing a square");
  }
}

// define the factory
public class ShapeFactory {
  public Shape getShape(String shapeType) {
    if (shapeType == null) return null;
    if (shapeType.equalsIgnoreCase("circle")) return new Circle();
    if (shapeType.equalsIgnoreCase("square")) return new Square();
    return null;
  }
}

public class FactoryPatternDemo {
    public static void main(String[] args) {
      ShapeFactory factory = new ShapeFactory();

      Shape shape1 = factory.getShape("CIRCLE");
      shape1.draw();

      Shape shape2 = factory.getShape("square");
      shape1.draw();
    }
}

// ---------------------------------------------
// State pattern
// ---------------------------------------------
// Instead of using lots of if-else or switch statements to handle different states, the State pattern delegates behavior to state-specific classes. This makes the code cleaner, more maintainable, and easier to extend.
// ---------------------------------------------
// Real-World Analogy:
// Think of a video game character:
//     When idle, it stands still.
//     When running, it moves forward
// ---------------------------------------------

// define the state
public interface State {
  void action(Context context);
}

// create concrete states
public class StartState implements State {
  public void action(Context context) {
    SOP("Player is starting");
    context.setState(this);
  }
}

public class StopState implements State {
  public void action(Context context) {
    SOP("Player is stopping");
    context.setState(this);
  }
}

// Create context class
public class Context {
  private State state;

  public void setState(State state) {
    this.state = state;
  }

  public State getState() {
    return state;
  }
}

// Use the pattern
public class StatePatternDemo {
  public static void main(String[] args){
    Context context = new Context();

    State start = new StartState();
    start.action(context);
    SOP(context.getState());

    State stop = new StopState();
    stop.action(context);
    SOP(context.getState());
  }
}


// ---------------------------------------------
// Strategy Design Pattern
// ---------------------------------------------
// defines a family of algorithms, encapsulates each one, and makes them interchangeable
// ---------------------------------------------
// When you have multiple ways to perform a task (e.g., sorting, payment processing)
// When you want to avoid long if-else or switch statements
// When you need to change behavior dynamically at runtime
// ---------------------------------------------
//Real-World Analogy
// Imagine a navigation app: You can choose between driving, walking, or cycling routes. Each is a strategy for getting from point A to B. The app doesn’t change—just the strategy it uses
// ---------------------------------------------

// define interface
public interface PaymentStrategy {
  void pay(int amount);
}

// implement concrete stategies
public class CreditCardPayment implements PaymentStrategy {
  public void pay(int amount) {
    SOP("Paid through credit card")
  }
}

public class UPIPayment implements PaymentStrategy {
  public void pay(int amount) {
    SOP("Paid through UPI")
  }
}

// create context class
public class ShoppingCart {
  private PaymentStrategy paymentStrategy;

  public void setPaymentStrategy(PaymentStrategy strategy) {
    this.paymentStrategy = strategy;
  }

  public void checkout(int amount) {
    paymentStrategy.pay(amount);
  }
}

// use strategy pattern

public class StrategyPatternDemo {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();

        cart.setPaymentStrategy(new CreditCardPayment());
        cart.checkout(500);

        cart.setPaymentStrategy(new PayPalPayment());
        cart.checkout(300);
    }
}

// ---------------------------------------------
// Repository pattern
// ---------------------------------------------
// Real-World Analogy
// Imagine a library: You don’t go searching through shelves yourself. You ask the librarian (repository) for a book, and they handle the retrieval. You don’t care how they do it—you just get the book.
// ---------------------------------------------

// Entity
public class User {
  private int id;
  private String name;
  // Constructors, getters, setters;
}

// repository interface
public interface UserRepository {
  User findById(int id);
  List<User> findAll();
  void save(User user);
  void delete(int id);
}

// concrete impl
public class InMemoryUserRepository implements UserRepository {
  private Map<Integer, User> users = new HashMap<>();
  
  public User findById(int id){
    return users.get(id);
  }

  public List<User> findAll() {
    return new ArrayList<>(users.values());
  }

  public void save(User user) {
    users.put(user.getId(), user);
  }

  public void delete(int id){
    users.remove(id);
  }
}

// using the repository in the service
public class UserService {
  private UserRepository UserRepository = new InMemoryUserRepository();

  public void registerUser(User user) {
    UserRepository.save(user);
  }

  public void getUser(int id) {
    return UserRepository.findById(id);
  }
}

// Out of topic: With Spring Data JPA
// Spring makes this pattern even easier with JpaRepository:

public interface UserRepository extends JpaRepository<User, integer> {
  List<User> findByName(String name);
}


// ---------------------------------------------
// Observer pattern
// ---------------------------------------------
// Real-World Analogy
// defines a one-to-many dependency between objects so that when one object (the subject) changes state, all its dependents (the observers) are notified and updated automatically.
// ---------------------------------------------
//Real-World Analogy
// Think of a YouTube channel:
//     The channel is the subject.
//     Subscribers are the observers.
//     When the channel uploads a new video, all subscribers get notified.
// ---------------------------------------------

// define obsever interface
public interface Observer {
  void update(float temp, float humidity);
}

// define subject interface
public interface Subject {
  void registerObserver(Observer o);
  void removeObserver(Observer o);
  void notifyObservers();
}

// concrete subject
public class WeatherStation implements Subject {
  private List<Observer> observers = new ArrayList<>();
  private float temp;
  private float humidity;

  public void registerObserver(Observer o) {
    observers.add(o);
  }
  
  public void removeObserver(Observer o) {
    observers.remove(o);
  }

  public void notifyObservers() {
    for (Observer o: observers) {
      o.update(temp, humidity);
    }
  }

  public void setMeasurementsForTheDay(float temp, float humid) {
    this.temp = temp;
    this.humidity = humid;
    notifyObservers();
  }
}

// concrete observers
public class CurrentConditionsDisplay implements Observer {
    public void update(float temperature, float humidity) {
        System.out.println("Current conditions: " + temperature + "°C and " + humidity + "% humidity");
    }
}

// use the pattern
public class ObserverPatternDemo {
    public static void main(String[] args) {
      WeatherStation station = new WeatherStation();
      Observer display = new CurrentConditionsDisplay();

      station.registerObserver(display);
      station.setMeasurementsForTheDay(28.5f, 65.0f);
    }
}

// ---------------------------------------------
// The Publish–Subscribe (Pub/Sub) Pattern is a powerful messaging pattern used to decouple components in a system. It allows senders (publishers) to broadcast messages without knowing who will receive them, and receivers (subscribers) to listen for messages without knowing who sent them.
// ---------------------------------------------
// In Pub/Sub:
// Publishers emit events or messages.
//     Subscribers register interest in specific types of events.
//     A message broker or event bus handles the delivery of messages from publishers to subscribers.
// ---------------------------------------------
// This pattern is ideal for event-driven architectures, real-time systems, and distributed applications.
//
//  Feature   	  |    Observer Pattern    	        |     Pub/Sub Pattern
// Coupling	Tight |  (subject knows observers)	    |   Loose (publisher doesn't know subscribers)
// Communication	|  Direct method calls	          |   Via message/event bus
// Scope	        |  Usually within one application |	  Can span across systems/networks

// define event
public class MessageEvent {
    private String content;

    public MessageEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

// define subscriber interface
public interface Subscriber {
    void onMessage(MessageEvent event);
}

// Create the Event Bus
public class EventBus {
    private List<Subscriber> subscribers = new ArrayList<>();

    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void publish(MessageEvent event) {
        for (Subscriber subscriber : subscribers) {
            subscriber.onMessage(event);
        }
    }
}

// Implement Subscribers
public class EmailService implements Subscriber {
    public void onMessage(MessageEvent event) {
        System.out.println("EmailService received: " + event.getContent());
    }
}

public class LoggingService implements Subscriber {
    public void onMessage(MessageEvent event) {
        System.out.println("LoggingService logged: " + event.getContent());
    }
}

// use the pattern
public class PubSubDemo {
    public static void main(String[] args) {
        EventBus bus = new EventBus();

        bus.subscribe(new EmailService());
        bus.subscribe(new LoggingService());

        bus.publish(new MessageEvent("User signed up"));
        bus.publish(new MessageEvent("Payment received"));
    }
}
