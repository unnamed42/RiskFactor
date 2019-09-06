import React, { forwardRef, ChangeEvent, useState } from "react";

import { Input } from "antd";
import { InputProps } from "antd/lib/input";

type CheckedInputProps = InputProps & {
  rule: RegExp;
};

export default forwardRef<any, CheckedInputProps>(({ rule, ...props }, ref) => {

  const [value, setValue] = useState(props.value);

  const check = (e: ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    if (value === "" || rule.test(value))
      setValue(value);
  };

  return (
    <Input ref={ref} {...props} type="text" value={value} onChange={check} />
  );
});
