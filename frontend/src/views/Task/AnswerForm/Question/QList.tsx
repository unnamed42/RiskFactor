import React, { forwardRef } from "react";

import { Question, QProps } from ".";

export const QList = forwardRef<any, QProps>(({ schema: { id, list } }, ref) => {

  if(list === undefined)
    throw new Error(`list ${id} has no associated list`);

  return <>
    {list.map(q => <Question schema={q} key={q.id} fieldPrefix={`$${id}`}/>)}
  </>;
});
