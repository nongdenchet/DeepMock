package com.deepmock;

import com.deepmock.annotations.Inject;
import com.deepmock.annotations.Mock;
import com.deepmock.annotations.Stub;
import com.deepmock.annotations.Target;
import com.deepmock.exceptions.NoConstructorException;
import com.deepmock.exceptions.NoTargetException;
import com.deepmock.exceptions.NotInstantiateException;

import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DeepMockInjector {
    private final Object testObject;
    private final Class testObjectClass;
    private final HashMap<Class, Field> targets = new HashMap<>();
    private final HashMap<Class, Field> injects = new HashMap<>();
    private final HashMap<Class, Object> stubs = new HashMap<>();
    private final HashMap<Class, Object> mocks = new HashMap<>();

    DeepMockInjector(Object testObject) {
        this.testObject = testObject;
        this.testObjectClass = testObject.getClass();
    }

    void execute() throws Exception {
        setUpFields();
        providesTargets();
    }

    private void providesTargets() throws Exception {
        if (targets.isEmpty()) {
            throw new NoTargetException("You have to provide at least one target");
        }
        for (Class clazz : targets.keySet()) {
            Field field = targets.get(clazz);
            field.set(testObject, providesTarget(clazz));
        }
    }

    private Object getFromProviders(Class clazz) {
        Object type;
        if ((type = getConcreteType(stubs, clazz)) != null) {
            return type;
        } else if ((type = getConcreteType(mocks, clazz)) != null) {
            return type;
        }
        return null;
    }

    @SuppressWarnings("PMD.SystemPrintln")
    private Object providesTarget(Class clazz) throws Exception {
        Object result = getFromProviders(clazz);
        if (result != null) {
            return result;
        }

        Constructor[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            throw new NoConstructorException("No public constructor in " + clazz.getName());
        }
        for (int i = 0; i < constructors.length; i++) {
            try {
                result = instantiateTarget(clazz, constructors[i]);
                break;
            } catch (Exception exception) {
                if (i == constructors.length - 1) {
                    throw exception;
                } else {
                    System.out.println("Fail to instantiate with constructor: " + i + "th");
                }
            }
        }
        return result;
    }

    private Object instantiateTarget(Class clazz, Constructor constructor) throws Exception {
        try {
            Object[] params = instantiateParams(clazz, constructor);
            return constructor.newInstance(params);
        } catch (InstantiationException exception) {
            throw new NotInstantiateException("Can not instantiate class: "
                    + clazz.getSimpleName());
        }
    }

    private Object[] instantiateParams(Class clazz, Constructor constructor) throws Exception {
        Object result;
        Class[] parameterTypes = constructor.getParameterTypes();
        List<Object> params = new ArrayList<>(parameterTypes.length);
        for (Class parameterType : parameterTypes) {
            if ((result = getFromProviders(clazz)) != null) {
                params.add(result);
            } else if ((result = providesTarget(parameterType)) != null) {
                provideInject(result, parameterType);
                params.add(result);
            } else {
                throw new NotInstantiateException("Can not instantiate class: "
                        + parameterType.getSimpleName());
            }
        }
        return params.toArray();
    }

    private void provideInject(Object result, Class type) throws IllegalAccessException {
        if (injects.keySet().contains(type)) {
            injects.get(type).set(testObject, result);
        } else {
            for (Map.Entry<Class, Field> entry : injects.entrySet()) {
                if (type.isInstance(entry.getKey())) {
                    entry.getValue().set(testObject, result);
                    break;
                }
            }
        }
    }

    private Object getConcreteType(HashMap<Class, Object> providers, Class type) {
        if (providers.keySet().contains(type)) {
            return providers.get(type);
        }
        for (Map.Entry<Class, Object> entry : providers.entrySet()) {
            if (type.isInstance(entry.getValue())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void setUpFields() throws IllegalAccessException {
        for (Field field : testObjectClass.getDeclaredFields()) {
            field.setAccessible(true);
            Class type = field.getType();
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Target.class) {
                    targets.put(type, field);
                } else if (annotation.annotationType() == Stub.class) {
                    stubs.put(type, field.get(testObject));
                } else if (annotation.annotationType() == Mock.class) {
                    Object mock = Mockito.mock(type);
                    field.set(testObject, mock);
                    mocks.put(type, mock);
                } else if (annotation.annotationType() == Inject.class) {
                    injects.put(type, field);
                }
            }
        }
    }
}
