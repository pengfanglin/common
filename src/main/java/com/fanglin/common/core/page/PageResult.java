package com.fanglin.common.core.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/15 19:23
 **/
@Data
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
