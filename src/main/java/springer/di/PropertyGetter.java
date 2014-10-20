package springer.di;

import java.util.Map;

/**
 * Utility class to easily create component source from a key-value map.
 *
 * @param <K> component key type in dependency graph
 */
public class PropertyGetter<K> {

    private final Map<?, ?> properties;
    private final Class<K> componentKeyClass;

    /**
     * Construct a <tt>PropertyGetter</tt> instance from a map of property key-value pairs and component key type.
     * @param properties a map of key-value pairs
     * @param componentKeyClass component type
     */
    public PropertyGetter(Map<?, ?> properties, Class<K> componentKeyClass) {
        this.properties = properties;
        this.componentKeyClass = componentKeyClass;
    }

    /**
     * Given a property key, retrieve the corresponding value and return a component source wrapper of the value.
     * @param key property key
     * @return component source that always returns the corresponding property value
     */
    public IComponentSource<?, K> get(Object key) {
        return DI.constantly(properties.get(key), componentKeyClass);
    }

    /**
     * Given a property key, retrieve the corresponding <tt>String</tt> value and return a component source wrapper of
     * the value.
     * @param key property key
     * @return component source that always returns the corresponding property value as <tt>String</tt>
     */
    public IComponentSource<String, K> getString(Object key) {
        final Object value = properties.get(key);
        if (value instanceof String || value == null) {
            return DI.constantly((String) value, componentKeyClass);
        }
        return DI.constantly(value.toString(), componentKeyClass);
    }

    /**
     * Given a property key, retrieve the corresponding <tt>Boolean</tt> value and return a component source wrapper of
     * the value.
     * @param key property key
     * @return component source that always returns the corresponding property value as <tt>Boolean</tt>
     */
    public IComponentSource<Boolean, K> getBoolean(Object key) {
        final Object value = properties.get(key);
        if (value instanceof Boolean) {
            return DI.constantly((Boolean) value, componentKeyClass);
        }
        if (value instanceof String) {
            return DI.constantly(Boolean.parseBoolean((String) value), componentKeyClass);
        }
        throw new IllegalArgumentException(String.format(
                "Corresponding value for key '%s' can not be parsed as Boolean: %s", key, value));
    }

    /**
     * Given a property key, retrieve the corresponding <tt>Integer</tt> value and return a component source wrapper of
     * the value.
     * @param key property key
     * @return component source that always returns the corresponding property value as <tt>Integer</tt>
     */
    public IComponentSource<Integer, K> getInteger(Object key) {
        final Object value = properties.get(key);
        if (value instanceof Number) {
            return DI.constantly(((Number) value).intValue(), componentKeyClass);
        }
        if (value instanceof String) {
            return DI.constantly(Integer.parseInt((String) value), componentKeyClass);
        }
        throw new IllegalArgumentException(String.format(
                "Corresponding value for key '%s' can not be parsed as Integer: %s", key, value));
    }

    /**
     * Given a property key, retrieve the corresponding <tt>Long</tt> value and return a component source wrapper of
     * the value.
     * @param key property key
     * @return component source that always returns the corresponding property value as <tt>Long</tt>
     */
    public IComponentSource<Long, K> getLong(Object key) {
        final Object value = properties.get(key);
        if (value instanceof Number) {
            return DI.constantly(((Number) value).longValue(), componentKeyClass);
        }
        if (value instanceof String) {
            return DI.constantly(Long.parseLong((String) value), componentKeyClass);
        }
        throw new IllegalArgumentException(String.format(
                "Corresponding value for key '%s' can not be parsed as Long: %s", key, value));
    }

    /**
     * Given a property key, retrieve the corresponding <tt>Float</tt> value and return a component source wrapper of
     * the value.
     * @param key property key
     * @return component source that always returns the corresponding property value as <tt>Float</tt>
     */
    public IComponentSource<Float, K> getFloat(Object key) {
        final Object value = properties.get(key);
        if (value instanceof Number) {
            return DI.constantly(((Number) properties.get(key)).floatValue(), componentKeyClass);
        }
        if (value instanceof String) {
            return DI.constantly(Float.parseFloat((String) value), componentKeyClass);
        }
        throw new IllegalArgumentException(String.format(
                "Corresponding value for key '%s' can not be parsed as Float: %s", key, value));
    }

    /**
     * Given a property key, retrieve the corresponding <tt>Double</tt> value and return a component source wrapper of
     * the value.
     * @param key property key
     * @return component source that always returns the corresponding property value as <tt>Double</tt>
     */
    public IComponentSource<Double, K> getDouble(Object key) {
        final Object value = properties.get(key);
        if (value instanceof Number) {
            return DI.constantly(((Number) value).doubleValue(), componentKeyClass);
        }
        if (value instanceof String) {
            return DI.constantly(Double.parseDouble((String) value), componentKeyClass);
        }
        throw new IllegalArgumentException(String.format(
                "Corresponding value for key '%s' can not be parsed as Double: %s", key, value));
    }

}
