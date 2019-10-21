import React, { forwardRef, ReactElement, useState, ChangeEvent } from "react";

import { Input } from "antd";
import { InputProps } from "antd/lib/input";

import { decorated } from "./utils";

type P = QuestionProps & Omit<InputProps, "form">;

export default forwardRef<any, P>(({ schema, form, ...remain }, ref) => {

  const [value, setValue] = useState("");

  const validate = (e: ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    const ruleNumber = /^-?(0|[1-9]\d*)(\.\d*)?$/;
    if (val === "" || ruleNumber.test(val))
      setValue(val);
  };

  return decorated(schema, form)(
    <Input ref={ref} type="text" value={value}
           onChange={schema.type === "NUMBER" ? validate : undefined}
           placeholder={schema.option && schema.option.placeholder}/>
  ) as ReactElement;
});
