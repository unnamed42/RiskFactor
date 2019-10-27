export interface QuestionOption {
  required?: boolean;
  enabler?: boolean;
  prefixPostfix?: boolean;
  defaultSelected?: string;
  detail?: string;
  filterKey?: string;
  placeholder?: string;
  message?: string;
}

export interface Question {
  type: "TEXT" | "NUMBER" | "DATE" | "CHOICE" | "IMMUTABLE" |
      "YESNO_CHOICE" | "LIST" | "LIST_APPENDABLE" |
      "MULTI_CHOICE" | "SINGLE_CHOICE" |
      "MULTI_SELECT" | "SINGLE_SELECT";
  field: string;
  label?: string;
  option?: QuestionOption;
  list?: Question[];
}

export interface Section {
  title: string;
  questions: Question[];
}

export interface QChangeEvent<T = any> {
  field: string;
  value: T;
}

export interface QuestionProps<T = any> {
  schema: Question;
  value?: T;
  child?: boolean;
  onChange?: (e: QChangeEvent<T>) => void;
}
