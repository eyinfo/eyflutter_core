library entity;

import 'package:eyflutter_core/gen/enums/database_keys.dart';

/// 根据nameInDb和databaseKey组合sql语句操作;
class Entity {
  ///在数据库中的表名
  final String nameInDb;

  ///对应数据库标识(目前针对android,iOS数据只保存在沙盒中.)
  final String databaseKey;

  const Entity({this.nameInDb: "", this.databaseKey: DatabaseKeys.privacy});
}
