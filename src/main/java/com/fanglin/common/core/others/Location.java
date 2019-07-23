package com.fanglin.common.core.others;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 经纬度
 * @author 彭方林
 * @date 2019/4/2 14:27
 * @version 1.0
 **/
@Setter
@Getter
@Accessors(chain = true)
public class Location {
	/**
	 * 经度
	 */
	private String longitude;
	/**
	 * 纬度
	 */
	private String latitude;
}
