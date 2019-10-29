import React, { FC } from "react";

import { Input } from "antd";

import { QProps } from ".";

export const QImmutable: FC<QProps> = ({ schema: { placeholder } }) => {
  return <Input defaultValue={placeholder}
    disabled={true}
  />;
};
