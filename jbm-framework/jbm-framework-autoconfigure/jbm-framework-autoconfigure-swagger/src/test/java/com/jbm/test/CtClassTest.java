package com.jbm.test;

import cn.hutool.core.util.ReflectUtil;
import com.jbm.framework.usage.paging.DataPaging;
import com.jbm.framework.usage.paging.PageForm;
import javassist.ClassPool;
import javassist.*;
import org.junit.Test;

/**
 * @program: JBM6
 * @author: wesley.zhang
 * @create: 2020-03-19 02:12
 **/
public class CtClassTest {

    @Test
    public static void testClass(String[] args) {
        ClassPool pool = ClassPool.getDefault();
        CtClass supClass = pool.makeClass(PageForm.class.getName());
        supClass.setName(PageForm.class.getSimpleName());
//        CtClass ctClass = pool.makeClass("com.test.ctClass", supClass);
        try {
            CtField ctField = new CtField(pool.makeClass(DataPaging.class.getName()), DataPaging.class.getSimpleName(), supClass);
            ctField.setModifiers(Modifier.PUBLIC);
            System.out.println(ReflectUtil.newInstance(supClass.toClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
