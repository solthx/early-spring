package org.earlyspring.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * @author czf
 * @Date 2020/5/8 9:35 下午
 */
public class ClassUtilTest {

    @Test
    @DisplayName("得到包下所有class对象的方法")
    public void scanPackageTest(){
        Set<Class<?>> classes = ClassUtil.scanPackage("com.web.entity");
        System.out.println(classes);
        Assertions.assertEquals(4, classes.size());
    }
}
