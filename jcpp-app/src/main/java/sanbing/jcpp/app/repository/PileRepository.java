/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.repository;

import sanbing.jcpp.app.dal.entity.Pile;

/**
 * @author baigod
 */
public interface PileRepository {

    Pile findPileByCode(String pileCode);
}