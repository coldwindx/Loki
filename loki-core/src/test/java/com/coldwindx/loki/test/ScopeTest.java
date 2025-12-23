package com.coldwindx.loki.test;

import com.coldwindx.loki.LokiCoreTest;
import com.coldwindx.loki.context.SharedScopeContext;
import com.coldwindx.loki.models.User;
import com.coldwindx.loki.models.UserService;
import com.coldwindx.loki.scope.SharedScoper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

@Slf4j
public class ScopeTest extends LokiCoreTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SharedScoper scope;

    @Test
    public void test() {
        SharedScopeContext.setSharedGroupId("123");
        User user = userService.create();
        log.info("user={}", user);
    }

    // 工具方法：在指定 conversation 上下文中执行操作
    private <T> T withSharedGroup(String gid, Supplier<T> action) {
        try {
            SharedScopeContext.setSharedGroupId(gid);
            return action.get();
        } finally {
            SharedScopeContext.clear();
        }
    }

    @Test
    public void testSameConversation_SharedInstance() {
        String gid = "gid-1";

        String u1 = withSharedGroup(gid, () -> userService.id());
        String u2 = withSharedGroup(gid, () -> userService.id());

        Assert.assertEquals(u1, u2);
    }

    @Test
    public void testDifferentConversations_DifferentInstances() {
        String u1 = withSharedGroup("gid-1", () -> userService.id());
        String u2 = withSharedGroup("gid-2", () -> userService.id());
        Assert.assertNotEquals(u1, u2);
    }

    @Test
    public void testBeanDestroyed_AfterLastSessionLeaves() throws InterruptedException {
        String gid = "gid-1";
        // 模拟两个 session 加入
        scope.join(UserService.class);
        scope.join(UserService.class);

        // 获取 bean 实例
        String u1 = withSharedGroup(gid, userService::id);

        // 第一个 session 离开 → 不应销毁
        scope.left(UserService.class, gid);
        Assert.assertTrue(scope.contains(UserService.class, gid));

        // 第二个 session 离开 → 应销毁
        scope.left(UserService.class, gid);

        // 等待可能的异步销毁（如果是同步实现则无需等待）
        Thread.sleep(100);
        Assert.assertFalse(scope.contains(UserService.class, gid));

        // 再次访问应创建新实例（可选验证）
        log.info(">>>>");
        scope.join(UserService.class, gid);
        String newBeanId = withSharedGroup(gid, userService::id);
        Assert.assertNotEquals(newBeanId, u1);
        scope.left(UserService.class, gid);
    }

}
