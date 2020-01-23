import React, { FC, useMemo } from "react";

import { Input, Form } from "antd";

import { QProps as P, Renderer } from ".";

export const QInput: FC<P> = ({ rule: { placeholder, list, id }, namePath }) => {
  const addon = useMemo(() => {
    if (!list) return {};
    const rule = list[0];
    if (rule === undefined)
      throw new Error(`输入类控件 ${id} 启用了前后置附加控件，但并未配置其内容`);
    const dom = <Renderer rule={rule} namePath={namePath} />;
    if (rule.addonPosition === "prefix")
      return { addonBefore: dom };
    else
      return { addonAfter: dom };
  }, [list]);

  return <Form.Item name={namePath} noStyle>
    <Input type="text" placeholder={placeholder} {...addon} />
  </Form.Item>;
};
