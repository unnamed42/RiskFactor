import React, { forwardRef, useState } from "react";

import { Checkbox } from "antd";
import { CheckboxValueType } from "antd/lib/checkbox/Group";

import { Question } from ".";

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

  return <Checkbox.Group ref={ref} value={selected} onChange={onChange}>
    {
      list!.map(({ label, list }, idx) => {
        if (!label)
          throw new Error(`CHOICE has no label`);
        return <div key={`d-${idx}`}>
          <Checkbox key={idx} value={label}>
            {label}
            {
              list && selected.includes(label) && list.map(q =>
                <Question schema={q} key={q.field} />
              )
            }
          </Checkbox>
          <br />
        </div>;
      })
    }
  </Checkbox.Group>;

});
