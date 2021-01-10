package io.nanovc.agentsim.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A lightweight clone of the .NET Activator which creates new instances of a type.
 */
public class Activator
{

    /**
     * This will create an instance of the nth Generic Parameter Type for the specified object.
     */
    public static <T> T createInstanceOfGenericParameterType(Object obj, int parameterIndex) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        Class<?> c = obj.getClass();
        ParameterizedType pt = (ParameterizedType) c.getGenericSuperclass();
        Type[] types = pt.getActualTypeArguments();
        Class<? extends T> returnClass = (Class<? extends T>) types[parameterIndex];
        return (T) returnClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Gets the parameter type of the specified class.
     *
     * @param c              The class to interrogate.
     * @param parameterIndex The index of the parameter to query.
     * @param <T>            The expected sub class type of the parameter.
     * @return The class representing the referenced generic parameter type.
     */
    public static <T> Class<? extends T> getGenericParameterType(Class c, int parameterIndex)
    {
        ParameterizedType pt = (ParameterizedType) c.getGenericSuperclass();
        Type[] types = pt.getActualTypeArguments();
        Class<? extends T> returnClass = (Class<? extends T>) types[parameterIndex];
        return returnClass;
    }

    /**
     * Finds a class with a similar name (based on text replacement) as the one specified.
     *
     * @param c                 The class to interrogate.
     * @param searchString      The string in the objects type to replace.
     * @param replacementString The replacement string to search for.
     * @return The class of the similar type. Null if it can't be found.
     */
    public static Class getSimilarType(Class c, String searchString, String replacementString) throws ClassNotFoundException
    {
        // Get the new class name that we want:
        String className = c.getName();
        className = className.replace(searchString, replacementString);
        Class returnClass = null;

        returnClass = Class.forName(className);
        return returnClass;
    }

    /**
     * This creates an instance of the given class but replaces the given search string with the replacement string.
     * It will pass in the original instance into the constructor if there is a constructor like that.
     * This is useful for classes that follow a naming convention.
     * This avoids an annotation based reflection mechanism and instead uses a naming convention.
     *
     * @param object            The object to create a similar instance of. This will be passed into the constructor if one exists with the same type.
     * @param searchString      The string in the objects type to replace.
     * @param replacementString The replacement string to search for.
     * @return An instance of the replacement type.
     */
    public static Object createInstanceOfSimilarType(Object object, String searchString, String replacementString) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        // Get the type of the object:
        Class c = object.getClass();

        // Get the new class name that we want:
        String className = c.getName();

        // Perform the replacement:
        className = className.replace(searchString, replacementString);

        Class classToCreate;

        try
        {
            // Lookup the class that we want to create:
            classToCreate = Class.forName(className);
        }
        catch (ClassNotFoundException classNotFoundException)
        {
            // Try the alternate naming strategy where the config is an inner static class of the agent:
            className = c.getName();

            // Strip off the inner static class separator '$' if there is one:
            className = className.replace("$", "");

            // Perform the replacement:
            className = className.replace(searchString, replacementString);

            // Lookup the class that we want to create:
            classToCreate = Class.forName(className);
        }

        // Search for a constructor that takes the object:
        Constructor[] constructors = classToCreate.getConstructors();
        Constructor constructorToCall = null;
        for (int i = 0; i < constructors.length; i++)
        {
            Constructor constructor = constructors[i];
            if (constructor.getParameterCount() == 1)
            {
                // This constructor takes one argument, which is what we are looking for.
                // Check whether the argument type matches our object type:
                if (constructor.getParameterTypes()[0] == c)
                {
                    // We have a constructor that takes the type we need.
                    constructorToCall = constructor;
                }
            }
        }

        if (constructorToCall == null)
        {
            return classToCreate.getDeclaredConstructor().newInstance();
        }
        else
        {
            return constructorToCall.newInstance(object);
        }
    }

    /**
     * This creates an instance of the given class.
     *
     * @param classToCreate The class to create an instance of.
     * @return An instance of the class.
     */
    public static <T> T createInstanceOfClass(Class<? extends T> classToCreate) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        return classToCreate.getDeclaredConstructor().newInstance();
    }

}
