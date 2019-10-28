import React, { FC, useState, useEffect } from "react";
import { Link } from "react-router-dom";

import { Card, Table } from "antd";

import * as api from "@/api/forms";

export const Overview: FC = props => {

  const [data, setData] = useState<Sections[]>();

  useEffect(() => {
    api.sections().then(setData);
  }, []);

  const redirect = (name: string) =>
    <Link to={`/forms/${name}`}>{name}</Link>;

  return (
    <Card title="已有表单" bordered={false}>
      <Table<Sections> dataSource={data} rowKey={ record => record.name }>
        <Table.Column<Sections> key="name" dataIndex="name" title="名称" render={redirect} />
        <Table.Column<Sections> key="creator" dataIndex="creator" title="创建者"/>
        <Table.Column<Sections> key="ctime" dataIndex="ctime" title="创建时间"/>
      </Table>
    </Card>
  );
};

export default Overview;
