import React, { forwardRef } from "react";

import { DatePicker } from "antd";

export const QDate = forwardRef<any, QProps>(({ schema, value, onChange }, ref) =>
  <DatePicker ref={ref} value={value}
    placeholder={schema.option && schema.option.placeholder}
    onChange={e => onChange && onChange({ field: schema.field, value: e })}
  />
);
