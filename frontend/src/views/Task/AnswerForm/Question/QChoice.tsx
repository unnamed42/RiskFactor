import React, { forwardRef, useState } from "react";

import { Radio } from "antd";
import { RadioChangeEvent } from "antd/lib/radio";

import { QProps } from ".";

export const QChoice = forwardRef<any, QProps>(({ value, onChange, schema }, ref) => {

  const [selected, setSelected] = useState(value);
  const { list } = schema;

  const changed = (e: RadioChangeEvent) => {
    const { value } = e.target;
    setSelected(value);
    onChange?.(value);
  };

  return <Radio.Group ref={ref} value={selected} onChange={changed}>
    {
      list!.map(q =>
        <Radio key={q.id} value={q.label}>{q.label}</Radio>
      )
    }
  </Radio.Group>;
});
