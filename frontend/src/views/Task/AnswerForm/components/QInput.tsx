import React, { FC, useMemo } from "react";

import { Input, InputNumber, Form } from "antd";

import { RenderProps as P, Renderer } from ".";

export const QInput: FC<P> = ({ rule: { placeholder, list, id, type }, namePath }) => {

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

  const InputControl = type === "number" ? InputNumber : Input;

  return <Form.Item name={namePath} noStyle>
    <InputControl placeholder={placeholder} {...addon} />
  </Form.Item>;
};
