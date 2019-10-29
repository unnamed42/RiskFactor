import React, { forwardRef, useState } from "react";

import { Radio } from "antd";
import { RadioChangeEvent } from "antd/lib/radio";

import { Question, QProps } from ".";

export const QYesNo = forwardRef<any, QProps<string>>(({ schema, value, onChange }, ref) => {

  const [choice, setChoice] = useState(value);

  const { field, list, isEnabler, yesno } = schema;
  if (!yesno)
    throw new Error(`QYesNoChoice ${field} has no option detail`);

  const [yes, no] = yesno.split("/");

  const parentChanged = (e: RadioChangeEvent) => {
    const { value } = e.target;
    if (value === choice)
      return;
    setChoice(value);
    if (onChange)
      onChange(value);
  };

  const renderChild = () => {
    if (choice !== yes || !isEnabler)
      return null;
    return list!.map(q =>
      <Question key={q.field} schema={q} />
    );
  };

  return <Radio.Group ref={ref} value={choice} onChange={parentChanged}>
    <Radio value={yes}>{yes}</Radio>
    <Radio value={no}>{no}</Radio>
    {
      isEnabler && choice === yes ?
        renderChild() :
        null
    }
  </Radio.Group>;

});
