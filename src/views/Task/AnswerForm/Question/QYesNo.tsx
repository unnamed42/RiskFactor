import React, { forwardRef, useState } from "react";

import { Radio } from "antd";
import { RadioChangeEvent } from "antd/lib/radio";

import { Question, QProps } from ".";

export const QYesNo = forwardRef<any, QProps<string>>(({ schema, value, onChange }, ref) => {

  const [choice, setChoice] = useState(value);

  const { list, yesno } = schema;

  const [yes, no] = (yesno || "是/否").split("/");

  const parentChanged = (e: RadioChangeEvent) => {
    const { value } = e.target;
    if (value === choice)
      return;
    setChoice(value);
    if (onChange)
      onChange(value);
  };

  const renderChild = () => {
    if (choice !== yes || !list)
      return null;
    return list.map(q =>
      <Question key={q.id} schema={q} />
    );
  };

  return <Radio.Group ref={ref} value={choice} onChange={parentChanged}>
    <Radio value={yes}>{yes}</Radio>
    <Radio value={no}>{no}</Radio>
    {
      list && choice === yes ?
        renderChild() :
        null
    }
  </Radio.Group>;

});
