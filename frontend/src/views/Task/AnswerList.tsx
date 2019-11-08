import React, { FC } from "react";
import { Link } from "react-router-dom";

import { Button, PageHeader, message, Table, Upload } from "antd";

import {
   taskAnswers, task, deleteAnswer,
  downloadAnswer, taskSectionNames
} from "@/api/task";
import { local } from "@/api/persist";

import { AnswerBrief } from "@/types";
import { usePromise } from "@/utils";
import { PageLoading } from "@/components";

interface P {
  taskId: string | number;
}

type T = AnswerBrief;

export const AnswerList: FC<P> = ({ taskId }) => {

  const source = usePromise(() =>
    Promise.all([task(taskId), taskSectionNames(taskId), taskAnswers(taskId)])
  );

  if(!source.loaded)
    return <PageLoading />;
  if("error" in source)
    return null;

  // const sectionLink = (answer: T, section: SectionBrief) =>
  //   <Link to={`/task/${taskId}/form/${answer.id}/${section.id}`}>{section.title}</Link>;

  const delAnswer = (answer: T) =>
    deleteAnswer(answer.id).then(() => { window.location.reload(); message.success("删除成功"); })
      .catch(err => message.error(err.message));

  const actions = (text: any, answer: T) => {
    return <span>
      <Link to={`/task/${taskId}/form/${answer.id}`}>查看</Link>
      &nbsp;
      <Link to="#" onClick={() => downloadAnswer(answer.id)}>导出</Link>
      &nbsp;
      <Link to="#" onClick={() => delAnswer(answer)}>删除</Link>
    </span>;
  };

  const [info, sections, answers] = source.value;

  const names = sections.reduce((reduced, curr) => {
    const [h1] = curr.title.split("/");
    if(!reduced.includes(h1))
      reduced.push(h1);
    return reduced;
  }, [] as string[]);

  return <div>
    <PageHeader title={info.name}
      extra={[
        <Button key="1" type="link" icon="plus">
          <Link to={`/task/${taskId}/form`}>添加受试者</Link>
        </Button>,
        <Upload key="_2" action={`http://localhost:8090/task/${taskId}/answer/file`}
          headers={{ authorization: `Bearer ${local.auth.token}` }}
          onChange={({ file, fileList }) => {
            if (file.status !== "uploading")
              console.log(file, fileList);
            if (file.status === "done")
              message.success(`${file.name} 上传成功`);
            else
              message.error(`${file.name} 上传失败`);
          }}
        >
          <Button key="2" type="link" icon="import">批量导入</Button>
        </Upload>
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
