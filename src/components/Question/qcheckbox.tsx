import React from "react";

import { Checkbox } from "antd";

import { QuestionGenerator } from ".";
import { decorated, generateChildren } from "./utils";

export const qcheckbox: QuestionGenerator = (schema, form) => {
  return decorated(schema, form)(
    <Checkbox.Group>
      {generateChildren(schema, Checkbox)}
    </Checkbox.Group>
  );
};
