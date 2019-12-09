import React, { FC, useRef, useState } from "react";
import { Link } from "react-router-dom";

import { Button, PageHeader, message, Table } from "antd";

import { taskAnswers, task, deleteAnswer, downloadAnswer, postAnswer } from "@/api/task";
import { AnswerBrief } from "@/types";
import { background, cachedStructure } from "@/utils";
import { File, Fetch } from "@/components";

interface P {
  taskId: string | number;
}

type T = AnswerBrief;

export const AnswerList: FC<P> = ({ taskId }) => {
  const file = useRef<HTMLInputElement>(null);

  const [parsing, setParsing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [answers, setAnswers] = useState<AnswerBrief[]>();

  const delAnswer = async (answer: T) => {
    try {
      await deleteAnswer(answer.id);
      message.success("删除成功");
      setAnswers(answers?.filter(a => a.id !== answer.id));
    } catch (e) {
      message.error(e.message);
    }
  };

  // TODO: 导出为json在后端已经删除，在前端完成该操作
  const actions = (text: any, answer: T) => {
    return <span>
      <Link to={`/task/${taskId}/form/${answer.id}`}>查看</Link>
      &nbsp;
      <Button type="link" onClick={() => downloadAnswer(answer.id)}>导出</Button>
      &nbsp;
      <Button type="link" onClick={() => delAnswer(answer)}>删除</Button>
    </span>;
  };

  const importAnswers = background(async (buffer: ArrayBuffer) => {
    setLoading(true);
    const { parsedExcel } = await import(/* webpackChunkName: "exceljs" */ "@/utils/excel");
    setParsing(true);
    try {
      const excel = await parsedExcel(taskId, buffer);
      await Promise.all(excel.map(item => postAnswer(taskId, item)));
    } catch (e) {
      message.error(e.message);
    } finally {
      setLoading(false);
      setParsing(false);
    }
  });

  return <Fetch fetch={() => Promise.all([task(taskId), taskAnswers(taskId), cachedStructure(taskId)])}
                onLoadEnd={([, ans,]) => setAnswers(ans)}>
    {([taskView, , struct]) => {
      const names = struct.map(s => s.name);
      return <>
        <PageHeader title={taskView.name}
          extra={[
            <Button key="1" type="link" icon="plus">
              <Link to={`/task/${taskId}/form`}>添加受试者</Link>
            </Button>,
            <div key="_2" style={{ display: "inline-block" }}>
              <File ref={file} accept=".xlsx" onLoaded={importAnswers}/>
              <Button key="2" type="link" icon="import" disabled={parsing} loading={loading}
                      onClick={() => file.current?.click()}>批量导入</Button>
            </div>
          ]}
        />
        <Table<T> dataSource={answers} rowKey="id">
          <Table.Column<T> key="id" dataIndex="id" title="受试者编号"/>
          <Table.Column<T> key="creator" dataIndex="creator" title="创建人"/>
          <Table.Column<T> key="mtime" dataIndex="mtime" title="修改时间"/>
          <Table.Column<T> key="status" dataIndex="" title="患者入组" render={() => <span>患者入组</span>}/>
          {
            names.map(name =>
              <Table.Column<T> key={name} dataIndex="" title={name}
                               render={() => <Link to="#"/>}/>
            )
          }
          <Table.Column<T> key="action" dataIndex="" title="动作" render={actions}/>
        </Table>
      </>;
    }}
  </Fetch>;
};
