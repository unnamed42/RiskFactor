import React, { forwardRef, useState, ChangeEvent } from "react";

import { Input } from "antd";

import { Question, QProps } from ".";

const reNumber = /^-?(0|[1-9]\d*)(\.\d*)?$/;

export const QInput = forwardRef<Input, QProps>(({ schema, onChange, value }, ref) => {

  const { type, list, placeholder } = schema;
  const [input, setInput] = useState(value);

  const changed = (e: ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    setInput(value);
    onChange?.(value);
  };

  const inputFilter = (e: ChangeEvent<HTMLInputElement>) => {
    const newVal = e.target.value;
    if (!newVal || reNumber.test(newVal))
      changed(e);
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
    onChange={type === "number" ? inputFilter : changed}
    placeholder={placeholder}
    {...addon()}
  />;
});
