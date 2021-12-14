class Property {
  ///在数据库中的字段
  final String nameInDb;

  ///字段长度(创建字段时若指定长度用varchar(length)类型,未指定用TEXT类型)
  final int length;

  const Property({this.nameInDb: "", this.length: 0});
}
