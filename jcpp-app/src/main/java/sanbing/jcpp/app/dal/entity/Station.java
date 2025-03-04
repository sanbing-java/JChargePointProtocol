/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.app.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sanbing.jcpp.app.dal.config.ibatis.enums.OwnerTypeEnum;
import sanbing.jcpp.app.dal.config.ibatis.enums.StationStatusEnum;
import sanbing.jcpp.infrastructure.cache.HasVersion;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@TableName("jcpp_station")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Station implements Serializable, HasVersion {

    @TableId(type = IdType.INPUT)
    private UUID id;

    private LocalDateTime createdTime;

    private JsonNode additionalInfo;

    private String stationName;

    private String stationCode;

    private UUID ownerId;

    private Float longitude;

    private Float latitude;

    private OwnerTypeEnum ownerType;

    private String province;

    private String city;

    private String county;

    private String address;

    private StationStatusEnum status;

    private Integer version;

}
