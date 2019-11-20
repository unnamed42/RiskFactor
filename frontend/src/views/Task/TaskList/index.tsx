import React, { FC } from "react";
import { Link } from "react-router-dom";

import { List, Avatar, message } from "antd";

import { tasks } from "@/api/task";
import { TaskView } from "@/types";
import { usePromise } from "@/utils";
import { PageLoading } from "@/components";

import "./index.less";

export const TaskList: FC = () => {

  const [state] = usePromise(async () => {
    const taskList = await tasks();
    return { list: taskList };
  }, e => message.error(e.message));

  if(state.loaded === null)
    return null;
  if(!state.loaded)
    return <PageLoading/>;

  const { list } = state;

  const description = (item: TaskView) => <span>
      <span className="task-item-description">归属中心：{item.center}</span>
      <span className="task-item-description">创建时间：{item.mtime}</span>
    </span>;

  const renderItem = (item: TaskView) => <List.Item>
    <List.Item.Meta title={<Link to={`/task/${item.id}/answers`}>{item.name}</Link>}
                    avatar={<Avatar shape="square" size="large" src="/public/briefcase.png"/>}
                    description={description(item)}
    />
  </List.Item>;

  return <div className="task-list">
    <List<TaskView> itemLayout="horizontal" dataSource={list}
                    renderItem={renderItem}
    />
  </div>;
};
