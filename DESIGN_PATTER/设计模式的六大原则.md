
# 设计模式的六大原则

- [单一职责原则](#单一职责原则)
- [接口隔离原则](#接口隔离原则)
- [开放封闭原则](#开放封闭原则)
- [里氏替换原则](#里氏替换原则)
- [依赖倒置原则](#依赖倒置原则)
- [迪米特法则](#迪米特法则)

### 单一职责原则

核心思想：一个类或接口只能赋予一种功能，它只能因为一种原因引起变更，不应该把多种功能赋予一个类或接口。

具体例子

```Java
public interface UserService {
    
    public void login(String username, String password);
    public void register(String email, String username, String password);
    public void logError(String msg);
    public void sendEmail(String email);
    
}
```

这段代码很显然存在很大的问题，UserService 既要负责用户的注册和登录，还要负责日志的记录和邮件的发送，并且后者的行为明显区别于前者。这就相当于一个员工既要编代码，中午还要给公司全体员工做午饭，并且公司的卫生也由他负责。

进行更改

UserService
```Java
public interface UserService {

    public void login(String username, String password);
    public void register(String email, String username, String password);

}
```

LogService
```Java
public interface LogService {

    public void logError(String msg);

}
```

EmailService
```Java
public interface EmailService {

    public void sendEmail(String email);

}
```

优点：职责单一原则给我带来最直观的感受是，类的复杂度降低了，并且，这种设计模式可以帮助定位每个类的使能。

### 接口隔离原则

核心思想：建立单一接口，不要建立庞大臃肿的接口，尽量细化接口，接口中的方法尽量属于同一种，但也不要让接口过于细化。

这个原则跟单一职责原则很像，都是为了精细化管理，将功能尽可能的细化。

但要注意不要过于细化接口，比如 单一职责原则 中的例子，如果将 UserService 再拆分为 LoginService 及 RegisterService，则不太合适。

### 开放封闭原则

核心思想：当需求变化或新增时，尽量通过新增代码块来实现，不通过修改原有代码实现。

吐槽：“开放封闭原则” 这个词实在起的不好，与核心思想不好关联起来，叫 “代码不变原则” 可能更好。

具体例子

Rectangle
```Java
// 矩形
public class Rectangle {

    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }

}
```

AreaCalculator
```Java
// 面积计算器
public class AreaCalculator {

    public double area(Rectangle shape){
        return shape.getWidth() * shape.getHeight();
    }

}
```

上面代码完全可以完成矩形面积的计算，但是，这时有一个新的需求，让我们计算圆形的面积，我们可以这样更改 AreaCalculator 代码，来满足这个需求：

Circular
```Java
// 圆形
public class Circular {

    public double getRadius(){
        return radius;
    }
    
}
```

更改后的 AreaCalculator
```Java
public class AreaCalculator {

    public double area(Object shape){
        if(shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            return rectangle.getWidth() * rectangle.getHeight();
        } else if (shape instanceof Circular) {
            Circular circular = (Circular) shape;
            return circular.getRadius() * circular.getRadius() * Math.PI;
        } else {
            throw new RuntimeException("There is no such type.");
        }
    }

}
```

这么更改完成，完全没有问题。但是在真实的生产环境中，情况更为复杂，更改涉及的部分较多，那样就可能导致牵一发动全身。并且，以前编写的经过测试的一些功能需要重新测试，甚至导致某些功能不可用。

进行改进

Shape
```Java
public interface Shape {

    public double area();

}
```

Rectangle
```Java
public class Rectangle implements Shape{

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double area() {
        return getWidth() * getHeight();
    }
    
}
```

这样，当需求变更，需要计算圆形面积的时候，我们只需创建一个圆形的类，并实现 Shape 接口即可

Circular
```Java
public class Circular implements Shape {

    public double getRadius(){
        return radius;
    }

    public double area() {
        return getRadius() * getRadius() * Math.PI;
    }
}
```

计算三角形面积、四边形面积、六边形面积等的时候，我们只需让它们去实现 Shape 接口即可，无需修改源代码。

优点：一般编写完的代码，都是经过精心设计和测试过的，如果我们对其进行修改，就需要重新测试，这个测试可能涉及到所有依赖这个方法的类，如果大量修改源代码的话，那就是个可观的工程。所以，开放封闭原则可以在保证我们代码的质量的前提下，实现功能的扩展，减少开发难度。

### 里氏替换原则

核心思想：在使用基类的地方可以任意使用其子类，能保证子类完美替换基类。

如果子类不能完整地实现父类的方法，或者父类的某些方法在子类中已经发生“畸变”，则建议断开父子继承关系 采用依赖、聚合、组合等关系代替继承。

比如鸟类有个 fly() 方法，企鹅也是鸟类的一种，但是企鹅如果继承鸟类的话，就必须得实现 fly() 方法，这时就产生了一种"畸形"，这种情况应该断开继承关系。如果强行继承的话，子类并无法完全替代父类，程序就有可能因此出现故障，比如一只企鹅在天上翱翔。

### 依赖倒置原则

核心思想：高层类不应该依赖底层类，二者都该依赖其抽象类或接口；抽象类或接口不应该依赖实现类，实现类应该依赖抽象类或接口。

两个实现类（既非抽象类又非接口）之间不应该相互依赖，它们应该都去依赖抽象类或接口。

具体例子

IntelCPU
```Java
public class IntelCPU {

    public int add(int a, int b) {
        return  a + b;
    }

}
```

Mainboard
```Java
// 主板
public class Mainboard {
    // 装配英特尔 CPU
    public void setCPU(IntelCPU cpu) {
        this.cpu = cpu;
    }

}
```

当某一天，CPU 需要更换的时候，我们只能装配英特尔 CPU。

虽然这个例子比较简单，但是在实际的开发中，我们经常会被眼前的需求所蒙蔽，而不去思考拓展性，导致每次来个新需求，都要违背开闭原则。

进行改进

CPU
```Java
public interface CPU {

    public int add(int a, int b);

}
```

IntelCPU
```Java
public class IntelCPU implements CPU {

    public int add(int a, int b) {
        return  a + b;
    }

}
```

AmdCPU
```Java
public class AmdCPU implements CPU {

    public int add(int a, int b) {
        return a + b - b + b;
    }
    
}
```

Mainboard
```Java
public class Mainboard {

    public void setCPU(CPU cpu) {
        this.cpu = cpu;
    }

}
```

更严格来说，Mainboard 应该也实现一个 interface。

优点：当需求变更的时候，我们可以很灵活的进行扩展，而不用破坏开闭原则。

### 迪米特法则

核心思想：降低类间的耦合性，如果两个类不必彼此通信，那么，这两个类就不要发生直接的作用。

具体例子

Phone
```Java
public class Phone {

    public void seeMovie(Movie movie) {
        String title = movie.getTitle();
        long totalTime = movie.getTotalTime();
        // ...
    }

}
```

Movie
```Java
public class Movie {

    public void getTitle() {
        // ...
    }

    public void getTotalTime() {
        // ...
    }
}
```

电影和我们的电话并没有直接的关系，这两个类的耦合度过高，也就是，电话类需要知道电影类的具体实现细节，这两个类之间没有必要进行直接的通信。

MovieApp
```Java
public interface MovieApp {

    public void seeMovie(Movie movie);

}
```

Phone
```Java
public class Phone {

    private MovieApp movieApp;

    public void setMovieApp(MovieApp movieApp) {
        this.movieApp = movieApp;
    }

    public void seeMovie(Movie movie) {
        movieApp.seeMovie(movie);
    }

}
```

优点：低耦合、低耦合、低耦合...

### Reference

- [简书 快速理解-设计模式六大原则](https://www.jianshu.com/p/807bc228dbc2)
- [简书 设计模式六大原则](https://www.jianshu.com/p/1423193f5598)
