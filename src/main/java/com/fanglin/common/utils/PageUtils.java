package com.fanglin.common.utils;

import com.fanglin.common.core.page.Page;
import com.github.pagehelper.PageRowBounds;
import org.apache.ibatis.session.RowBounds;


/**
 * 分页
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/8/13 11:26
 **/
public class PageUtils {
 
    /**
     * 只分页不求总条数
     *
     * @param page
     * @return
     */
    public static RowBounds page(Page page) {
        return new RowBounds(page.getPage(), page.getLimit());
    }

    /**
     * 既分页又取总条数
     *
     * @param page
     * @return
     */
    public static PageRowBounds rowBounds(Page page) {
        return new PageRowBounds(page.getPage(), page.getLimit());
    }
}
