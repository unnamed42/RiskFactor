import React, { forwardRef, useState } from "react";

import { Radio } from "antd";
import { RadioChangeEvent } from "antd/lib/radio";

import { Question } from ".";

interface V {
  answer?: string;
  enabled?: any;
}

export const QYesNo = forwardRef<any, QProps<V>>((props, ref) => {

  const value: V = props.value || {};

  const [choice, setChoice] = useState(value.answer);

  const { schema: { option, field, list } } = props;
  if (!option)
    throw new Error(`QYesNoChoice ${field} has no option`);
  const { enabler, detail } = option;
  if (!detail)
    throw new Error(`QYesNoChoice ${field} has no option detail`);

  const [yes, no] = detail.split("/");

  const parentChanged = (e: RadioChangeEvent) => {
    const val = e.target.value;
    if (val === choice) return;
    setChoice(val);
    if (props.onChange)
      props.onChange({
        field, value: {
          answer: val
        }
      });
  };

  const childChanged = (e: QChangeEvent) => props.onChange && props.onChange({
    field,
    value: {
      answer: choice,
      enabled: {
        ...value.enabled,
        [e.field]: e.value
      }
    }
  });

  const renderChild = () => {
    if (choice !== yes || !enabler)
      return null;
    return list!.map(q =>
      <Question key={q.field} schema={q} onChange={childChanged} />
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
