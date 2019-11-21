import React, { ReactElement, forwardRef, useState } from "react";

import { Select } from "antd";

import { QProps } from ".";

export const QSelect = forwardRef<Select, QProps>(({ schema, onChange, value }, ref) => {
  const { type, id, choices, placeholder } = schema;
  if (!choices || choices.length === 0)
    throw new Error(`select ${id} has no list`);
  const canFilter = choices[0].indexOf("/") !== -1;

  const [selected, setSelected] = useState(value);

  const inputFilter = (input: string, option: ReactElement) =>
    option.props["data-key"]?.toString().startsWith(input);

  const changed = (value: any) => {
    setSelected(value);
    onChange?.(value);
  };

  return <Select ref={ref} showSearch={canFilter} placeholder={placeholder}
                 value={selected} onChange={changed}
                 filterOption={canFilter ? inputFilter : undefined}
                 mode={type === "select-multi" ? "multiple" : undefined}>
    {
      choices.map((choice, idx) => {
        const [label, filter] = choice.split("/");
        return <Select.Option key={idx} value={label} data-key={filter}>{label}</Select.Option>;
      })
    }
  </Select>;
});
