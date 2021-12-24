import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:xml_parser/xml_parser.dart';

/// xml解析工具
class XmlParserUtils {
  factory XmlParserUtils() => _getInstance();

  static XmlParserUtils get instance => _getInstance();
  static XmlParserUtils? _instance;

  XmlParserUtils._internal();

  static XmlParserUtils _getInstance() {
    return _instance ??= new XmlParserUtils._internal();
  }

  XmlDocument? _getDocument(String xml) {
    if (xml.isEmptyString) {
      return null;
    }
    XmlDocument? document = XmlDocument.from(xml);
    return document;
  }

  /// 获取xml节点
  /// [xml] xml内容
  /// [nodePath] 节点路径 eg. root->node1->node2->node3
  XmlElement? getRootElement({required String xml}) {
    XmlDocument? document = _getDocument(xml);
    if (document == null) {
      return null;
    }
    return document.root;
  }

  XmlElement? _getNextElement(XmlElement? parentElement, List<String> elementList) {
    if (parentElement == null || elementList.isEmptyList || !parentElement.hasChildren) {
      return null;
    }
    var xmlElement = parentElement.getElementWhere(name: elementList.first);
    elementList.removeAt(0);
    if (xmlElement == null) {
      return _getNextElement(parentElement, elementList);
    }
    if (elementList.isEmptyList) {
      return xmlElement;
    } else {
      return _getNextElement(xmlElement, elementList);
    }
  }

  /// 获取节点元素值
  /// [element] XmlElement
  /// [nodePath] 节点路径 eg. root->node1->node2->node3
  XmlElement? getElement({required XmlElement element, required String nodePath}) {
    if (nodePath.isEmptyString) {
      return null;
    }
    var list = nodePath.split("->");
    var xmlElement = _getNextElement(element, list);
    return xmlElement;
  }

  /// 获取节点元素值
  /// [element] XmlElement
  /// [nodePath] 节点路径 eg. root->node1->node2->node3
  String getElementValue({required XmlElement element, required String nodePath}) {
    var xmlElement = getElement(element: element, nodePath: nodePath);
    return xmlElement?.text ?? "";
  }

  /// 获取属性值
  /// [element] XmlElement
  /// [nodePath] 节点路径 eg. root->node1->node2->node3
  /// [attributeName] 元素属性名
  String getAttrValue({required XmlElement element, required String nodePath, required String attributeName}) {
    if (attributeName.isEmptyString) {
      return "";
    }
    var xmlElement = getElement(element: element, nodePath: nodePath);
    return xmlElement?.getAttribute(attributeName) ?? "";
  }
}
