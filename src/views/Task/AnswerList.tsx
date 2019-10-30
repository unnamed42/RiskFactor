import React, { FC, useState, useEffect } from "react";
import { Link } from "react-router-dom";

import { Button, PageHeader, message, Table } from "antd";

import {
  taskSections, taskAnswers, task, deleteAnswer,
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

type T = AnswerBrief;

export const AnswerList: FC<P> = ({ taskId }) => {

  const [state, setState] = useState<S>({});

  useEffect(() => {
    Promise.all([task(taskId), taskSections(taskId), taskAnswers(taskId)])
      .then(([info, sections, answers]) => setState({ info, sections, answers }))
      .catch((error: Error) => message.error(`加载表单时发生错误：${error.message}`));
  }, []);

  const { info, sections, answers } = state;

  if (!info || !sections)
    return <div />;

  const sectionLink = (answer: T, section: SectionBrief) =>
    <Link to={`/task/${taskId}/form/${answer.id}/${section.id}`}>{section.title}</Link>;

  const delAnswer = (answer: T) =>
    deleteAnswer(answer.id).then(() => { window.location.reload(); message.success("删除成功") })
        .catch(err => message.error(err.message));

  const actions = (text: any, answer: T) => {
    return <span>
      <Link to={`/task/${taskId}/form/${answer.id}`}>查看</Link>
      &nbsp;
      <Link to="#" onClick={() => delAnswer(answer)}>删除</Link>
    </span>;
  };

  return <div>
    <PageHeader title={info.name}
      extra={[
        <Button key="1" type="link" icon="plus">
          <Link to={`/task/${taskId}/form`}>添加受试者</Link>
        </Button>,
        <Button key="2" type="link" icon="import">批量导入</Button>
      ]}
    />
    <Table<T> dataSource={answers} rowKey="id">
      <Table.Column<T> key="id" dataIndex="id" title="受试者编号" />
      <Table.Column<T> key="creator" dataIndex="creator" title="创建人" />
      <Table.Column<T> key="mtime" dataIndex="mtime" title="修改时间" />
      <Table.Column key="status" dataIndex="" title="患者入组" render={() => <span>患者入组</span>} />
      {
        sections.map(section =>
          <Table.Column<T> key={section.title} dataIndex="" title={section.title}
            render={(_, answer) => sectionLink(answer, section)} />
        )
      }
      <Table.Column<T> key="action" dataIndex="" title="动作" render={actions} />
    </Table>
  </div>;
};
