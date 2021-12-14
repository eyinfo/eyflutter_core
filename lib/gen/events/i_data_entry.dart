mixin IDataEntry {
  Map<String, dynamic> toJsonMap();

  String getSchema();

  String getFields();
}
