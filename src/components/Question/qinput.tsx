import React from "react";

import { Input } from "antd";
import { InputProps } from "antd/lib/input";

import { decorated } from "./utils";
import { QuestionGenerator } from ".";

type P = QuestionProps & Omit<InputProps, "form">;

export const qinput: QuestionGenerator = (schema, form) => {

  // const ruleNumber = /^-?(0|[1-9]\d*)(\.\d*)?$/;

  return decorated(schema, form, {
    rules: [{ type: "number", message: "请输入数字" }],
    validateTrigger: ["onChange", "onBlur"]
  })(
    <Input type="text"
           placeholder={schema.option && schema.option.placeholder}/>
  );
};
