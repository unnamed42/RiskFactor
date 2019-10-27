import React, { ReactElement, forwardRef } from "react";

import { Select } from "antd";
import { OptionProps } from "antd/lib/select";

export const QSelect = forwardRef<Select, QProps>((props, ref) => {
  const { schema: { option, list, type, field } } = props;

  if (!option)
    throw new Error(`QSelect ${field} has no option`);
  if (!list)
    throw new Error(`QSelect ${field} has no list`);

  const inputFilter = (input: string, option: ReactElement<OptionProps>) =>
    option.props.value!.toString().startsWith(input);

  const onChange = (value: any) =>
    props.onChange && props.onChange({ field, value });

  return <Select ref={ref}
    showSearch={!!(option.filterKey)}
    placeholder={option.placeholder}
    filterOption={option.filterKey ? inputFilter : undefined}
    mode={type === "MULTI_SELECT" ? "multiple" : undefined}
    value={props.value} onChange={onChange}
  >
    {
      list.map(({ label, option, field }) => {
        const value = (option && option.filterKey) || label;
        return <Select.Option key={field} value={value}>
          {label}
        </Select.Option>;
      })
    }
  </Select>;
});
