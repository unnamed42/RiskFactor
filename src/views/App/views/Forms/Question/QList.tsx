import React, { forwardRef } from "react";

import { Question } from ".";

export const QList = forwardRef<any, QProps>((props, ref) => {

  const { schema: { field, list } } = props;

  const childChanged = (e: QChangeEvent) => props.onChange && props.onChange({
    field, value: { ...props.value, [e.field]: e.value }
  });

  return <div ref={ref}>
    {
      list!.map(q =>
        <Question key={q.field} schema={q} onChange={childChanged} />)
    }
  </div>;
});
