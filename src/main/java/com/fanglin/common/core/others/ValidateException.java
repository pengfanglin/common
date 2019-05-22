package com.fanglin.common.core.others;

/**
 * 用户自定义异常，只打印异常信息，不打印堆栈信息
 * @author 彭方林
 * @date 2019/4/2 17:57
 * @version 1.0
 **/
public class ValidateException extends RuntimeException{
  public ValidateException(String message){
    super(message);
  }
}
