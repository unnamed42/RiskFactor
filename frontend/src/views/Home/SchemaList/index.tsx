import React, { FC } from "react";
import { Link } from "react-router-dom";
import dayjs from "dayjs";

import { List, Avatar } from "antd";

import { SchemaInfo, getSchemas } from "@/api";
import { datePattern } from "@/config";
import { Fetch } from "@/components";

import "./index.less";
// eslint-disable-next-line @typescript-eslint/no-var-requires
const briefcase = require("@public/briefcase.png");

const renderItem = (info: SchemaInfo) => <List.Item>
  <List.Item.Meta title={<Link to={`/app/${info.id}/answers`}>{info.name}</Link>}
    avatar={<Avatar shape="square" size="large" src={briefcase} />}
    description={<span>
      <span className="rf-schema-description">归属中心：{info.creator.group}</span>
      <span className="rf-schema-description">创建时间：{dayjs(info.createdAt).format(datePattern)}</span>
    </span>} />
</List.Item>;

export const SchemaList: FC = () => <Fetch fetch={getSchemas}>
  {({data}) => <List<SchemaInfo> dataSource={data} className="rf-schema-list" renderItem={renderItem} />}
</Fetch>;

export default SchemaList;
