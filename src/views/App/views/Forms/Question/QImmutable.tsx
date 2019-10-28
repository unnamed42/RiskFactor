import React, { FC } from "react";

import { Input } from "antd";

import { QProps } from ".";

export const QImmutable: FC<QProps> = props => {
  const { option } = props.schema;

  return <Input defaultValue={option && option.placeholder}
    disabled={true}
  />;
};
