import React, { forwardRef } from "react";

import { Form, DatePicker } from "antd";
import { DatePickerProps } from "antd/lib/date-picker/interface";

import { decorated } from "./utils";

type P = QuestionProps & DatePickerProps;

export default forwardRef<any, P>(({ schema, form, ...remain }, ref) => {
  const { option } = schema;
  return (
    <Form.Item label={schema.description}>
      {
        decorated(schema, form)(
          <DatePicker ref={ref} placeholder={option ? option.placeholderText : undefined} {...remain} />
        )
      }
    </Form.Item>
  );
});
