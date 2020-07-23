import React, { FC } from "react";
import { Link } from "react-router-dom";
import dayjs from "dayjs";

import { List, Avatar } from "antd";

import { SchemaInfo, getSchemas } from "@/api";
import { useApi } from "@/hooks";
import { datePattern } from "@/config";

import style from "@/styles/schema-list.mod.less";
import briefcase from "@public/briefcase.png";

const renderItem = (info: SchemaInfo) => <List.Item>
  <List.Item.Meta title={<Link to={`/app/${info.id}/answers`}>{info.name}</Link>}
    avatar={<Avatar shape="square" size="large" src={briefcase} />}
    description={<span>
      <span className={style.description}>归属中心：{info.creator.group}</span>
      <span className={style.description}>创建时间：{dayjs(info.createdAt).format(datePattern)}</span>
    </span>} />
</List.Item>;

export const SchemaList: FC = () => {
  const [state, ] = useApi(getSchemas, []);

  if(state === undefined || state.error)
    return null;
  if(state.alt !== undefined)
    return state.alt;

  return <List<SchemaInfo> dataSource={state.data} className={style.list}
                           renderItem={renderItem} />;
};
