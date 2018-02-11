package com.dream.pay.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption Xml序列化与反序列化工具类<br/>
 * <p>
 * 转Map使用dom4j<br/>
 * 对象与xml互转使用jaxb(Java API for XML Binding)
 * </p>
 */
@Slf4j
public class XmlUtil {

    /**
     * XML串转Map
     *
     * @param xmlString xml字符串
     * @return
     */
    public static Map<String, Object> fromXml(String xmlString) {
        return fromXml(xmlString, false, null);
    }

    /**
     * 解析xml并选择xpathExpression路径下的节点数据转Map
     *
     * @param xmlString       xml字符串
     * @param xpathExpression 节点路径表达式
     * @return
     */
    public static Map<String, Object> fromXml(String xmlString,
                                              String xpathExpression) {
        return fromXml(xmlString, xpathExpression, null);
    }

    /**
     * XML串转Map
     *
     * @param xmlString        xml字符串
     * @param listElementNames 转成List的元素名称,默认情况下只有同级同名节点数大于1才会会转成List
     * @return
     */
    public static Map<String, Object> fromXml(String xmlString,
                                              String[] listElementNames) {
        return fromXml(xmlString, false, listElementNames);
    }

    /**
     * 解析xml并选择xpathExpression路径下的节点数据转Map
     *
     * @param xmlString       xml字符串
     * @param xpathExpression 节点路径表达式
     * @param toListElements  转成List的元素名称,默认情况下只有同级同名节点数大于1才会会转成List
     * @return
     */
    public static Map<String, Object> fromXml(String xmlString,
                                              String xpathExpression,
                                              String[] toListElements) {
        if (StringUtils.isBlank(xmlString)) {
            return null;
        }
        Element rootElement = parseXml(xmlString);
        Set<String> toListElementSet = new HashSet<String>();
        if (toListElements != null) {
            toListElementSet.addAll(Arrays.asList(toListElements));
        }
        if (StringUtils.isNotBlank(xpathExpression)) {
            List<Node> nodeList = rootElement.selectNodes(xpathExpression);
            rootElement.clearContent();  // 清除所有根节点并添加上选择的路径子节点
            for (Node node : nodeList) {
                rootElement.add(node.detach());
            }
        }
        return element2map(rootElement, toListElementSet);
    }

    /**
     * XML串转Map
     *
     * @param xmlString      xml字符串
     * @param warpRoot       是否包含根节点
     * @param toListElements 需要强制转成List的节点名称,默认情况下只有同级同名节点数大于1才会会转成List
     * @return
     */
    public static Map<String, Object> fromXml(String xmlString,
                                              boolean warpRoot,
                                              String[] toListElements) {
        if (StringUtils.isBlank(xmlString)) {
            return null;
        }
        Element rootElement = parseXml(xmlString);
        Set<String> toListElementSet = new HashSet<String>();
        if (toListElements != null) {
            toListElementSet.addAll(Arrays.asList(toListElements));
        }

        Map<String, Object> map = element2map(rootElement, toListElementSet);
        if (warpRoot && !isLeafNode(rootElement)) {
            Map<String, Object> warpMap = new HashMap<String, Object>();
            warpMap.put(rootElement.getName(), map);
            return warpMap;
        }
        return map;
    }

    /**
     * 解析xml并返回根节点
     *
     * @param xmlString
     * @return
     */
    private static Element parseXml(String xmlString) {
        if (StringUtils.isBlank(xmlString)) {
            return null;
        }
        try {
            Document doc = DocumentHelper.parseText(xmlString);
            return doc.getRootElement();
        } catch (DocumentException e) {
            log.error("XML解析错误", e);
            throw new RuntimeException("XML解析错误", e);
        }
    }

    private static Map<String, Object> element2map(Element element,
                                                   Set<String> toListElementSet) {

        Map<String, Object> map = new HashMap<String, Object>();
        List<Element> elements = element.elements();// 获得当前节点的子节点
        if (elements.size() == 0) {  // 没有子节点说明当前节点是叶子节点，直接取值即可
            map.put(element.getName(), element.getText());
        } else if (elements.size() == 1) {
            Element firstElement = elements.get(0);

            if (isLeafNode(firstElement)) {
                map.put(firstElement.getName(), firstElement.getText());
            } else {
                map.put(firstElement.getName(),
                        element2map(firstElement, toListElementSet));
            }
        } else {
            Map<String, Object> elementNameMap = new HashMap<String, Object>(); // 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的,构造一个map用来去重
            for (Element ele : elements) {
                elementNameMap.put(ele.getName(), null);
            }
            Set<String> elementNameSet = elementNameMap.keySet();

            for (String elementName : elementNameSet) {
                List<Element> namedElements = element.elements(elementName);
                if (toListElementSet.contains(elementName)  // 如果该节点需要转成List或同名的节点数目大于1，则构建list
                        || namedElements.size() > 1) {
                    List<Object> list = new ArrayList<Object>();
                    for (Element ele : namedElements) {
                        if (isLeafNode(ele)) {
                            list.add(ele.getText());
                        } else {
                            list.add(element2map(ele, toListElementSet));
                        }
                    }
                    map.put(elementName, list);
                } else {
                    Element firstElement = namedElements.get(0);
                    if (isLeafNode(firstElement)) {
                        map.put(firstElement.getName(), firstElement.getText());
                    } else {
                        map.put(firstElement.getName(),
                                element2map(firstElement, toListElementSet));
                    }
                }
            }
        }

        return map;
    }

    private static boolean isLeafNode(Element element) {
        return element.elements().size() == 0;
    }

    /**
     * XML串转对象
     *
     * @param xmlString xml字符串
     * @param valueType 对象类型
     * @return {@code List<T>}
     * @throws ClassCastException
     */
    private static <T> T fromXml(String xmlString, Class<T> valueType) {
        if (StringUtils.isBlank(xmlString)) {
            return null;
        }
        try {
            JAXBContext context = JAXBContext.newInstance(valueType);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return valueType.cast(unmarshaller.unmarshal(new StringReader(
                    xmlString)));
        } catch (JAXBException e) {
            log.error("XML解析错误", e);
            throw new RuntimeException("XML解析错误", e);
        }
    }

    /**
     * 对象转XML
     *
     * @param t 对象
     * @return
     */
    private static <T> String toXml(T t) {
        if (t == null) {
            return null;
        }
        try {
            JAXBContext context = JAXBContext.newInstance(t.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
            StringWriter writer = new StringWriter();
            marshaller.marshal(t, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("XmlUtil.toXml error", e);
        }
    }

    /**
     * @param map
     * @return String 返回类型
     * @Title: mapToXml
     * @Description: 将map转换成xml格式的string
     */
    public static String mapToXml(Map<String, String> map) {
        StringBuffer returnXml = new StringBuffer();
        try {
            returnXml.append("<xml>");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String k = (String) entry.getKey();
                String v = (String) entry.getValue();
                if (null != v && !"".equals(v)) {
                    if (isNumeric(v)) {// 是否是数字
                        returnXml.append("<" + k + ">" + v + "</" + k + ">\n");
                    } else {
                        returnXml.append("<" + k + "><![CDATA[" + v + "]]></" + k + ">\n");
                    }
                }
            }
            returnXml.append("</xml>");
        } catch (Exception e) {
            log.error("mapToXml |error|", e);
        }
        return returnXml.toString();
    }

    /**
     * @param str
     * @return boolean
     * @Title: isNumeric
     * @Description: 判断是否是数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}
