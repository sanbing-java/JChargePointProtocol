/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import sanbing.jcpp.app.dal.entity.Pile;

/**
 * @author baigod
 */
public interface PileMapper extends BaseMapper<Pile> {

    @Select("SELECT " +
            " *  " +
            "FROM " +
            " jcpp_pile  " +
            "WHERE " +
            " pile_code = #{pileCode}")
    Pile selectByCode(String pileCode);
}