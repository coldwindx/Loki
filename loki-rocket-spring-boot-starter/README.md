# Loki

## loki-rocket 基于注解的rocket框架
1. 开启rocket注解，指定自定义生产者和消费者的扫描路径
```java
@EnableRocket(packages={"..."})
```

2. 构建生产者，每一个@RocketConfig会构建一个生产者
```java
@RocketProvider(value={
        @RocketConfig(cluster="1", topic="1", group="1"),
        @RocketConfig(cluster="2", topic="2", group="2")
})
public class YouRocketProvider extends AbstractRocketProvider{}
```
3. 构建消费者，每一个@RocketConfig会构建一个消费者
```java
@RocketConsumer(value={
        @RocketConfig(cluster="1", topic="1", group="1", tags="1"),
        @RocketConfig(cluster="2", topic="2", group="2", tags="1")
})
public class YouRocketConsumer extends AbstractRocketConsumer{}
```
4. 生产者注入，注解放在属性上

```java
@Component
public class YouService{
    @RocketConfig(cluster="1", topic="1", group="1")
    private AbstractRocketProvider provider;
}
```
5. 消费者逻辑实现，注解放在方法上

```java

@Component
public class YouService {
    @RocketConfig(cluster = "1", topic = "1", group = "1")
    public void receive(Message<?> message) {}
}
```