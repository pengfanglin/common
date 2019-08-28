package com.fanglin.common.core.others;

import com.fanglin.common.core.enums.BusinessEnum;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 用户自定义异常，只打印异常信息，不打印堆栈信息
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 17:57
 **/
public class BusinessException extends RuntimeException {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println(sdf.getTimeZone());
    }

    private static boolean pro = true;

    static {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pro = false;
        }
    }

    @Getter
    private int code;

    public BusinessException() {
        super("服务器异常");
        this.code = 400;
    }

    public BusinessException(BusinessEnum businessEnum) {
        super(businessEnum.getMessage());
        this.code = businessEnum.getCode();
    }

    public BusinessException(String error) {
        super(error);
        this.code = 400;
    }

    @Override
    public Throwable fillInStackTrace() {
        if (pro) {
            return this;
        } else {
            return super.fillInStackTrace();
        }
    }
}
