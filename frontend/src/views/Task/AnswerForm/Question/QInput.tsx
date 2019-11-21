import React, { forwardRef, useState } from "react";

import { Input } from "antd";

import { Question, QProps } from ".";

export const QInput = forwardRef<Input, QProps>(({ schema, onChange, value }, ref) => {

  const { list, placeholder } = schema;
  const [input, setInput] = useState(value);

  const changed = (newValue: string) => {
    setInput(newValue);
    onChange?.(newValue);
  };

  const addon = () => {
    if (!list)
      return {};
    const { addonPosition } = list[0];
    if (addonPosition === undefined)
      throw new Error(`Input addon is invalid`);
    const element = <Question schema={list[0]} noFormItem
      formItemProps={{ labelCol: {}, wrapperCol: {} }}
    />;
    if (addonPosition === "prefix")
      return { addonBefore: element };
    else
      return { addonAfter: element };
  };

  return <Input ref={ref} value={input} type="text"
    onChange={e => changed(e.target.value)}
    placeholder={placeholder}
    {...addon()}
  />;
});
