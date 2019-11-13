import React, { forwardRef, useState } from "react";

import { Question, QProps } from ".";

interface S {
  [key: string]: string;
}

export const QList = forwardRef<any, QProps>(({ schema: { id, list }, value, onChange }, ref) => {

  const [values, setValues] = useState<S>(value ? JSON.parse(value) : {});

  if(list === undefined)
    throw new Error(`list ${id} has no associated list`);

  const itemChanged = (v: string, idx: number) => {
    const newValues: S = { ...values, [idx]: v };
    setValues(newValues);
    onChange?.(JSON.stringify(newValues));
  };

  return <div>
    {
      list.map((q, idx) =>
         <Question noDecorator={true} schema={q} key={q.id} value={values[idx]}
                   onChange={v => itemChanged(v, idx)} />
      )
    }
  </div>;
});
