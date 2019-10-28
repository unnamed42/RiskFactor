import React, { forwardRef, useState } from "react";

import { Radio } from "antd";
import { RadioChangeEvent } from "antd/lib/radio";

import { Question, QProps } from ".";

export const QYesNo = forwardRef<any, QProps<string>>((props, ref) => {

  const [choice, setChoice] = useState(props.value);

  const { schema: { option, field, list } } = props;
  if (!option)
    throw new Error(`QYesNoChoice ${field} has no option`);
  const { enabler, detail } = option;
  if (!detail)
    throw new Error(`QYesNoChoice ${field} has no option detail`);

  const [yes, no] = detail.split("/");

  const parentChanged = (e: RadioChangeEvent) => {
    const { value } = e.target;
    if (value === choice)
      return;
    setChoice(value);
    if (props.onChange)
      props.onChange(value);
  };

  const renderChild = () => {
    if (choice !== yes || !enabler)
      return null;
    return list!.map(q =>
      <Question key={q.field} schema={q} />
    );
  };

  return <Radio.Group ref={ref} value={choice} onChange={parentChanged}>
    <Radio value={yes}>{yes}</Radio>
    <Radio value={no}>{no}</Radio>
    {
      enabler && choice === yes ?
        renderChild() :
        null
    }
  </Radio.Group>;

});
