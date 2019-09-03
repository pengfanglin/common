package com.fanglin.common.core.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 分页对象
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 17:55
 **/
@Getter
@ApiModel("分页对象")
public class Page implements Serializable {
    @ApiModelProperty("当前第几页")
    private Integer page;
    @ApiModelProperty("一页多少条")
    private Integer limit;

    public Page() {
        this.page = 1;
        this.limit = 10;
    }

    public Page(Integer page, Integer limit) {
        this.page = page == null || page <= 0 ? 1 : page;
        this.limit = limit == null || limit < 0 ? 10 : limit;
    }

    public Page setPage(Integer page) {
        this.page = page == null || page <= 0 ? 1 : page;
        return this;
    }

    public Page setLimit(Integer limit) {
        this.limit = limit == null || limit < 0 ? 10 : limit;
        return this;
    }
}
