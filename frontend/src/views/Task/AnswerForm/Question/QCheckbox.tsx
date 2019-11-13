import React, { forwardRef, useState } from "react";

import { Checkbox } from "antd";
import { CheckboxValueType } from "antd/lib/checkbox/Group";

import { QProps } from ".";
import { QList } from "./QList";

export const QCheckbox = forwardRef<any, QProps>(({ schema, onChange, value }, ref) => {

  const { selected, list, id } = schema;
  const defaultValue = (value || selected) ?? "[]";

  if(list === undefined)
    throw new Error(`choice ${id} is incorrectly configured - no list`);

  const [chosen, setChosen] = useState<string[]>(JSON.parse(defaultValue));

  const changed = (values: CheckboxValueType[]) => {
    const strValues = values as string[];
    setChosen(strValues);
    onChange?.(JSON.stringify(strValues));
  };

  return <Checkbox.Group ref={ref} value={chosen} onChange={changed}>
    {
      list.map((q, idx) => {
        const { label, list } = q;
        if (!label)
          throw new Error(`choice item ${id} has no label`);
        return <div key={`d-${idx}`}>
          <Checkbox key={idx} value={label}>
            {label}
            {
              list && chosen.includes(label) && <QList schema={q} />
            }
          </Checkbox>
          <br />
        </div>;
      })
    }
  </Checkbox.Group>;

});
