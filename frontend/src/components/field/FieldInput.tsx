import React, { FC, useMemo } from "react";

import { Input, InputNumber, Form } from "antd";

import type { RuleInput } from "@/api";
import { FieldProps, Field } from ".";

type P = FieldProps<RuleInput>;

export const FieldInput: FC<P> = ({ rule, namePath }) => {

  const addon = useMemo(() => {
    if (!rule.list)
      return {};
    const [elemRule] = rule.list;
    // if (elemRule === undefined)
    //   throw new Error(`输入类控件 ${id} 启用了前后置附加控件，但并未配置其内容`);
    const dom = <Field rule={elemRule} namePath={namePath} />;
    if (elemRule.addonPosition === "prefix")
      return { addonBefore: dom };
    else
      return { addonAfter: dom };
  }, [rule, namePath]);

  const InputControl = rule.type === "number" ? InputNumber : Input;

  return <Form.Item name={namePath} noStyle>
    <InputControl placeholder={rule.placeholder} {...addon} />
  </Form.Item>;
};
