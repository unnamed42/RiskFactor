import React, { ReactElement, forwardRef } from "react";

import { Select } from "antd";
import { OptionProps, SelectProps } from "antd/lib/select";

const inputFilter = (input: string, option: ReactElement<OptionProps>) => {
  const text = option.props.value!.toString();
  return text.startsWith(input);
};

type P = SelectProps & QuestionProps;

export const QSelect = forwardRef<Select, P>(({ schema, ...props }, ref) => {
  if (!schema.option)
    throw new Error(`QSelect ${schema.field} has no option`);
  if (!schema.list)
    throw new Error(`QSelect ${schema.field} has no list`);

  return <Select ref={ref}
    showSearch={!!(schema.option.filterKey)}
    placeholder={schema.option.placeholder}
    filterOption={schema.option.filterKey ? inputFilter : undefined}
    mode={schema.type === "MULTI_SELECT" ? "multiple" : undefined}
  >
    {
      schema.list.map(({ label, option, field }) => {
        const value = (option && option.filterKey) || label;
        return <Select.Option key={field} value={value}>{label}</Select.Option>;
      })
    }
  </Select>;
});
