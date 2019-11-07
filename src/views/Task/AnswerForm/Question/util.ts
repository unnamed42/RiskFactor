import {GetFieldDecoratorOptions} from "antd/lib/form/Form";

import {Question as QSchema} from "@/types/task";

export const validationRules = (schema: QSchema): GetFieldDecoratorOptions => {
  const ret: GetFieldDecoratorOptions = {};

  if (schema.required) {
    ret.rules = (ret.rules || []);
    ret.rules.push({
      required: true,
      message: "该项必填"
    });
  }

  if (schema.selected)
    ret.initialValue = schema.selected.split(",");

  return ret;
};
