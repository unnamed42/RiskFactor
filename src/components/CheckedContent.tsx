import React, { ReactNode, forwardRef, useState } from "react";

import { Checkbox } from "antd";

interface CheckedContentProps {
  text?: string;
  checked?: boolean;
  children?: ReactNode;
}

export default forwardRef<any, CheckedContentProps>((props, ref) => {

  const [checked, setChecked] = useState(props.checked || false);

  return (
    <Checkbox ref={ref} checked={checked} onChange={e => setChecked(e.target.checked)}>
      <span style={{ marginRight: 8 }}>{props.text}</span>
      {checked ? props.children : null}
    </Checkbox>
  );
});
