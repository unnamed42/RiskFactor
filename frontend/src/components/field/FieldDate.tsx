import React, { FC, useState } from "react";

import { Form } from "antd";
import dayjs, { Dayjs } from "dayjs";
import dayjsGenerateConfig from "rc-picker/es/generate/dayjs";
import generatePicker from "antd/es/date-picker/generatePicker";
import "antd/es/date-picker/style/index";

import { datePattern } from "@/config";
import type { RuleDate } from "@/api";
import type { FieldProps } from ".";

const AntdDatePicker = generatePicker<Dayjs>(dayjsGenerateConfig);

interface ComponentProps {
  value?: string;
  onChange?: (value: string) => void;
  placeholder?: string;
}

/**
 * ant design的DatePicker的value是一个`object`，需要在上传给服务器之前转换成`string`
 */
const DatePicker: FC<ComponentProps> = ({ value, onChange, ...props }) => {
  const [date, setDate] = useState(value && dayjs(value, { format: datePattern }) || undefined);

  const changed = (newDate: Dayjs | null) => {
    if (newDate === null) return;
    setDate(newDate);
    onChange?.(newDate.format(datePattern));
  };

  return <AntdDatePicker value={date} onChange={changed} {...props} />;
};

type P = FieldProps<RuleDate>;

export const FieldDate: FC<P> = ({ rule: { placeholder }, namePath }) => <Form.Item name={namePath} noStyle>
  <DatePicker placeholder={placeholder} />
</Form.Item>;
