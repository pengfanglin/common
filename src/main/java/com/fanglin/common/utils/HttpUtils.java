package com.fanglin.common.utils;

import com.fanglin.common.core.others.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * http请求
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 16:33
 **/
@Component
@Slf4j
@ConditionalOnClass(CloseableHttpClient.class)
public class HttpUtils {


    private static CloseableHttpClient httpClient;
    /**
     * 微信退款专用http客户端
     */
    private static CloseableHttpClient wxHttpClient;
    /**
     * 微信公众号退款专用http客户端
     */
    private static CloseableHttpClient wxPubHttpClient;

    public HttpUtils(
        @Autowired(required = false) CloseableHttpClient httpClient,
        @Autowired(required = false) CloseableHttpClient wxHttpClient,
        @Autowired(required = false) CloseableHttpClient wxPubHttpClient
    ) {
        log.info("HttpUtils配置成功");
        HttpUtils.httpClient = httpClient;
        HttpUtils.wxHttpClient = wxHttpClient;
        HttpUtils.wxPubHttpClient = wxPubHttpClient;
    }

    /**
     * 无请求头get请求
     */
    public static String get(String url, Map<String, Object> params) {
        return get(url, null, params);
    }

    /**
     * 有参数，有请求头get请求
     */
    public static String get(String url, Map<String, String> headers, Map<String, Object> params) {
        //构建GET请求头
        String apiUrl = getUrlWithParams(url, params);
        HttpGet httpGet = new HttpGet(apiUrl);
        //设置header信息
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        CloseableHttpResponse response = null;
        try {
            //发送get请求
            response = httpClient.execute(httpGet);
            //如果返回值为空，或者返回状态码为空，代表请求失败，返回null
            if (response == null || response.getStatusLine() == null) {
                return null;
            }
            //得到状态码
            int statusCode = response.getStatusLine().getStatusCode();
            //请求成功
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityResponse = response.getEntity();
                //将返回结果编码后返回
                if (entityResponse != null) {
                    return EntityUtils.toString(entityResponse);
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("get请求发送失败:{}", e.getMessage());
            throw new BusinessException("get请求发送失败:" + e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 无请求头post请求
     */
    public static String post(String url, Map<String, Object> params) {
        return post(url, null, params);
    }

    /**
     * 有参数，有请求头post请求
     */
    public static String post(String url, Map<String, String> headers, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(url);
        //配置请求headers
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        //配置请求参数
        if (params != null && params.size() > 0) {
            HttpEntity entityReq = getUrlEncodedFormEntity(params);
            httpPost.setEntity(entityReq);
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entityRes = response.getEntity();
            if (statusCode == HttpStatus.SC_OK) {
                if (entityRes != null) {
                    return EntityUtils.toString(entityRes, "UTF-8");
                }
            } else {
                log.warn("{}", EntityUtils.toString(entityRes, "UTF-8"));
            }
            throw new BusinessException("post请求发送失败:" + statusCode);
        } catch (IOException e) {
            log.warn("post请求发送失败:{}", e.getMessage());
            throw new BusinessException("post请求发送失败:" + e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }

    /**
     * 构建post请求体
     */
    private static HttpEntity getUrlEncodedFormEntity(Map<String, Object> params) {
        List<NameValuePair> pairList = new LinkedList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
        }
        return new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8"));
    }

    /**
     * 将url和请求参数编码成字符串
     */
    private static String getUrlWithParams(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        boolean first = true;
        StringBuilder sb = new StringBuilder(url);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            char ch = '&';
            if (first) {
                ch = '?';
                first = false;
            }
            if (entry.getKey() == null) {
                continue;
            }
            String value = entry.getValue().toString();
            try {
                String encodeParams = URLEncoder.encode(value, "UTF-8");
                sb.append(ch).append(entry.getKey()).append("=").append(encodeParams);
            } catch (UnsupportedEncodingException e) {
                log.warn(e.getMessage());
                throw new BusinessException("get请求参数编码失败:");
            }
        }
        return sb.toString();
    }

    /**
     * post方式发送json请求
     */
    public static String postByJson(String url, String json) {
        return postByJson(url, null, null, json);
    }

    /**
     * post方式发送json请求
     */
    public static String postByJson(String url, Map<String, Object> params, String json) {
        return postByJson(url, params, null, json);
    }

    /**
     * post方式发送json请求
     */
    public static String postByJson(String url, Map<String, Object> params, Map<String, String> headers, String json) {
        HttpPost post = new HttpPost(getUrlWithParams(url, params));
        try {
            StringEntity s = new StringEntity(json, "UTF-8");
            //配置请求headers
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            s.setContentEncoding("UTF-8");
            s.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            s.setChunked(false);
            post.setEntity(s);
            HttpResponse res = httpClient.execute(post);
            int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return EntityUtils.toString(res.getEntity());
            } else {
                log.warn("{} {}", statusCode, res.getEntity());
                throw new BusinessException(statusCode + " " + res.getEntity());
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * post请求发送xml数据
     */
    public static String postByXml(String url, Map<String, Object> params) {
        return httpClientPost(httpClient, url, params);
    }

    private static String httpClientPost(CloseableHttpClient httpClient, String url, Map<String, Object> params) {
        HttpPost post = new HttpPost(url);
        String xml = OthersUtils.mapToXml(params);
        try {
            StringEntity s = new StringEntity(xml, "UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType(ContentType.TEXT_XML.getMimeType());
            post.setEntity(s);
            HttpResponse res = httpClient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(res.getEntity(), "UTF-8");
            } else {
                log.warn("{} {}", res.getStatusLine().getStatusCode() + EntityUtils.toString(res.getEntity(), "UTF-8"));
                throw new BusinessException(res.getStatusLine().getStatusCode() + " " + EntityUtils.toString(res.getEntity(), "UTF-8"));
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 微信退款post请求发送xml数据，携带有证书文件
     */
    public static String wxPostByXml(String url, Map<String, Object> params) {
        return httpClientPost(wxHttpClient, url, params);
    }

    /**
     * 微信公众号退款post请求发送xml数据，携带有证书文件
     */
    public static String wxPubPostByXml(String url, Map<String, Object> params) {
        return httpClientPost(wxPubHttpClient, url, params);
    }
}
