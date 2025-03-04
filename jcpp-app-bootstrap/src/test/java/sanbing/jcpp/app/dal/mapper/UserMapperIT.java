/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.app.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import sanbing.jcpp.AbstractTestBase;
import sanbing.jcpp.app.dal.config.ibatis.enums.UserStatusEnum;
import sanbing.jcpp.app.dal.entity.User;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author baigod
 */
class UserMapperIT extends AbstractTestBase {
    static final UUID NORMAL_USER_ID = UUID.fromString("21cbf909-a23a-4396-840a-f34061f59f95");

    @Resource
    private UserMapper userMapper;

    @Test
    void curdTest() {
        userMapper.delete(Wrappers.lambdaQuery());

        User user = User.builder()
                .id(NORMAL_USER_ID)
                .createdTime(LocalDateTime.now())
                .additionalInfo(JacksonUtil.newObjectNode())
                .status(UserStatusEnum.ENABLE)
                .userName("sanbing")
                .userCredentials(JacksonUtil.newObjectNode())
                .build();

        userMapper.insertOrUpdate(user);

        log.info("{}", userMapper.selectById(NORMAL_USER_ID));
    }
}