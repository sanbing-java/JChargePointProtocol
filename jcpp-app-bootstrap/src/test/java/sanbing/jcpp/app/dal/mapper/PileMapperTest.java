/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.mapper;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import sanbing.jcpp.AbstractTestBase;
import sanbing.jcpp.app.dal.config.ibatis.enums.OwnerTypeEnum;
import sanbing.jcpp.app.dal.config.ibatis.enums.PileStatusEnum;
import sanbing.jcpp.app.dal.config.ibatis.enums.PileTypeEnum;
import sanbing.jcpp.app.dal.entity.Pile;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.UUID;

import static sanbing.jcpp.app.dal.mapper.StationMapperTest.NORMAL_STATION_ID;
import static sanbing.jcpp.app.dal.mapper.UserMapperTest.NORMAL_USER_ID;

/**
 * @author baigod
 */
public class PileMapperTest extends AbstractTestBase {
    static final UUID[] NORMAL_PILE_ID = new UUID[]{
            UUID.fromString("fd7b3f60-db6c-4347-bff3-3c922985b95c"),
            UUID.fromString("fa621927-6458-4e09-9666-99c52230db2b"),
            UUID.fromString("afec0b0a-ad82-4923-97da-70e4a5d5e2c6"),
            UUID.fromString("3e45ae30-2848-4d5a-a7b8-bd8504a6713d"),
            UUID.fromString("349ff65e-ce8e-435a-928b-52fdef2828f2"),
            UUID.fromString("e60d5b2d-8014-4f8f-b828-e207e6cf4a8f"),
            UUID.fromString("8f010829-b505-4e57-8b93-6bdf981ac4e1"),
            UUID.fromString("081842e2-9e74-4abb-aeab-b2cbfeb7a335"),
            UUID.fromString("f04cf40a-0fbe-40f7-a07c-5b663ad68e98"),
            UUID.fromString("ec522751-e1d3-4117-a887-3bdae7892369")
    };

    @Resource
    PileMapper pileMapper;

    @Test
    void curdTest() {
        pileMapper.delete(Wrappers.lambdaQuery());

        for (int i = 0; i < 10; i++) {
            UUID pileId = NORMAL_PILE_ID[i];
            Pile pile = Pile.builder()
                    .id(pileId)
                    .createdTime(LocalDateTime.now())
                    .additionalInfo(JacksonUtil.newObjectNode())
                    .pileName(String.format("三丙家的%d号充电桩", i + 1))
                    .pileCode("202312120000" + new DecimalFormat("00").format(i + 1))
                    .protocol("yunkuaichongV150")
                    .stationId(NORMAL_STATION_ID)
                    .ownerId(NORMAL_USER_ID)
                    .ownerType(OwnerTypeEnum.C)
                    .brand("星星")
                    .model("10A")
                    .manufacturer("星星")
                    .status(PileStatusEnum.IDLE)
                    .type(PileTypeEnum.AC)
                    .build();

            pileMapper.insertOrUpdate(pile);

            log.info("{}", pileMapper.selectById(pileId));

        }
    }
}