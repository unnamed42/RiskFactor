import React, { ReactElement } from "react";

import { Select } from "antd";
import { OptionProps } from "antd/lib/select";

import { decorated, generateChildren } from "./utils";
import { QuestionGenerator } from ".";

const inputFilter = (input: string, option: ReactElement<OptionProps>) => {
  const text = option.props.value!.toString();
  return text.startsWith(input);
};

export const qselect: QuestionGenerator = (schema, form) => {
  if (schema.option === undefined)
    throw new Error(`selection ${schema.field} has no option`);

  return decorated(schema, form)(
    <Select showSearch={schema.option!.filterKey !== undefined}
            placeholder={schema.option!.placeholder}
            filterOption={schema.option!.filterKey ? inputFilter : undefined}
            mode={schema.type === "MULTI_SELECT" ? "multiple" : undefined}>
      {generateChildren(schema, Select.Option)}
    </Select>
  );
};
