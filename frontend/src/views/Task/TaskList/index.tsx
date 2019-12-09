import React, { FC } from "react";
import { Link } from "react-router-dom";

import { List, Avatar } from "antd";

import { tasks } from "@/api/task";
import { Fetch } from "@/components";

import "./index.less";

export const TaskList: FC = () => <Fetch fetch={tasks}>
  {list => <div className="task-list">
    <List itemLayout="horizontal" dataSource={list}
          renderItem={item => <List.Item>
            <List.Item.Meta title={<Link to={`/task/${item.id}/answers`}>{item.name}</Link>}
                            avatar={<Avatar shape="square" size="large" src="/public/briefcase.png"/>}
                            description={<span>
                              <span className="task-item-description">归属中心：{item.center}</span>
                              <span className="task-item-description">创建时间：{item.mtime}</span>
                            </span>}
            />
          </List.Item>}
    />
  </div>}
</Fetch>;
