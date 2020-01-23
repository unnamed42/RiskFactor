import React, { FC } from "react";

import { QProps as P, Renderer } from ".";

export const QList: FC<P> = ({ rule: { id, list }, namePath }) => {
  if (list === undefined)
    throw new Error(`问题列表 ${id} 未配置内容`);
  return <>
    {list.map(rule => <Renderer rule={rule} namePath={namePath} key={rule.id} />)}
  </>;
};
