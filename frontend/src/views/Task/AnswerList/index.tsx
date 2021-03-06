import React, { FC, useCallback } from "react";
import { Link } from "react-router-dom";
import { useParams } from "react-router";
import dayjs from "dayjs";

import { Button, PageHeader, Table } from "antd";
import { PlusOutlined, ImportOutlined } from "@ant-design/icons";

import { useApi, useData } from "@/hooks";
import {
  AnswerInfo, ApiIdType,
  getAnswerList, getSchemaInfo, removeAnswer
} from "@/api";
import { datePattern } from "@/config";

interface RouteParams {
  schemaId: string;
}

const fetch = (schemaId: ApiIdType) =>
  Promise.all([getAnswerList(schemaId), getSchemaInfo(schemaId)]);

const renderTime = (time: string) =>
  dayjs(time).format(datePattern);

export const AnswerList: FC = () => {
  const { schemaId } = useParams<RouteParams>();

  const [, deleteAnswer] = useApi(removeAnswer, [], { success: "删除成功", immediate: false });
  const [resp] = useApi(() => fetch(schemaId), [schemaId]);

  const [source, setSource] = useData(resp);

  // onClick={() => downloadAnswer(answer.id)}
  //  onClick={() => delAnswer(answer)}

  const rmAnswer = useCallback(async (answerId: ApiIdType) => {
    await deleteAnswer(answerId);
    setSource(prevSource => {
      if (prevSource !== undefined) {
        const [prevAns, prevInfo] = prevSource;
        return [prevAns.filter(ans => ans.id !== answerId), prevInfo];
      }
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const actions = useCallback((_: any, { id }: AnswerInfo) => {
    return <span>
      <Link to={`/app/${schemaId}/form/${id}`}>查看</Link>&nbsp;
      <Button type="link">导出</Button>&nbsp;
      <Button type="link" onClick={() => rmAnswer(id)}>删除</Button>
    </span>;
  }, [schemaId, rmAnswer]);


  if (resp.alt !== undefined)
    return resp.alt;

  const [answers, schemaInfo] = source ?? [];

  return <>
    <PageHeader title={schemaInfo?.name}
      extra={[
        <Button key="1" type="link" icon={<PlusOutlined />}>
          <Link to={`/app/${schemaId}/form`}>添加受试者</Link>
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
