package com.fanglin.common.util;

import com.fanglin.common.core.others.Ajax;
import com.fanglin.common.core.others.BusinessException;
import com.fanglin.common.properties.CommonProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author 彭方林
 * @version 1.0
 * @date 2019/8/9 16:28
 **/
@Slf4j
@Component
public class UploadUtils {
    private static CommonProperties commonProperties;

    public UploadUtils(CommonProperties commonProperties) {
        UploadUtils.commonProperties = commonProperties;
    }

    /**
     * 未文件压缩，生成缩略图
     *
     * @param files 文件数组
     * @param small 是否生成缩略图
     * @param path  保存根目录
     * @return
     */
    public static Ajax uploadFiles(MultipartFile[] files, Boolean small, String path) {
        //图片相对路径数组
        Set<String> fileNames = new HashSet<>();
        //判断file数组不能为空并且长度大于0
        if (files != null && files.length > 0) {
            //生成文件保存相对路径
            path = setDefaultPath(path);
            path += "/" + TimeUtils.getCurrentTime("yyyyMMdd") + "/";
            //循环获取file数组中得文件
            for (MultipartFile multipartFile : files) {
                if (!multipartFile.isEmpty()) {
                    //文件名
                    String fileName = String.valueOf(UUIDUtils.nextId());
                    //文件后缀
                    String suffix;
                    if (OthersUtils.notEmpty(multipartFile.getOriginalFilename())) {
                        suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")).toLowerCase();
                    } else {
                        suffix = "";
                    }
                    //父目录
                    String basePath = getFileSaveParentPath();
                    //创建目录
                    File file = new File(basePath + path);
                    if (!file.exists()) {
                        boolean success = file.mkdirs();
                        if (!success) {
                            log.warn("创建文件或目录失败:目录{} 文件名{}", file.getPath(), file.getName());
                            return Ajax.error("创建文件或目录失败");
                        }
                    }
                    String originalFileName = basePath + path + fileName + suffix;
                    fileNames.add(path + fileName + suffix);
                    File originalFile = new File(originalFileName);
                    try {
                        multipartFile.transferTo(originalFile);
                    } catch (IOException e) {
                        log.warn("文件上传失败:{}", e.getMessage());
                        return Ajax.error("文件上传失败:" + e.getMessage());
                    }
                }
            }
        }
        return Ajax.ok(fileNames);
    }

    /**
     * 设置默认上传路径
     */
    public static String setDefaultPath(String path) {
        String finalPath;
        if (OthersUtils.isEmpty(path)) {
            finalPath = "/files/others/";
        } else {
            if (path.contains("%")) {
                try {
                    finalPath = URLDecoder.decode(path, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.warn("路径参数有误:{}", e.getMessage());
                    throw new BusinessException("路径参数有误");
                }
            } else {
                finalPath = path;
            }
        }
        return finalPath;
    }

    /**
     * 获取文件默认保存路径，windows和linux保存的路径不一样
     */
    public static String getFileSaveParentPath() {
        String parentPath;
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            parentPath = request.getSession().getServletContext().getRealPath("/");
        } else {
            parentPath = commonProperties.getStaticDir();
        }
        return parentPath;
    }
}
