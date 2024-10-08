/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JCPPPair<S, T> {
    private S first;
    private T second;

    public static <S, T> JCPPPair<S, T> of(S first, T second) {
        return new JCPPPair<>(first, second);
    }
}
