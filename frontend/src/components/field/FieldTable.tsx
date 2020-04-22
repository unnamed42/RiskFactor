import React, { FC, useCallback } from "react";

import { Table } from "antd";

import type { RuleList, RuleTable } from "@/api";
import { FieldProps, Field } from ".";

type P = FieldProps<RuleTable>;

export const FieldTable: FC<P> = ({ rule: { list: [cols, ...rows] }, namePath }) => {
  // if (!list)
  //   throw new Error(`表格 ${id} 内容不能为空`);
  // const [cols, ...rows] = list;
  // if (!cols.choices)
  //   throw new Error(`表格 ${id} 规则错误 - 无表头`);

  const renderRow = useCallback(({ list }: RuleList, idx: number) => {
    // if (type !== "list")
    //   throw new Error(`表格行 ${id} 规则错误 - 类型必须为 list`);
    // if (!list)
    //   throw new Error(`表格行 ${id} 规则错误 - 未配置内容`);
    const cell = list[idx];
    if (cell.type === undefined)
      return cell.label;
    return <Field rule={cell} noStyle namePath={namePath} />;
  }, [namePath]);

  return <Table bordered dataSource={rows} pagination={false} rowKey="id">
    {
      cols.choices.map((choice, idx) => (
        <Table.Column<RuleList> key={idx} title={choice} dataIndex=""
                                render={(_, schema) => renderRow(schema, idx)}/>
      ))
    }
  </Table>;
};
