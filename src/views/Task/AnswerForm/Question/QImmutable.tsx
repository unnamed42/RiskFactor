import React, {forwardRef} from "react";

import { Input } from "antd";

import { QProps } from ".";

export const QImmutable = forwardRef<any, QProps>(({ schema }, ref) =>
    <Input ref={ref} placeholder={schema.placeholder} disabled />
);