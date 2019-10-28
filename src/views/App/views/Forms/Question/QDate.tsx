import React, { forwardRef, useState } from "react";

import { DatePicker } from "antd";
import moment, { Moment } from "moment";

import { QProps } from ".";

const format = "YYYY-MM-DD";

export const QDate = forwardRef<any, QProps<string>>((props, ref) => {

  const [value, setValue] = useState((props.value && moment(props.value, format)) || undefined);

  const { schema: { option } } = props;

  const onChange = (date: Moment | null) => {
    if (date === null)
      return;
    setValue(date);
    const value = date.format(format);
    if (props.onChange)
      props.onChange(value);
  };

  return <DatePicker ref={ref} value={value} onChange={onChange}
    placeholder={option && option.placeholder}
  />;
});
