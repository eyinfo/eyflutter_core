import 'dart:collection';
import 'dart:convert' as JSON;

import 'package:analyzer/dart/element/element.dart';
import 'package:build/build.dart';
import 'package:eyflutter_core/gen/annotations/entity.dart';
import 'package:eyflutter_core/gen/beans/field_describe.dart';
import 'package:source_gen/source_gen.dart';

class DbSchemaGenerator extends GeneratorForAnnotation<Entity> {
  @override
  generateForAnnotatedElement(Element element, ConstantReader annotation, BuildStep buildStep) async {
    if (element is! ClassElement) {
      return "";
    }
    buildStep.inputLibrary.then((LibraryElement value) {});
    ClassElement classElement = element as ClassElement;
    var entityAnnotation = _getEntityAnnotation(classElement.metadata);
    if (entityAnnotation == null) {
      return "";
    }

    var className = classElement.displayName;
    Map<String, dynamic> propertyMap = HashMap<String, dynamic>();

    var buffer = StringBuffer();
    buffer.writeln("import 'dart:collection';\n");
    var source = classElement.source;
    buffer.writeln("import '${source.shortName}';\n");
    buffer.writeln("class ${className}Schema {");
    buffer.writeln("  ${className}Schema._();\n");
    _genFieldsDefine(buffer, classElement.fields ?? []);
    _genMap(buffer, classElement.fields ?? [], className);
    _getSchema(buffer, annotation, propertyMap);
    buffer.writeln();
    _genDefineFields(buffer, classElement.fields ?? [], propertyMap);
    buffer.writeln();
    _genSchema(buffer, propertyMap);
    buffer.writeln("}");
    return buffer.toString();
  }

  void _genSchema(StringBuffer buffer, Map<String, dynamic> propertyMap) {
    buffer.writeln("  static String get schema {");
    buffer.writeln("    return '${JSON.jsonEncode(propertyMap)}';");
    buffer.writeln("  }");
  }

  void _getSchema(StringBuffer buffer, ConstantReader annotation, Map<String, dynamic> propertyMap) {
    propertyMap["tableName"] = (annotation.peek("nameInDb")?.stringValue ?? "");
    propertyMap["databaseKey"] = (annotation.peek("databaseKey")?.stringValue ?? "");
  }

  void _genDefineFields(StringBuffer buffer, List<FieldElement> fields, Map<String, dynamic> propertyMap) {
    buffer.writeln("  static String get sqlFields {");
    List<FieldDescribe> list = [];
    fields.forEach((FieldElement element) {
      var metadata = element.metadata ?? [];
      var annotations = _getAnnotations(metadata);
      String fieldTag = "Property";
      //没有Property标识的注解，不会作为数据库字段
      if (annotations.containsKey(fieldTag)) {
        var annotation = annotations[fieldTag]?.computeConstantValue();
        var nameInDb = annotation?.getField("nameInDb");
        var nameInDbValue = (nameInDb?.toStringValue() ?? element.name);
        var lengthField = annotation?.getField("length");
        var lengthValue = (lengthField?.toIntValue() ?? 0);
        if (nameInDbValue.isNotEmpty) {
          bool primary = false;
          bool autoincrement = false;
          bool unique = false;
          var typeValue = (element.type?.toString() ?? "").toLowerCase();
          //主键属性
          if (annotations.containsKey("Id")) {
            primary = true;
            var idAnnotation = annotations["Id"]?.computeConstantValue();
            var idAutoValue = idAnnotation?.getField("autoincrement");
            autoincrement = (idAutoValue?.toBoolValue() ?? false);
          }
          //索引属性
          if (annotations.containsKey("Index")) {
            var indexAnnotation = annotations["Index"]?.computeConstantValue();
            var indexUniqueField = indexAnnotation?.getField("unique");
            unique = (indexUniqueField?.toBoolValue() ?? false);
          }
          var fieldDescribe = FieldDescribe(
              name: nameInDbValue,
              type: typeValue,
              primary: primary,
              autoincrement: autoincrement,
              unique: unique,
              length: lengthValue);
          list.add(fieldDescribe);
        }
      }
    });
    buffer.writeln("    return '${JSON.jsonEncode(list)}';");
    buffer.writeln("  }");
  }

  ClassElement _getEntityAnnotation(List<ElementAnnotation> annotations) {
    for (ElementAnnotation element in annotations) {
      if (element.element is ConstructorElement) {
        var annotationElement = element.element as ConstructorElement;
        var enclosingElement = annotationElement.enclosingElement;
        if (enclosingElement.name == "Entity") {
          return enclosingElement;
        }
      }
    }
    return null;
  }

  Map<String, ElementAnnotation> _getAnnotations(List<ElementAnnotation> annotations) {
    Map<String, ElementAnnotation> map = HashMap<String, ElementAnnotation>();
    annotations.forEach((ElementAnnotation element) {
      var annotationElement = element.element as ConstructorElement;
      var enclosingElement = annotationElement.enclosingElement;
      map[enclosingElement.name] = element;
    });
    return map;
  }

  void _genMap(StringBuffer buffer, List<FieldElement> fields, String className) {
    buffer.writeln("  static Map<String,dynamic> toJsonMap($className entry) {");
    buffer.writeln("    Map<String, dynamic> map = new HashMap<String, dynamic>();");
    fields.forEach((FieldElement element) {
      buffer.writeln('    map["${element.name}"] = entry.${element.name};');
    });
    buffer.writeln("    return map;");
    buffer.writeln("  }");
  }

  void _genFieldsDefine(StringBuffer buffer, List<FieldElement> fields) {
    fields?.forEach((element) {
      var metadata = element.metadata ?? [];
      var annotations = _getAnnotations(metadata);
      String fieldTag = "Property";
      //没有Property标识的注解，不会作为数据库字段
      if (annotations.containsKey(fieldTag)) {
        var annotation = annotations[fieldTag]?.computeConstantValue();
        var nameInDb = annotation?.getField("nameInDb");
        var nameInDbValue = (nameInDb?.toStringValue() ?? element.name);
        if (nameInDbValue.isNotEmpty) {
          buffer.writeln("  static const ${element.name} = \"$nameInDbValue\";\n");
        }
      }
    });
  }
}
