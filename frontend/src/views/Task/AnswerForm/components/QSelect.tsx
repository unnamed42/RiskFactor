import React, { FC } from "react";

import { Select, Form } from "antd";

import { RenderProps as P } from ".";

export const QSelect: FC<P> = ({ rule: { id, choices, placeholder, type }, namePath }) => {
  if (!choices || choices.length === 0)
    throw new Error(`下拉菜单选择 ${id} 未配置选项`);

  // 是否开启“输入时筛选”功能
  const filterable = choices[0].includes("/");

  return <Form.Item name={namePath} noStyle>
    <Select<string> showSearch={filterable} placeholder={placeholder} optionLabelProp="label"
      allowClear filterOption={filterable} mode={type === "select-multi" ? "multiple" : undefined}>
      {
        choices.map((choice, idx) => {
          const [label, filter] = choice.split("/");
          return <Select.Option key={idx} value={filter} label={label}>{label}</Select.Option>;
        })
      }
    </Select>
  </Form.Item>;
};
