import React, { forwardRef, ReactElement } from "react";

import { DatePicker } from "antd";
import { DatePickerProps } from "antd/lib/date-picker/interface";

import { decorated } from "./utils";

type P = QuestionProps & DatePickerProps;

export default forwardRef<any, P>(({ schema, form, ...remain }, ref) => {
  const text = schema.option && schema.option.placeholder;
  return decorated(schema, form)(
    <DatePicker ref={ref} placeholder={text} {...remain} />
  ) as ReactElement;
});
