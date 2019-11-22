import React, { forwardRef } from "react";

import { Table } from "antd";

import { QProps, QSchema, Question } from ".";

export const QTable = forwardRef<any, QProps>(({ schema: { id, list } }, ref) => {
  if (!list)
    throw new Error(`Question ${id} has no list`);
  const [columns, ...rows] = list;
  if(columns.choices === undefined)
    throw new Error(`Question ${id} is incorrectly configured - no table header`);

  const renderRow = ({ type, id, list }: QSchema, idx: number) => {
    if (type !== "list")
      throw new Error(`table row ${id} is incorrectly configured - not a question list`);
    if (list === undefined)
      throw new Error(`table row ${id} is incorrectly configured - no list provided`);

    const cell = list[idx];
    if (!cell.type)
      return <>{cell.label}</>;
    return <Question schema={cell} fieldPrefix={`${id}`}/>;
  };

  return <Table bordered={true} dataSource={rows} pagination={false} rowKey="id">
    {
      columns.choices.map((choice, idx) =>
        <Table.Column<QSchema> key={idx} title={choice} dataIndex=""
                               render={(text, schema) => renderRow(schema, idx)} />
      )
    }
  </Table>;
});
