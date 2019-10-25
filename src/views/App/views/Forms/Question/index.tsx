import React, { forwardRef } from "react";

import { QInput } from "./QInput";
import { QSelect } from "./QSelect";
import { QDate } from "./QDate";

const renderer = (type: Question["type"]) => {
  switch (type) {
    case "NUMBER": case "TEXT": return QInput;
    case "SINGLE_SELECT": case "MULTI_SELECT": return QSelect;
    case "DATE": return QDate;
    default: return forwardRef(() => <div />);
  }
};

export const Question = forwardRef<any, QuestionProps>(({ schema }, ref) => {
  const Child = renderer(schema.type);
  return <Child ref={ref} schema={schema} />;
});

export const decoratorOptions = ({ option }: Question) => {
  if (!option) return {};
  return {
    rules: [{
      required: option.required,
      message: option.message,
      placeholder: option.placeholder
    }]
  };
};
