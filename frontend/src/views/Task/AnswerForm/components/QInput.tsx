import React, { FC, useMemo } from "react";

import { Input, Form } from "antd";

import { RenderProps as P, Renderer } from ".";

export const QInput: FC<P> = ({ rule: { placeholder, list, id }, namePath }) => {

  const addon = useMemo(() => {
    if (!list) return {};
    const elemRule = list[0];
    if (elemRule === undefined)
      throw new Error(`输入类控件 ${id} 启用了前后置附加控件，但并未配置其内容`);
    const dom = <Renderer rule={elemRule} namePath={namePath} />;
    if (elemRule.addonPosition === "prefix")
      return { addonBefore: dom };
    else
      return { addonAfter: dom };
  }, [list, id, namePath]);

  return <Form.Item name={namePath} noStyle>
    <Input type="text" placeholder={placeholder} {...addon} />
  </Form.Item>;
};
