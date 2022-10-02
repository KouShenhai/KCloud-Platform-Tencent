package org.laokou.common.function;
import java.io.Serializable;
import java.util.function.Function;
/**
 * @author Kou Shenhai
 */
@FunctionalInterface
public interface FieldFunction<T> extends Function<T,Object>, Serializable {
}
