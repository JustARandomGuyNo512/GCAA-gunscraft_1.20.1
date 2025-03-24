package sheridan.gcaa.client.animation.recoilAnimation;

import javassist.*;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImpulseScriptGenerator {
    static Pattern pattern = Pattern.compile("\\b[A-Za-z_][A-Za-z0-9_]*\\b");
    static AtomicInteger counter = new AtomicInteger(0);
    static Map<String, Class<?>> classesCache = new WeakHashMap<>();

    public static DoubleSupplier createDoubleSupplier(NewRecoilData externalInstance, String script) throws Exception {
        Class<?> orDefault = classesCache.getOrDefault(script, null);
        if (orDefault != null) {
            return (DoubleSupplier) classesCache.get(script).getDeclaredConstructor(NewRecoilData.class)
                    .newInstance(externalInstance);
        }
        try {
            double v = Double.parseDouble(script.trim());
            return () -> v;
        } catch (Exception ignored) {}
        System.out.println("aaa");
        String code = processScript(script, externalInstance);
        String className = NewRecoilData.class.getName();
        ClassPool pool = ClassPool.getDefault();
        String classNameGen = NewRecoilData.class.getPackage().getName() + ".DynamicDoubleSupplier" + counter.getAndIncrement();
        CtClass cc = pool.makeClass(classNameGen);
        CtClass doubleSupplierInterface = pool.get("java.util.function.DoubleSupplier");
        cc.addInterface(doubleSupplierInterface);
        CtField field = new CtField(pool.get(className), "recoilDataInstance", cc);
        cc.addField(field);
        CtConstructor constructor = CtNewConstructor.make(
                "public DynamicDoubleSupplier(" + className + " recoilDataInstance) { " +
                        "this.recoilDataInstance = recoilDataInstance; " +
                        "}", cc);
        cc.addConstructor(constructor);
        CtMethod method = CtNewMethod.make(
                "public double getAsDouble() { " +
                        "return (double) (" + code + "); " +
                        "}", cc);
        cc.addMethod(method);

        byte[] byteCode = cc.toBytecode();
        Class<?> clazz = MethodHandles.lookup()
                .defineClass(byteCode);
        return (DoubleSupplier) clazz.getDeclaredConstructor(NewRecoilData.class)
                .newInstance(externalInstance);
    }

    public static String processScript(String expression, NewRecoilData externalInstance) {
        Matcher matcher = pattern.matcher(expression);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String match = matcher.group();
            String replacement = externalInstance.getScriptMapping(match);
            if (replacement != null && !replacement.isEmpty()) {
                matcher.appendReplacement(result, replacement);
            } else {
                throw new RuntimeException("Unknown variable or function: " + match);
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

}


//    byte[] byteCode = cc.toBytecode();
//        loader.addClass(classNameGen, byteCode); // 将字节码添加到自定义类加载器
//                Class<?> clazz = loader.loadClass(classNameGen); // 使用自定义类加载器加载类
//        return (DoubleSupplier) clazz.getDeclaredConstructor(NewRecoilData.class)
//        .newInstance(externalInstance);