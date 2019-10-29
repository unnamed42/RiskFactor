import React, { ReactElement, forwardRef, useState } from "react";

import { Select } from "antd";

import { QProps } from ".";

export const QSelect = forwardRef<Select, QProps<any | any[]>>(({ schema, onChange, value }, ref) => {
  const { type, list, field, filterKey, placeholder } = schema;

  if (!list)
    throw new Error(`QSelect ${field} has no list`);

  const [selected, setSelected] = useState(value);

  const inputFilter = (input: string, option: ReactElement) =>
    option.props["data-key"]!.toString().startsWith(input);

  const changed = (value: any) => {
    setSelected(value);
    if (onChange)
      onChange(value);
  };

  return <Select ref={ref}
    showSearch={!!filterKey}
    placeholder={placeholder}
    filterOption={filterKey ? inputFilter : undefined}
    mode={type === "MULTI_SELECT" ? "multiple" : undefined}
    value={selected} onChange={changed}
  >
    {
      list.map(({ label, filterKey, field }) => {
        return <Select.Option key={field} value={label} data-key={filterKey}>
          {label}
        </Select.Option>;
      })
    }
  </Select>;
});
