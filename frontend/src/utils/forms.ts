import { GetFieldDecoratorOptions, WrappedFormUtils } from "antd/lib/form/Form";

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

export const validateAndScrollAsync = <T = any>(form: WrappedFormUtils<T>) => new Promise((resolve, reject) => {
  form.validateFieldsAndScroll((errors, values) => {
    if(errors) reject(errors);
    else resolve(values);
  });
});
