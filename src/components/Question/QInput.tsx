import React, { forwardRef, useState } from "react";

import { Input } from "antd";
import { InputProps } from "antd/lib/input";

type P = QuestionProps & InputProps;

const reNumber = /^-?(0|[1-9]\d*)(\.\d*)?$/;

export const QInput = forwardRef<Input, P>(({ schema: { type, option }, ...props }, ref) => {

  const [value, setValue] = useState(props.value);

  const inputFilter = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newVal = e.target.value;
    if (!newVal || reNumber.test(newVal)) {
      setValue(newVal);
      props.onChange!(e);
    }
  };

  return <Input {...props} ref={ref} value={value} type="text"
    onChange={type === "NUMBER" ? inputFilter : undefined}
    placeholder={ option && option.placeholder }
  />;
});
