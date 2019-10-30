import React, { forwardRef, useState } from "react";

import { DatePicker } from "antd";
import moment, { Moment } from "moment";

import { QProps } from ".";

const format = "YYYY-MM-DD";

export const QDate = forwardRef<any, QProps<string>>(({ schema, onChange, value }, ref) => {

  const [date, setDate] = useState((value && moment(value, format)) || undefined);

  const changed = (date: Moment | null) => {
    if (date === null)
      return;
    setDate(date);
    if (onChange)
      onChange(date.format(format));
  };

  return <DatePicker ref={ref} value={date} onChange={changed}
    placeholder={schema.placeholder}
  />;
});
