import 'package:eyflutter_core/storage/db/enums/db_operation_type.dart';
import 'package:eyflutter_core/storage/db/sql_where_builder.dart';

mixin IDataFactory {
  void onPrepareData<T>(DbOperationType operationType, T data, {SqlWhereBuilder builder});

  Future<R> onHandle<R>(DbOperationType operationType, String schema, String fields, {SqlWhereBuilder builder});
}
