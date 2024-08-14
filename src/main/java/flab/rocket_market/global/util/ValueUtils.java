package flab.rocket_market.global.util;

public class ValueUtils {
    public static <T> T getNonNullValue(T newValue, T oldValue) {
        return newValue == null || (newValue instanceof String && ((String) newValue).isEmpty()) ? oldValue : newValue;
    }
}
