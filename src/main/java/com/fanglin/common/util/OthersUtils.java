package com.fanglin.common.util;

import com.fanglin.common.core.others.Assert;
import com.fanglin.common.core.others.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 其他工具类方法
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 16:37
 **/
@Slf4j
public class OthersUtils {

    /**
     * 请求头中过滤的头
     */
    private static Map<String, String> FILTER_HEADER;

    static {
        FILTER_HEADER = new HashMap<>(8);
        FILTER_HEADER.put("Accept", null);
        FILTER_HEADER.put("Cache-Control", null);
        FILTER_HEADER.put("Connection", null);
        FILTER_HEADER.put("Content-Length", null);
        FILTER_HEADER.put("Postman-Token", null);
        FILTER_HEADER.put("Accept-Encoding", null);
        FILTER_HEADER.put("Cookie", null);
        FILTER_HEADER.put("User-Agent", null);
    }

    /**
     * 逗号分隔字符串去重
     *
     * @param string
     * @return
     */
    public static String distinct(String string) {
        if (isEmpty(string)) {
            return "";
        }
        String[] split = string.split(",");
        Set<String> set = new HashSet<>(split.length);
        set.addAll(Arrays.asList(split));
        return String.join(",", set);
    }

    /**
     * 对象序列化为二进制
     *
     * @param data
     * @return
     */
    public static byte[] objectToByte(Object data) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(os).writeObject(data);
        } catch (IOException e) {
            log.error("序列化异常:{}", e.getMessage());
            throw new BusinessException("序列化异常:" + e.getMessage());
        }
        return os.toByteArray();
    }

    /**
     * 二进制序列化为对象
     *
     * @param data
     * @return
     */
    public static Object byteToObject(byte[] data) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        try {
            return new ObjectInputStream(is).readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("反序列化异常:{}", e.getMessage());
            throw new BusinessException("反序列化异常:" + e.getMessage());
        }
    }

    /**
     * 向html文件中写入内容
     */
    public static boolean writeHtml(String fileName, String desc, String htmlStyle) {
        try {
            File pathFile = new File(fileName.substring(0, fileName.lastIndexOf("/")));
            if (!pathFile.exists()) {
                boolean success = pathFile.mkdirs();
                if (!success) {
                    log.warn("创建目录失败:{}", pathFile.getName());
                    throw new BusinessException("创建目录失败");
                }
            }
            File file = new File(fileName);
            if (!file.exists()) {
                boolean success = file.createNewFile();
                if (!success) {
                    log.warn("创建文件失败 目录:{} 文件名:{}", file.getPath(), file.getName());
                    throw new BusinessException("创建文件失败");
                }
            }
            // 建立文件输出流
            FileOutputStream fileoutputstream = new FileOutputStream(fileName);
            OutputStreamWriter writer = new OutputStreamWriter(fileoutputstream, StandardCharsets.UTF_8);
            String style;
            if (htmlStyle == null) {
                style = desc;
            } else {
                style = htmlStyle;
                int start = desc.indexOf("<content>");
                int end = desc.indexOf("</content>");
                if (start > 0 && end > 0) {
                    style = desc;
                } else {
                    style = style.replace("<content>", "<content>" + desc);
                }
            }
            byte[] tagBytes = style.getBytes();
            fileoutputstream.write(tagBytes);
            writer.flush();
            fileoutputstream.close();
            writer.close();
            return true;
        } catch (Exception e) {
            log.warn("html内容写入异常:{}", e.getMessage());
            throw new BusinessException("html内容写入异常");
        }
    }

    /**
     * 读取html内容
     */
    public static String readHtml(String fileName) {
        try {
            String templateContent;
            FileInputStream fileinputstream = new FileInputStream(fileName);
            int length = fileinputstream.available();
            byte[] bytes = new byte[length];
            int readSize = fileinputstream.read(bytes);
            if (readSize <= 0) {
                return "";
            }
            fileinputstream.close();
            templateContent = new String(bytes);
            return templateContent;
        } catch (Exception e) {
            log.warn("读取html内容异常:{}", e.getMessage());
            return "";
        }
    }

    /**
     * 从请求中读取数据
     */
    public static String readDataFromRequest(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
        return sb.toString();
    }

    /**
     * 获取项目的根路径
     */
    public static String getClassRootPath() {
        return Objects.requireNonNull(OthersUtils.class.getClassLoader().getResource("")).getPath();
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            return "".equals(object);
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else if (object instanceof Collection) {
            return ((Collection) object).isEmpty();
        } else if (object instanceof Map) {
            return ((Map) object).size() == 0;
        } else {
            return false;
        }
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean notEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * xml转map
     */
    public static Map<String, Object> xmlToMap(String xml) {
        Map<String, Object> data = new HashMap<>(10);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        InputStream stream;
        Document doc;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            doc = documentBuilder.parse(stream);
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                data.put(element.getNodeName(), element.getTextContent());
            }
        }
        try {
            stream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    /**
     * map转xml
     */
    public static String mapToXml(Map<String, Object> data) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.warn(e.getMessage());
            throw new BusinessException("解析失败");
        }
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("xml");
        document.appendChild(root);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                Element filed = document.createElement(entry.getKey());
                filed.appendChild(document.createTextNode(entry.getValue().toString()));
                root.appendChild(filed);
            }
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            log.warn(e.getMessage());
            throw new BusinessException("解析失败");
        }
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            log.warn(e.getMessage());
            throw new BusinessException("解析失败");
        }
        String output = writer.getBuffer().toString();
        try {
            writer.close();
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
        return output;
    }

    /**
     * 产生随机数(纯数字)
     */
    public static int random(int length) {
        Assert.isTrue(length > 0 && length < 9, "长度范围(1-8)");
        int start = (int) Math.pow(10, length - 1);
        int end = (int) Math.pow(10, length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(start, end);
    }

    /**
     * 产生随机数(字母+数字)
     */
    public static String randomString(int length) {
        Assert.isTrue(length > 0, "长度不合法");
        String strTable = "0123456789abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        StringBuilder stringBuffer = new StringBuilder();
        while (stringBuffer.length() < length) {
            double a = Math.random() * len;
            int b = (int) Math.floor(a);
            char c = strTable.charAt(b);
            if (('0' <= c) && (c <= '9')) {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 字符串转Integer
     */
    public static Integer toInteger(String value) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.intValue();
    }

    /**
     * 字符串转Integer
     */
    public static Integer toInteger(Float value) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.intValue();
    }

    /**
     * 字符串转指定长度的Float
     */
    public static Float toFloat(String value, int length) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(length, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * Float转指定长度的Float
     */
    public static Float toFloat(Float value, int length) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(length, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 字符串转指定长度的Double
     */
    public static Double toDouble(String value, int length) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(length, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 从request中读取参数
     *
     * @param request
     * @return
     */
    public static Map<String, Object> readRequestParams(HttpServletRequest request) {
        Map<String, Object> map = new LinkedHashMap<>(5);
        map.put("URL", request.getRequestURL());
        map.put("Method", request.getMethod());
        Map<String, String> params = new LinkedHashMap<>(10);
        for (Enumeration<String> names = request.getParameterNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            params.put(name, request.getParameter(name));
        }
        map.put("params", params);
        return map;
    }

    /**
     * 从request中读取请求头参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> readRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>(5);
        for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            if (!FILTER_HEADER.containsKey(name)) {
                headers.put(name, request.getHeader(name));
            }
        }
        return headers;
    }

    /**
     * url转码
     *
     * @param value
     * @return
     * @throws Exception
     */
    public static String specialUrlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            log.warn("编码失败:{}", e.getMessage());
            throw new BusinessException("编码失败");
        }
    }
}
