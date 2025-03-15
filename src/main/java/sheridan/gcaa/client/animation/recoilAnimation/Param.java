package sheridan.gcaa.client.animation.recoilAnimation;

import java.math.BigDecimal;

public class Param {
    private BigDecimal decimal;
    private double value;

    public Param(String value) {
        this.decimal = new BigDecimal(value);
        this.value = decimal.doubleValue();
    }

    public void setValue(String value) {
        decimal = new BigDecimal(value);
        this.value = decimal.doubleValue();
    }

    public void setValue(double val) {
        decimal = new BigDecimal(val);
        this.value = decimal.doubleValue();
    }

    public String strVal() {
        return decimal.toString();
    }

    public double val() {
        return value;
    }

    public void copy(Param param) {
        this.decimal = new BigDecimal(param.decimal.toString());
        this.value = param.value;
    }

    public static Param of(String value) {
        return new Param(value);
    }
}
