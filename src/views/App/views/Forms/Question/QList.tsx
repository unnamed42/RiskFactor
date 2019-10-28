import React, { forwardRef, useState } from "react";

import { Question, QProps } from ".";
import { fieldName } from "./util";

export const QList = forwardRef<any, QProps>((props, ref) => {

  const [values, setValues] = useState(props.value || {});
  const { schema: { list }, onChange } = props;

  const propChanged = (child: string, value: any) => {
    const newValue = { ...values, [child]: value };
    setValues(newValue);
    if (onChange)
      onChange(newValue);
  };

  return <div ref={ref}>
    {
      list!.map(q =>
        <Question key={q.field} schema={q}
          onChange={value => propChanged(fieldName(q.field), value)}
        />)
    }
  </div>;
});
