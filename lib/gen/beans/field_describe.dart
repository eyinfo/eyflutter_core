class FieldDescribe {
  ///字段名
  final String name;

  ///字段类型
  final String type;

  ///是否主键
  final bool primary;

  ///是否唯一
  final bool unique;

  ///是否自增
  final bool autoincrement;

  ///字段长度(创建字段时若指定长度用varchar(length)类型,未指定用TEXT类型)
  final int length;

  FieldDescribe(
      {this.name: "",
      this.type: "",
      this.primary: false,
      this.unique: false,
      this.autoincrement: false,
      this.length: 0});

  Map<String, dynamic> toJson() {
    return {
      "name": this.name,
      "type": this.type,
      "primary": this.primary,
      "unique": unique,
      "autoincrement": autoincrement,
      "length": length
    };
  }
}
