package inlinedawt.shared;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FieldUtils2 {

    public static List<Field> getDeclaredFields(Class cls, boolean forceAccess) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }

        List<Field> fields = new ArrayList<>();

        for (Field f : cls.getDeclaredFields()) {
            makeAccessibleIfNeeded(f);
            fields.add(f);
        }

        return fields;
    }

    public static void makeAccessibleIfNeeded(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
    }

}
