import React, { forwardRef, useState } from "react";

import { Checkbox } from "antd";
import { CheckboxValueType } from "antd/lib/checkbox/Group";

export const QCheckbox = forwardRef<any, QProps<string[]>>((props, ref) => {

  const { schema: { option, field, list } } = props;

  const defaultValue = ((option && option.defaultSelected) || "").split(",");

  const [selected, setSelected] = useState(props.value || defaultValue);

  const onChange = (values: CheckboxValueType[]) => {
    const value = values as string[];
    setSelected(value);
    if (props.onChange)
      props.onChange({ field, value });
  };

  // TODO: 联动多选框
  return <Checkbox.Group ref={ref} value={selected}
    onChange={onChange}
    options={list!.map(({ label }) => ({ label: label!, value: label! }))}
  />;

});
