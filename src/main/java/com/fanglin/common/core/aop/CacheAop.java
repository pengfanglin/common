package com.fanglin.common.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存切面类，首先从缓存中取数据，数据存在返回缓存数据，否则去数据库取
 *
 * @author 方林
 */
@Component
@ConditionalOnClass(ExpressionParser.class)
@Slf4j
public class CacheAop {

    /**
     * 解析el表达式生成缓存的key
     *
     * @param args 目标方法参数
     * @param key  表达式
     * @return
     */
    protected String getCacheKey(Method method, Object[] args, String key) {
        //创建SpringEL表达式转换器
        ExpressionParser parser = new SpelExpressionParser();
        //Spring
        EvaluationContext context = new StandardEvaluationContext();
        //获取目标方法参数名
        String[] paramNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
        if (paramNames == null) {
            return "cache:" + key;
        }
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        Expression expression = parser.parseExpression(key);
        Object value = expression.getValue(context);
        return "cache:" + (value == null || "".equals(value) ? key : value.toString());
    }

    /**
     * 时间单位转换为毫秒数
     *
     * @param time 时间
     * @param unit 单位
     * @return
     */
    protected long parseTime(long time, TimeUnit unit) {
        switch (unit) {
            case DAYS:
                time = time * 24 * 3600 * 1000;
                break;
            case HOURS:
                time = time * 3600 * 1000;
                break;
            case MINUTES:
                time = time * 60 * 1000;
                break;
            case SECONDS:
                time = time * 1000;
                break;
            case MILLISECONDS:
                break;
            default:
                throw new RuntimeException("不支持的时间单位");
        }
        return time;
    }
}

