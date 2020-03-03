import React, { FC } from "react";

import { Radio, Form } from "antd";

import { QProps as P, Renderer } from ".";
import { shouldUpdate } from "@/utils";

export const QYesNo: FC<P> = ({ rule: { id, list, yesno }, namePath }) => {
  const splitted = (yesno ?? "是/否").split("/");
  if (splitted.length < 2)
    throw new Error(`双选问题 ${id} 的选项名配置不正确`);
  const [yes, no] = splitted;

  return <Form.Item name={namePath} noStyle>
    <Radio.Group>
      <Radio value={yes}>{yes}</Radio>
      <Radio value={no}>{no}</Radio>
    </Radio.Group>
    <Form.Item noStyle shouldUpdate={shouldUpdate(namePath)}>
      {form => {
        if (form.getFieldValue(namePath) !== yes)
          return null;
        if (list === undefined)
          throw new Error(`双选问题 ${id} 启用了联动，但未配置联动内容`);
        return list.map(rule => <Renderer rule={rule} key={rule.id} namePath={namePath} />);
      }}
    </Form.Item>
  </Form.Item>;
};
