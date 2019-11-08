import React, { forwardRef, useState } from "react";

import { DatePicker } from "antd";
import moment, { Moment } from "moment";

import { QProps } from ".";
import { datePattern } from "@/config";

export const QDate = forwardRef<any, QProps<string>>(({ schema, onChange, value }, ref) => {

  const [date, setDate] = useState((value && moment(value, datePattern)) || undefined);

  const changed = (date: Moment | null) => {
    if (date === null)
      return;
    setDate(date);
    onChange?.(date.format(datePattern));
  };

  return <DatePicker ref={ref} value={date} onChange={changed}
    placeholder={schema.placeholder}
  />;
});
