package sheridan.gcaa.client.animation.recoilAnimation;

import java.util.*;
import java.util.function.DoubleSupplier;

@Deprecated
public class ImpulseScriptParser {
    public DoubleSupplier parse(String expression, NewRecoilData newRecoilData) {
        List<String> tokens = tokenize(expression);
        List<String> rpn = toRPN(tokens);
        return evaluateRPN(rpn, newRecoilData);
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        for (char c : expression.toCharArray()) {
            if (Character.isWhitespace(c)) continue;
            if (Character.isLetterOrDigit(c) || c == '.') {
                token.append(c);
            } else {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(Character.toString(c));
            }
        }
        if (token.length() > 0) tokens.add(token.toString());
        return tokens;
    }

    private List<String> toRPN(List<String> tokens) {
        Map<String, Integer> precedence = Map.of("+", 1, "-", 1, "*", 2, "/", 2);
        List<String> output = new ArrayList<>();
        Deque<String> operators = new ArrayDeque<>();

        for (String token : tokens) {
            if (Character.isLetterOrDigit(token.charAt(0))) {
                output.add(token);
            } else if ("+-*/".contains(token)) {
                while (!operators.isEmpty() && precedence.getOrDefault(operators.peek(), 0) >= precedence.get(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else if ("(".equals(token)) {
                operators.push(token);
            } else if (")".equals(token)) {
                while (!operators.isEmpty() && !"(".equals(operators.peek())) {
                    output.add(operators.pop());
                }
                operators.pop();
            }
        }
        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }
        return output;
    }

    public DoubleSupplier evaluateRPN(List<String> rpnTokens, NewRecoilData newRecoilData) {
        System.out.println(Arrays.toString(rpnTokens.toArray()));
        Deque<Object> stack = new ArrayDeque<>();
        for (String token : rpnTokens) {
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isVariable(token)) {
                stack.push(getVariables(token, newRecoilData));
            } else {
                Object b = stack.pop();
                Object a = stack.pop();
                if (a instanceof Double && b instanceof Double) {
                    double result = switch (token) {
                        case "+" -> (double) a + (double) b;
                        case "-" -> (double) a - (double) b;
                        case "*" -> (double) a * (double) b;
                        case "/" -> (double) a / (double) b;
                        default -> throw new IllegalStateException("Unexpected operator: " + token);
                    };
                    stack.push(result);
                } else {
                    DoubleSupplier supplier;
                    if (a instanceof DoubleSupplier supplierA && b instanceof Double bVal) {
                        if (bVal == 0.0 && "+-".contains(token)) {
                            stack.push(supplierA);
                            continue;
                        }
                        switch (token) {
                            case "+" -> supplier = () -> supplierA.getAsDouble() + bVal;
                            case "-" -> supplier = () -> supplierA.getAsDouble() - bVal;
                            case "*" -> supplier = () -> supplierA.getAsDouble() * bVal;
                            case "/" -> supplier = () -> supplierA.getAsDouble() / bVal;
                            default -> throw new IllegalStateException("Unexpected operator: " + token);
                        }
                    }
                    else if (a instanceof Double aVal && b instanceof DoubleSupplier supplierB) {
                        if (aVal == 0.0 && "+-".contains(token)) {
                            stack.push(supplierB);
                            continue;
                        }
                        switch (token) {
                            case "+" -> supplier = () -> aVal + supplierB.getAsDouble();
                            case "-" -> supplier = () -> aVal - supplierB.getAsDouble();
                            case "*" -> supplier = () -> aVal * supplierB.getAsDouble();
                            case "/" -> supplier = () -> aVal / supplierB.getAsDouble();
                            default -> throw new IllegalStateException("Unexpected operator: " + token);
                        }
                    }
                    else if (a instanceof DoubleSupplier supplierA && b instanceof DoubleSupplier supplierB) {
                        switch (token) {
                            case "+" -> supplier = () -> supplierA.getAsDouble() + supplierB.getAsDouble();
                            case "-" -> supplier = () -> supplierA.getAsDouble() - supplierB.getAsDouble();
                            case "*" -> supplier = () -> supplierA.getAsDouble() * supplierB.getAsDouble();
                            case "/" -> supplier = () -> supplierA.getAsDouble() / supplierB.getAsDouble();
                            default -> throw new IllegalStateException("Unexpected operator: " + token);
                        }
                    } else {
                        throw new RuntimeException("Unknown Type: " + a + " " + b);
                    }
                    stack.push(supplier);
                }
            }
        }
        return (DoubleSupplier) stack.pop();
    }


    private boolean isVariable(String str) {
        return Character.isLetter(str.charAt(0));
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public DoubleSupplier getVariables(String name, NewRecoilData newRecoilData) {
        return () -> 0;
    }
}
