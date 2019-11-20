import React, { forwardRef, useState } from "react";

import { omit } from "lodash";

import { Table } from "antd";

import { QProps, QSchema, Question } from ".";

interface S {
  [idx: number]: string;
}

export const QTable = forwardRef<any, QProps>(({ schema: { id, list }, value, onChange }, ref) => {
  const [values, setValues] = useState<S>(value ? JSON.parse(value) : {});

  if (!list)
    throw new Error(`Question ${id} has no list`);
  const [columns, ...rows] = list;
  if(columns.list === undefined)
    throw new Error(`Question ${id} is incorrectly configured - no table header`);

  const changed = (value: string, idx: number) => {
    if(!value)
      setValues(omit(values, idx.toString()));
    else
      setValues({ ...values, [idx]: value });
    onChange?.(JSON.stringify(values));
  };

  const renderRow = ({ type, id, list }: QSchema, idx: number) => {
    if (type !== "list")
      throw new Error(`table row ${id} is incorrectly configured - not a question list`);
    if (list === undefined)
      throw new Error(`table row ${id} is incorrectly configured - no list provided`);

    const cell = list[idx];
    if (!cell.type)
      return <span>{cell.label}</span>;
    return <Question schema={cell} onChange={v => changed(v, idx)}
                     value={values[idx]} />;
  };

  return <Table bordered={true} dataSource={rows} pagination={false} rowKey="id">
    {
      columns.list.map(({ label }, idx) =>
        <Table.Column<QSchema> key={idx} title={label} dataIndex=""
                               render={(text, schema) => renderRow(schema, idx)}
        />
      )
    }
  </Table>;
});
