package com.fanglin.common.core.others;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * excel列信息
 * @author 彭方林
 * @date 2019/4/2 17:58
 * @version 1.0
 **/
@Data
@Accessors(chain = true)
public class Excel {
	/**
	 * 标题
	 */
	private String name;
	/**
	 * 取值字段
	 */
	private String key;

	public Excel(String name, String key){
		this.name=name;
		this.key=key;
	}
}
