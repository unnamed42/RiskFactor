import React, { forwardRef } from "react";

import { Question, QProps } from ".";
import { Question as QSchema } from "@/types/task";

interface P extends Omit<QProps, "schema"> {
  list: QSchema[];
}

export const QList = forwardRef<any, P>(({ list }, ref) => {

  return <div>
    {
      list!.map(q =>
        <Question key={q.id} schema={q} ref={ref} />)
    }
  </div>;
});
