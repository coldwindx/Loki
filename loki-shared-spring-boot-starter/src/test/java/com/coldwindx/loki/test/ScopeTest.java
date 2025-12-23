package com.coldwindx.loki.test;

import com.coldwindx.loki.LokiCoreTest;
import com.coldwindx.loki.models.User;
import com.coldwindx.loki.models.UserService;
import com.coldwindx.loki.support.SharedScopeThreadLocal;
import com.coldwindx.loki.support.SharedScoper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ScopeTest extends LokiCoreTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SharedScoper scope;

    private String KEY = "userService";

    @Test
    public void test() {
        SharedScopeThreadLocal.put(KEY, "gid-123");
        User user = userService.create();
        log.info("user={}", user);
    }

    @Test
    public void testSameConversation_SharedInstance() {
        SharedScopeThreadLocal.put(KEY, "gid-123");
        String u1 = userService.id();
        String u2 = userService.id();

        Assert.assertEquals(u1, u2);
    }

    @Test
    public void testDifferentConversations_DifferentInstances() {
        SharedScopeThreadLocal.put(KEY, "gid-1");
        String u1 = userService.id();
        SharedScopeThreadLocal.put(KEY, "gid-2");
        String u2 = userService.id();
        Assert.assertNotEquals(u1, u2);
    }

    @Test
    public void testBeanDestroyed_AfterLastSessionLeaves() throws InterruptedException {
        String gid = "gid-1";
        SharedScopeThreadLocal.put(KEY, gid);

        // 模拟两个 session 加入
        scope.join(UserService.class);
        scope.join(UserService.class);

        // 获取 bean 实例
        String u1 = userService.id();

        // 第一个 session 离开 → 不应销毁
        scope.leave(UserService.class);
        Assert.assertTrue(scope.contains(UserService.class, KEY, gid));

        // 第二个 session 离开 → 应销毁
        scope.leave(UserService.class);

        // 等待可能的异步销毁（如果是同步实现则无需等待）
        Thread.sleep(100);
        Assert.assertFalse(scope.contains(UserService.class, KEY, gid));

        // 再次访问应创建新实例（可选验证）
        log.info(">>>>");
        scope.join(UserService.class);
        String newBeanId = userService.id();
        Assert.assertNotEquals(newBeanId, u1);
        scope.leave(UserService.class);
    }

}
