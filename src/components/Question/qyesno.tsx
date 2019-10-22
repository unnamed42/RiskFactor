import React from "react";

import { Radio } from "antd";

import { decorated } from "./utils";
import { QuestionGenerator } from ".";

export const qyesno: QuestionGenerator = (schema, form) => {
  if (schema.option === undefined)
    throw new Error(`YESNO_CHOICE ${schema.field} has no associated option`);
  if (schema.option.detail === undefined)
    throw new Error(`YESNO_CHOICE ${schema.field} has no label configured`);

  const [yes, no] = schema.option.detail.split("/");

  return decorated(schema, form)(
    <Radio.Group>
      <Radio value={yes} />
      <Radio value={no} />
    </Radio.Group>
  );
};
