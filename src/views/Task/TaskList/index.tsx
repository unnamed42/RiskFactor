import React, { FC } from "react";
import { Link } from "react-router-dom";

import { List, Avatar } from "antd";

import { tasks } from "@/api/task";
import { TaskBrief } from "@/types";
import { usePromise } from "@/utils";
import { PageLoading } from "@/components";

import "./index.less";

export const TaskList: FC = () => {

  const source = usePromise(tasks());

  if (!source.loaded)
    return <PageLoading/>;
  if (source.error)
    return null;

  const description = (item: TaskBrief) => <span>
      <span className="task-item-description">归属中心：{item.center}</span>
      <span className="task-item-description">创建时间：{item.mtime}</span>
    </span>;

  const renderItem = (item: TaskBrief) => <List.Item>
    <List.Item.Meta title={<Link to={`/task/${item.id}/answers`}>{item.name}</Link>}
                    avatar={<Avatar shape="square" size="large" src="/public/briefcase.png"/>}
                    description={description(item)}
    />
  </List.Item>;

  return <div className="task-list">
    <List<TaskBrief> itemLayout="horizontal" dataSource={source.value}
                     renderItem={renderItem}
    />
  </div>;
};
