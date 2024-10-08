/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.mapper;

import cn.hutool.core.math.Money;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import sanbing.jcpp.AbstractTestBase;
import sanbing.jcpp.app.dal.config.ibatis.enums.OrderStatusEnum;
import sanbing.jcpp.app.dal.config.ibatis.enums.OrderTypeEnum;
import sanbing.jcpp.app.dal.entity.Order;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static sanbing.jcpp.app.dal.mapper.GunMapperTest.NORMAL_GUN_ID;
import static sanbing.jcpp.app.dal.mapper.PileMapperTest.NORMAL_PILE_ID;
import static sanbing.jcpp.app.dal.mapper.StationMapperTest.NORMAL_STATION_ID;
import static sanbing.jcpp.app.dal.mapper.UserMapperTest.NORMAL_USER_ID;

/**
 * @author baigod
 */
public class OrderMapperTest extends AbstractTestBase {

    @Resource
    OrderMapper orderMapper;

    @Test
    void testOrderMapper() {
        orderMapper.delete(Wrappers.lambdaQuery());

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .internalOrderNo(IdUtil.getSnowflake(1, 1).nextIdStr())
                .externalOrderNo(IdUtil.getSnowflake(1, 1).nextIdStr())
                .pileOrderNo(RandomStringUtils.randomNumeric(16))
                .createdTime(LocalDateTime.now())
                .additionalInfo(JacksonUtil.newObjectNode())
                .updatedTime(LocalDateTime.now())
                .cancelledTime(null)
                .status(OrderStatusEnum.IN_CHARGING)
                .type(OrderTypeEnum.CHARGE)
                .creatorId(NORMAL_USER_ID)
                .stationId(NORMAL_STATION_ID)
                .pileId(NORMAL_PILE_ID[0])
                .gunId(NORMAL_GUN_ID[0])
                .plateNo("浙A88888")
                .settlementAmount(new Money(100D).getCent())
                .settlementDetails(JacksonUtil.newObjectNode())
                .electricityQuantity(new BigDecimal("100"))
                .build();

        orderMapper.insertOrUpdate(order);

        log.info("{}", orderMapper.selectById(order.getId()));

    }
}