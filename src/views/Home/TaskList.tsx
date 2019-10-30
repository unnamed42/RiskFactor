import React, { FC, useState, useEffect } from "react";
import { Link } from "react-router-dom";

import { List, Avatar } from "antd";

import { tasks, TaskBrief } from "@/api/task";

export const TaskList: FC = () => {

  const [source, setSource] = useState<TaskBrief[]>();

  useEffect(() => {
    tasks().then(setSource);
  }, []);

  return <div style={{margin: "auto", width: "80%"}}>
    <List<TaskBrief> itemLayout="horizontal" dataSource={source}
      renderItem={item => <List.Item>
        <List.Item.Meta title={<Link to={`/task/${item.id}`}>{item.name}</Link>}
          avatar={<Avatar shape="square" src="/public/briefcase.png" />}
          description={`${item.center} ${item.mtime}`}
        />
      </List.Item>}
    />
  </div>;
};
