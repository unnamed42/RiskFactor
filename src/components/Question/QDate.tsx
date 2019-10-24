import React, { forwardRef } from "react";

import { DatePicker } from "antd";
import { DatePickerProps } from "antd/lib/date-picker/interface";

type P = QuestionProps & DatePickerProps;

// DatePicker 不是个类型？
export const QDate = forwardRef<any, P>(({ schema: { option } }, ref) =>
  <DatePicker ref={ref} placeholder={option && option.placeholder} />
);
