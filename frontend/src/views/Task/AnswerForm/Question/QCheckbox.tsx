import React, { forwardRef, useState } from "react";

import { Checkbox } from "antd";
import { CheckboxValueType } from "antd/lib/checkbox/Group";

import { QProps } from ".";
import { QList } from "./QList";

export const QCheckbox = forwardRef<any, QProps<string[]>>(({ schema, onChange, value }, ref) => {

  const { selected, list, id } = schema;
  const defaultValue = value || (selected ?? "").split(",");

  if(list === undefined)
    throw new Error(`choice ${id} is incorrectly configured - no list`);

  const [chosen, setChosen] = useState(defaultValue);

  const changed = (values: CheckboxValueType[]) => {
    const strValues = values as string[];
    setChosen(strValues);
    onChange?.(strValues);
  };

  return <Checkbox.Group ref={ref} value={chosen} onChange={changed}>
    {
      list.map(({ label, list, id }, idx) => {
        if (!label)
          throw new Error(`choice item ${id} has no label`);
        return <div key={`d-${idx}`}>
          <Checkbox key={idx} value={label}>
            {label}
            {
              list && chosen.includes(label) && <QList list={list} />
            }
          </Checkbox>
          <br />
        </div>;
      })
    }
  </Checkbox.Group>;

});
