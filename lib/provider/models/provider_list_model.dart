import 'dart:collection';

import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:flutter/material.dart';

class ProviderListModel<T> extends ChangeNotifier {
  /// 数据集合
  final List<T> _items = [];

  /// 禁止外部改变数据集
  UnmodifiableListView<T> get items => UnmodifiableListView(_items);

  /// 获取数据类目
  T getItem(int index) => items[index];

  /// 统计记录数
  int get count => _items.length;

  /// 将item从集合中移除
  /// item 要移除的数据
  void removeItem({required T item}) {
    if (item == null || _items.length == 0) {
      return;
    }
    _items.remove(item);
  }

  void updateAt(int index, T item) {
    if (item == null || index < 0 || (index + 1) >= _items.length) {
      return;
    }
    _items[index] = item;
  }

  /// 将item添加到集合中
  /// [items] 数据集合
  /// [isClear] 添加数据至集合前是否需要清空
  void addAll({required List<T> items, bool isClear = false}) {
    if (items.isNotEmptyList) {
      if (isClear) {
        _items.clear();
      }
      _items.addAll(items);
      //通知监听器，构建InheritedProvider，更新状态。
      notifyListeners();
    }
  }

  /// 将item添加到集合中
  /// [item] 数据模型
  void add({required T item}) {
    if (item != null) {
      _items.add(item);
      //通知监听器，构建InheritedProvider，更新状态。
      notifyListeners();
    }
  }

  /// 插件数据至集合中
  /// [item] 数据模型
  /// [index] 插入数据至集合中指定位置的索引,null或大于items.length将添加至集合末尾。
  void insert({required T item, int? index}) {
    if (item != null) {
      var len = _items.length;
      var idx = (index == null || (index + 1) >= len) ? len : index;
      _items.insert(idx, item);
      notifyListeners();
    }
  }

  /// 清除数据
  void clear() {
    _items.clear();
    notifyListeners();
  }
}
