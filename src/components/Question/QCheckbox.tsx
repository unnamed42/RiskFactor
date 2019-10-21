import React, { forwardRef, ReactElement } from "react";

import { Checkbox } from "antd";
import { CheckboxProps } from "antd/lib/checkbox";

import { decorated, generateChildren } from "./utils";

type P = QuestionProps & CheckboxProps;

export default forwardRef<any, P>(({ schema, form, ...remain }, ref) => {
  return decorated(schema, form)(
    <Checkbox.Group ref={ref}>
      {generateChildren(schema, Checkbox)}
    </Checkbox.Group>
  ) as ReactElement;
});
