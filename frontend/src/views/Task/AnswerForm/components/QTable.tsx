import React, { FC } from "react";

import { Table } from "antd";

import { RenderProps as P, Renderer } from ".";
import type { RuleInfo } from "@/api";

export const QTable: FC<P> = ({ rule: { id, list } }) => {
  if (!list)
    throw new Error(`表格 ${id} 内容不能为空`);
  const [cols, ...rows] = list;
  if (!cols.choices)
    throw new Error(`表格 ${id} 规则错误 - 无表头`);

  const renderRow = (rule: RuleInfo, idx: number) => {
    const { type, list, id } = rule;
    if (type !== "list")
      throw new Error(`表格行 ${id} 规则错误 - 类型必须为 list`);
    if (!list)
      throw new Error(`表格行 ${id} 规则错误 - 未配置内容`);
    const cellRule = list[idx];
    if (cellRule.type === undefined)
      return <>{cellRule.label}</>;
    return <Renderer rule={cellRule} noStyle />;
  };

  return <Table bordered dataSource={rows} pagination={false} rowKey="id">
    {
      cols.choices.map((choice, idx) => (
        <Table.Column<RuleInfo> key={idx} title={choice} dataIndex=""
                                render={(_, schema) => renderRow(schema, idx)}/>
      ))
    }
  </Table>;
};
