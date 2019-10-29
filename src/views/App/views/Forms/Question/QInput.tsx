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
    if (onChange)
      onChange(value);
  };

  const inputFilter = (e: ChangeEvent<HTMLInputElement>) => {
    const newVal = e.target.value;
    if (!newVal || reNumber.test(newVal))
      changed(e);
  };

  const addon = () => {
    if (!list)
      return {};
    const [childSchema] = list;
    const { addonPosition } = childSchema;
    if (addonPosition === undefined)
      throw new Error(`Input addon is invalid`);
    const element = <Question schema={childSchema}
      formItemProps={{ labelCol: {}, wrapperCol: {} }}
    />;
    if (addonPosition === "prefix")
      return { addonBefore: element };
    else
      return { addonAfter: element };
  };

  return <Input ref={ref} value={input} type="text"
    onChange={type === "NUMBER" ? inputFilter : onChange}
    placeholder={placeholder}
    {...addon()}
  />;
});
