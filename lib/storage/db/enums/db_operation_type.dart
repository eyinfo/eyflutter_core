enum DbOperationType {
  ///插入或更新
  insertOrReplace,

  ///删除
  deleteInTx,

  ///删除所有
  deleteInTxAll,

  ///查询
  query,

  ///查询列表
  queryList,

  ///统计
  count,

  ///检测数据是否存在
  exists
}
