package com.splittree.annotation;

import com.splittree.penum.PropagationEnum;
import com.splittree.penum.TccPatternEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yangmingquan on 2018/9/30.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Tcc {
    /**
     * spring事务传播.
     * @return {@linkplain PropagationEnum}
     */
    PropagationEnum propagation() default PropagationEnum.PROPAGATION_REQUIRED;

    /**
     * tcc框架确认方法 tcc中第一个c.
     *
     * @return confirm方法名称
     */
    String confirmMethod() default "";

    /**
     * tcc框架确认方法 tcc中第二个c.
     *
     * @return cancel方法名称
     */
    String cancelMethod() default "";

    /**
     * 模式 tcc 和cc模式.
     * tcc模式代表try中有数据库操作，try需要回滚.
     * cc模式代表try中无数据库操作，try不需要回滚.
     *
     * @return {@linkplain TccPatternEnum}
     */
    TccPatternEnum pattern() default TccPatternEnum.TCC;
}
