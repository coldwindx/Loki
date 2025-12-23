# Loki Shared Spring Boot Starter
> 支持分组共享的Spring bean容器管理

## 快速开始

1. 通过@Shared注解标记需要共享的bean，指定scope为共享分组标记

```java
@Shared(scope = "group")
public class UserService {
    // ...
}
```

2. 在需要使用共享bean的地方，通过@Autowired注入

```java
@Service
public class OrderService {
    @Autowired
    private UserService userService;
    // ...
}
```

3. 通过SharedScopeThreadLocal，设置当前线程访问的共享分组id. 此时，该线程访问的UserService bean，就是分组标记为"group"下，组"group-id-123"中的bean.
```java
SharedScopeThreadLocal.put("group", "group-id-123");
```

4. 生命周期管理。

- 强引用：通过join方法，为bean添加到一个引用计数器。
- 解除强引用：通过leave方法，将bean从当前线程的引用计数器中移除。
- 引用计数器：每个共享bean，都有一个引用计数器，记录当前线程有多少个引用。当引用计数器为0时，bean会被销毁。
- 当不使用join构建引用时，bean的引用计数器不会增加，也不会被销毁。

```java
@Autowired
private SharedScoper scoper;

// 强引用
scoper.join(UserService.class);
// 解除强引用
scoper.leave(UserService.class);
```