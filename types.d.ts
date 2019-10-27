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

  type QProps<T = any> = q.QuestionProps<T>;
  type QChangeEvent<T = any> = Readonly<q.QChangeEvent<T>>;
}

// utility types
declare global {
  type Override<T, U> = Pick<T, Exclude<keyof T, keyof U>> & U;
}
