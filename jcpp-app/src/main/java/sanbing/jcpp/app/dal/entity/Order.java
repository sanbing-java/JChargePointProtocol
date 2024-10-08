/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
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
import sanbing.jcpp.app.dal.config.ibatis.enums.OrderStatusEnum;
import sanbing.jcpp.app.dal.config.ibatis.enums.OrderTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@TableName("jcpp_order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    @TableId(type = IdType.INPUT)
    private UUID id;

    private String internalOrderNo;

    private String externalOrderNo;

    private String pileOrderNo;

    private LocalDateTime createdTime;

    private JsonNode additionalInfo;

    private LocalDateTime updatedTime;

    private LocalDateTime cancelledTime;

    private OrderStatusEnum status;

    private OrderTypeEnum type;

    private UUID creatorId;

    private UUID stationId;

    private UUID pileId;

    private UUID gunId;

    private String plateNo;

    private Long settlementAmount;

    private JsonNode settlementDetails;

    private BigDecimal electricityQuantity;


}
