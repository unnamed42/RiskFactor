import React, { forwardRef, useState } from "react";

import { Radio } from "antd";
import { RadioProps } from "antd/lib/radio";

type P = QuestionProps & RadioProps;

export default forwardRef<any, P>(({ schema, ...remain }, ref) => {

  const [state, setState] = useState(0);

  if (schema.option === undefined)
    throw new Error(`YESNO_CHOICE ${schema.field} has no associated option`);
  if (schema.option.detail === undefined)
    throw new Error(`YESNO_CHOICE ${schema.field} has no label configured`);

  const texts = schema.option.detail.split("/");

  return (
    <Radio.Group onChange={e => setState(e.target.value)} value={state} {...remain}>
      <Radio value={0}>
        {texts[1]}
      </Radio>
      {/* 将“是”的选项置于后面，以便联动 */}
      <Radio value={1}>
        {texts[0]}
      </Radio>
    </Radio.Group>
  );
});
