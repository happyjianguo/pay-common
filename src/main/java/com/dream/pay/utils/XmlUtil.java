package com.dream.pay.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Xml序列化与反序列化工具类
 * <p>
 * 转Map使用dom4j<br/>
 * 对象与xml互转使用jaxb(Java API for XML Binding)
 * </p>
 * 
 * @author mengzhenbin
 *
 */
public class XmlUtil {

	/**
	 * XML串转Map
	 * 
	 * @param xmlString
	 *            xml字符串
	 * @return
	 */
	public static Map<String, Object> fromXml(String xmlString) {
		return fromXml(xmlString, false, null);
	}

	/**
	 * 解析xml并选择xpathExpression路径下的节点数据转Map
	 * 
	 * @param xmlString
	 *            xml字符串
	 * @param xpathExpression
	 *            节点路径表达式
	 * @return
	 */
	public static Map<String, Object> fromXml(String xmlString,
			String xpathExpression) {
		return fromXml(xmlString, xpathExpression, null);
	}

	/**
	 * XML串转Map
	 * 
	 * @param xmlString
	 *            xml字符串
	 * @param listElementNames
	 *            转成List的元素名称,默认情况下只有同级同名节点数大于1才会会转成List
	 * @return
	 */
	public static Map<String, Object> fromXml(String xmlString,
			String[] listElementNames) {
		return fromXml(xmlString, false, listElementNames);
	}

	/**
	 * 解析xml并选择xpathExpression路径下的节点数据转Map
	 * 
	 * @param xmlString
	 *            xml字符串
	 * @param xpathExpression
	 *            节点路径表达式
	 * @param toListElements
	 *            转成List的元素名称,默认情况下只有同级同名节点数大于1才会会转成List
	 * @return
	 */
	public static Map<String, Object> fromXml(String xmlString,
			String xpathExpression, String[] toListElements) {
		if (StringUtils.isBlank(xmlString)) {
			return null;
		}
		Element rootElement = parseXml(xmlString);
		Set<String> toListElementSet = new HashSet<String>();
		if (toListElements != null) {
			toListElementSet.addAll(Arrays.asList(toListElements));
		}
		if (StringUtils.isNotBlank(xpathExpression)) {
			@SuppressWarnings("unchecked")
			List<Node> nodeList = rootElement.selectNodes(xpathExpression);
			// 清除所有根节点并添加上选择的路径子节点
			rootElement.clearContent();
			for (Node node : nodeList) {
				rootElement.add(node.detach());
			}
		}

		return element2map(rootElement, toListElementSet);
	}

	/**
	 * XML串转Map
	 * 
	 * @param xmlString
	 *            xml字符串
	 * @param warpRoot
	 *            是否包含根节点
	 * @param toListElements
	 *            需要强制转成List的节点名称,默认情况下只有同级同名节点数大于1才会会转成List
	 * 
	 * @return
	 */
	public static Map<String, Object> fromXml(String xmlString,
			boolean warpRoot, String[] toListElements) {
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
			Map<String, Object> warpMap = new HashMap<String,Object>();
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
			throw new RuntimeException("XmlUtil.parseXml error", e);
		}
	}

	private static Map<String, Object> element2map(Element element,
			Set<String> toListElementSet) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 获得当前节点的子节点
		@SuppressWarnings("unchecked")
		List<Element> elements = element.elements();

		// 没有子节点说明当前节点是叶子节点，直接取值即可
		if (elements.size() == 0) {
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
			// 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
			// 构造一个map用来去重
			Map<String, Object> elementNameMap = new HashMap<String, Object>();
			for (Element ele : elements) {
				elementNameMap.put(ele.getName(), null);
			}
			Set<String> elementNameSet = elementNameMap.keySet();

			for (String elementName : elementNameSet) {
				@SuppressWarnings("unchecked")
				List<Element> namedElements = element.elements(elementName);
				// 如果该节点需要转成List或同名的节点数目大于1，则构建list
				if (toListElementSet.contains(elementName)
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
	 * @param xmlString
	 *            xml字符串
	 * @param valueType
	 *            对象类型
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
			throw new RuntimeException("XmlUtil.fromXml error", e);
		}
	}

	/**
	 * 对象转XML
	 * 
	 * @param t
	 *            对象
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

	public static void main(String[] args) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<GmMail><template><template_id>2</template_id></template><to_address>test@corp.gm.com</to_address><to_alias>tom</to_alias><message>XML-未来世界Message</message><recipient_name>xml-tom</recipient_name><send_address>xml-test@163.com</send_address><sender_comp_name>xml-gm</sender_comp_name><subject>xml-helloguys</subject><selected_items><item_group><item_url>http://china.gm.com</item_url><item_name>gm中文站</item_name><item_desc>gm中文站desc</item_desc><item_desc2>gm中文站desc2</item_desc2><item_desc3>gm中文站desc3</item_desc3><item_desc4>gm中文站desc4</item_desc4></item_group><items_source>products</items_source><item_group><item_url>http://www.gm.com</item_url><item_name>gm</item_name><item_desc>gmdesc</item_desc></item_group></selected_items><attachments><attachment-group><attachment_url>http://china.gm.com/pics/com.cn.gm/story/frsc.jpg</attachment_url><attachment_name>黄晓豫</attachment_name><attachment_desc><desc1>attach-desc1</desc1><desc2>attach-desc2</desc2><desc3>attach-desc3</desc3><desc4>attach-desc4</desc4></attachment_desc></attachment-group><attachment-group><attachment_url>http://china.gm.com/pics/com.cn.gm/story/wr.jpg</attachment_url><attachment_name>张中汉</attachment_name><attachment_desc><desc1>attach-desc1</desc1><desc2>attach-desc2</desc2></attachment_desc><attachment_desc><desc1>attach-desc1</desc1><desc2>attach-desc2</desc2></attachment_desc></attachment-group></attachments><desc_group><desc_item>desc_item_1</desc_item><desc_item>desc_item_2</desc_item><desc_item>desc_item_3</desc_item><desc_item>desc_item_4</desc_item><desc_item>desc_item_5</desc_item></desc_group></GmMail>";
		Map<String, Object> map = fromXml(xml,
				"/GmMail/attachments/attachment-group");
		System.out.println(JsonUtil.toJson(map));
	}
}
