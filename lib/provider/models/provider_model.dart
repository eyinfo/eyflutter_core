import 'dart:collection';

import 'package:eyflutter_core/provider/events/on_base_provider_listener.dart';
import 'package:flutter/material.dart';

mixin UniversalModel<P> {
  P? _parent;

  P? get parent => this._parent;

  void setParent(P parent) {
    _parent = parent;
  }
}

/// 表单模型
/// <T> 模型数据类型
class ProviderModel<T, P> extends ChangeNotifier with UniversalModel<P> {
  String? _modKey;

  String? get modKey => _modKey;

  String? getModKey() => _modKey;

  ProviderModel({String? modKey}) {
    _modKey = modKey;
  }

  /// 数据对象
  T? _entity;

  /// 子组件模型（即页面有多个子组件，每个组件都可以用一个CM或ProviderModel来处理）
  final Map<String, dynamic> _models = HashMap<String, dynamic>();

  /// 获取数据对象
  T? get entity => _entity;

  /// 更新数据对象
  void update(T entity) {
    if (entity != null) {
      _entity = entity;
    }
  }

  /// get model listener
  OnBaseModelListener? get modelListener => null;

  /// 添加子组件模型
  /// [key] 模型唯一识别码
  /// [model] 子组件模型
  void addModel<M extends ProviderModel>(String key, M model) {
    _models.putIfAbsent(key, () => model);
  }

  /// 获取子组件模型
  /// [key] 模型唯一识别码
  M getModel<M extends ProviderModel>(String key) {
    return _models[key];
  }

  void notify() {
    notifyListeners();
  }
}
