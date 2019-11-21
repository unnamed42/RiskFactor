import React, { FC, useRef, useState } from "react";
import { Link } from "react-router-dom";

import { Button, PageHeader, message, Table } from "antd";

import { taskAnswers, task, deleteAnswer, downloadAnswer } from "@/api/task";
import { AnswerBrief } from "@/types";
import { usePromise, cachedStructure, parsedExcel } from "@/utils";
import { PageLoading, File } from "@/components";

interface P {
  taskId: string | number;
}

type T = AnswerBrief;

export const AnswerList: FC<P> = ({ taskId }) => {

  const file = useRef<HTMLInputElement>(null);

  const [parsing, setParsing] = useState(false);
  const [state, updateState] = usePromise(async () => {
    const [taskView, answers] = await Promise.all([task(taskId), taskAnswers(taskId)]);
    const struct = await cachedStructure(taskId);
    return { taskView, struct, answers };
  }, e => message.error(e.message));

  if(state.loaded == null)
    return null;
  if(!state.loaded || state.struct === undefined)
    return <PageLoading/>;
  const { taskView, struct, answers } = state;

  const names = struct.map(s => s.name);

  const delAnswer = async (answer: T) => {
    try {
      await deleteAnswer(answer.id);
      message.success("删除成功");
      updateState({ answers: answers.filter(a => a.id !== answer.id) });
    } catch (e) { message.error(e.message); }
  };

  const actions = (text: any, answer: T) => {
    return <span>
      <Link to={`/task/${taskId}/form/${answer.id}`}>查看</Link>
      &nbsp;
      <Link to="#" onClick={() => downloadAnswer(answer.id)}>导出</Link>
      &nbsp;
      <Link to="#" onClick={() => delAnswer(answer)}>删除</Link>
    </span>;
  };

  const importAnswers = (buffer: ArrayBuffer) => {
    setParsing(true);
    parsedExcel(taskId, buffer)
      .catch(e => message.error(e.message))
      .then(() => setParsing(false));
  };

  return <div>
    <PageHeader title={taskView.name}
      extra={[
        <Button key="1" type="link" icon="plus">
          <Link to={`/task/${taskId}/form`}>添加受试者</Link>
        </Button>,
        <div key="_2" style={{ display: "inline-block" }}>
          <File ref={file} accept=".xlsx" onLoaded={importAnswers}/>
          <Button key="2" type="link" icon="import" disabled={parsing} loading={parsing}
                  onClick={() => file.current?.click()}>批量导入</Button>
        </div>
      ]}
    />
    <Table<T> dataSource={answers} rowKey="id">
      <Table.Column<T> key="id" dataIndex="id" title="受试者编号" />
      <Table.Column<T> key="creator" dataIndex="creator" title="创建人" />
      <Table.Column<T> key="mtime" dataIndex="mtime" title="修改时间" />
      <Table.Column<T> key="status" dataIndex="" title="患者入组" render={() => <span>患者入组</span>} />
      {
        names.map(name =>
          <Table.Column<T> key={name} dataIndex="" title={name}
            render={() => <Link to="#" />} />
        )
      }
      <Table.Column<T> key="action" dataIndex="" title="动作" render={actions} />
    </Table>
  </div>;
};
