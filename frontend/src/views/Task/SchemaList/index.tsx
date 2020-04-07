import React, { FC } from "react";
import { Link } from "react-router-dom";
import dayjs from "dayjs";

import { List, Avatar } from "antd";

import "./index.less";

import { SchemaInfo, getSchemas } from "@/api";
import { useResponse } from "@/utils";
import { datePattern } from "@/config";

const renderItem = (info: SchemaInfo) => <List.Item>
  <List.Item.Meta title={<Link to={`/task/${info.id}/answers`}>{info.name}</Link>}
    avatar={<Avatar shape="square" size="large" src="/public/briefcase.png" />}
    description={<span>
      <span className="task-item-description">归属中心：{info.creator.group}</span>
      <span className="task-item-description">创建时间：{dayjs(info.createdAt).format(datePattern)}</span>
    </span>} />
</List.Item>;

export const SchemaList: FC = () => {
  const response = useResponse(getSchemas, []);
  if (response.error !== undefined)
    return response.error;
  return <List<SchemaInfo> dataSource={response.state.data}
    className="task-list" renderItem={renderItem} />;
};
