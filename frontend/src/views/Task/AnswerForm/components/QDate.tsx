import React, { FC } from "react";

import { Form } from "antd";

import { QProps as P } from ".";
import { DatePicker } from "@/components";

export const QDate: FC<P> = ({ rule: { placeholder }, namePath }) => <Form.Item name={namePath} noStyle>
  <DatePicker placeholder={placeholder} />
</Form.Item>;
