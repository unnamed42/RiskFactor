import React, { forwardRef } from "react";

import { Input } from "antd";

import { QProps } from ".";
import { evalExpr, useForm } from "@/utils";

export const QImmutable = forwardRef<any, QProps>(({ schema: { placeholder } }, ref) => {
  const form = useForm();

  const content = (() => {
    if(placeholder === undefined)
      return undefined;
    const [type, expr] = placeholder.split(":");
    if(type === "var")
      return form.getFieldValue(`#vars.${expr}`);
    if(type === "expr")
      return evalExpr(expr, form);
  })();

  return <Input ref={ref} placeholder={content} disabled/>;
});
