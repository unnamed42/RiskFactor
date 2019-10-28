import React, { forwardRef, useState, ChangeEvent } from "react";

import { Input } from "antd";

import { Question, QProps } from ".";

const reNumber = /^-?(0|[1-9]\d*)(\.\d*)?$/;

export const QInput = forwardRef<Input, QProps>((props, ref) => {

  const { type, option, list } = props.schema;
  const [input, setInput] = useState(props.value);

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    setInput(value);
    if (props.onChange)
      props.onChange(value);
  };

  const inputFilter = (e: ChangeEvent<HTMLInputElement>) => {
    const newVal = e.target.value;
    if (!newVal || reNumber.test(newVal))
      onChange(e);
  };

  const addon = () => {
    if (!list) return {};
    const [schema] = list;
    if (!schema.option || schema.option.prefixPostfix === undefined)
      throw new Error(`Input addon is invalid`);
    const { prefixPostfix } = schema.option;
    const element = <Question schema={schema}
      formItemProps={{ labelCol: {}, wrapperCol: {} }}
    />;
    if (prefixPostfix)
      return { addonBefore: element };
    else
      return { addonAfter: element };
  };

  return <Input ref={ref} value={input} type="text"
    onChange={type === "NUMBER" ? inputFilter : onChange}
    placeholder={option && option.placeholder}
    {...addon()}
  />;
});
