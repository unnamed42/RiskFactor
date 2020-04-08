import React, { FC } from "react";
import { Link } from "react-router-dom";
import { useParams } from "react-router";
import dayjs from "dayjs";

import { Button, PageHeader, Table } from "antd";
import { PlusOutlined, ImportOutlined } from "@ant-design/icons";

import { useApi, useData } from "@/utils";
import { IdType, AnswerInfo, getAnswerList, getSchemaInfo, removeAnswer } from "@/api";
import { datePattern } from "@/config";

interface RouteParams {
  schemaId: string;
}

const fetch = (schemaId: IdType) =>
  Promise.all([getAnswerList(schemaId), getSchemaInfo(schemaId)]);

const renderTime = (time: string) =>
  dayjs(time).format(datePattern);

export const AnswerList: FC = () => {
  const schemaId = Number(useParams<RouteParams>().schemaId);

  const [, deleteAnswer] = useApi(removeAnswer, [], { success: "删除成功", immediate: false });
  const [resp] = useApi(() => fetch(schemaId), [schemaId]);

  const [source, setSource] = useData(resp);

  if (resp.alt !== undefined)
    return resp.alt;

  const [answers, schemaInfo] = source ?? [];

  // onClick={() => downloadAnswer(answer.id)}
  //  onClick={() => delAnswer(answer)}

  const rmAnswer = async (answerId: IdType) => {
    await deleteAnswer(answerId);
    setSource(prevSource => {
      if (prevSource !== undefined) {
        const [prevAns, prevInfo] = prevSource;
        return [prevAns.filter(ans => ans.id !== answerId), prevInfo];
      }
    });
  };

  const actions = (_: any, { id }: AnswerInfo) => {
    return <span>
      <Link to={`/task/${schemaId}/form/${id}`}>查看</Link>&nbsp;
      <Button type="link">导出</Button>&nbsp;
      <Button type="link" onClick={() => rmAnswer(id)}>删除</Button>
    </span>;
  };

  return <>
    <PageHeader title={schemaInfo?.name}
      extra={[
        <Button key="1" type="link" icon={<PlusOutlined />}>
          <Link to={`/task/${schemaId}/form`}>添加受试者</Link>
        </Button>,
        <Button key="2" type="link" icon={<ImportOutlined />}>批量导入</Button>
      ]}
    />
    <Table<AnswerInfo> dataSource={answers} rowKey="id">
      <Table.Column key="id" dataIndex="id" title="受试者编号" />
      <Table.Column key="creator" dataIndex="creator" title="创建人"/>
      <Table.Column key="modifiedAt" dataIndex="modifiedAt" title="修改时间" render={renderTime} />
      <Table.Column key="status" dataIndex="" title="患者入组" render={() => "患者入组"} />
      {
        // names.map(name =>
        //   <Table.Column<T> key={name} dataIndex="" title={name}
        //     render={() => <Link to="#" />} />
        // )
      }
      <Table.Column key="action" dataIndex="" title="动作" render={actions} />
    </Table>
  </>;
};

export default AnswerList;
