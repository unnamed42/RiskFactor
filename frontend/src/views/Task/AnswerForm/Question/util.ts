import { GetFieldDecoratorOptions } from "antd/lib/form/Form";

import { Question as QSchema } from "@/types/task";
import { numberRegex, text } from "@/config";

export const validationRules = (schema: QSchema): GetFieldDecoratorOptions => {
  const ret: GetFieldDecoratorOptions = {};

  if (schema.required) {
    ret.rules = (ret.rules || []);
    ret.rules.push({
      required: true,
      message: text.required
    });
  }

  if(schema.type === "number") {
    ret.rules = (ret.rules || []);
    ret.rules.push({
      pattern: numberRegex,
      message: text.numberRequired
    });
  }

  return ret;
};
