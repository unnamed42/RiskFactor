import { GetFieldDecoratorOptions } from "antd/lib/form/Form";

import { Question as QSchema } from "@/types/task";
import { numberRegex, text } from "@/config";

export const validationRules = ({ required, type }: QSchema): GetFieldDecoratorOptions => {
  const rules: GetFieldDecoratorOptions["rules"] = [];

  if(required)
    rules.push({ required: true, message: text.required });
  if(type === "number")
    rules.push({ pattern: numberRegex, message: text.numberRequired });

  return { rules };
};
