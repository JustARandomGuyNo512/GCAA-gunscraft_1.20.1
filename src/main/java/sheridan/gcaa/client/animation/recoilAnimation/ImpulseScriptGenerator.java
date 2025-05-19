package sheridan.gcaa.client.animation.recoilAnimation;

import javassist.*;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @Warning
 * 请勿在任何环境下直接调用次类，这将导致JVM泄露！！！
 * 这个工具的设计适用于加载时生成指定的有限数量的类，而不是无限生成类
 *
 * Please do not call this class directly in any environment, this will cause JVM leaks!!!
 * This tool is designed for generating specified limited number of classes at load time, not infinite generation of classes
 * */
@Deprecated
public class ImpulseScriptGenerator {
    static Pattern pattern = Pattern.compile("\\b[A-Za-z_][A-Za-z0-9_]*\\b");
    static AtomicInteger counter = new AtomicInteger(0);
    static Map<String, Class<?>> classesCache = new WeakHashMap<>();

    public static DoubleSupplier createDoubleSupplier(RecoilData externalInstance, String script) throws Exception {
        Class<?> orDefault = classesCache.getOrDefault(script, null);
        if (orDefault != null) {
            return (DoubleSupplier) classesCache.get(script).getDeclaredConstructor(RecoilData.class)
                    .newInstance(externalInstance);
        }
        try {
            double v = Double.parseDouble(script.trim());
            return () -> v;
        } catch (Exception ignored) {}
        String code = processScript(script, externalInstance);
        String className = RecoilData.class.getName();
        ClassPool pool = ClassPool.getDefault();
        String classNameGen = RecoilData.class.getPackage().getName() + ".DynamicDoubleSupplier" + counter.getAndIncrement();
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
        return (DoubleSupplier) clazz.getDeclaredConstructor(RecoilData.class)
                .newInstance(externalInstance);
    }

    public static String processScript(String expression, RecoilData externalInstance) {
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
//        loader.addClass(classNameGen, byteCode);
//                Class<?> clazz = loader.loadClass(classNameGen);
//        return (DoubleSupplier) clazz.getDeclaredConstructor(RecoilData.class)
//        .newInstance(externalInstance);