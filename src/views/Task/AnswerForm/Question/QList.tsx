import React, { forwardRef, useState } from "react";

import { Question, QProps } from ".";
import { Question as QSchema } from "@/types/task";

interface P extends Omit<QProps, "schema"> {
  list: QSchema[];
}

export const QList = forwardRef<any, P>(({ value, list, onChange }, ref) => {

  const [values, setValues] = useState(value || {});

  const propChanged = (child: string, value: any) => {
    const newValue = { ...values, [child]: value };
    setValues(newValue);
    if (onChange)
      onChange(newValue);
  };

  return <div ref={ref}>
    {
      list!.map(q =>
        <Question key={q.id} schema={q}
          onChange={value => propChanged(q.id.toString(), value)}
        />)
    }
  </div>;
});
