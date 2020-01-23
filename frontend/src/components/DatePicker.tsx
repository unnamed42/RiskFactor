import React, { FC, useState } from "react";
import dayjs, { Dayjs } from "dayjs";
import dayjsGenerateConfig from "rc-picker/es/generate/dayjs";
import generatePicker, { PickerProps } from "antd/es/date-picker/generatePicker";
import "antd/es/date-picker/style/index";

import { datePattern } from "@/config";

const AntdDatePicker = generatePicker<Dayjs>(dayjsGenerateConfig);

interface ControlProps {
  value?: string;
  onChange?: (value: string) => void;
}

type AntdPickerProps = Omit<PickerProps<Dayjs>, "value" | "onChange" | "picker">;

/**
 * ant design的DatePicker的value是一个`object`，需要在上传给服务器之前转换成`string`
 */
export const DatePicker: FC<AntdPickerProps & ControlProps> = ({ value, onChange, ...props }) => {
  const [date, setDate] = useState(value && dayjs(value, { format: datePattern }) || undefined);

  const changed = (newDate: Dayjs | null) => {
    if (newDate === null) return;
    setDate(newDate);
    onChange?.(newDate.format(datePattern));
  };

  return <AntdDatePicker value={date} onChange={changed} {...(props as AntdPickerProps)} />;
};
