import React, { forwardRef, useState } from "react";

import { Radio } from "antd";
import { RadioChangeEvent } from "antd/lib/radio";
import RadioGroup from "antd/lib/radio/group";

import { Question, QProps } from ".";
import { enablerFId } from "@/utils";

export const QYesNo = forwardRef<RadioGroup, QProps>(({ schema, value, onChange }, ref) => {

  const [choice, setChoice] = useState(value);

  const { list, yesno, id } = schema;

  const [yes, no] = (yesno ?? "是/否").split("/");

  const parentChanged = (e: RadioChangeEvent) => {
    const { value } = e.target;
    if (value === choice)
      return;
    setChoice(value);
    onChange?.(value);
  };

  return <Radio.Group ref={ref} value={choice} onChange={parentChanged}>
    <Radio value={yes}>{yes}</Radio>
    <Radio value={no}>{no}</Radio>
    {
      list && choice === yes ?
        list.map(q => <Question key={q.id} schema={q} fieldPrefix={enablerFId(id)} />) :
        null
    }
  </Radio.Group>;

});
