import 'package:eyflutter_core/eyflutter_core.dart';

/// Author lijinghuan
/// Email:ljh0576123@163.com
/// CreateTime:2021-12-24
/// Description:Generated file. Do not edit.
/// Modifier:
/// ModifyContent:

class BaseResponse {
  /// api状态返回码
  int code;

  /// message
  String msg;

  BaseResponse({this.code = 0,this.msg = '', Map<String, dynamic>? json});

  factory BaseResponse.fromJson(Map<String,dynamic> json) => _$BaseResponseFromJson(json);

  Map<String,dynamic> toJson() => _$BaseResponseToJson(this);
}

BaseResponse _$BaseResponseFromJson(Map<String, dynamic> json) {
  return BaseResponse(
      code:(json['code'] is String)?("${json['code']}".toInt):((json['code'] ?? 0) as num).toNumInt,
      msg:(json['msg'] is String)?((json['msg'] ?? '') as String):'', json: json);
}

Map<String, dynamic> _$BaseResponseToJson(BaseResponse instance) {
  final val = <String, dynamic>{};
  val['code'] = instance.code;
  val['msg'] = instance.msg;
  return val;
}

//define_fields:{"BaseResponse": {"dart_file_name": {"name": "base_response", "type": "", "subType": "object", "childClassName": "", "defaultValue": "", "describe": ""}, "code": {"name": "code", "type": "int", "subType": "", "childClassName": "", "defaultValue": 0, "describe": "api\u72b6\u6001\u8fd4\u56de\u7801"}, "msg": {"name": "msg", "type": "string", "subType": "", "childClassName": "", "defaultValue": "", "describe": "message"}}}