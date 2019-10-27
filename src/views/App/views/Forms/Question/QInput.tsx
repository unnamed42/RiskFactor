import React, { forwardRef, useState, ChangeEvent } from "react";

import { Input } from "antd";

const reNumber = /^-?(0|[1-9]\d*)(\.\d*)?$/;

export const QInput = forwardRef<Input, QProps>((props, ref) => {

  const { field, type, option } = props.schema;
  const [input, setInput] = useState(props.value);

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    setInput(value);
    if (props.onChange)
      props.onChange({ field, value });
  };

  const inputFilter = (e: ChangeEvent<HTMLInputElement>) => {
    const newVal = e.target.value;
    if (!newVal || reNumber.test(newVal))
      onChange(e);
  };

  return <Input ref={ref} value={input} type="text"
    onChange={type === "NUMBER" ? inputFilter : onChange}
    placeholder={option && option.placeholder}
  />;
});
