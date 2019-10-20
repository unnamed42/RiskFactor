import React, { forwardRef } from "react";

import { Form, Checkbox } from "antd";
import { CheckboxProps } from "antd/lib/checkbox";

import { decorated, generateChildren } from "./utils";

type P = QuestionProps & CheckboxProps;

export default forwardRef<any, P>(({ schema, form, ...remain }, ref) => {
  return (
    <Form.Item label={schema.fieldName}>
      {
        decorated(schema, form)(
          <Checkbox.Group ref={ref}>
            {generateChildren(schema, Checkbox)}
          </Checkbox.Group>
        )
      }
    </Form.Item>
  );
});
