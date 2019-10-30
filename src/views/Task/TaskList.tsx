import React, { FC, useState, useEffect } from "react";
import { Link } from "react-router-dom";

import { List, Avatar, message } from "antd";

import { tasks, TaskBrief } from "@/api/task";
import { Error } from "@/api/http";

export const TaskList: FC = () => {

  const [source, setSource] = useState<TaskBrief[]>();

  useEffect(() => {
    tasks().then(setSource).catch((error: Error) => message.error(error.message));
  }, []);

  return <div style={{margin: "20px auto 0 auto", width: "80%"}}>
    <List<TaskBrief> itemLayout="horizontal" dataSource={source}
      renderItem={item => <List.Item>
        <List.Item.Meta title={<Link to={`/task/${item.id}/answers`}>{item.name}</Link>}
          avatar={<Avatar shape="square" src="/public/briefcase.png" />}
          description={`${item.center} ${item.mtime}`}
        />
      </List.Item>}
    />
  </div>;
};
