package com.fanglin.common.core.page;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/15 19:23
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
public class PageResult<T> implements Serializable {
    /**
     * 数据
     */
    private List<T> data;
    /**
     * 总记录数
     */
    private long total;
}
