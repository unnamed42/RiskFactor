import React, { FC, useState, useEffect } from "react";
import { Link } from "react-router-dom";

import { Card, Table } from "antd";

import * as api from "@/api/task";

export const Overview: FC = props => {

  const [data, setData] = useState<Task[]>();

  useEffect(() => {
    api.tasks().then(setData);
  }, []);

  const redirect = (name: string, task: Task) =>
    <Link to={`/task/${task.id}`}>{name}</Link>;

  return (
    <Card title="已有表单" bordered={false}>
      <Table<Task> dataSource={data} rowKey={ record => record.id.toString() }>
        <Table.Column<Task> key="name" dataIndex="name" title="名称" render={redirect} />
        <Table.Column<Task> key="center" dataIndex="center" title="中心名称"/>
        <Table.Column<Task> key="mtime" dataIndex="mtime" title="修改时间"/>
      </Table>
    </Card>
  );
};

export default Overview;
