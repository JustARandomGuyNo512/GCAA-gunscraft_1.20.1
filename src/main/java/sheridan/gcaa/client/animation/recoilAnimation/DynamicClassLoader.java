package sheridan.gcaa.client.animation.recoilAnimation;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class DynamicClassLoader extends ClassLoader {
    private final Map<String, byte[]> classBytes = new HashMap<>();

    public DynamicClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void addClass(String name, byte[] bytes) {
        classBytes.put(name, bytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classBytes.get(name);
        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
}