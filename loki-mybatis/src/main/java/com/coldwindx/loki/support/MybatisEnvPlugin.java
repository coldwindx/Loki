package com.coldwindx.loki.support;

import com.coldwindx.loki.annotation.InterceptAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

@Slf4j
@Component
@Intercepts(@Signature(type= StatementHandler.class, method = "prepare", args = {java.sql.Connection.class, Integer.class}))
public class MybatisEnvPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        MetaObject metaObject = MetaObject.forObject(handler,
                SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        String sql = this.enhance(mappedStatement, boundSql);
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, sql);
        return invocation.proceed();
    }

    private String enhance(MappedStatement mappedStatement, BoundSql boundSql) {
        String sql = boundSql.getSql().toLowerCase();
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        String id = mappedStatement.getId();
        String methodName = id.substring(id.lastIndexOf(".") + 1);
        String className = id.substring(0, id.lastIndexOf("."));
        Class<?> clazz = ClassUtils.resolveClassName(className, ClassUtils.getDefaultClassLoader());

        String paramString = Optional.ofNullable(boundSql.getParameterObject()).map(Object::toString).orElse("");

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                InterceptAnnotation annotation = method.getAnnotation(InterceptAnnotation.class);
                if (annotation == null) continue;

                /// 分库分表等逻辑
                System.out.println(sql);
            }
        }
        return sql;
    }


}
