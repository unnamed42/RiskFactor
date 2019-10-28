import { GetFieldDecoratorOptions } from "antd/lib/form/Form";

export const validationRules = ({ option }: Question): GetFieldDecoratorOptions => {
  const ret: GetFieldDecoratorOptions = {};

  if (!option)
    return ret;

  if (option.required) {
    ret.rules = (ret.rules || []);
    ret.rules.push({
      required: true,
      message: "该项必填"
    });
  }

  if (option.defaultSelected) {
    const selection = option.defaultSelected.split(",");
    ret.initialValue = selection;
  }

  return ret;
};
