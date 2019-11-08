import React, { Component } from "react";

import { Table } from "antd";

import { QProps, QSchema, Question } from ".";

export class QTable extends Component<QProps> {

  renderRow = ({ type, id, list }: QSchema, idx: number) => {
    if (type !== "list")
      throw new Error(`table row ${id} is incorrectly configured - not a question list`);
    if (list === undefined)
      throw new Error(`table row ${id} is incorrectly configured - no list provided`);

    const cell = list[idx];
    if (!cell.type)
      return <span>{cell.label}</span>;
    return <Question schema={cell}/>;
  };

  render() {
    const { id, list } = this.props.schema;
    if (!list)
      throw new Error(`Question ${id} has no list`);

    const [columns, ...rows] = list;
    if (!columns.list)
      throw new Error(`Question ${id} is incorrectly configured - no table header`);

    return <Table bordered={true} dataSource={rows}
                  pagination={false} rowKey="id">
      {
        columns.list.map(({ label }, idx) =>
          <Table.Column<QSchema> key={idx} title={label} dataIndex=""
            render={(text, schema) => this.renderRow(schema, idx)}/>
        )
      }
    </Table>;
  }
}
