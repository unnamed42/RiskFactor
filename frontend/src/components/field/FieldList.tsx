import React, { FC } from "react";

import type { RuleList } from "@/api";
import { FieldProps, Field } from ".";

type P = FieldProps<RuleList>;

export const FieldList: FC<P> = ({ rule: { list }, namePath }) => {
  // if (list === undefined)
  //   throw new Error(`问题列表 ${id} 未配置内容`);
  return <>
    {
      list.map(rule => <Field rule={rule} key={rule.id} namePath={namePath} />)
    }
  </>;
};
