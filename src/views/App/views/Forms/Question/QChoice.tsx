import React, { forwardRef, useState } from "react";

import { Radio } from "antd";
import { RadioChangeEvent } from "antd/lib/radio";

import { QProps } from ".";

export const QChoice = forwardRef<any, QProps>((props, ref) => {

  const [selected, setSelected] = useState(props.value);
  const { schema: { list } } = props;

  const onChange = (e: RadioChangeEvent) => {
    const { value } = e.target;
    setSelected(value);
    if (props.onChange)
      props.onChange(value);
  };

  return <Radio.Group ref={ref} value={selected} onChange={onChange}>
    {
      list!.map(q =>
        <Radio key={q.field} value={q.label}>{q.label}</Radio>
      )
    }
  </Radio.Group>;
});
