import { GetFieldDecoratorOptions } from "antd/lib/form/Form";

export const validationRules = (schema: Question): GetFieldDecoratorOptions => {
  const ret: GetFieldDecoratorOptions = {};

  if (schema.required) {
    ret.rules = (ret.rules || []);
    ret.rules.push({
      required: true,
      message: "该项必填"
    });
  }

  if (schema.selected) {
    const selection = schema.selected.split(",");
    ret.initialValue = selection;
  }

  return ret;
};

export const fieldName = (field: string, delim = "/") =>
  field.substring(Math.max(field.lastIndexOf(delim), 0));
