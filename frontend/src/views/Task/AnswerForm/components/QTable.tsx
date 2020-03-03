import React, { FC } from "react";

import { Table } from "antd";

import { QProps as P, Renderer } from ".";
import { Question } from "@/types";

export const QTable: FC<P> = ({ rule: { id, list }, namePath }) => {
  if (!list)
    throw new Error(`表格 ${id} 内容不能为空`);
  const [cols, ...rows] = list;
  if (!cols.choices)
    throw new Error(`表格 ${id} 规则错误 - 无表头`);

  const renderRow = (schema: Question, idx: number) => {
    const { type, list, id } = schema;
    if (type !== "list")
      throw new Error(`表格行 ${id} 规则错误 - 类型必须为 list`);
    if (!list)
      throw new Error(`表格行 ${id} 规则错误 - 未配置内容`);
    const cell = list[idx];
    if (cell.type === undefined)
      return <>{cell.label}</>;
    return <Renderer rule={cell} namePath={namePath} bare />;
  };

  return <Table bordered dataSource={rows} pagination={false} rowKey="id">
    {
      cols.choices.map((choice, idx) => (
        <Table.Column<Question> key={idx} title={choice} dataIndex=""
                                render={(_, schema) => renderRow(schema, idx)}/>
      ))
    }
  </Table>;
};
