import React, { FC } from "react";

import { Card, Table } from "antd";

export const Overview: FC = props => {
  const columns = [
    {
      title: "名称",
      dataIndex: "name",
      key: "name"
    }, {
      title: "中心编号",
      dataIndex: "centerId",
      key: "centerId"
    }, {
      title: "创建者",
      dataIndex: "creator",
      key: "creator"
    }, {
      title: "修改时间",
      dataIndex: "mtime",
      key: "mtime"
    }
  ];

  return (
    <Card title="已有表单" bordered={false}>
      <Table columns={columns}></Table>
    </Card>
  );
};

export default Overview;
