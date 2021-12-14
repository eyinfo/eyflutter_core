import 'dart:collection';

import 'package:eyflutter_core/gen/events/i_data_entry.dart';
import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:eyflutter_core/kit/utils/string/path_extension.dart';
import 'package:eyflutter_core/mq/cloud_channel_manager.dart';
import 'package:eyflutter_core/storage/db/enums/db_operation_type.dart';
import 'package:eyflutter_core/storage/db/events/i_data_factory.dart';
import 'package:eyflutter_core/storage/db/sql_where_builder.dart';

class DataFactory implements IDataFactory {
  String _dbAction = "cabc4e3d3aca4fd394c0b2d18c1c1d3a";
  String _dataKey = "data";
  String _operationTypeKey = "operationType";
  String _whereKey = "where";

  @override
  void onPrepareData<T>(DbOperationType operationType, T data, {SqlWhereBuilder builder}) {
    if (operationType == DbOperationType.insertOrReplace) {
      _handler<void, T>(operationType, "", "", builder, data: data);
    }
  }

  @override
  Future<T> onHandle<T>(DbOperationType operationType, String schema, String fields, {SqlWhereBuilder builder}) {
    switch (operationType) {
      case DbOperationType.query:
      case DbOperationType.queryList:
      case DbOperationType.count:
      case DbOperationType.exists:
      case DbOperationType.deleteInTx:
      case DbOperationType.deleteInTxAll:
        return _handler<T, IDataEntry>(operationType, schema, fields, builder);
      default:
        return Future.value();
    }
  }

  Future<R> _handler<R, T>(DbOperationType operationType, String schema, String fields, SqlWhereBuilder builder,
      {T data}) {
    Map<String, dynamic> map = HashMap<String, dynamic>();
    map[_operationTypeKey] = operationType.toString().suffixName;
    if (builder != null) {
      map[_whereKey] = builder.build;
    }
    if (data == null) {
      map["schema"] = schema;
      map["fields"] = fields;
    } else {
      if (data is IDataEntry) {
        map[_dataKey] = data.toJsonMap();
        map["schema"] = data.getSchema();
        map["fields"] = data.getFields();
      } else if (data is List) {
        List<Map<String, dynamic>> lst = [];
        for (IDataEntry entry in data) {
          lst.add(entry.toJsonMap());
        }
        map[_dataKey] = lst;
        if (!data.isEmptyList && data[0] is IDataEntry) {
          IDataEntry entity = data[0] as IDataEntry;
          map["schema"] = entity.getSchema();
          map["fields"] = entity.getFields();
        }
      }
    }
    return CloudChannelManager.instance.send<R>(_dbAction, arguments: map);
  }
}
