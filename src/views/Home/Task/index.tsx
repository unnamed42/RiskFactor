import React, { FC, useState, useEffect } from "react";

import { Button, PageHeader, message } from "antd";

import {
  taskSections, taskAnswers, task,
  SectionBrief, AnswerBrief, TaskBrief
} from "@/api/task";

import { Error } from "@/api/http";

interface P {
  taskId: string | number;
}

interface S {
  info?: TaskBrief;
  sections?: SectionBrief[];
  answers?: AnswerBrief[];
}

export const TaskAnswers: FC<P> = ({ taskId }) => {

  const [state, setState] = useState<S>({});

  useEffect(() => {
    Promise.all([task(taskId), taskSections(taskId)])
      .then(([info, sections]) => { setState({ ...state, info, sections }); return taskAnswers(taskId); })
      .catch((error: Error) => message.error(`加载表单时发生错误：${error.stacktrace}`))
      .then(answers => setState({ ...state, answers }))
      .catch((error: Error) => message.error(`加载受试者信息时发生错误：${error.message}`));
  }, []);

  const { info, sections, answers } = state;

  if (!info || !sections)
    return <div />;

  return <div>
    <PageHeader title={info.name}
      extra={[
        <Button key="1" type="link" icon="plus">添加受试者</Button>,
        <Button key="2" type="link" icon="import">批量导入</Button>
      ]}
    />
  </div>;
};

export default TaskAnswers;
