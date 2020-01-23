import React, { FC } from "react";

import { Radio, Form } from "antd";

import { QProps as P, Renderer } from ".";

export const QYesNo: FC<P> = ({ rule: { id, list, yesno }, namePath }) => {
  const [yes, no] = (yesno ?? "是/否").split("/");
  const enabledContent = () => {
    if (list === undefined)
      throw new Error(`双选问题 ${id} 启用了联动，但未配置联动内容`);
    return <>{list.map(rule => <Renderer rule={rule} key={rule.id} namePath={namePath} />)}</>;
  };
  return <Form.Item name={namePath} noStyle>
    {form => <div>
      <Radio.Group>
        <Radio value={yes}>{yes}</Radio>
        <Radio value={no}>{no}</Radio>
      </Radio.Group>
      {form.getFieldValue(namePath) === yes ?
        enabledContent() :
        null}
    </div>}
  </Form.Item>;
};
