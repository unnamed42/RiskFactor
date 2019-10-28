import React, { ReactElement, forwardRef, useState } from "react";

import { Select } from "antd";

import { QProps } from ".";

export const QSelect = forwardRef<Select, QProps<any | any[]>>((props, ref) => {
  const { schema: { option, list, type, field } } = props;

  if (!option)
    throw new Error(`QSelect ${field} has no option`);
  if (!list)
    throw new Error(`QSelect ${field} has no list`);

  const [selected, setSelected] = useState(props.value);

  const inputFilter = (input: string, option: ReactElement) =>
    option.props["data-key"]!.toString().startsWith(input);

  const onChange = (value: any) => {
    setSelected(value);
    if (props.onChange)
      props.onChange(value);
  };

  return <Select ref={ref}
    showSearch={!!(option.filterKey)}
    placeholder={option.placeholder}
    filterOption={option.filterKey ? inputFilter : undefined}
    mode={type === "MULTI_SELECT" ? "multiple" : undefined}
    value={selected} onChange={onChange}
  >
    {
      list.map(({ label, option, field }) => {
        return <Select.Option key={field} value={label} data-key={option && option.filterKey}>
          {label}
        </Select.Option>;
      })
    }
  </Select>;
});
