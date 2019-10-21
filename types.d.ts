import * as api from "@/types/api";
declare global {
  type ApiToken = Readonly<api.ApiToken>;
  type ApiError = Readonly<api.ApiError>;
  type ApiTokenInfo = Readonly<api.ApiTokenInfo>;
}

import * as q from "@/types/question";
declare global {
  type QuestionOption = Readonly<q.QuestionOption>;
  type Question = Readonly<q.Question>;
  type Section = Readonly<q.Section>;
}

import { WrappedFormUtils } from "antd/lib/form/Form";

declare global {
  // 用于部件的属性上
  interface QuestionProps<T = any> {
    schema: Question;
    form: WrappedFormUtils<T>;
    isChild?: boolean;
  }
}
