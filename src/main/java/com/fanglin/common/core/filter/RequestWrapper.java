package com.fanglin.common.core.filter;

import com.fanglin.tkmapper.generator.my.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author 彭方林
 * @version 1.0
 * @date 2019/7/29 21:04
 **/
public class RequestWrapper extends HttpServletRequestWrapper {


    private static final String UTF_8 = "UTF-8";
    private Map<String, String[]> paramsMap;
    private final byte[] body;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = readBytes(request.getInputStream());
        if ("POST".equals(request.getMethod().toUpperCase())) {
            paramsMap = getParamMapFromPost(this);
        } else {
            paramsMap = getParamMapFromGet(this);
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return paramsMap;
    }

    @Override
    public String getParameter(String name) {
        String[] values = paramsMap.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {// 同上
        return paramsMap.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(paramsMap.keySet());
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    private Map<String, String[]> getParamMapFromGet(HttpServletRequest request) {
        return parseQueryString(request.getQueryString());
    }

    private HashMap<String, String[]> getParamMapFromPost(HttpServletRequest request) {
        String body = StringUtils.EMPTY;
        try {
            body = getRequestBody(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, String[]> result = new HashMap<>(0);
        if (body.length() == 0) {
            return result;
        }
        return parseQueryString(body);
    }

    private String getRequestBody(InputStream stream) throws IOException {
        String line;
        StringBuilder body = new StringBuilder();
        int counter = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while ((line = reader.readLine()) != null) {
            if (counter > 0) {
                body.append("rn");
            }
            body.append(line);
            counter++;
        }
        reader.close();
        return body.toString();
    }

    public HashMap<String, String[]> parseQueryString(String s) {
        String[] valArray;
        if (s == null) {
            throw new IllegalArgumentException();
        }
        HashMap<String, String[]> ht = new HashMap<>();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                continue;
            }
            String key = pair.substring(0, pos);
            String val = pair.substring(pos + 1);
            if (ht.containsKey(key)) {
                String[] oldValue = ht.get(key);
                valArray = new String[oldValue.length + 1];
                System.arraycopy(oldValue, 0, valArray, 0, oldValue.length);
                valArray[oldValue.length] = decodeValue(val);
            } else {
                valArray = new String[1];
                valArray[0] = decodeValue(val);
            }
            ht.put(key, valArray);
        }
        return ht;
    }


    private static byte[] readBytes(InputStream in) throws IOException {
        BufferedInputStream bufIn = new BufferedInputStream(in);
        final int buffSize = 1024;
        ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);
        byte[] temp = new byte[buffSize];
        int size;
        while ((size = bufIn.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        out.flush();
        byte[] content = out.toByteArray();
        bufIn.close();
        out.close();
        return content;
    }

    /**
     * 自定义解码函数
     */
    private String decodeValue(String value) {
        if (value.contains("%u")) {
            try {
                return URLDecoder.decode(value, UTF_8);
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        } else {
            try {
                return URLDecoder.decode(value, UTF_8);
            } catch (UnsupportedEncodingException e) {
                return StringUtils.EMPTY;
            }
        }
    }
}
