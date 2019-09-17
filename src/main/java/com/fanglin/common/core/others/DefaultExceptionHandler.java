package com.fanglin.common.core.others;

import com.fanglin.common.util.JsonUtils;
import com.fanglin.common.util.OthersUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


/**
 * 默认全局异常捕获（用户需要继承这个类，加上@ControllerAdvice注解，并调用父类的构造方法）
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 14:16
 **/
@Slf4j
public class DefaultExceptionHandler {

    private static boolean pro = true;

    public DefaultExceptionHandler() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pro = false;
        }
    }

    /**
     * 必传参数未传异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Ajax handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return Ajax.error("【" + e.getParameterName() + "】必传，参数类型:【" + e.getParameterType() + "】");
    }

    /**
     * 请求方式不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public Ajax handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = e.getMethod() + "不支持" + (e.getSupportedMethods() == null ? "" : "请使用" + Arrays.toString(e.getSupportedMethods()));
        return Ajax.status(405, message);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Ajax handleBusinessException(BusinessException e) {
        if (!pro) {
            e.printStackTrace();
        }
        return Ajax.status(e.getCode(), e.getMessage());
    }


    /**
     * 业务异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Ajax handleBindException(BindException e) {
        //获取异常内容
        FieldError fieldError = e.getFieldError();
        if (fieldError == null) {
            return Ajax.error("参数格式非法");
        }
        Class clazz = e.getFieldType(fieldError.getField());
        if (clazz == null) {
            return Ajax.error("参数格式非法");
        }
        String hopeType;
        switch (clazz.getSimpleName()) {
            case "Integer":
            case "int":
            case "Long":
            case "long":
                hopeType = "整数";
                break;
            case "Boolean":
            case "boolean":
                hopeType = "布尔类型";
                break;
            case "Double":
            case "double":
            case "Float":
            case "float":
                hopeType = "小数";
                break;
            case "Date":
                hopeType = "日期";
                break;
            default:
                hopeType = clazz.getSimpleName();
                break;
        }
        //用户输入的内容
        Object input = fieldError.getRejectedValue();
        if (input == null) {
            return Ajax.error("参数格式非法");
        }
        return Ajax.error("【" + input.toString() + "】格式非法,请输入【" + hopeType + "】");
    }

    /**
     * 默认异常处理
     *
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public Ajax handleException(Exception e, HttpServletRequest request) {
        if (pro) {
            log.warn("异常原因:{},请求参数:{}", e.getMessage(), JsonUtils.objectToJson(OthersUtils.readRequestParams(request)));
        } else {
            e.printStackTrace();
        }
        return Ajax.error("服务器错误");
    }

    @ExceptionHandler
    @ResponseBody
    public Ajax handleException(MethodArgumentTypeMismatchException e) {
        return Ajax.error(String.format("方法参数类型不匹配,参数名[%s],类型[%s]", e.getName(), e.getParameter().getParameterType().getSimpleName()));
    }

    @ExceptionHandler
    @ResponseBody
    public Ajax handleException(MissingRequestHeaderException e) {
        return Ajax.error(String.format("请求头缺少参数,参数名[%s],类型[%s]", e.getHeaderName(), e.getParameter().getParameterType().getSimpleName()));
    }

    /**
     * 唯一索引冲突
     */
    @ExceptionHandler
    @ResponseBody
    public Ajax handleException(DuplicateKeyException e) {
        return Ajax.error("记录已存在");
    }
}
