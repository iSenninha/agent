package com.senninha.util.objsize;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * calculate obj's size
 * Coded by senninha on 2019/12/3
 */
public class ObjectSizeUtil {
    @SuppressWarnings("unused")
    private static Instrumentation instrumentation;

    public static long sizeOfObj(Object object, Set<Object> filterSet) {
        try {
            long count = 0;
            if (object == null) {
                return count;
            }
            if (!filterSet.add(object)) {
                // 1.remove duplicate count object;
                // 2.prevent infinite recursion
                return count;
            }
            Class<?> clazz = object.getClass();
            if (clazz.isArray()) {
                count = getObjectSize(object);
                if (isPrimitiveArray(clazz)) {
                    return count;
                }
                Object[] ojbArray = (Object[]) object;
                for (Object o : ojbArray) {
                    if (o == null) {
                        continue;
                    }
                    count += sizeOfObj(o, filterSet);
                }
            } else {
                count = getObjectSize(object);
                while (clazz != null) {
                    for (Field declaredField : clazz.getDeclaredFields()) {
                        if (Modifier.isStatic(declaredField.getModifiers())) {
                            continue;
                        }
                        if (declaredField.getType().isPrimitive()) {
                            continue;
                        }
                        declaredField.setAccessible(true);
                        count += sizeOfObj(declaredField.get(object), filterSet);
                    }
                    clazz = clazz.getSuperclass();
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static boolean isPrimitiveArray(Class clazz) {
        String regex = "^\\[+([ZBSIJCFD])";
        return clazz.getName().matches(regex);
    }

    private static long getObjectSize(Object object) {
        if (instrumentation == null) {
            return 1;
        }
        return instrumentation.getObjectSize(object);
    }
}